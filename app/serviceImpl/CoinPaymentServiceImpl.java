package serviceImpl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import model.Payment;
import model.PaymentTransactionClientRequest;
import model.User;
import common.ServerException;
import play.libs.Json;
import play.mvc.Http;
import service.HMACSHA512Service;
import service.PaymentService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class CoinPaymentServiceImpl
    implements
        PaymentService
{
	// --- NESTED TYPES --- //

	private static enum Method
	{
			GET,
			POST,
			PUT,
			DELETE
	}

	// --- FIELDS --- //
	private static final String	HMAC				= "hmac";
	private static final String	CONTENT_TYPE		= "Content-Type";
	private static final String	CONTENT_TYPE_FORM	= "application/x-www-form-urlencoded";

	private static final String	UNSUPPORTED_METHOD			= "Unsupported method: ";
	private static final String	ERROR_CALLING_PAYMENT_GW	= "Error calling the payment gateway";
	private static final String	ERROR_PARSING_RESPONSE		= "Error parsing response";
	private static final String	INVALID_GW_RESPONSE			= "Invalid Response Status from payment gateway ";

	private static final String	PGW_CREATE_TRANSACTION_CMD	= "create_transaction";
	private static final double	PGW_API_VERSION				= 1.0;
	private static final String	PGW_RESPONSE_TYPE			= "json";

	private final OkHttpClient									httpClient;
	private final serviceImpl.ApplicationConfigurationService	config;
	private final HMACSHA512Service								hmacSha512;

	@Inject
	public CoinPaymentServiceImpl(
	                              OkHttpClient httpClient,
	                              serviceImpl.ApplicationConfigurationService config,
	                              HMACSHA512Service hmacSha512)
	{
		super();
		this.httpClient = httpClient;
		this.config = config;
		this.hmacSha512 = hmacSha512;
	}

	private Response doRequest(
	    String url,
	    Object payload,
	    Method m)
	    throws IOException
	{
		Builder rq = new Request.Builder().url(url);

		String formData = "";
		RequestBody body = null;
		if (payload != null)
		{
			formData = toFormData(payload);
			String hmac = hmacSha512.getHmacSHA512(formData, CONFIGURATION.PGW_PRIVATE_KEY.getValue());
			// Logger.debug(hmac);
			rq.addHeader(HMAC, hmac);
			rq.addHeader(CONTENT_TYPE, CONTENT_TYPE_FORM);
			body = RequestBody.create(MediaType.parse(CONTENT_TYPE_FORM), formData);

		}

		Response res = null;

		switch (m)
		{
			case GET :
				res = executeHttpCall(rq.get()
				                        .build());
				break;

			case POST :
				res = executeHttpCall(rq.post(body)
				                        .build());
				break;

			default :
				throw new ServerException(UNSUPPORTED_METHOD + m);
		}

		return res;
	}

	private Response executeHttpCall(
	    Request request)
	    throws IOException
	{
		return httpClient.newCall(request)
		                 .execute();
	}

	private String toFormData(
	    Object payload)
	{

		JsonNode node = Json.toJson(payload);
		String formData = "";
		Iterator<Entry<String, JsonNode>> fields = node.fields();
		while (fields.hasNext())
		{
			Entry<String, JsonNode> next = fields.next();
			String value = next.getValue()
			                   .asText();
			String key = next.getKey();
			if (value != "null")
			{
				String connector = (formData.length() == 0) ? "" : "&";
				formData = formData + connector + key + "=" + value;
			}
		}
		return formData;
	}

	@Override
	public JsonNode createTransaction(
	    Payment pay,
	    User user)
	{
		PaymentTransactionClientRequest transaction = createTransactionRequest(pay, user);

		Response rs = null;
		try
		{
			rs = doRequest(CONFIGURATION.PGW_SITE.getValue(), transaction, Method.POST);
		} catch (IOException e)
		{
			throw new ServerException(ERROR_CALLING_PAYMENT_GW);
		}
		switch (rs.code())
		{
			case Http.Status.OK :
			{
				JsonNode jsonNode = null;
				try
				{
					jsonNode = Json.parse(rs.body()
					                        .string());
				} catch (IOException e)
				{
					throw new ServerException(ERROR_PARSING_RESPONSE);
				}
				return jsonNode;
			}
			default :
				throw new ServerException(INVALID_GW_RESPONSE + rs.code());
		}
	}

	private PaymentTransactionClientRequest createTransactionRequest(
	    Payment pay,
	    User user)
	{
		PaymentTransactionClientRequest transaction = new PaymentTransactionClientRequest();
		transaction.setCmd(PGW_CREATE_TRANSACTION_CMD);
		transaction.setVersion(PGW_API_VERSION);
		transaction.setKey(CONFIGURATION.PGW_PUBLIC_KEY.getValue());
		transaction.setIpn_url(CONFIGURATION.PGW_IPN_URL.getValue());
		transaction.setFormat(PGW_RESPONSE_TYPE);
		transaction.setAmount(pay.getAmount());
		transaction.setCurrency1(pay.getCurrency());
		transaction.setCurrency2(pay.getCurrency());
		transaction.setBuyer_name(user.getName());
		transaction.setBuyer_email(user.getEmail());
		return transaction;
	}
}
