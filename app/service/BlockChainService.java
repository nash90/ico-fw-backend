package service;

import java.math.BigDecimal;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.google.inject.ImplementedBy;
import common.ServerException;
import serviceImpl.Web3jServiceImpl;

@ImplementedBy(Web3jServiceImpl.class)
public interface BlockChainService
{
	public TransactionReceipt sendToken(
	    String wallet,
	    BigDecimal amount)
	    throws ServerException;

	public void loadContract();

	public boolean isValidContract();

	public String getBonusInfo();
}
