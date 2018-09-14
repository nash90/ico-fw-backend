package serviceImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.ServerException;
import content.InitializePage;
import content.SecuredInformation;
import play.libs.Json;
import secure.Vault;
import service.AESService;
import service.AppPropertiesService;
import service.AppPropertiesService.PropertyKey;
import service.BlockChainService;
import service.InitializationService;
import service.UserService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class InitializationServiceImpl
    implements
        InitializationService
{

	UserService									userService;
	EmailService								emailService;
	serviceImpl.ApplicationConfigurationService	config;
	AppPropertiesService						properties;
	AESService									aes;
	BlockChainService							blockchainService;

	public final String	INIT_PAGE;
	public final String	ERROR_DECRYPTION		= "Decryption Failed!!";
	public final String	ERROR_NO_ETHEREUM_KEY	= "Ethereum Wallet Key not porvided";
	public final String	ERROR_INVALID_CONTRACT	= "Contract is not valid, backend wrapper mismatch to the provided contract";

	@Inject
	InitializationServiceImpl(
	                          UserService userService,
	                          EmailService emailService,
	                          serviceImpl.ApplicationConfigurationService config,
	                          AppPropertiesService properties,
	                          AESService aes,
	                          BlockChainService blockchainService)
	{
		this.userService = userService;
		this.emailService = emailService;
		this.config = config;
		this.properties = properties;
		this.aes = aes;
		this.blockchainService = blockchainService;

		this.INIT_PAGE = InitializePage.INIT_PAGE_HTML.replace("{DYNAMIC_CONTENT_LINE}", Arrays.stream(SecuredInformation.values())
		                                                                                       .map(s -> {
			                                                                                       String renderedText;
			                                                                                       renderedText = InitializePage.DYNAMIC_CONTENT_LINE
			                                                                                                                                         .replaceAll("\\{KEY\\}", s.getKey());
			                                                                                       // System.out.println(renderedText);
			                                                                                       return renderedText;
		                                                                                       })
		                                                                                       .collect(Collectors.joining()))
		                                              .replace("{SITEURL}", CONFIGURATION.SITE_URL.getValue())
		                                              .toString();
	}

	@Override
	public String init()
	{
		String Body = "";

		if (Vault.getInstance()
		         .getInitializedState())
		{
			Body = this.postInitialization();

		} else
		{
			Body = this.preInitialization();
		}
		return Body;
	}

	private String preInitialization()
	{
		String initPage = this.INIT_PAGE;
		if (isSetAgentKey())
		{
			initPage = initPage.replace("{AGENT_KEY_CONTENT}", InitializePage.AGENT_KEY_CONTENT);
		} else
		{
			initPage = initPage.replace("{AGENT_KEY_CONTENT}", "");
		}
		return initPage;
	}

	private String postInitialization()
	{
		String Body = "";
		Body = Vault.getInstance()
		            .getAsHashedJson();
		return Body;
	}

	private boolean isSetAgentKey()
	{
		String agentKey = properties.getValue(PropertyKey.ENCRYPTED_KEY);
		if (agentKey == null || agentKey.equals(""))
		{
			return false;
		}
		return true;
	}

	@Override
	public String storeKey(
	    Map<String, String[]> formData)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		for (SecuredInformation v : SecuredInformation.values())
		{
			json.put(v.getKey(), formData.get(v.getKey())[0].trim());
		}

		try
		{
			Vault.getInstance()
			     .setData(json.toString());
		} catch (Exception e)
		{
			throw new ServerException(InitializePage.INIT_MALFORMED);
		}

		return Vault.getInstance()
		            .getAsHashedJson();
	}

	private boolean validateAuthentication(
	    String authKey)
	{
		String checkKey = CONFIGURATION.AUTH_KEY.getValue();
		if (checkKey.equals(authKey))
		{
			return true;
		} else
		{
			throw new ServerException(InitializePage.AUTH_FAILED);
		}
	}

	@Override
	public String handleInitialization(
	    Map<String, String[]> formData)
	{
		String agentKey = "";
		if (formData.get(InitializePage.AGENT_KEY_FIELD) != null)
		{
			agentKey = formData.get(InitializePage.AGENT_KEY_FIELD)[0];
		}

		if (agentKey.equals(""))
		{
			validateFormData(formData);
			String res = setVaultAndEncryption(formData);
			validateSmartContract();
			sendMailWithAgentKey(res);
			return res;
		} else
		{
			String res = retriveKeysFromAgentKey(agentKey);
			validateSmartContract();
			return res;
		}
	}

	public void validateSmartContract()
	{
		boolean isValid = false;
		try
		{
			blockchainService.loadContract();
			isValid = blockchainService.isValidContract();
			if (!isValid)
			{
				throw new Exception(ERROR_INVALID_CONTRACT);
			}
		} catch (Exception | Error e)
		{
			// reset vault state
			Vault.getInstance()
			     .setInitializedState(false);
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	private void validateFormData(
	    Map<String, String[]> formData)
	{
		if (formData.get(SecuredInformation.EthereumPrivateKey.getKey()) == null)
		{
			throw new ServerException(ERROR_NO_ETHEREUM_KEY);
		} else if (formData.get(SecuredInformation.EthereumPrivateKey.getKey())[0].equals(""))
		{
			throw new ServerException(ERROR_NO_ETHEREUM_KEY);
		}
	}

	private void sendMailWithAgentKey(
	    String agentKey)
	{
		String from = CONFIGURATION.CONTACT_EMAIL.getValue();
		String to = CONFIGURATION.AGENT_KEY_EMAIL.getValue();
		String subject = "Agent Key Created";
		String body = "Agent Key is : " + agentKey;
		emailService.sendGeneralEmail(from, to, subject, body);
	}

	private String setVaultAndEncryption(
	    Map<String, String[]> formData)
	{
		String AuthorizationCode = formData.get(InitializePage.INIT_AUTHORIZATION_FIELD)[0];
		this.validateAuthentication(AuthorizationCode);
		this.storeKey(formData);
		String generatedAgentKey = this.userService.generateRandomString();
		String encryptedKey = Vault.getInstance()
		                           .getAsEncryptedJson(generatedAgentKey);
		properties.setValue(PropertyKey.ENCRYPTED_KEY, encryptedKey);
		properties.savePropertiesFile();
		return generatedAgentKey;
	}

	private String retriveKeysFromAgentKey(
	    String agentKey)
	{
		String storedEncryptedKey = properties.getValue(PropertyKey.ENCRYPTED_KEY);
		String decryptedKey = aes.decryptWithKey(agentKey, storedEncryptedKey);
		if (decryptedKey == null)
		{
			throw new ServerException(ERROR_DECRYPTION);
		}
		// Logger.debug("decKey: " + decryptedKey);
		Map<String, String[]> newformData = new HashMap<String, String[]>();
		JsonNode node = Json.parse(decryptedKey);
		for (SecuredInformation v : SecuredInformation.values())
		{
			String key = v.getKey();
			String value = node.get(key)
			                   .asText();
			if (value != null)
			{
				String[] valueArray = new String[1];
				valueArray[0] = value;
				newformData.put(key, valueArray);
			}
		}
		this.storeKey(newformData);
		return Vault.getInstance()
		            .getAsHashedJson();
	}
}
