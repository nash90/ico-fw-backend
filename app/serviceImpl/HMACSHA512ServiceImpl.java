package serviceImpl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import common.ServerException;
import play.Logger;
import service.HMACSHA512Service;

public class HMACSHA512ServiceImpl
    implements
        HMACSHA512Service
{
	// --- FIELDS --- //
	private static final String	HMAC_SHA512	= "HmacSHA512";
	private static final String	HMAC		= "hmac";

	private final serviceImpl.ApplicationConfigurationService config;

	@Inject
	public HMACSHA512ServiceImpl(
	                             serviceImpl.ApplicationConfigurationService config)
	{
		super();
		this.config = config;
	}

	@Override
	public String getHmacSHA512(
	    String data,
	    String key)
	{
		Logger.debug(data);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
		Mac mac;
		try
		{
			mac = Mac.getInstance(HMAC_SHA512);
		} catch (NoSuchAlgorithmException e)
		{
			throw new ServerException(e);
		}
		try
		{
			mac.init(secretKeySpec);
		} catch (InvalidKeyException e)
		{
			throw new ServerException(e);
		}
		return toHexString(mac.doFinal(data.getBytes()));
	}

	private static String toHexString(
	    byte[] bytes)
	{
		Formatter formatter = new Formatter();
		for (byte b : bytes)
		{
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
}
