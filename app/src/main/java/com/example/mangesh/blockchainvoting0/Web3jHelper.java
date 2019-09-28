package com.example.mangesh.blockchainvoting0;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;

public class Web3jHelper {

    private static final String PRIVATE_KEY = "C1A40E82FBFCACAEE616B40382B887D6C7C92DCBC5E7B720F64D510C9E900082";
    private static final String CONTRACT_ADDRESS = "0x216b27f421104dE453D0FEA365F7d1f05FF4Ed4d";
    private static final String ROPSTEN_URL = "https://ropsten.infura.io/f5f2eeac05914a8cb609689820a61d11";

    private static Web3j web3j;
    private static iWeb3jConnection iWeb3jConnection;
    private static iIMEICheck iIMEICheck;
    private static iVoterRegistration iVoterRegistration;
    private static iVoterActivity iVoterActivity;
    private static iVoteScreenActivity iVoteScreenActivity;
    private static iAdminActivity iAdminActivity;
    private static iAddCandidates iAddCandidates;
    private static iSetElectionDates iSetElectionDates;
    private static Context context;
    private static String ACTIVITY_NAME;

    private static final Credentials credentials = Credentials.create(PRIVATE_KEY);
    private static final ContractGasProvider contractGasProvider = new DefaultGasProvider();

    interface iWeb3jConnection{
        void connection(Boolean isConnected);
    }

    interface iIMEICheck{
        void OnGetIMEI_AdminResult(String adminIMEI);
        void OnGetVoterInfoResult(String voterName, int CandidateId);
    }

    interface iVoterRegistration{
        void OnSetValidVoterResult(Boolean isAddedSuccessfully);
    }

