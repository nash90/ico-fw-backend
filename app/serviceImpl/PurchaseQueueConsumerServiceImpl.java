package serviceImpl;

import java.math.BigDecimal;
import java.util.Date;
import javax.inject.Inject;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.fasterxml.jackson.databind.JsonNode;
import model.BlockchainTransaction;
import model.BlockchainTransaction.TokenStatus;
import model.PaymentTransaction;
import model.PaymentTransaction.Status;
import model.User;
import common.DatabaseExecutionContext;
import common.ServerException;
import json.PartialList;
import persistence.BlockchainTransactionPersistenceService;
import persistence.PayTransactionPersistenceService;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.Json;
import service.BlockChainService;
import service.PurchaseQueueConsumerService;
import service.PurchaseQueueService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class PurchaseQueueConsumerServiceImpl
    implements
        PurchaseQueueConsumerService
{
	private static final int	MINUTE_TO_MILLI_SEC	= 1000 * 60;
	private static final int	DAY_TO_MILLI_SEC	= 1000 * 60 * 60 * 24;

	private static final String BLOCKCHAIN_TXN_ID = "transactionHash";

	private static final String	TOKEN_SEND_FAILED			= "Error: PurchaseQueueConsumerServiceImpl.runConsumer: Failed to send token through blockchain client, transaction task pushed back to queue to retry";
	private static final String	ERROR_NOT_PAID				= "Queue Consumer Error: Payment status is not PAID";
	private static final String	ERROR_TOKEN_AREADY_SENT		= "Queue Consumer Error: Token has alredy been sent for this payment with blockchain txn id: ";
	private static final String	ERROR_TASK_ALREADY_STARTED	= "Queue Consumer Error: Task already started from queue, preventing double token send ";
	private static final String	ERROR_INVALID_WALLET		= "Wallet is not set in user account";

	PurchaseQueueService						purchaseQueueService;
	BlockChainService							blockchainService;
	serviceImpl.ApplicationConfigurationService	config;

	PayTransactionPersistenceService		payTranPs;
	BlockchainTransactionPersistenceService	blockTranPs;
	DatabaseExecutionContext				dbContext;

	private final JPAApi jpaApi;

	public static boolean activeConsumer = false;

	public static Thread worker;

	@Inject
	public PurchaseQueueConsumerServiceImpl(
	                                        PurchaseQueueService purchaseQueueService,
	                                        BlockChainService blockchainService,
	                                        serviceImpl.ApplicationConfigurationService config,
	                                        JPAApi jpaApi,
	                                        PayTransactionPersistenceService payTranPs,
	                                        BlockchainTransactionPersistenceService blockTranPs,
	                                        DatabaseExecutionContext dbContext)
	{
		super();
		this.purchaseQueueService = purchaseQueueService;
		this.blockchainService = blockchainService;
		this.config = config;
		this.jpaApi = jpaApi;
		this.payTranPs = payTranPs;
		this.blockTranPs = blockTranPs;
		this.dbContext = dbContext;
		// this.startQueueConsumer();
	}

	class QueueRunnable
	    implements
	        Runnable
	{
		@Override
		public void run()
		{
			while (PurchaseQueueConsumerServiceImpl.activeConsumer)
			{
				try
				{
					runConsumerTask();
				} catch (Exception | Error ex)
				{
					ex.printStackTrace();
				}
			}

		}

	}

	public void startThread()
	{
		Logger.debug("starting token send queue thread");
		Runnable runnable = new QueueRunnable();
		// CompletableFuture.runAsync(runnable, dbContext);

		worker = new Thread(runnable);
		worker.start();
	}

	@Override
	public void startQueueConsumer()
	{
		purchaseQueueService.startQueue();
		loadQueueFromDB();
		PurchaseQueueConsumerServiceImpl.activeConsumer = true;
		startThread();
	}

	@Override
	public void stopQueueConsumer()
	{
		synchronized (purchaseQueueService.getQueue())
		{
			synchronized (worker)
			{
				PurchaseQueueConsumerServiceImpl.activeConsumer = false;
				purchaseQueueService.getQueue()
				                    .clear();
				worker.notify();
			}
		}
	}

	@Override
	public Thread getWorker()
	{
		return PurchaseQueueConsumerServiceImpl.worker;
	}

	@Override
	public void notifyWorker()
	{
		synchronized (worker)
		{
			worker.notify();
		}

	}

	private void loadQueueFromDB()
	{
		jpaApi.withTransaction(() -> {
			PartialList<PaymentTransaction> pendingPay = payTranPs.getAllPendingPayment();

			for (PaymentTransaction txn : pendingPay.getItems())
			{
				// reset the retry date to current and count to 0
				txn.getBlockchain_txn()
				   .setTxn_retry_date(new Date());
				txn.getBlockchain_txn()
				   .setTxn_retry_count(0);
				purchaseQueueService.addToQueue(txn);
			}
			Logger.debug("Loaded " + pendingPay.getLength() + " pending purchases from DB");
		});
	}

	private synchronized void runConsumerTask()
	{

		if (validTimeToRunTask())
		{
			Logger.debug("Pop task from queue");
			// Always get data from DB to get latest update of status
			PaymentTransaction txn = getPaymentTransaction(purchaseQueueService.removeFromQueue()
			                                                                   .getId());
			BlockchainTransaction task = txn.getBlockchain_txn();

			String wallet = txn.getUser()
			                   .getWalletAddress();

			if (wallet == null || wallet.equals(""))
			{
				throw new ServerException(ERROR_INVALID_WALLET);
			}

			BigDecimal total = txn.getTotal_token();

			TransactionReceipt transaction = null;

			if (!txn.getStatus()
			        .equals(Status.PAID))
			{
				throw new ServerException(ERROR_NOT_PAID); // remove comment
			}

			if (task.getBlockchain_txn_status()
			        .equals(TokenStatus.SENT))
			{
				throw new ServerException(ERROR_TOKEN_AREADY_SENT + task.getBlockchain_txn_id());
			}

			if (task.getBlockchain_txn_status()
			        .equals(TokenStatus.PROCESSING))
			{
				throw new ServerException(ERROR_TASK_ALREADY_STARTED + task.getBlockchain_txn_id());
			}

			beforeApiCallHandler(txn);

			try
			{
				transaction = blockchainService.sendToken(wallet, total);

			} catch (Exception | Error err)
			{
				onFailHandler(txn, task);
			}

			if (transaction != null)
			{
				onSuccessHandler(txn, transaction);
			}

		} else if (!purchaseQueueService.checkEmptyQueue())
		{
			long time = getTimeToNextTry(purchaseQueueService.getQueue()
			                                                 .peek()
			                                                 .getBlockchain_txn());
			if (time > 0)
			{
				synchronized (worker)
				{
					try
					{
						Logger.debug("Waiting until next try, waiting .... " + time + " milli sec");
						worker.wait(time);
					} catch (InterruptedException e)
					{
						Logger.debug("Next try Queue Thread Wait Interrupted ");
					}
				}
			}
		} else
		{
			synchronized (worker)
			{
				try
				{
					int waitTime = new Integer(CONFIGURATION.QUEUE_THREAD_WAIT_TIME.getValue());
					Logger.debug("Nothing in queue, waiting " + waitTime + " minute ....");
					worker.wait(waitTime * MINUTE_TO_MILLI_SEC);
				} catch (InterruptedException e)
				{
					Logger.debug("Empty Queue Thread Wait Interrupted ");
				}
			}
		}
	}

	private void beforeApiCallHandler(
	    PaymentTransaction txn)
	{
		txn.getBlockchain_txn()
		   .setBlockchain_txn_status(TokenStatus.PROCESSING);
		jpaApi.withTransaction(() -> {
			PaymentTransaction updatetxn = payTranPs.getById(txn.getId());
			BlockchainTransaction block = updatetxn.getBlockchain_txn();
			if (block != null)
			{
				block.setBlockchain_txn_status(TokenStatus.PROCESSING);
			}
		});
	}

	private void onSuccessHandler(
	    PaymentTransaction txn,
	    TransactionReceipt transaction)
	{
		Logger.debug(transaction.toString());
		JsonNode jsonNode = Json.toJson(transaction);
		String transactionId = jsonNode.get(BLOCKCHAIN_TXN_ID)
		                               .asText();
		jpaApi.withTransaction(() -> {

			PaymentTransaction updatetxn = payTranPs.getById(txn.getId());
			User updateUser = updatetxn.getUser();
			// Remove sent token from unsent count
			updateUser.setUnsentToken(updateUser.getUnsentToken()
			                                    .subtract(updatetxn.getTotal_token()));

			updateUser.setPresaleBonus(updateUser.getPresaleBonus()
			                                     .add(updatetxn.getBonus_token()));
			updateUser.setTokenPurchased(updateUser.getTokenPurchased()
			                                       .add(updatetxn.getPrebonus_token()));

			BlockchainTransaction block = updatetxn.getBlockchain_txn();
			block.setBlockchain_txn_id(transactionId);
			block.setBlockchain_txn_status(TokenStatus.SENT);
		});
	}

	private void onFailHandler(
	    PaymentTransaction txn,
	    BlockchainTransaction task)
	{
		int retryCount = task.getTxn_retry_count() + 1;
		Date newRetryTime = getNewRetryTime(task);
		task.setTxn_retry_count(retryCount);
		task.setTxn_retry_date(newRetryTime);
		purchaseQueueService.addToQueue(txn);

		jpaApi.withTransaction(() -> {
			PaymentTransaction updatetxn = payTranPs.getById(txn.getId());
			BlockchainTransaction block = updatetxn.getBlockchain_txn();
			if (block != null)
			{
				block.setTxn_retry_count(retryCount);
				block.setTxn_retry_date(newRetryTime);
				block.setBlockchain_txn_status(TokenStatus.FAILED);
			}
		});
		Logger.info(TOKEN_SEND_FAILED);
	}

	private PaymentTransaction getPaymentTransaction(
	    Long id)
	{
		PaymentTransaction txn = new PaymentTransaction();
		txn = jpaApi.withTransaction(() -> {
			return payTranPs.getById(id);
		});

		return txn;
	}

	private Date getNewRetryTime(
	    BlockchainTransaction task)
	{
		int tryCount = task.getTxn_retry_count();
		long addTime = tryCount * 2 * (tryCount + 1) * MINUTE_TO_MILLI_SEC;
		if (addTime > DAY_TO_MILLI_SEC)
		{
			addTime = DAY_TO_MILLI_SEC;
		}
		long newTry = new Date().getTime() + addTime;
		Date newTryDate = new Date(newTry);
		return newTryDate;
	}

	private long getTimeToNextTry(
	    BlockchainTransaction blockchainTransaction)
	{
		long currentDate = new Date().getTime();
		long tryDate = blockchainTransaction.getTxn_retry_date()
		                                    .getTime();
		long sleepTime = tryDate - currentDate;
		if (sleepTime < 0)
		{
			sleepTime = 0;
		}
		return sleepTime;
	}

	private boolean validTimeToRunTask()
	{
		boolean tryTimePassed = false;
		boolean isTryTime = false;

		boolean queueEmpty = purchaseQueueService.checkEmptyQueue();

		Date currentDate = new Date();
		if (!queueEmpty)
		{
			tryTimePassed = purchaseQueueService.getQueue()
			                                    .peek()
			                                    .getBlockchain_txn()
			                                    .getTxn_retry_date()
			                                    .before(currentDate);
			isTryTime = purchaseQueueService.getQueue()
			                                .peek()
			                                .getBlockchain_txn()
			                                .getTxn_retry_date()
			                                .equals(currentDate);
		}
		return tryTimePassed || isTryTime;
	}
}
