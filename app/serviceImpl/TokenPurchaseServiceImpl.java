package serviceImpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import model.BlockchainTransaction;
import model.Payment;
import model.Payment.Currency;
import model.PaymentTransaction;
import model.PaymentTransaction.Status;
import model.PaymentTransactionClientResponse;
import model.TokenBonusPhase;
import model.User;
import common.ServerException;
import json.PartialList;
import persistence.PayTransactionPersistenceService;
import play.Logger;
import play.libs.Json;
import service.BlockChainService;
import service.HMACSHA512Service;
import service.PaymentService;
import service.PurchaseQueueConsumerService;
import service.PurchaseQueueService;
import service.TokenBonusService;
import service.TokenPurchaseService;
import service.UserService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class TokenPurchaseServiceImpl
    implements
        TokenPurchaseService
{
	private static final String	TXN_ID						= "txn_id";
	private static final String	TXN_AMOUNT_RECEIVED			= "received_amount";
	private static final String	TXN_STATUS					= "status";
	private static final String	TXN_MERCHANT				= "merchant";
	private static final String	IPN_TYPE					= "ipn_type";
	private static final String	IPN_TYPE_API				= "api";
	private static final String	TRUE						= "true";
	private static final int	MAX_LENGTH					= 2048;
	private static final int	SECOND_INTO_MILLI			= 1000;
	private final static String	DATE_TIME_ZONE_FORMAT		= "yyyy-MM-dd HH:mm:ss XXX";
	private final static String	WITHDRAW_REQUEST_MSG		= "Withdraw requested at :";
	private final static String	TRANSACTION_NOT_IN_QUEUE	= "Transaction is not in queue";

	private static final String	INVALID_HMAC_DATA		= "hmac from ipn did not match data sent";
	private static final String	INVALID_TRANSACTION		= "Transaction not found for the ipn request";
	private static final String	INVALID_MERCHANT		= "Ipn merchant does not match to app merchant";
	private static final String	INVALID_IPN_TYPE		= "Not Expected Callback ipn type";
	private static final String	INVALID_CURRENCY		= ": This currency is not activated";
	private static final String	INVALID_BONUS_SETTING	= "Could not fetch bonus configuration";
	private static final String	LTCT_NOT_ACCEPTED		= "LTCT is not accepted";

	PaymentService								payService;
	UserService									userService;
	HMACSHA512Service							hmacSha512;
	PayTransactionPersistenceService			payTranPs;
	serviceImpl.ApplicationConfigurationService	config;
	BlockChainService							blockchainService;
	TokenBonusService							tokenBonusService;
	PurchaseQueueService						purchaseQueueService;
	PurchaseQueueConsumerService				purchaseQueueConsumerService;

	@Inject
	public TokenPurchaseServiceImpl(
	                                PaymentService payService,
	                                UserService userService,
	                                PayTransactionPersistenceService payTranPs,
	                                HMACSHA512Service hmacSha512,
	                                serviceImpl.ApplicationConfigurationService config,
	                                BlockChainService blockchainService,
	                                TokenBonusService tokenBonusService,
	                                PurchaseQueueService purchaseQueueService,
	                                PurchaseQueueConsumerService purchaseQueueConsumerService)
	{
		super();
		this.payService = payService;
		this.userService = userService;
		this.payTranPs = payTranPs;
		this.hmacSha512 = hmacSha512;
		this.config = config;
		this.blockchainService = blockchainService;
		this.tokenBonusService = tokenBonusService;
		this.purchaseQueueService = purchaseQueueService;
		this.purchaseQueueConsumerService = purchaseQueueConsumerService;
	}

	@Override
	public JsonNode createTransaction(
	    Payment pay,
	    User user)
	{
		if (pay.getCurrency()
		       .equals(Currency.LTCT) && !CONFIGURATION.LTCT_ACTIVATE.getValue()
		                                                             .equalsIgnoreCase("true"))
		{
			throw new ServerException(LTCT_NOT_ACCEPTED);
		}

		JsonNode tran = payService.createTransaction(pay, user);
		PaymentTransactionClientResponse res = Json.fromJson(tran.get("result"), PaymentTransactionClientResponse.class);
		PaymentTransaction tranData = setTransactionModel(user, pay, res);

		tranData = allocateTokensFromAmount(pay.getAmount(), tranData);
		payTranPs.add(tranData);
		return tran;
	}

	@Override
	public PaymentTransaction getPaymentTransactionById(
	    Long id)
	{
		return payTranPs.getById(id);
	}

	@Override
	public PartialList<PaymentTransaction> getPaymentTransactionByUserId(
	    long id,
	    int offset,
	    int size)
	{
		PartialList<PaymentTransaction> tans = payTranPs.getByUserId(id, offset, size);
		return tans;
	}

	@Override
	public void sendIcoTokens(
	    String hmac,
	    String rawBody)
	{
		checkValidHmac(hmac, rawBody);

		String json = toJson(rawBody);
		JsonNode body = Json.parse(json);
		String ipn_type = body.get(IPN_TYPE)
		                      .asText();

		if (!ipn_type.equals(IPN_TYPE_API))
		{
			throw new ServerException(INVALID_IPN_TYPE);
		}

		String merchant_id = body.get(TXN_MERCHANT)
		                         .asText();
		String txn_id = body.get(TXN_ID)
		                    .asText();
		String amount = body.get(TXN_AMOUNT_RECEIVED)
		                    .asText();
		String status = body.get(TXN_STATUS)
		                    .asText();
		Status convStatus = getTxnStatus(status);

		checkValidMerchant(merchant_id);

		PaymentTransaction txn = payTranPs.getByTransactionId(txn_id);
		if (txn == null)
		{
			throw new ServerException(INVALID_TRANSACTION);
		}

		User user = txn.getUser();

		txn.setCallback_log(constructIpnLog(json, txn.getCallback_log()));
		// IPN call is async
		if (!txn.getStatus()
		        .equals(Status.PAID))
		{
			txn.setStatus(convStatus);

			if (convStatus.equals(Status.PAID))
			{
				// This process was moved to createTransaction instead
				// txn = allocateTokensFromAmount(new BigDecimal(amount), txn);

				// Keep track of unsent incase of blockchainService failure
				user.setUnsentToken(user.getUnsentToken()
				                        .add(txn.getTotal_token()));

				BlockchainTransaction block = new BlockchainTransaction();
				block.setTxn_created_date(new Date());
				block.setTxn_retry_date(new Date(new Date().getTime() + 2 * SECOND_INTO_MILLI));
				block.setPayment_id(txn.getId());

				txn.setBlockchain_txn(block);
				addTaskToQueue(txn);
			}
		}
	}

	@Override
	public String retryWithdraw(
	    PaymentTransaction txn)
	{
		if (purchaseQueueService.removeByIdFromQueue(txn.getId()))
		{
			Date retryDate = new Date();
			txn.getBlockchain_txn()
			   .setTxn_retry_date(retryDate);
			addTaskToQueue(txn);

			SimpleDateFormat isoFormat = new SimpleDateFormat(DATE_TIME_ZONE_FORMAT);
			String message = WITHDRAW_REQUEST_MSG + isoFormat.format(retryDate);
			txn.setMessage(message);
			return message;
		} else
		{
			throw new ServerException(TRANSACTION_NOT_IN_QUEUE);
		}
	}

	private void addTaskToQueue(
	    PaymentTransaction task)
	{
		// Add block task to queue
		purchaseQueueService.addToQueue(task);
		// wake queue consumer thread
		purchaseQueueConsumerService.notifyWorker();
	}

	private PaymentTransaction setTransactionModel(
	    User user,
	    Payment pay,
	    PaymentTransactionClientResponse res)
	{
		PaymentTransaction payTran = new PaymentTransaction();
		payTran.setPayment_created_date(new Date());
		payTran.setAmount(res.getAmount());
		payTran.setCurrency1(pay.getCurrency());
		payTran.setCurrency2(pay.getCurrency());
		payTran.setTxn_id(res.getTxn_id());
		payTran.setAddress(res.getAddress());
		payTran.setConfirms_needed(res.getConfirms_needed());
		payTran.setTimeout(res.getTimeout());
		payTran.setQrcode_url(res.getQrcode_url());
		payTran.setStatus_url(res.getStatus_url());
		payTran.setUser(user);
		payTran.setStatus(Status.PENDING);
		return payTran;
	}

	private boolean checkValidHmac(
	    String hmac,
	    String rawBody)
	{
		String key = CONFIGURATION.PGW_IPN_KEY.getValue();
		String checkHmac = hmacSha512.getHmacSHA512(rawBody, key);
		Logger.debug(hmac);
		Logger.debug(rawBody);
		Logger.debug(checkHmac);
		if (hmac.equals(checkHmac))
		{
			return true;
		} else
		{
			throw new ServerException(INVALID_HMAC_DATA);
		}

	}

	private boolean checkValidMerchant(
	    String reqMerchant)
	{
		String appMerchant = CONFIGURATION.PGW_MERCHANT_ID.getValue();
		if (appMerchant.equals(reqMerchant))
		{
			return true;
		} else
		{
			throw new ServerException(INVALID_MERCHANT);
		}
	}

	private static String toJson(
	    String paramIn)
	{
		paramIn = paramIn.replaceAll("=", "\":\"");
		paramIn = paramIn.replaceAll("&", "\",\"");
		return "{\"" + paramIn + "\"}";
	}

	private Status getTxnStatus(
	    String statusCode)
	{
		int status = Integer.parseInt(statusCode);
		if (status >= 100)
		{
			return Status.PAID;
		} else if (status >= 0)
		{
			return Status.PENDING;
		} else
		{
			return Status.FAILED;
		}

	}

	private String constructIpnLog(
	    String newlog,
	    String oldlog)
	{
		String callbacklog = newlog + "; " + oldlog;
		if (callbacklog.length() > MAX_LENGTH)
		{
			callbacklog = callbacklog.substring(0, MAX_LENGTH);
		}
		return callbacklog;
	}

	private BigDecimal calculateTokens(
	    Currency currency,
	    BigDecimal amount)
	{
		BigDecimal token = BigDecimal.ZERO;
		BigDecimal rate = BigDecimal.ZERO;
		switch (currency)
		{
			case ETH :
				if (CONFIGURATION.ETH_ACTIVATE.getValue()
				                              .equals(TRUE))
				{
					rate = new BigDecimal(CONFIGURATION.ETH_RATE.getValue());
					token = amount.divide(rate);
				} else
				{
					throw new ServerException(Currency.ETH + INVALID_CURRENCY);
				}
				break;
			case BTC :
				if (CONFIGURATION.BTC_ACTIVATE.getValue()
				                              .equals(TRUE))
				{
					rate = new BigDecimal(CONFIGURATION.BTC_RATE.getValue());
					token = amount.divide(rate);
				} else
				{
					throw new ServerException(Currency.BTC + INVALID_CURRENCY);
				}
				break;
			case LTCT :
				if (CONFIGURATION.LTCT_ACTIVATE.getValue()
				                               .equals(TRUE))
				{
					rate = new BigDecimal(CONFIGURATION.LTCT_RATE.getValue());
					token = amount.divide(rate);
				} else
				{
					throw new ServerException(Currency.LTCT + INVALID_CURRENCY);
				}
				break;
			default :
				break;
		}
		return token;
	}

	private BigDecimal calculateBonus(
	    BigDecimal token)
	{
		PartialList<TokenBonusPhase> phases = tokenBonusService.getBonusPhases();
		if (phases == null)
		{
			throw new ServerException(INVALID_BONUS_SETTING);
		}
		LinkedList<TokenBonusPhase> phaseList = new LinkedList<TokenBonusPhase>(phases.getItems());
		Date date = new Date();
		BigDecimal bonus = BigDecimal.ZERO;
		Iterator<TokenBonusPhase> iter = phaseList.descendingIterator();

		while (iter.hasNext())
		{
			TokenBonusPhase phase = iter.next();
			if (date.after(phase.getDate()))
			{
				BigDecimal bonusRate = BigDecimal.valueOf(((double) phase.getBonus() / 100.0));
				bonus = bonusRate.multiply(token);
			}
		}
		return bonus;
	}

	private PaymentTransaction allocateTokensFromAmount(
	    BigDecimal amountDecimal,
	    PaymentTransaction txn)
	{
		BigDecimal token = calculateTokens(txn.getCurrency1(), amountDecimal);
		BigDecimal bonus = calculateBonus(token);
		BigDecimal total = token.add(bonus);

		txn.setTotal_token(total);
		txn.setBonus_token(bonus);
		txn.setPrebonus_token(token);
		return txn;
	}

}
