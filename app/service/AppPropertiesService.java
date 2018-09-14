package service;

import com.google.inject.ImplementedBy;
import serviceImpl.AppPropertiesServiceImpl;

@ImplementedBy(AppPropertiesServiceImpl.class)
public interface AppPropertiesService
{
	public static enum PropertyKey
	{
			ENCRYPTED_KEY (
			    "encryptedKey",
			    "");

		private String	key;
		private String	defaultValue;

		PropertyKey(
		            String key,
		            String value)
		{
			this.key = key;
			this.defaultValue = value;
		}

		public String getKey()
		{
			return key;
		}

		public String getValue()
		{
			return defaultValue;
		}
	}

	public void loadProperties();

	public String getValue(
	    PropertyKey key);

	public void setValue(
	    PropertyKey key,
	    String value);

	public void savePropertiesFile();
}
