package serviceImpl;

import java.io.File;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import common.ServerException;
import play.Logger;
import play.libs.Json;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

/**
 * Simple Settings service that provides configuration properties
 * from application.conf file.
 */
@Singleton
public class ApplicationConfigurationService
{

	public static enum CONFIGURATION
	{
			CONFIG_PATH (
			    "custom.settings.configPath",
			    "prod.conf",
			    TYPE.STRING),
			INIT_REDIRECT_URL (
			    "custom.settings.initRedirectUrl",
			    "/init",
			    TYPE.STRING),
			SECURE_COOKIE (
			    "custom.settings.secureCookie",
			    "false",
			    TYPE.STRING),
			SITE_URL (
			    "custom.settings.siteUrl",
			    "",
			    TYPE.STRING),
			CONTACT_EMAIL (
			    "custom.settings.contactMail",
			    "",
			    TYPE.STRING),
			WALLET_ADDRESS (
			    "custom.settings.walletAddress",
			    "",
			    TYPE.STRING),
			WALLET_KEY (
			    "custom.settings.walletKey",
			    "",
			    TYPE.STRING),
			AUTH_KEY (
			    "custom.settings.authKey",
			    "",
			    TYPE.STRING),
			HASH_SEED (
			    "custom.settings.hashSeed",
			    "",
			    TYPE.STRING),
			PGW_SITE (
			    "custom.settings.pgw.site",
			    "",
			    TYPE.STRING),
			PGW_IPN_URL (
			    "custom.settings.pgw.ipnUrl",
			    "",
			    TYPE.STRING),
			PGW_IPN_KEY (
			    "custom.settings.pgw.ipnSecret",
			    "",
			    TYPE.STRING),
			PGW_MERCHANT_ID (
			    "custom.settings.pgw.merchantId",
			    "",
			    TYPE.STRING),
			PGW_PUBLIC_KEY (
			    "custom.settings.pgw.publicKey",
			    "",
			    TYPE.STRING),
			PGW_PRIVATE_KEY (
			    "custom.settings.pgw.privateKey",
			    "",
			    TYPE.STRING),
			NETWORK_CLIENT (
			    "custom.settings.blockchain.client",
			    "",
			    TYPE.STRING),
			CONTRACT_ADDRESS (
			    "custom.settings.blockchain.contractAddress",
			    "",
			    TYPE.STRING),
			GAS_PRICE (
			    "custom.settings.blockchain.gasPrice",
			    "20",
			    TYPE.STRING),
			GAS_LIMIT (
			    "custom.settings.blockchain.gasLimit",
			    "47000",
			    TYPE.STRING),
			TOKEN_DECIMAL (
			    "custom.settings.blockchain.tokenDecimal",
			    "",
			    TYPE.STRING),
			ETH_RATE (
			    "custom.settings.crypto.rate.ETH",
			    "",
			    TYPE.STRING),
			BTC_RATE (
			    "custom.settings.crypto.rate.BTC",
			    "",
			    TYPE.STRING),
			LTCT_RATE (
			    "custom.settings.crypto.rate.LTCT",
			    "",
			    TYPE.STRING),
			ETH_ACTIVATE (
			    "custom.settings.crypto.activation.ETH",
			    "true",
			    TYPE.STRING),
			BTC_ACTIVATE (
			    "custom.settings.crypto.activation.BTC",
			    "true",
			    TYPE.STRING),
			LTCT_ACTIVATE (
			    "custom.settings.crypto.activation.LTCT",
			    "false",
			    TYPE.STRING),
			BONUS_PHASE (
			    "custom.settings.bonus",
			    new Object(),
			    TYPE.ARRAY),
			QUEUE_THREAD_WAIT_TIME (
			    "custom.settings.queueConsumerWaitTime",
			    "10",
			    TYPE.STRING),
			AGENT_KEY_EMAIL (
			    "custom.settings.target.email",
			    "",
			    TYPE.STRING),
			XCOIN_DATA_FILE (
			    "custom.settings.propertyFile",
			    "conf/app.properties",
			    TYPE.STRING),;

		private static enum TYPE
		{
				STRING,
				NUMBER,
				OBJECT,
				ARRAY,
				BOOLEAN,
				NULL;
		}

		private final String	key;
		public String			value	= null;
		public TYPE type;
		private Object			object;

		private final Config config = null;

		CONFIGURATION(
		              String configParameter,
		              String value,
		              ApplicationConfigurationService.CONFIGURATION.TYPE jsonType)
		{
			this.key = configParameter;
			this.value = value;
			this.type = jsonType;
		}

		CONFIGURATION(
		              String configParameter,
		              Object value,
		              ApplicationConfigurationService.CONFIGURATION.TYPE jsonType)
		{
			this.key = configParameter;
			this.object = value;
			this.type= jsonType;
		}

		public String getKey()
		{
			return this.key;
		}

		public String getValue()
		{
			return this.value;
		}
		
		public Object getObjectValue() {
			return this.object;
		}
		
		public TYPE getType()
		{
			return this.type;
		}

		public void setValue(
		    String value)
		{
			this.value = value;
		}
		
		public void setObjectValue(Object value) {
			this.object = value;
		}

		@Override
		public String toString()
		{
			return this.value.toString();
		}
	}

	private final Config config;

	// --- CONSTRUCTORS --- //

	@Inject
	public ApplicationConfigurationService(
	Config config)
	{
		// First, load the application.conf file from the resources
		// This overwrites the hard coded values
		// This is overwriteable from the the -Dconfig.file=XXX parameter
		// settings.
		this.config = config;
		updateToConfig(this.config);
		  
		File configFile = new File(CONFIGURATION.CONFIG_PATH.getValue());
		  
		// Then check to see if there is a config file at the config path
		// specified.
		// This overwrites the fixed application.conf values, or the
		// -Dconfig.file=XXX parameter settings.
		if (configFile.exists())
		{
			Config loadedConfig = ConfigFactory.parseFile(configFile);
			this.updateToConfig(loadedConfig);
	
		}else {
			Logger.debug("Missing configuration file at path: {} -> {}",
			CONFIGURATION.CONFIG_PATH.getValue(), configFile.getAbsolutePath());
		}	  
	}
	 

	public void updateToConfig(
	    Config config)
	{
		for (CONFIGURATION c : CONFIGURATION.values())
		{
			if (config.hasPath(c.getKey()))
			{
				if(c.getType().equals(CONFIGURATION.TYPE.STRING)) {
					c.setValue(config.getString(c.getKey()));
				}else if(c.getType().equals(CONFIGURATION.TYPE.ARRAY)){
					c.setObjectValue(config.getAnyRefList(c.getKey()));
				}
			}
		}
	}

	public List<? extends Object> getBonusPhase()
	{
		@SuppressWarnings("unchecked")
		List<? extends Object> val = (List<? extends Object>) CONFIGURATION.BONUS_PHASE.getObjectValue();
		return val;
	}

	public JsonNode getCryptoRates()
	{
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("ETH", CONFIGURATION.ETH_RATE.getValue());
		node.put("BTC", CONFIGURATION.BTC_RATE.getValue());
		node.put("LTCT", CONFIGURATION.LTCT_RATE.getValue());

		return Json.toJson(node);
	}
}
