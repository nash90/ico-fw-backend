package service;

import com.google.inject.ImplementedBy;
import serviceImpl.AESServiceImpl;

@ImplementedBy(AESServiceImpl.class)
public interface AESService
{
	public void setKey(
	    String myKey);

	public String getDecryptedString();

	public String getEncryptedString();

	public String encryptWithKey(
	    String key,
	    String msg);

	public String decryptWithKey(
	    String key,
	    String msg);
}
