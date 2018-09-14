package serviceImpl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import service.AESService;

public class AESServiceImpl
    implements
        AESService
{
	private static SecretKeySpec	secretKey;
	private static byte[]			key;

	private static String	decryptedString;
	private static String	encryptedString;

	@Override
	public void setKey(
	    String myKey)
	{

		MessageDigest sha = null;
		try
		{
			key = myKey.getBytes("UTF-8");
			System.out.println(key.length);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			System.out.println(key.length);
			System.out.println(new String(key, "UTF-8"));
			secretKey = new SecretKeySpec(key, "AES");

		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getDecryptedString()
	{
		return decryptedString;
	}

	private void setDecryptedString(
	    String decryptedString)
	{
		AESServiceImpl.decryptedString = decryptedString;
	}

	@Override
	public String getEncryptedString()
	{
		return encryptedString;
	}

	private void setEncryptedString(
	    String encryptedString)
	{
		AESServiceImpl.encryptedString = encryptedString;
	}

	public void encrypt(
	    String strToEncrypt)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			setEncryptedString(Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8"))));

		} catch (Exception e)
		{
			System.out.println("Error while encrypting:");
			e.printStackTrace();
		}
	}

	public void decrypt(
	    String strToDecrypt)
	{
		try
		{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			setDecryptedString(new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt))));

		} catch (Exception e)
		{
			System.out.println("Error while decrypting: ");
			e.printStackTrace();
		}
	}

	@Override
	public String encryptWithKey(
	    String key,
	    String msg)
	{
		this.setKey(key);
		this.encrypt(msg);
		return this.getEncryptedString();
	}

	@Override
	public String decryptWithKey(
	    String key,
	    String msg)
	{
		this.setKey(key);
		this.decrypt(msg);
		return this.getDecryptedString();
	}
}
