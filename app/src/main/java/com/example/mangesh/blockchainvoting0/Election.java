package com.example.mangesh.blockchainvoting0;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
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
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class Election extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060408051808201909152600f8082527f333533333835303935373438343739000000000000000000000000000000000060209092019182526100559160059161006a565b50600060038190556004819055600855610105565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100ab57805160ff19168380011785556100d8565b828001600101855582156100d8579182015b828111156100d85782518255916020019190600101906100bd565b506100e49291506100e8565b5090565b61010291905b808211156100e457600081556001016100ee565b90565b610ee1806101146000396000f3fe6080604052600436106100cf5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630b97bc8681146100d4578063211a27271461015e5780632d35a8a2146101855780633477ee2e1461019a57806342169e48146102b7578063478c4e0e146102cc5780635a73082e146102e35780636d5d7bf41461032657806384fe2be214610460578063ad10174414610517578063b384abef1461052c578063c24a0f8b1461055c578063ca420e4a14610571578063e74f925214610629575b600080fd5b3480156100e057600080fd5b506100e961076b565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561012357818101518382015260200161010b565b50505050905090810190601f1680156101505780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561016a57600080fd5b506101736107f9565b60408051918252519081900360200190f35b34801561019157600080fd5b506101736107ff565b3480156101a657600080fd5b506101c4600480360360208110156101bd57600080fd5b5035610805565b604051808681526020018060200180602001858152602001848152602001838103835287818151815260200191508051906020019080838360005b838110156102175781810151838201526020016101ff565b50505050905090810190601f1680156102445780820380516001836020036101000a031916815260200191505b50838103825286518152865160209182019188019080838360005b8381101561027757818101518382015260200161025f565b50505050905090810190601f1680156102a45780820380516001836020036101000a031916815260200191505b5097505050505050505060405180910390f35b3480156102c357600080fd5b50610173610947565b3480156102d857600080fd5b506102e161094d565b005b3480156102ef57600080fd5b5061030d6004803603602081101561030657600080fd5b50356109f9565b6040805192835260208301919091528051918290030190f35b34801561033257600080fd5b506102e16004803603604081101561034957600080fd5b81019060208101813564010000000081111561036457600080fd5b82018360208201111561037657600080fd5b8035906020019184600183028401116401000000008311171561039857600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092959493602081019350359150506401000000008111156103eb57600080fd5b8201836020820111156103fd57600080fd5b8035906020019184600183028401116401000000008311171561041f57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610a12945050505050565b34801561046c57600080fd5b5061048a6004803603602081101561048357600080fd5b5035610a3e565b6040518085815260200180602001848152602001838152602001828103825285818151815260200191508051906020019080838360005b838110156104d95781810151838201526020016104c1565b50505050905090810190601f1680156105065780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b34801561052357600080fd5b506100e9610af0565b34801561053857600080fd5b506102e16004803603604081101561054f57600080fd5b5080359060200135610b4b565b34801561056857600080fd5b506100e9610bfa565b34801561057d57600080fd5b506102e16004803603606081101561059457600080fd5b8101906020810181356401000000008111156105af57600080fd5b8201836020820111156105c157600080fd5b803590602001918460018302840111640100000000831117156105e357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505082359350505060200135610c55565b34801561063557600080fd5b506102e16004803603606081101561064c57600080fd5b81019060208101813564010000000081111561066757600080fd5b82018360208201111561067957600080fd5b8035906020019184600183028401116401000000008311171561069b57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929584359590949093506040810192506020013590506401000000008111156106f657600080fd5b82018360208201111561070857600080fd5b8035906020019184600183028401116401000000008311171561072a57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610cf9945050505050565b6006805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107f15780601f106107c6576101008083540402835291602001916107f1565b820191906000526020600020905b8154815290600101906020018083116107d457829003601f168201915b505050505081565b60085481565b60035481565b600060208181529181526040908190208054600180830180548551600293821615610100026000190190911692909204601f810187900487028301870190955284825291949293909283018282801561089f5780601f106108745761010080835404028352916020019161089f565b820191906000526020600020905b81548152906001019060200180831161088257829003601f168201915b50505060028085018054604080516020601f60001961010060018716150201909416959095049283018590048502810185019091528181529596959450909250908301828280156109315780601f1061090657610100808354040283529160200191610931565b820191906000526020600020905b81548152906001019060200180831161091457829003601f168201915b5050505050908060030154908060040154905085565b60045481565b60015b600354811161099e576000818152602081905260408120818155906109786001830182610dd3565b610986600283016000610dd3565b50600060038201819055600490910155600101610950565b5060015b60045481116109d7576000818152600260209081526040808320600190810154845291829052822060030191909155016109a2565b50600060038190556109eb90600690610dd3565b6109f760076000610dd3565b565b6002602052600090815260409020805460019091015482565b8151610a25906006906020850190610e1a565b508051610a39906007906020840190610e1a565b505050565b600160208181526000928352604092839020805481840180548651600296821615610100026000190190911695909504601f81018590048502860185019096528585529094919392909190830182828015610ada5780601f10610aaf57610100808354040283529160200191610ada565b820191906000526020600020905b815481529060010190602001808311610abd57829003601f168201915b5050505050908060020154908060030154905084565b6005805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107f15780601f106107c6576101008083540402835291602001916107f1565b60008181526001602052604090206003015415610b6757600080fd5b600082118015610b7c57506003546001018211155b1515610b8757600080fd5b600354600101821415610ba257600880546001019055610bbb565b6000828152602081905260409020600401805460010190555b6000818152600160205260408082206003018490555183917ffff3c900d938d21d0990d786e819f29b8d05c1ef587b462b939609625b684b1691a25050565b6007805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107f15780601f106107c6576101008083540402835291602001916107f1565b610c5e81610d8f565b1515610c6957600080fd5b6004805460019081019091556040805160808101825284815260208082018781528284018690526000606084018190528781528583529390932082518155925180519294610cbf93908501929190910190610e1a565b5060408201518160020155606082015181600301559050508160026000600454815260200190815260200160002060010181905550505050565b600380546001908101918290556040805160a08101825283815260208082018881528284018790526060830188905260006080840181905295865285825292909420815181559151805191949293610d579390850192910190610e1a565b5060408201518051610d73916002840191602090910190610e1a565b5060608201516003820155608090910151600490910155505050565b600060015b6004548111610dc857600081815260016020526040902060020154831415610dc0576000915050610dce565b600101610d94565b50600190505b919050565b50805460018160011615610100020316600290046000825580601f10610df95750610e17565b601f016020900490600052602060002090810190610e179190610e98565b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610e5b57805160ff1916838001178555610e88565b82800160010185558215610e88579182015b82811115610e88578251825591602001919060010190610e6d565b50610e94929150610e98565b5090565b610eb291905b80821115610e945760008155600101610e9e565b9056fea165627a7a7230582036ae892517d242368faf58080208544ca4e804be86ae367bd401be6c840572300029";

    public static final String FUNC_STARTDATE = "startDate";

    public static final String FUNC_NOTACOUNT = "notaCount";

    public static final String FUNC_CANDIDATESCOUNT = "candidatesCount";

    public static final String FUNC_CANDIDATES = "candidates";

    public static final String FUNC_VOTERCOUNT = "voterCount";

    public static final String FUNC_RESETDATA = "resetData";

    public static final String FUNC_VOTERID = "voterID";

    public static final String FUNC_SETELECTIONDATES = "setElectionDates";

    public static final String FUNC_VALIDVOTERS = "validVoters";

    public static final String FUNC_IMEI_ADMIN = "IMEI_admin";

    public static final String FUNC_VOTE = "vote";

    public static final String FUNC_ENDDATE = "endDate";

    public static final String FUNC_ADDVALIDVOTERS = "addValidVoters";

    public static final String FUNC_ADDCANDIDATE = "addCandidate";

    public static final Event VOTEDEVENT_EVENT = new Event("votedEvent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    @Deprecated
    protected Election(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Election(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Election(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Election(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<String> startDate() {
        final Function function = new Function(FUNC_STARTDATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> notaCount() {
        final Function function = new Function(FUNC_NOTACOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> candidatesCount() {
        final Function function = new Function(FUNC_CANDIDATESCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> candidates(BigInteger param0) {
        final Function function = new Function(FUNC_CANDIDATES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple5<BigInteger, String, String, BigInteger, BigInteger>>(
                new Callable<Tuple5<BigInteger, String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<BigInteger, String, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<BigInteger, String, String, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> voterCount() {
        final Function function = new Function(FUNC_VOTERCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> resetData() {
        final Function function = new Function(
                FUNC_RESETDATA, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> voterID(BigInteger param0) {
        final Function function = new Function(FUNC_VOTERID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> setElectionDates(String _startD, String _endD) {
        final Function function = new Function(
                FUNC_SETELECTIONDATES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_startD), 
                new org.web3j.abi.datatypes.Utf8String(_endD)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<BigInteger, String, BigInteger, BigInteger>> validVoters(BigInteger param0) {
        final Function function = new Function(FUNC_VALIDVOTERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<BigInteger, String, BigInteger, BigInteger>>(
                new Callable<Tuple4<BigInteger, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<BigInteger, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, String, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<String> IMEI_admin() {
        final Function function = new Function(FUNC_IMEI_ADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> vote(BigInteger _candidateId, BigInteger _IMEI) {
        final Function function = new Function(
                FUNC_VOTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_candidateId), 
                new org.web3j.abi.datatypes.generated.Uint256(_IMEI)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> endDate() {
        final Function function = new Function(FUNC_ENDDATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> addValidVoters(String _name, BigInteger _IMEI, BigInteger _uid) {
        final Function function = new Function(
                FUNC_ADDVALIDVOTERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.generated.Uint256(_IMEI), 
                new org.web3j.abi.datatypes.generated.Uint256(_uid)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addCandidate(String _name, BigInteger _urlLogo, String _partyName) {
        final Function function = new Function(
                FUNC_ADDCANDIDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.generated.Uint256(_urlLogo), 
                new org.web3j.abi.datatypes.Utf8String(_partyName)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<VotedEventEventResponse> getVotedEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VOTEDEVENT_EVENT, transactionReceipt);
        ArrayList<VotedEventEventResponse> responses = new ArrayList<VotedEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VotedEventEventResponse typedResponse = new VotedEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._candidateId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<VotedEventEventResponse> votedEventEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, VotedEventEventResponse>() {
            @Override
            public VotedEventEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(VOTEDEVENT_EVENT, log);
                VotedEventEventResponse typedResponse = new VotedEventEventResponse();
                typedResponse.log = log;
                typedResponse._candidateId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<VotedEventEventResponse> votedEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VOTEDEVENT_EVENT));
        return votedEventEventFlowable(filter);
    }

    @Deprecated
    public static Election load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Election(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Election load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Election(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Election load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Election(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Election load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Election(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Election> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Election.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Election> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Election.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Election> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Election.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Election> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Election.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class VotedEventEventResponse {
        public Log log;

        public BigInteger _candidateId;
    }
}
