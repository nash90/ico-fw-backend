package serviceImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.inject.Inject;
import common.ServerException;
import service.AppPropertiesService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class AppPropertiesServiceImpl
    implements
        AppPropertiesService
{

	private final static String ERROR_SAVING_PROPERTY = "Properties File could not be saved!!";

	Properties									properties;
	serviceImpl.ApplicationConfigurationService	config;

	@Inject
	public AppPropertiesServiceImpl(
	                                serviceImpl.ApplicationConfigurationService config)
	{
		super();
		this.config = config;
		this.loadProperties();
	}

	@Override
	public String getValue(
	    PropertyKey key)
	{
		return properties.getProperty(key.getKey(), key.getValue());
	}

	@Override
	public void setValue(
	    PropertyKey key,
	    String value)
	{
		properties.setProperty(key.getKey(), value);
	}

	@Override
	public void loadProperties()
	{
		try
		{
			properties = new Properties();
			InputStream is = new FileInputStream(CONFIGURATION.XCOIN_DATA_FILE.getValue());
			// is = new
			// FileInputStream(config.getConfigValue(SETTING.XCOIN_DATA_FILE.getValue()));
			properties.load(is);
			is.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void savePropertiesFile()
	{
		try
		{
			OutputStream out = new FileOutputStream(CONFIGURATION.XCOIN_DATA_FILE.getValue());
			properties.store(out, "Properties");
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new ServerException(ERROR_SAVING_PROPERTY);
		}
	};
}
