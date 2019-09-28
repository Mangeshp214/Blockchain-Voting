package com.example.mangesh.blockchainvoting0;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.tuples.generated.Tuple5;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminActivity extends AppCompatActivity implements Web3jHelper.iWeb3jConnection, Web3jHelper.iAdminActivity{

    private Button buttonStartElection;
    private TextView tvAdminInfo, tvProgress;
    private LinearLayout linLayProBar;
    private ImageView ivRefresh;
    private Web3jHelper web3jHelper;
    private Date startD, endD, resultD, todaysD;
    private String results, startDate, endDate, IMEI;
    private Boolean isResultDateSet = false, isResultOut = false;
    private int candidateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initUI();

        IMEI = getIMEI();
        web3jHelper = new Web3jHelper(AdminActivity.this, "AdminActivity");

        buttonStartElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isResultOut){
                    new AlertDialog.Builder(AdminActivity.this)
                            .setTitle("Restart Election!")
                            .setMessage("Are you sure you want to restart the election? All the candidate vote count data will be lost.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    displayProgressBar("Resetting candidate data...");
                                    web3jHelper.resetElectionData();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }else{
                    Intent intent = new Intent(AdminActivity.this, AddCandidateActivity.class);
                    startActivity(intent);
                }

            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                displayProgressBar("Refreshing! Please wait...");
                ivRefresh.setBackgroundColor(Color.GRAY);
                tvAdminInfo.setText("");
                displayAdminInfo();
                Toast.makeText(AdminActivity.this, "Voter info refreshed...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initUI() {

        buttonStartElection = findViewById(R.id.buttonConfirmDates);
        buttonStartElection.setEnabled(false);
        tvAdminInfo = findViewById(R.id.textViewAdminInfo);
        tvProgress = findViewById(R.id.textViewProgress);
        linLayProBar = findViewById(R.id.linLayProBarTextAdmin);
        ivRefresh = findViewById(R.id.imageViewRefreshAdmin);
        linLayProBar.setVisibility(View.INVISIBLE);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayAdminInfo() {

        SimpleDateFormat formatter6=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        todaysD = new Date();
        try {
            startD = formatter6.parse(startDate);
            endD = formatter6.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            hideProgressBar();
            ivRefresh.setBackgroundColor(Color.GREEN);
            results = "<br><br><h2>Proceed to start election by clicking <font color=\"#F44336\">START ELECTION</font> button</h2>";
            tvAdminInfo.setText(Html.fromHtml(results,0));
            buttonStartElection.setEnabled(true);
        }


        if(startD == null || endD == null){

            results = "<br><br><h2>Proceed to start election by clicking <font color=\"#F44336\">START ELECTION</font> button</h2>";
            tvAdminInfo.setText(Html.fromHtml(results));
            buttonStartElection.setEnabled(true);
            hideProgressBar();
            ivRefresh.setBackgroundColor(Color.GREEN);

        }else{

            try {
                todaysD = formatter6.parse(formatter6.format(todaysD));

                if(!isResultDateSet){
                    resultD = endD;
                    resultD.setMinutes(resultD.getMinutes()+05);
                    isResultDateSet = true;
                }

                if(todaysD.before(startD)){
                    results = "<br><br><h2>Election starting soon...<br><br><br>";
                    results = results + "Start Date : <font color=\"#4CAF50\"><br>" + startDate + "</font><br><br>";
                    results = results + "End Date : <font color=\"#4CAF50\"><br>" + endDate + "</font><br><br>";
                    results = results + "Total Candidates : " + candidateCount + "</h1>";

                    tvAdminInfo.setText(Html.fromHtml(results));

                    hideProgressBar();
                    ivRefresh.setBackgroundColor(Color.GREEN);
                }else if(todaysD.after(endD)){

                    if(todaysD.before(resultD)){
                        tvAdminInfo.setText(Html.fromHtml("<br><br><h1><font color=\"#F44336\">Election ended. </font>" +
                                "Thank you for voting.<br></h1>"+
                                "<br><h2>Results will be displayed on : " +
                                "<font color=\"#4CAF50\"><br>"+resultD+"</font><br><br>"));
                        hideProgressBar();
                        ivRefresh.setBackgroundColor(Color.GREEN);

                    }else if(todaysD.after(resultD) || todaysD.equals(resultD)){
                        hideProgressBar();
                        displayProgressBar("Getting election results...");
                        web3jHelper.getAllCandidates(candidateCount);
                    }

                }else if(todaysD.after(startD) && todaysD.before(endD)){

                    results = "<br><br>"
                            + "<h2>Election is "
                            + "<font color=\"#F44336\"><bold>"
                            + "LIVE"
                            + "</bold></font>"
                            + " now !!!</h2><br><br>";

                    results = results + "<h2>Results will be displayed on : <font color=\"#4CAF50\"><br>"+resultD+"</font><br><br>";
                    results = results + "Election has started on : <font color=\"#4CAF50\"><br>"+startDate+"</font><br><br>";
                    results = results + "Election will end on : <font color=\"#4CAF50\"><br>"+endD+"</font><br><br>";
                    results  = results + "<font color=\"#03A9F4\">Happy Voting :)</font></h2>";
                    tvAdminInfo.setText(Html.fromHtml(results, 0));
                    hideProgressBar();
                    ivRefresh.setBackgroundColor(Color.GREEN);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    private String getIMEI() {

        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);

        if (ActivityCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            Toast.makeText(AdminActivity.this, "Phone permission not provided.", Toast.LENGTH_SHORT).show();
            Toast.makeText(AdminActivity.this, "Please provide permission from phone settings.", Toast.LENGTH_SHORT).show();

            return "";
        }

        String IMEI = m_telephonyManager.getDeviceId().toString();
        return IMEI;

    }

    @Override
    public void connection(Boolean isConnected) {
        if(isConnected){
            displayProgressBar("Getting admin info...");
            web3jHelper.getElectionDates();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void OnGetCandidateCountResult(int candidateCount) {

        this.candidateCount = candidateCount;
        hideProgressBar();
        displayProgressBar("Displaying admin info...");
        displayAdminInfo();

    }

    @Override
    public void OnGetElectionDatesResult(String startDate, String endDate) {
        if(startDate.equals("") && endDate.equals("")){
            hideProgressBar();
            results = "<br><br><h2>Proceed to start election by clicking <font color=\"#F44336\">START ELECTION</font> button</h2>";
            tvAdminInfo.setText(Html.fromHtml(results));
            buttonStartElection.setEnabled(true);
        }else{
            this.startDate = startDate;
            this.endDate = endDate;
            buttonStartElection.setEnabled(false);
            web3jHelper.getCandidateCount();
        }
    }

    @Override
    public void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo) {

        results = "<br><br><h1><font color=\"#F44336\">Election ended. </font>" +
                "Click on START ELECTION button to restart election.<br></h1>";
        buttonStartElection.setEnabled(true);
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

        results = "<br><br><h2>Winner of the election is : <font color=\"#4CAF50\">"+winnerName+"</font> " +
                "with total vote count : <font color=\"#4CAF50\">"+winnerVotes+"</font></h2>" + results;
        tvAdminInfo.setText(Html.fromHtml(results));

        buttonStartElection.setEnabled(true);
        buttonStartElection.setText("Restart Election!");
        isResultOut = true;
        hideProgressBar();
        ivRefresh.setBackgroundColor(Color.GREEN);

    }

    @Override
    public void OnResetElection(Boolean isResetSuccessful) {

        hideProgressBar();
        isResultOut = false;
        Intent intent = new Intent(AdminActivity.this, AddCandidateActivity.class);
        startActivity(intent);
    }
}
