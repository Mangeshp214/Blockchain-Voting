package com.example.mangesh.blockchainvoting0;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VoterRegistrationActivity extends AppCompatActivity implements Web3jHelper.iWeb3jConnection, Web3jHelper.iVoterRegistration{

    private TextInputEditText tietVoterName;
    private TextInputEditText tietVoterUID;
    private TextInputEditText tietVoterPhoneNo;
    private Button buttonRegister;
    private LinearLayout linLayProBarText;
    private TextView tvProgress;
    private ProgressBar progressBar;
    //private MainActivity mainActivity;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private Web3jHelper web3jHelper;

    String IMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_registration);

        initUI();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Voter");
        mAuth = FirebaseAuth.getInstance();

        displayProgressBar("Connecting to blockchain...");
        web3jHelper = new Web3jHelper(VoterRegistrationActivity.this, "VoterRegistrationActivity");

        //listen for button click event
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validate all text inputs
                if (isAllFieldsValid()) {

                    //validate fields in database and age as well for the entered UID
                    isValidVoter();

                }

            }
        });

    }

    private String getIMEI() {

        //Get IMEI number of device
        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);

        if (ActivityCompat.checkSelfPermission(VoterRegistrationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            Toast.makeText(VoterRegistrationActivity.this, "Phone permission not provided.", Toast.LENGTH_SHORT).show();
            Toast.makeText(VoterRegistrationActivity.this, "Please provide permission from phone settings.", Toast.LENGTH_SHORT).show();
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        return m_telephonyManager.getDeviceId();

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

    private void isValidVoter() {

        displayProgressBar("Verifying voter ID and Phone...");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){
                    if(dataSnapshot.child(tietVoterUID.getText().toString()).hasChildren()){
                        Voter voter = dataSnapshot.child(tietVoterUID.getText().toString()).getValue(Voter.class);
                        Toast.makeText(VoterRegistrationActivity.this, "Phone : "+voter.getPhone(), Toast.LENGTH_SHORT).show();

                        if(tietVoterPhoneNo.getText().toString().equals(voter.getPhone()+"")){
                            hideProgressBar();
                            displayProgressBar("Auto verifying OTP. Please wait...");
                            verifyOTP(voter.getPhone());
                        }else{
                            String ph = String.valueOf(voter.getPhone() % 10000);
                            ph = "******"+ph;

                            hideProgressBar();
                            tietVoterPhoneNo.requestFocus();
                            tietVoterPhoneNo.setError("Phone number didn't match. Registered number is : \n"+ph);
                        }


                    }else{
                        hideProgressBar();
                        tietVoterUID.requestFocus();
                        tietVoterUID.setError("Wrong UID. No account present");
                    }

                }else{
                    hideProgressBar();
                    Toast.makeText(VoterRegistrationActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressBar();
                Toast.makeText(VoterRegistrationActivity.this,
                        "Operation cancelled "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void verifyOTP(long phone) {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(VoterRegistrationActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    hideProgressBar();
                                    Toast.makeText(VoterRegistrationActivity.this, "OTP verified", Toast.LENGTH_SHORT).show();
                                    IMEI = getIMEI();
                                    if(!IMEI.equals(null)) {
                                        displayProgressBar("Adding voter information onto blockchain...");
                                        web3jHelper.setValidVoter(tietVoterName.getText().toString(), IMEI, tietVoterUID.getText().toString());
                                    }
                                }else{
                                    hideProgressBar();
                                    Toast.makeText(VoterRegistrationActivity.this,
                                            "OTP verification failed. Try again after some time", Toast.LENGTH_LONG).show();
                                }


                            }
                        });

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                hideProgressBar();
                Toast.makeText(VoterRegistrationActivity.this,  "Failure, Try again after some time", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                hideProgressBar();
                Toast.makeText(VoterRegistrationActivity.this,
                        "Problem retrieving code. Please ensure you have proper operator signal", Toast.LENGTH_LONG).show();
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+ phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );

    }

    private boolean isAllFieldsValid() {

        //check for empty string
        if(tietVoterName.getText().toString().equals("")){
            tietVoterName.setError(getApplicationContext().getResources().getString(R.string.name_empty_error));
            tietVoterName.requestFocus();
            return false;
        }

        if(!tietVoterName.getText().toString().matches("[A-Za-z\\s]*")){
            tietVoterName.setError("Name must contain only characters!");
            tietVoterName.requestFocus();
            return false;
        }

        //check for empty string and length
        if(tietVoterUID.getText().toString().equals("")){
            tietVoterUID.setError(getApplicationContext().getResources().getString(R.string.uid_empty_error));
            tietVoterUID.requestFocus();
            return false;
        }

        if(tietVoterUID.getText().toString().length() != 8){
            tietVoterUID.setError(getApplicationContext().getResources().getString(R.string.uid_length_error));
            tietVoterUID.requestFocus();
            return false;
        }

        //check for empty string and length
        if(tietVoterPhoneNo.getText().toString().equals("")){
            tietVoterPhoneNo.setError(getApplicationContext().getResources().getString(R.string.phone_no_empty_error));
            tietVoterPhoneNo.requestFocus();
            return false;
        }

        if(tietVoterPhoneNo.length() != 10){
            tietVoterPhoneNo.setError(getApplicationContext().getResources().getString(R.string.phone_no_length_error));
            tietVoterPhoneNo.requestFocus();
            return false;
        }

        return true;

    }

    private void initUI() {

        tietVoterName = findViewById(R.id.tietVoterName);
        tietVoterName.requestFocus();
        tietVoterUID = findViewById(R.id.tietUID);
        tietVoterPhoneNo = findViewById(R.id.tietPhoneNo);
        buttonRegister = findViewById(R.id.buttonRegister);
        //mainActivity = new MainActivity();
        linLayProBarText = findViewById(R.id.linLayProBarText);
        linLayProBarText.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.textViewProgress);

    }

    @Override
    public void connection(Boolean isConnected) {
        if(isConnected){
            hideProgressBar();
        }
    }

    @Override
    public void OnSetValidVoterResult(Boolean isAddedSuccessfully) {
        if(isAddedSuccessfully){
            hideProgressBar();
            Intent intent = new Intent(VoterRegistrationActivity.this, VoterActivity.class);
            startActivity(intent);
            MainActivity mainActivity = new MainActivity();
            mainActivity.finish();
            finish();
        }else{
            Toast.makeText(VoterRegistrationActivity.this, "Enter information again...", Toast.LENGTH_SHORT).show();
        }
    }
}

@IgnoreExtraProperties
class Voter{

    private String Name;
    private long Phone;

    public Voter() {
    }

    public Voter(long Phone, String Name) {
        this.Name = Name;
        this.Phone = Phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public long getPhone() {
        return Phone;
    }

    public void setPhone(long phone) {
        this.Phone = phone;
    }
}
