package secure;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.ServerException;
import content.SecuredInformation;
import play.libs.Json;
import service.AESService;
import serviceImpl.AESServiceImpl;

public class Vault
{

	private Vault()
	{
	}

	private static Vault instance = null;

	public static Vault getInstance()
	{
		if (instance == null)
			synchronized (Vault.class)
			{
				if (instance == null)
					instance = new Vault();
			}
		return instance;
	}

	private Boolean	set		= new Boolean(false);
	private byte[]	AONTKey	= new byte[]
		{
		        00, 00
		};

	private Map<SecuredInformation, String> codes = new HashMap<SecuredInformation, String>();

	public void setData(
	    String jsonString)
	    throws Exception
	{
		synchronized (set)
		{
			if (!set)
			{
				// Lock the singleton from receiving changes after the initial
				// set
				set = true;
				JsonNode rootNode = Json.parse(jsonString);
				// Only search for keys in the JSON for which we care about
				for (SecuredInformation v : SecuredInformation.values())
				{
					String vaultKey = v.getKey();
					if (vaultKey != null)
					{
						JsonNode dataNode = rootNode.get(vaultKey);
						if (dataNode != null)
						{
							// found a matching node for both the set string
							// and
							// the vault key
							// TODO: Implement AONT for storing the data in
							// the
							// codes array
							codes.put(v, dataNode.asText());
						}
					}
				}

			} else
			{
				throw new ServerException("Already Initialized");
			}
		}
	}

	private byte[] AONT(
	    byte[] key,
	    byte[] data)
	{
		return data;
	}

	private byte[] AONT_1(
	    byte[] data)
	{
		return data;
	}

	public Boolean getInitializedState()
	{
		return this.set;
	}

	public void setInitializedState(
	    Boolean flag)
	{
		synchronized (set)
		{
			set = flag;
		}
	}

	public String getVaultedItem(
	    SecuredInformation item)
	{
		// TODO: Validate that the calling function is allowed to use this
		// vaulted item
		// TODO: User AONT_1 to return the decoded data on a one time use basis.
		return codes.get(item);
	}

	public String getHashedVaultedItem(
	    SecuredInformation item)
	{
		return this.easy1WayHash(this.codes.get(item)
		                                   .toString());
	}

	/**
	 * This is a one way hashing algorithm used for the purpose of creating a
	 * nearly unique, yet, non-reversible hashing of an input string.
	 * 
	 * @param input
	 * @return
	 */
	private String easy1WayHash(
	    String input)
	{
		MessageDigest messageDigest;
		StringBuffer result = new StringBuffer("0x");

		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(input.getBytes(Charset.forName("UTF8")));
			final byte[] resultByte = messageDigest.digest();
			for (byte b : resultByte)
			{
				result.append(String.format("%02X", b));
				// result.append(" "); // delimiter
			}

		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.append(".md5");
		return result.toString();

	}

	/**
	 * Creates a json of all the hashed valued along with their keys.
	 * This hashing is not reversible and can be safely displayed or used in
	 * emails.
	 * 
	 * @return
	 */
	public String getAsHashedJson()
	{
		Map<String, String> viewableCodes = new HashMap<String, String>();
		for (SecuredInformation v : SecuredInformation.values())
			viewableCodes.put(v.getKey(), this.easy1WayHash(v.getKey()));

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode hashedNode = mapper.createObjectNode();

		viewableCodes.forEach((
		    k, // Key, Value
		    v) -> hashedNode.put(k, v));
		return hashedNode.toString();
	}

	/**
	 * 
	 * 
	 *         returns the encrypted string of a json with keys.
	 *         The encrypted value is to store in properties file
	 * 
	 * @return
	 */
	public String getAsEncryptedJson(
	    String key)
	{
		Map<String, String> viewableCodes = new HashMap<String, String>();
		for (SecuredInformation v : SecuredInformation.values())
			viewableCodes.put(v.getKey(), getVaultedItem(v));

		String stringNode = Json.stringify(Json.toJson(viewableCodes));
		AESService aes = new AESServiceImpl();
		String encryptedKeys = aes.encryptWithKey(key, stringNode);

		return encryptedKeys;
	}
}
