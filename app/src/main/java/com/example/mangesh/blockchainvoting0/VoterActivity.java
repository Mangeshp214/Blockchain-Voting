package com.example.mangesh.blockchainvoting0;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.tuples.generated.Tuple5;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VoterActivity extends AppCompatActivity implements Web3jHelper.iWeb3jConnection, Web3jHelper.iVoterActivity{

    private ProgressBar progressBar;
    private LinearLayout linLayProBar;
    private TextView tvProgress;
    private TextView tvVoterInfo;
    private Button buttonVoteNow;
    private ImageView ivRefresh;
    private String IMEI, results;
    private String startDate, endDate;
    private Date startD, endD, todaysD;
    private Date resultD;
    private int candidateCount;
    private Web3jHelper web3jHelper;
    private String voterName;
    private Boolean isVoted = false, isResultDateSet = false;
    private int notaCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter);

        initUI();

        IMEI = getIMEI();
        //connect to the smart contract
        displayProgressBar("Connecting to blockchain...");
        web3jHelper = new Web3jHelper(VoterActivity.this, "VoterActivity");

        //display voter information
        //display election dates
        
        //vote button
        buttonVoteNow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                todaysD = new Date();
                if(todaysD.after(startD) && todaysD.before(endD)){
                    Intent intent = new Intent(VoterActivity.this, VoteScreenActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(VoterActivity.this, "Election has Ended...", Toast.LENGTH_SHORT).show();
                    displayVoterInfo();
                }

            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                displayProgressBar("Refreshing! Please wait...");
                ivRefresh.setBackgroundColor(Color.GRAY);
                tvVoterInfo.setText("");
                displayVoterInfo();
                Toast.makeText(VoterActivity.this, "Voter info refreshed...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void displayProgressBar(String status) {
        linLayProBar.setVisibility(View.VISIBLE);
        tvProgress.setText(status);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar(){
        linLayProBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void connection(Boolean isConnected) {
        if(isConnected){
            hideProgressBar();
            displayProgressBar("Getting election dates...");
            web3jHelper.getElectionDates();
        }
    }

    @Override
    public void OnGetCandidateCountResult(int candidateCount) {
        hideProgressBar();
        this.candidateCount = candidateCount;
        displayProgressBar("Getting voter information...");
        web3jHelper.getVoterInfo(IMEI);
    }

    @Override
    public void OnGetElectionDatesResult(String startDate, String endDate) {
        hideProgressBar();
        this.startDate = startDate;
        this.endDate = endDate;
        displayProgressBar("Getting candidate count...");
        web3jHelper.getCandidateCount();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void OnGetVoterInfoResult(String voterName, int CandidateId) {
        if(CandidateId != 0) {
            isVoted = true;
        }

        //isVoted = false;
        this.voterName = voterName;
        if(!startDate.equals("") && !endDate.equals(""))
            displayVoterInfo();
        else{
            hideProgressBar();
            results = "<br><br><h1><font color=\"#E91E63\">Welcome! "+voterName +
                    "</font><br>Election dates not decided yet. Stay tuned for more info...<br><br><br>";
            tvVoterInfo.setText(Html.fromHtml(results,0));
        }

    }

    @Override
    public void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo) {

        results = "<br><br><h1><font color=\"#F44336\">Election ended. </font>" +
                "Thank you for voting.<br></h1>";
        int winnerVotes = allCandidatesInfo.get(0).getValue5().intValue();
        String winnerName = allCandidatesInfo.get(0).getValue2();
        int totalCandidates = allCandidatesInfo.size();

        for(int i=0; i<totalCandidates; i++){

            results = results + "<h3><br>Candidate ID : <font color=\"#4CAF50\">"+allCandidatesInfo.get(i).getValue1()+"</font><br>";
            results = results + "Candidate Name : <font color=\"#4CAF50\">"+allCandidatesInfo.get(i).getValue2()+"</font><br>";
            results = results + "Vote count : <font color=\"#4CAF50\">"+allCandidatesInfo.get(i).getValue5()+"</font></br><br></h3>";

            if(allCandidatesInfo.get(i).getValue5().intValue() > winnerVotes){

                winnerName = allCandidatesInfo.get(i).getValue2();
                winnerVotes = allCandidatesInfo.get(i).getValue5().intValue();

            }

        }

        results = results + "<h3>Number of people voted for None Of The Above : "+notaCount+"</h3>";

        results = "<br><br><h2>Winner of the election is : <font color=\"#4CAF50\">"+winnerName+"</font> " +
                "with total vote count : <font color=\"#4CAF50\">"+winnerVotes+"</font></h2>" + results;
        tvVoterInfo.setText(Html.fromHtml(results));

        isVoted = false;
        hideProgressBar();
        ivRefresh.setBackgroundColor(Color.GREEN);
    }

    @Override
    public void OnGetNotaCountResult(int notaCount) {

        this.notaCount = notaCount;
        web3jHelper.getAllCandidates(candidateCount);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayVoterInfo() {

        SimpleDateFormat formatter6=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        todaysD = new Date();
        try {
            startD = formatter6.parse(startDate);
            endD = formatter6.parse(endDate);
            todaysD = formatter6.parse(formatter6.format(todaysD));

            if(!isResultDateSet){
                resultD = endD;
                resultD.setMinutes(resultD.getMinutes()+05);
                isResultDateSet = true;
            }

            if(todaysD.before(startD)){
                buttonVoteNow.setEnabled(false);
                results = "<br><br><h1><font color=\"#E91E63\">Welcome! "+voterName+"</font><br>Election starting soon...<br><br><br>";
                results = results + "Start Date : <font color=\"#4CAF50\"><br>" + startDate + "</font><br><br>";
                results = results + "End Date : <font color=\"#4CAF50\"><br>" + endDate + "</font><br><br>";
                results = results + "Total Candidates : " + candidateCount + "</h1>";

                tvVoterInfo.setText(Html.fromHtml(results));

                hideProgressBar();
                ivRefresh.setBackgroundColor(Color.GREEN);
            }else if(todaysD.after(endD)){
                buttonVoteNow.setEnabled(false);

                if(todaysD.before(resultD)){
                    tvVoterInfo.setText(Html.fromHtml("<br><br><h1><font color=\"#F44336\">Election ended. </font>" +
                            "Thank you for voting.<br></h1>"+
                            "<br><h2>Results will be displayed on : " +
                            "<font color=\"#4CAF50\"><br>"+resultD+"</font><br><br>"));
                    hideProgressBar();
                    ivRefresh.setBackgroundColor(Color.GREEN);

                }else if(todaysD.after(resultD) || todaysD.equals(resultD)){
                    hideProgressBar();
                    displayProgressBar("Getting election results...");
                    web3jHelper.getNotaCount();
                }

            }else if(todaysD.after(startD) && todaysD.before(endD)){

                if(isVoted){
                    buttonVoteNow.setEnabled(false);

                    results = "<br><br>"
                            + "<h2>Election is "
                            + "<font color=\"#F44336\"><bold>"
                            + "LIVE"
                            + "</bold></font>"
                            + " now !!!</h2><br><br>";

                    results = results + "<h1>Thank you for voting.</h1><br><br>";
                    results = results + "<h2>Results will be displayed on : <font color=\"#4CAF50\"><br>"+resultD+"</font><br><br>";
                    results = results + "Election has started on : <font color=\"#4CAF50\"><br>"+startDate+"</font><br><br>";
                    results = results + "Election will end on : <font color=\"#4CAF50\"><br>"+endD+"</font><br><br>";
                    tvVoterInfo.setText(Html.fromHtml(results, 0));
                    hideProgressBar();
                    ivRefresh.setBackgroundColor(Color.GREEN);

                }else{
                    buttonVoteNow.setEnabled(true);

                    results = "<br><br>"
                            + "<h2>Election is "
                            + "<font color=\"#F44336\"><bold>"
                            + "LIVE"
                            + "</bold></font>"
                            + " now !!!<br><br>";

                    results = results + "Cast your vote by clicking CAST VOTE button<br><br>";
                    results = results + "Election will end on : <font color=\"#4CAF50\"><br>"+endDate+"</font><br><br>";
                    results = results + "Election has started on : <font color=\"#4CAF50\"><br>"+startDate+"</font><br><br>";
                    results  = results + "<font color=\"#03A9F4\">Happy Voting :)</font></h2>";
                    tvVoterInfo.setText(Html.fromHtml(results, 0));
                    hideProgressBar();
                    ivRefresh.setBackgroundColor(Color.GREEN);

                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
            hideProgressBar();
            ivRefresh.setBackgroundColor(Color.GREEN);
            results = "<br><br><h1><font color=\"#E91E63\">Welcome! "+voterName +
                    "</font><br>Election dates not decided yet. Stay tuned for more info...<br><br><br>";
            tvVoterInfo.setText(Html.fromHtml(results,0));
        }

    }

    private String getIMEI() {

        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);

        if (ActivityCompat.checkSelfPermission(VoterActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            Toast.makeText(VoterActivity.this, "Phone permission not provided.", Toast.LENGTH_SHORT).show();
            Toast.makeText(VoterActivity.this, "Please provide permission from phone settings.", Toast.LENGTH_SHORT).show();

            return "";
        }

        String IMEI = m_telephonyManager.getDeviceId().toString();
        return IMEI;

    }

    private void initUI() {

        tvVoterInfo = findViewById(R.id.tvVoterInfo);
        buttonVoteNow = findViewById(R.id.buttonVoteNow);
        buttonVoteNow.setEnabled(false);
        progressBar = findViewById(R.id.progressBarVoterActivity);
        linLayProBar = findViewById(R.id.linLayProBarTextVoter);
        linLayProBar.setVisibility(View.INVISIBLE);
        tvProgress = findViewById(R.id.textViewProgress);
        ivRefresh = findViewById(R.id.imageViewRefresh);

    }

}
