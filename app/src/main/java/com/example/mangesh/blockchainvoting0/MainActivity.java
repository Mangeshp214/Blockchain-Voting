package com.example.mangesh.blockchainvoting0;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kenai.jffi.Main;

import org.web3j.tuples.generated.Tuple5;
import java.math.BigInteger;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Web3jHelper.iWeb3jConnection, Web3jHelper.iIMEICheck{

    private Button buttonVoterRegistration, buttonAdminLoin;
    private LinearLayout linLayProBarText;
    private TextView tvProgress;
    private String IMEI;
    private Web3jHelper web3jHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise all the UI widgets.
        initUI();

        if(!isPermissionGranted()){

            requestPhoneStatePermission();

        }

        if(isOnline()){

            String serviceName = Context.TELEPHONY_SERVICE;
            TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Phone permission not provided.", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Please provide permission from phone settings.", Toast.LENGTH_SHORT).show();

                return;
            }
            IMEI = m_telephonyManager.getDeviceId();

            displayProgressBar("Connecting to blockchain...");
            web3jHelper = new Web3jHelper(MainActivity.this, "MainActivity");

            buttonVoterRegistration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //direct to registration page.
                    Intent intent = new Intent(MainActivity.this, VoterRegistrationActivity.class);
                    startActivity(intent);

                }
            });

            buttonAdminLoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //direct to admin loin page.
                    Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                    startActivity(intent);

                }
            });

        }else{

            Toast.makeText(this, "Please check the Internet Connection!", Toast.LENGTH_LONG).show();

        }

    }

    private boolean isPermissionGranted() {

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private void requestPhoneStatePermission() {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed for getting IMEI number of your device so that you identity can be uniquely verified.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_PHONE_STATE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();

                if(isOnline()){

                    String serviceName = Context.TELEPHONY_SERVICE;
                    TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(this, "Phone permission not provided.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Please provide permission from phone settings.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    IMEI = m_telephonyManager.getDeviceId();

                    displayProgressBar("Connecting to blockchain...");
                    web3jHelper = new Web3jHelper(MainActivity.this, "MainActivity");

                    buttonVoterRegistration.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //direct to registration page.
                            Intent intent = new Intent(MainActivity.this, VoterRegistrationActivity.class);
                            startActivity(intent);

                        }
                    });

                    buttonAdminLoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //direct to admin loin page.
                            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                            startActivity(intent);

                        }
                    });

                }else{

                    Toast.makeText(this, "Please check the Internet Connection!", Toast.LENGTH_LONG).show();

                }

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                requestPhoneStatePermission();
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void displayProgressBar(String status) {
        linLayProBarText.setVisibility(View.VISIBLE);
        tvProgress.setText(status);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar(){
        linLayProBarText.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkForRegisteredIMEI(String imei) {

        hideProgressBar();
        displayProgressBar("Checking registered IMEI...");
        web3jHelper.getVoterInfo(imei);
        //continue to OnGetVoterInfoResult() method.

    }

    private void checkForAdminIMEI(){
        hideProgressBar();
        displayProgressBar("Checking for admin IMEI...");
        web3jHelper.getIMEI_admin();
        //continue to OnGetIMEI_AdminResult() method.
    }

    private void initUI() {

        buttonAdminLoin = findViewById(R.id.buttonAdminLogin);
        buttonVoterRegistration = findViewById(R.id.buttonVoterRegistration);
        linLayProBarText = findViewById(R.id.linLayProBarTextMain);
        linLayProBarText.setVisibility(View.INVISIBLE);
        tvProgress = findViewById(R.id.textViewProgress);
        buttonAdminLoin.setEnabled(false);
        buttonVoterRegistration.setEnabled(false);

    }



    @Override
    public void connection(Boolean isConnected) {
        if(isConnected){
            hideProgressBar();
            //Check if already registered as voter or admin. If yes direct to specific activity.
            checkForAdminIMEI();
        }
    }

    @Override
    public void OnGetIMEI_AdminResult(String adminIMEI) {
        if(IMEI.equals(adminIMEI)){//TODO : remove ! after testing.
            hideProgressBar();
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            finish();
        }else{
            hideProgressBar();
            buttonAdminLoin.setEnabled(false);//TODO: change code after debugging.
            checkForRegisteredIMEI(IMEI);
        }/*else{
            hideProgressBar();
            buttonAdminLoin.setEnabled(true);
        }*/
    }

    @Override
    public void OnGetVoterInfoResult(String voterName, int CandidateId) {

        if(!voterName.equals("")){
            hideProgressBar();
            Intent intent = new Intent(MainActivity.this, VoterActivity.class);
            startActivity(intent);
            finish();
        }else{
            hideProgressBar();
            buttonVoterRegistration.setEnabled(true);
        }

    }

}
