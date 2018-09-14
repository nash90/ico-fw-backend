package serviceImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>
 * Auto generated code.
 * <p>
 * <strong>Do not modify!</strong>
 * <p>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j
 * command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen
 * module</a> to update.
 *
 * <p>
 * Generated with web3j version 3.4.0.
 */
public class Xcoin
    extends
        Contract
{
	private static final String BINARY = "0x60806040526040805190810160405280600581526020017f58636f696e00000000000000000000000000000000000000000000000000000081525060039080519060200190620000519291906200015e565b506040805190810160405280600581526020017f58636f696e000000000000000000000000000000000000000000000000000000815250600490805190602001906200009f9291906200015e565b506002600555600554600a0a620f424002600655348015620000c057600080fd5b506006546000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055503373ffffffffffffffffffffffffffffffffffffffff1660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef6006546040518082815260200191505060405180910390a36200020d565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001a157805160ff1916838001178555620001d2565b82800160010185558215620001d2579182015b82811115620001d1578251825591602001919060010190620001b4565b5b509050620001e19190620001e5565b5090565b6200020a91905b8082111562000206576000816000905550600101620001ec565b5090565b90565b611321806200021d6000396000f3006080604052600436106100ba576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde03146100bf578063095ea7b31461014f57806318160ddd146101b457806323b872dd146101df5780632ff2e9dc14610264578063313ce5671461028f57806366188463146102ba57806370a082311461031f57806395d89b4114610376578063a9059cbb14610406578063d73dd6231461046b578063dd62ed3e146104d0575b600080fd5b3480156100cb57600080fd5b506100d4610547565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101145780820151818401526020810190506100f9565b50505050905090810190601f1680156101415780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561015b57600080fd5b5061019a600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506105e5565b604051808215151515815260200191505060405180910390f35b3480156101c057600080fd5b506101c96106d7565b6040518082815260200191505060405180910390f35b3480156101eb57600080fd5b5061024a600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506106e1565b604051808215151515815260200191505060405180910390f35b34801561027057600080fd5b50610279610a9b565b6040518082815260200191505060405180910390f35b34801561029b57600080fd5b506102a4610aa1565b6040518082815260200191505060405180910390f35b3480156102c657600080fd5b50610305600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610aa7565b604051808215151515815260200191505060405180910390f35b34801561032b57600080fd5b50610360600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610d38565b6040518082815260200191505060405180910390f35b34801561038257600080fd5b5061038b610d80565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103cb5780820151818401526020810190506103b0565b50505050905090810190601f1680156103f85780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561041257600080fd5b50610451600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610e1e565b604051808215151515815260200191505060405180910390f35b34801561047757600080fd5b506104b6600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061103d565b604051808215151515815260200191505060405180910390f35b3480156104dc57600080fd5b50610531600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611239565b6040518082815260200191505060405180910390f35b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105dd5780601f106105b2576101008083540402835291602001916105dd565b820191906000526020600020905b8154815290600101906020018083116105c057829003601f168201915b505050505081565b600081600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b6000600154905090565b60008073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415151561071e57600080fd5b6000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054821115151561076b57600080fd5b600260008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205482111515156107f657600080fd5b610847826000808773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546112c090919063ffffffff16565b6000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506108da826000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546112d990919063ffffffff16565b6000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506109ab82600260008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546112c090919063ffffffff16565b600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190509392505050565b60065481565b60055481565b600080600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905080831115610bb8576000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610c4c565b610bcb83826112c090919063ffffffff16565b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b8373ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a3600191505092915050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b60048054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610e165780601f10610deb57610100808354040283529160200191610e16565b820191906000526020600020905b815481529060010190602001808311610df957829003601f168201915b505050505081565b60008073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614151515610e5b57600080fd5b6000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020548211151515610ea857600080fd5b610ef9826000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546112c090919063ffffffff16565b6000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550610f8c826000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546112d990919063ffffffff16565b6000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b60006110ce82600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546112d990919063ffffffff16565b600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a36001905092915050565b6000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b60008282111515156112ce57fe5b818303905092915050565b600081830190508281101515156112ec57fe5b809050929150505600a165627a7a723058207e35193244fdaf4dd0266f379d0c4f1f277457b454b89d0386f8c2ef2cde5a900029";

	public static final String FUNC_NAME = "name";

	public static final String FUNC_APPROVE = "approve";

	public static final String FUNC_TOTALSUPPLY = "totalSupply";

	public static final String FUNC_TRANSFERFROM = "transferFrom";

	public static final String FUNC_INITIAL_SUPPLY = "INITIAL_SUPPLY";

	public static final String FUNC_DECIMALS = "decimals";

	public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";

	public static final String FUNC_BALANCEOF = "balanceOf";

	public static final String FUNC_SYMBOL = "symbol";

	public static final String FUNC_TRANSFER = "transfer";

	public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";

	public static final String FUNC_ALLOWANCE = "allowance";

	public static final Event APPROVAL_EVENT = new Event("Approval", Arrays.<TypeReference<?>>asList(new TypeReference<Address>()
	{
	}, new TypeReference<Address>()
	{
	}), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
	{
	}));;

	public static final Event TRANSFER_EVENT = new Event("Transfer", Arrays.<TypeReference<?>>asList(new TypeReference<Address>()
	{
	}, new TypeReference<Address>()
	{
	}), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
	{
	}));;

	protected static final HashMap<String, String> _addresses;

	static
	{
		_addresses = new HashMap<String, String>();
		_addresses.put("3", "0x877bacac227ea28a7736abe5fca55a62ede271be");
	}

	protected Xcoin(
	                String contractAddress,
	                Web3j web3j,
	                Credentials credentials,
	                BigInteger gasPrice,
	                BigInteger gasLimit)
	{
		super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
	}

	protected Xcoin(
	                String contractAddress,
	                Web3j web3j,
	                TransactionManager transactionManager,
	                BigInteger gasPrice,
	                BigInteger gasLimit)
	{
		super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
	}

	public RemoteCall<Utf8String> name()
	{
		final Function function = new Function(FUNC_NAME, Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public RemoteCall<TransactionReceipt> approve(
	    Address _spender,
	    Uint256 _value)
	{
		final Function function = new Function(FUNC_APPROVE, Arrays.<Type>asList(_spender, _value), Collections.<TypeReference<?>>emptyList());
		return executeRemoteCallTransaction(function);
	}

	public RemoteCall<Uint256> totalSupply()
	{
		final Function function = new Function(FUNC_TOTALSUPPLY, Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public RemoteCall<TransactionReceipt> transferFrom(
	    Address _from,
	    Address _to,
	    Uint256 _value)
	{
		final Function function = new Function(FUNC_TRANSFERFROM, Arrays.<Type>asList(_from, _to, _value), Collections.<TypeReference<?>>emptyList());
		return executeRemoteCallTransaction(function);
	}

	public RemoteCall<Uint256> INITIAL_SUPPLY()
	{
		final Function function = new Function(FUNC_INITIAL_SUPPLY, Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public RemoteCall<Uint256> decimals()
	{
		final Function function = new Function(FUNC_DECIMALS, Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public RemoteCall<TransactionReceipt> decreaseApproval(
	    Address _spender,
	    Uint256 _subtractedValue)
	{
		final Function function = new Function(FUNC_DECREASEAPPROVAL, Arrays.<Type>asList(_spender, _subtractedValue), Collections.<TypeReference<?>>emptyList());
		return executeRemoteCallTransaction(function);
	}

	public RemoteCall<Uint256> balanceOf(
	    Address _owner)
	{
		final Function function = new Function(FUNC_BALANCEOF, Arrays.<Type>asList(_owner), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public RemoteCall<Utf8String> symbol()
	{
		final Function function = new Function(FUNC_SYMBOL, Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public RemoteCall<TransactionReceipt> transfer(
	    Address _to,
	    Uint256 _value)
	{
		final Function function = new Function(FUNC_TRANSFER, Arrays.<Type>asList(_to, _value), Collections.<TypeReference<?>>emptyList());
		return executeRemoteCallTransaction(function);
	}

	public RemoteCall<TransactionReceipt> increaseApproval(
	    Address _spender,
	    Uint256 _addedValue)
	{
		final Function function = new Function(FUNC_INCREASEAPPROVAL, Arrays.<Type>asList(_spender, _addedValue), Collections.<TypeReference<?>>emptyList());
		return executeRemoteCallTransaction(function);
	}

	public RemoteCall<Uint256> allowance(
	    Address _owner,
	    Address _spender)
	{
		final Function function = new Function(FUNC_ALLOWANCE, Arrays.<Type>asList(_owner, _spender), Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>()
		{
		}));
		return executeRemoteCallSingleValueReturn(function);
	}

	public static RemoteCall<Xcoin> deploy(
	    Web3j web3j,
	    Credentials credentials,
	    BigInteger gasPrice,
	    BigInteger gasLimit)
	{
		return deployRemoteCall(Xcoin.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
	}

	public static RemoteCall<Xcoin> deploy(
	    Web3j web3j,
	    TransactionManager transactionManager,
	    BigInteger gasPrice,
	    BigInteger gasLimit)
	{
		return deployRemoteCall(Xcoin.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
	}

	public List<ApprovalEventResponse> getApprovalEvents(
	    TransactionReceipt transactionReceipt)
	{
		List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
		ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
		for (Contract.EventValuesWithLog eventValues : valueList)
		{
			ApprovalEventResponse typedResponse = new ApprovalEventResponse();
			typedResponse.log = eventValues.getLog();
			typedResponse.owner = (Address) eventValues.getIndexedValues()
			                                           .get(0);
			typedResponse.spender = (Address) eventValues.getIndexedValues()
			                                             .get(1);
			typedResponse.value = (Uint256) eventValues.getNonIndexedValues()
			                                           .get(0);
			responses.add(typedResponse);
		}
		return responses;
	}

	public Observable<ApprovalEventResponse> approvalEventObservable(
	    EthFilter filter)
	{
		return web3j.ethLogObservable(filter)
		            .map(new Func1<Log, ApprovalEventResponse>()
		            {
			            @Override
			            public ApprovalEventResponse call(
			                Log log)
			            {
				            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
				            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
				            typedResponse.log = log;
				            typedResponse.owner = (Address) eventValues.getIndexedValues()
				                                                       .get(0);
				            typedResponse.spender = (Address) eventValues.getIndexedValues()
				                                                         .get(1);
				            typedResponse.value = (Uint256) eventValues.getNonIndexedValues()
				                                                       .get(0);
				            return typedResponse;
			            }
		            });
	}

	public Observable<ApprovalEventResponse> approvalEventObservable(
	    DefaultBlockParameter startBlock,
	    DefaultBlockParameter endBlock)
	{
		EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
		filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
		return approvalEventObservable(filter);
	}

	public List<TransferEventResponse> getTransferEvents(
	    TransactionReceipt transactionReceipt)
	{
		List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
		ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
		for (Contract.EventValuesWithLog eventValues : valueList)
		{
			TransferEventResponse typedResponse = new TransferEventResponse();
			typedResponse.log = eventValues.getLog();
			typedResponse.from = (Address) eventValues.getIndexedValues()
			                                          .get(0);
			typedResponse.to = (Address) eventValues.getIndexedValues()
			                                        .get(1);
			typedResponse.value = (Uint256) eventValues.getNonIndexedValues()
			                                           .get(0);
			responses.add(typedResponse);
		}
		return responses;
	}

	public Observable<TransferEventResponse> transferEventObservable(
	    EthFilter filter)
	{
		return web3j.ethLogObservable(filter)
		            .map(new Func1<Log, TransferEventResponse>()
		            {
			            @Override
			            public TransferEventResponse call(
			                Log log)
			            {
				            Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
				            TransferEventResponse typedResponse = new TransferEventResponse();
				            typedResponse.log = log;
				            typedResponse.from = (Address) eventValues.getIndexedValues()
				                                                      .get(0);
				            typedResponse.to = (Address) eventValues.getIndexedValues()
				                                                    .get(1);
				            typedResponse.value = (Uint256) eventValues.getNonIndexedValues()
				                                                       .get(0);
				            return typedResponse;
			            }
		            });
	}

	public Observable<TransferEventResponse> transferEventObservable(
	    DefaultBlockParameter startBlock,
	    DefaultBlockParameter endBlock)
	{
		EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
		filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
		return transferEventObservable(filter);
	}

	public static Xcoin load(
	    String contractAddress,
	    Web3j web3j,
	    Credentials credentials,
	    BigInteger gasPrice,
	    BigInteger gasLimit)
	{
		return new Xcoin(contractAddress, web3j, credentials, gasPrice, gasLimit);
	}

	public static Xcoin load(
	    String contractAddress,
	    Web3j web3j,
	    TransactionManager transactionManager,
	    BigInteger gasPrice,
	    BigInteger gasLimit)
	{
		return new Xcoin(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
	}

	protected String getStaticDeployedAddress(
	    String networkId)
	{
		return _addresses.get(networkId);
	}

	public static String getPreviouslyDeployedAddress(
	    String networkId)
	{
		return _addresses.get(networkId);
	}

	public static class ApprovalEventResponse
	{
		public Log log;

		public Address owner;

		public Address spender;

		public Uint256 value;
	}

	public static class TransferEventResponse
	{
		public Log log;

		public Address from;

		public Address to;

		public Uint256 value;
	}
}
