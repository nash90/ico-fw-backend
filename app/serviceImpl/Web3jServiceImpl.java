package serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.inject.Inject;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import common.ServerException;
import content.SecuredInformation;
import play.Logger;
import secure.Vault;
import service.BlockChainService;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class Web3jServiceImpl
    implements
        BlockChainService
{
	private static final String	INVALID_CONTRACT				= "Smart Contract is invalid or did not match to remote";
	private static final String	ERROR_SMART_CONTRACT_CLIENT		= "Smart Contract Client ERROR: ";
	private static final String	ERROR_SMART_CONTRACT_CONNECTION	= "Smart Contract Client ERROR: Cant connect to Node";
	private static final String	ERROR_SMART_CONTRACT_CREDENTIAL	= "Smart Contract Client ERROR: Cant create credential to execute smart contract, issue with the key";

	private static final BigInteger GWEI_TO_WEI = new BigInteger("1000000000");

	serviceImpl.ApplicationConfigurationService	config;
	private static Xcoin						contract;

	@Inject
	public Web3jServiceImpl(
	                        serviceImpl.ApplicationConfigurationService config)
	{
		super();
		this.config = config;
	}

	@Override
	public TransactionReceipt sendToken(
	    String wallet,
	    BigDecimal amount)
	    throws ServerException
	{
		int tokenDecimalParse = Integer.parseInt(CONFIGURATION.TOKEN_DECIMAL.getValue());
		System.out.println(CONFIGURATION.TOKEN_DECIMAL.getValue());
		BigDecimal tokenDecimal = BigDecimal.valueOf(Math.pow(10, tokenDecimalParse));
		BigDecimal amountDecimal = tokenDecimal.multiply(amount);

		BigInteger amountToStore = amountDecimal.toBigInteger();

		if (isValidContract())
		{
			Address address = new Address(wallet);
			try
			{
				return contract.transfer(address, new Uint256(amountToStore))
				               .send();
			} catch (Exception e)
			{
				throw new ServerException(ERROR_SMART_CONTRACT_CLIENT + e.getMessage());
			}
		} else
		{
			throw new ServerException(INVALID_CONTRACT);
		}
	}

	@Override
	public void loadContract()
	{
		Web3j web3 = Web3j.build(new HttpService(CONFIGURATION.NETWORK_CLIENT.getValue()));
		String key = Vault.getInstance()
		                  .getVaultedItem(SecuredInformation.EthereumPrivateKey)
		                  .replace("\"", "")
		                  .trim();

		Credentials credentials = null;

		try
		{
			credentials = Credentials.create(key);
		} catch (Error | Exception e)
		{
			e.printStackTrace();
			throw new ServerException(ERROR_SMART_CONTRACT_CREDENTIAL);
		}

		BigInteger gasPrice = new BigInteger(CONFIGURATION.GAS_PRICE.getValue()).multiply(GWEI_TO_WEI);
		BigInteger gasLimit = new BigInteger(CONFIGURATION.GAS_LIMIT.getValue());

		contract = Xcoin.load(CONFIGURATION.CONTRACT_ADDRESS.getValue(), web3, credentials, gasPrice, gasLimit);
		Logger.debug("Loaded the contract successfully!!!");
	}

	@Override
	public boolean isValidContract()
	{
		try
		{
			return contract.isValid();
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new ServerException(ERROR_SMART_CONTRACT_CONNECTION);
		}
	}

	@Override
	public String getBonusInfo()
	{
		return null; // TODO get bonus config from contract
	}

}