    interface iVoterActivity{
        void OnGetCandidateCountResult(int candidateCount);
        void OnGetElectionDatesResult(String startDate, String endDate);
        void OnGetVoterInfoResult(String voterName, int CandidateId);
        void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo);
        void OnGetNotaCountResult(int notaCount);
    }

    interface iVoteScreenActivity{
        void OnGetCandidateCountResult(int candidateCount);
        void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo);
        void OnCastVoteResult(Boolean isVotedSuccessfully);
    }

    interface iAdminActivity{
        void OnGetCandidateCountResult(int candidateCount);//2
        void OnGetElectionDatesResult(String startDate, String endDate);//3
        void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo);
        void OnResetElection(Boolean isResetSuccessful);
    }

    interface iAddCandidates{
        void OnGetCandidateCountResult(int candidateCount);
        void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo);
        void OnAddCandidateResult(Boolean isAddedSuccessfully);
    }

    interface iSetElectionDates{
        void OnSetElectionDatesResult(Boolean isDateSetSuccessful);
    }

    public Web3jHelper(Context context, String ACTIVITY_NAME) {

        this.iWeb3jConnection = (iWeb3jConnection) context;
        this.ACTIVITY_NAME = ACTIVITY_NAME;
        switch (ACTIVITY_NAME){
            case "MainActivity":
                this.iIMEICheck = (iIMEICheck) context;
                break;
            case "VoterRegistrationActivity":
                this.iVoterRegistration = (iVoterRegistration) context;
                break;
            case "VoterActivity":
                this.iVoterActivity = (iVoterActivity) context;
                break;
            case "VoteScreenActivity":
                this.iVoteScreenActivity = (iVoteScreenActivity) context;
                break;
            case "AdminActivity":
                this.iAdminActivity = (iAdminActivity) context;
                break;
            case "AddCandidateActivity":
                this.iAddCandidates = (iAddCandidates) context;
                break;
            case "SetElectionDateActivity":
                this.iSetElectionDates = (iSetElectionDates) context;
                break;
        }
        this.context = context;
        initWeb3j();
    }

    public void initWeb3j(){
        InitWeb3JTask initWeb3JTask = new InitWeb3JTask();
        initWeb3JTask.execute(ROPSTEN_URL);
    }

    public void getCandidateCount(){
        ReadCandidateCount readCandidateCount = new ReadCandidateCount();
        readCandidateCount.execute();
    }

    public void getElectionDates(){
        ReadElectionDates readElectionDates = new ReadElectionDates();
        readElectionDates.execute();
    }

    public void getIMEI_admin(){
        ReadAdminIMEI readAdminIMEI = new ReadAdminIMEI();
        readAdminIMEI.execute();
    }

    public void getVoterInfo(String IMEI){
        ReadVoterInfo readVoterInfo = new ReadVoterInfo();
        readVoterInfo.execute(IMEI);
    }

    public void getAllCandidates(int candidateCount){
        ReadAllCandidatesInfo readAllCandidatesInfo = new ReadAllCandidatesInfo();
        readAllCandidatesInfo.execute(""+candidateCount);
    }

    public void setElectionDates(String startD, String endD){
        WriteElectionDates writeElectionDates = new WriteElectionDates();
        writeElectionDates.execute(startD, endD);
    }

    public void setValidVoter(String voterName, String IMEI, String uid){
        WriteValidVoter writeValidVoter = new WriteValidVoter();
        writeValidVoter.execute(voterName, IMEI, uid);
    }

    public void addCandidate(String candidateName, int logo, String partyName){
        WriteCandidateInfo writeCandidateInfo = new WriteCandidateInfo();
        writeCandidateInfo.execute(candidateName, Integer.toString(logo), partyName);
    }

    public void castVote(int candidateId, String IMEI){

        BigInteger iIMEI = BigInteger.valueOf(Long.parseLong(IMEI));

        WriteVote writeVote = new WriteVote();
        writeVote.execute(""+candidateId, ""+iIMEI);
    }

    public void resetElectionData(){
        ResetElectionData resetElectionData = new ResetElectionData();
        resetElectionData.execute();
    }

    public void getNotaCount(){
        ReadNotaCount readNotaCount = new ReadNotaCount();
        readNotaCount.execute();
    }



    private static class InitWeb3JTask extends AsyncTask<String, String, String> {

        Boolean isConnected = false;
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            InfuraHttpService infuraHttpService;
            String result;
            try {
                infuraHttpService = new InfuraHttpService(url);
                web3j = Web3j.build(infuraHttpService);
                result = "Success initializing web3j/infura !!!!!!!!!!!!!!!!!!";
                isConnected = true;
            } catch (Exception wtf) {
                String exception = wtf.toString();
                result = exception;
                Log.d("wat", "Error initializing web3j/infura. Error: " + exception);
                isConnected = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            iWeb3jConnection.connection(isConnected);
        }
    }

    private static class ReadCandidateCount extends AsyncTask<String, String, String> {

        String result;
        int candidateCount = 0;
        @Override
        protected String doInBackground(String... params) {

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                candidateCount = election.candidatesCount().send().intValue();
                result = "Success..."+candidateCount;


            } catch (Exception e) {
                result = "Error reading candidate count. Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "VoterActivity":
                    iVoterActivity.OnGetCandidateCountResult(candidateCount);
                    break;
                case "VoteScreenActivity":
                    iVoteScreenActivity.OnGetCandidateCountResult(candidateCount);
                    break;
                case "AdminActivity":
                    iAdminActivity.OnGetCandidateCountResult(candidateCount);
                    break;
                case "AddCandidateActivity":
                    iAddCandidates.OnGetCandidateCountResult(candidateCount);
                    break;
            }

        }
    }

    private static class ReadElectionDates extends AsyncTask<String, String, String> {

        String result;
        String startDate, endDate;

        @Override
        protected String doInBackground(String... params) {

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                startDate = election.startDate().send();
                endDate = election.endDate().send();
                /*startDate = "26-Apr-2019 12:19:00";
                endDate = "28-May-2019 12:19:30";
                startDate = "";
                endDate = "";
                result = "Success...";*/

            } catch (Exception e) {
                result = "Error reading election dates. Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "VoterActivity":
                    iVoterActivity.OnGetElectionDatesResult(startDate, endDate);
                    break;
                case "AdminActivity":
                    iAdminActivity.OnGetElectionDatesResult(startDate, endDate);
                    break;
            }

        }
    }

    private static class ReadAdminIMEI extends AsyncTask<String, String, String> {

        String result;
        String adminIMEI;

        @Override
        protected String doInBackground(String... params) {

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                adminIMEI = election.IMEI_admin().send();
                result = "Success...";


            } catch (Exception e) {
                result = "Error reading candidate count. Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();


            switch (ACTIVITY_NAME){
                case "MainActivity":
                    iIMEICheck.OnGetIMEI_AdminResult(adminIMEI);
                    break;
            }

        }
    }

    private static class ReadVoterInfo extends AsyncTask<String, String, String> {

        String result;
        String voterName;
        int candidateId;
        int uid;

        @Override
        protected String doInBackground(String... params) {

            String voterIMEI = params[0];
            BigInteger imeiBig = BigInteger.valueOf(Long.parseLong(voterIMEI));
            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);

                Tuple4<BigInteger, String, BigInteger, BigInteger> voterInfo = election.validVoters(imeiBig).send();

                voterName = voterInfo.getValue2();
                candidateId = voterInfo.getValue4().intValue();

                result = "Success...";


            } catch (Exception e) {
                result = "Error reading voter info. Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();


            switch (ACTIVITY_NAME){
                case "MainActivity":
                    iIMEICheck.OnGetVoterInfoResult(voterName, candidateId);
                    break;
                case "VoterActivity":
                    iVoterActivity.OnGetVoterInfoResult(voterName, candidateId);
                    break;
            }

        }
    }

    private static class ReadAllCandidatesInfo extends AsyncTask<String, String, String> {

        String result;
        ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo = new ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>>();

        @Override
        protected String doInBackground(String... params) {

            int candidateCount = Integer.valueOf(params[0]);

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);

                for(int i=1; i<=candidateCount; i++){

                    Tuple5<BigInteger, String, String, BigInteger, BigInteger> candidate = election.candidates(BigInteger.valueOf(i)).send();
                    allCandidatesInfo.add(candidate);

                }

                result = "Success...";


            } catch (Exception e) {
                result = "Error reading candidates info. Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();


            switch (ACTIVITY_NAME){
                case "VoterActivity":
                    iVoterActivity.OnGetAllCandidatesResult(allCandidatesInfo);
                    break;
                case "VoteScreenActivity":
                    iVoteScreenActivity.OnGetAllCandidatesResult(allCandidatesInfo);
                    break;
                case "AdminActivity":
                    iAdminActivity.OnGetAllCandidatesResult(allCandidatesInfo);
                    break;
                case "AddCandidateActivity":
                    iAddCandidates.OnGetAllCandidatesResult(allCandidatesInfo);
                    break;
            }

        }
    }

    private static class WriteValidVoter extends AsyncTask<String, String, String> {

        Boolean isAddedSuccessfully = false;
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String IMEI = params[1];
            String uid = params[2];
            Long imeiL = Long.parseLong(IMEI);
            BigInteger imei = BigInteger.valueOf(imeiL);
            Long uidL = Long.parseLong(uid);
            BigInteger uidB = BigInteger.valueOf(uidL);

            String result;
            try {
                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                TransactionReceipt transactionReceipt = election.addValidVoters(name, imei, uidB).send();
                result = "Registered successfully. Gas used: " + transactionReceipt.getGasUsed();
                isAddedSuccessfully = true;
            } catch (Exception e) {
                result = "Error during transaction. Error: " + e.getMessage();
                isAddedSuccessfully = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "VoterRegistrationActivity":
                    iVoterRegistration.OnSetValidVoterResult(isAddedSuccessfully);
                    break;
            }

        }
    }

    private static class WriteCandidateInfo extends AsyncTask<String, String, String> {

        Boolean isAddedSuccessfully = false;
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            int logo = Integer.parseInt(params[1]);
            BigInteger logoBig = BigInteger.valueOf(logo);
            String party = params[2];

            String result;
            try {
                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                TransactionReceipt transactionReceipt = election.addCandidate(name, logoBig, party).send();
                result = "Registered successfully. Gas used: " + transactionReceipt.getGasUsed();
                isAddedSuccessfully = true;
            } catch (Exception e) {
                result = "Error during transaction. Error: " + e.getMessage();
                isAddedSuccessfully = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "AddCandidateActivity":
                    iAddCandidates.OnAddCandidateResult(isAddedSuccessfully);
                    break;
            }

        }
    }

    private static class WriteVote extends AsyncTask<String, String, String> {

        Boolean isVotedSuccessfully = false;
        @Override
        protected String doInBackground(String... params) {
            int candidateId = Integer.valueOf(params[0]);
            BigInteger IMEI = BigInteger.valueOf(Long.parseLong(params[1]));

            String result;
            try {
                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                TransactionReceipt transactionReceipt = election.vote(BigInteger.valueOf(candidateId), IMEI).send();
                result = "Voted successfully. Gas used: " + transactionReceipt.getGasUsed();
                isVotedSuccessfully = true;
            } catch (Exception e) {
                result = "Error during transaction. Error: " + e.getMessage();
                isVotedSuccessfully = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "VoteScreenActivity":
                    iVoteScreenActivity.OnCastVoteResult(isVotedSuccessfully);
                    break;
            }

        }
    }

    private static class WriteElectionDates extends AsyncTask<String, String, String> {

        String result;
        String startDate, endDate;
        Boolean isDateSetSuccessful = false;

        @Override
        protected String doInBackground(String... params) {

            startDate = params[0];
            endDate = params[1];

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                TransactionReceipt transactionReceipt = election.setElectionDates(startDate, endDate).send();
                result = "Dates written successfully. Gas used: " + transactionReceipt.getGasUsed();
                isDateSetSuccessful = true;

            } catch (Exception e) {
                result = "Error writing election dates. Error: " + e.getMessage();
                isDateSetSuccessful = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "SetElectionDateActivity":
                    iSetElectionDates.OnSetElectionDatesResult(isDateSetSuccessful);
                    break;
            }

        }
    }

    private static class ResetElectionData extends AsyncTask<String, String, String> {

        String result;
        Boolean isResetSuccessful = false;
        @Override
        protected String doInBackground(String... params) {

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                TransactionReceipt transactionReceipt = election.resetData().send();
                result = "Reset successfully. Gas used: " + transactionReceipt.getGasUsed();
                isResetSuccessful = true;

            } catch (Exception e) {
                result = "Error resetting data. Error: " + e.getMessage();
                isResetSuccessful = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "AdminActivity":
                    iAdminActivity.OnResetElection(isResetSuccessful);
                    break;
            }

        }
    }

    private static class ReadNotaCount extends AsyncTask<String, String, String> {

        String result;
        int notaCount = 0;
        @Override
        protected String doInBackground(String... params) {

            try {

                Election election = Election.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
                notaCount = election.notaCount().send().intValue();
                result = "Success..."+notaCount;


            } catch (Exception e) {
                result = "Error reading candidate count. Error: " + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();

            switch (ACTIVITY_NAME){
                case "VoterActivity":
                    iVoterActivity.OnGetNotaCountResult(notaCount);
                    break;
            }

        }
    }

}
