package content;

public enum SecuredInformation
{
		/**
		 * All keys that need to be stored in the vault should be defined
		 * here.
		 * This ensures that these keys are only used in a secure and error
		 * resistant manner.
		 * 
		 * The format is very simple.
		 * Start with an ENUMerated name for the key.
		 * 
		 * The 1st field:
		 * Then the key is initialized with both the text output, and the
		 * field name as the same entry.
		 * 
		 * the 2nd field: (NOT IMPLEMENTE YET)
		 * The second field is an array of functions/methods which are
		 * allows to use this particular key, this will prevent the
		 * excessive use of keys through the system, and enforce better
		 * tracking of where the keys are being used
		 */
		BitCoinPrivateKey (
		    "Bitcoin Private Key"),

		EthereumPrivateKey (
		    "Ethereum Private Key");

	/**
	 * The remaining part of this ENUM is for management of the enum.
	 */
	private String key;

	private SecuredInformation(
	                           String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return this.key;
	}
}
