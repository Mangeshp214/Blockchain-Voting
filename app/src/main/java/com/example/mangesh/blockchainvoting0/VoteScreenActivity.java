package com.example.mangesh.blockchainvoting0;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.tuples.generated.Tuple5;

import java.math.BigInteger;
import java.util.ArrayList;

public class VoteScreenActivity extends AppCompatActivity implements FingerprintHandler.FingerprintHelperListener,
        Web3jHelper.iWeb3jConnection,
        Web3jHelper.iVoteScreenActivity{

    Button buttonVote;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private LinearLayout linLayProBar;
    private ListView lvCandidates;
    private TextView tvProgress;
    private Web3jHelper web3jHelper;
    private ArrayList<CandidateListModel> allCandidatesModel;
    private CandidateListAdapter candidateListAdapter;
    private int position = -1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VoteScreenActivity.this, VoterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_screen);

        initUI();
        displayProgressBar("Connecting to blockchain...");
        web3jHelper = new Web3jHelper(VoteScreenActivity.this, "VoteScreenActivity");

        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = candidateListAdapter.getCurrentPosition();
                if(position != -1){


                    String selectedCandidateName = allCandidatesModel.get(position).getCandidateName();
                    String selectedPartyName = allCandidatesModel.get(position).getPartyName();

                    builder = new AlertDialog.Builder(VoteScreenActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.popup_selected_candidate, null);
                    builder.setView(view);
                    dialog = builder.create();
                    dialog.show();

                    TextView tvSelectedCandidateName = view.findViewById(R.id.textViewCandidateName);
                    TextView tvSelectedCandidateParty = view.findViewById(R.id.textViewPartyName);
                    TextView tvOne = view.findViewById(R.id.textViewYHS);
                    TextView tvTwo = view.findViewById(R.id.textViewOP);
                    TextView tvThree = view.findViewById(R.id.textViewPPYFTC);
                    tvOne.setText(R.string.You_have_selected);
                    tvTwo.setText(R.string.of_party);
                    tvThree.setText(R.string.fingerprint_vote_screen);

                    tvSelectedCandidateName.setText(selectedCandidateName);
                    tvSelectedCandidateParty.setText(selectedPartyName);

                    Intent intent = new Intent(VoteScreenActivity.this, VoterActivity.class);
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(VoteScreenActivity.this, intent);

                    fingerprintHandler.keyguardManager =
                            (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    fingerprintHandler.fingerprintManager =
                            (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                    try {
                        fingerprintHandler.generateKey();
                    } catch (FingerprintHandler.FingerprintException e) {
                        e.printStackTrace();
                    }

                    if (fingerprintHandler.initCipher()) {
                        //If the cipher is initialized successfully, then create a CryptoObject instance//
                        fingerprintHandler.cryptoObject = new FingerprintManager.CryptoObject(fingerprintHandler.cipher);

                        // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                        // for starting the authentication process (via the startAuth method) and processing the authentication process events//

                        fingerprintHandler.startAuth(fingerprintHandler.fingerprintManager, fingerprintHandler.cryptoObject);

                    }

                }else{
                    Toast.makeText(VoteScreenActivity.this, "Please select candidate.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void setListViewCandidates() {

        allCandidatesModel.add(new CandidateListModel(allCandidatesModel.size()+1,
                "None Of The Above",
                "NOTA",
                0));
        candidateListAdapter = new CandidateListAdapter(VoteScreenActivity.this, allCandidatesModel);
        lvCandidates.setAdapter(candidateListAdapter);
        position = lvCandidates.getCheckedItemPosition();

    }

    @Override
    public void authenticationFailed(String error) {

        Toast.makeText(VoteScreenActivity.this, error, Toast.LENGTH_LONG).show();
        dialog.dismiss();

    }

    @Override
    public void authenticationSuccess(String result) {

        Toast.makeText(VoteScreenActivity.this, "Success...", Toast.LENGTH_LONG).show();
        dialog.dismiss();

        int candidateId = allCandidatesModel.get(position).getCandidateId();
        displayProgressBar("Adding your vote onto Blockchain(cID) "+candidateId+"...");
        web3jHelper.castVote(candidateId, getIMEI());

    }

    private String getIMEI() {

        String serviceName = Context.TELEPHONY_SERVICE;
        TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);

        if (ActivityCompat.checkSelfPermission(VoteScreenActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            Toast.makeText(VoteScreenActivity.this, "Phone permission not provided.", Toast.LENGTH_SHORT).show();
            Toast.makeText(VoteScreenActivity.this, "Please provide permission from phone settings.", Toast.LENGTH_SHORT).show();

            return "";
        }

        String IMEI = m_telephonyManager.getDeviceId().toString();
        return IMEI;

    }

    private void initUI() {

        buttonVote = findViewById(R.id.buttonVote);
        linLayProBar = findViewById(R.id.linLayProBarVoteScreen);
        linLayProBar.setVisibility(View.INVISIBLE);
        lvCandidates = findViewById(R.id.listViewCandidates);
        tvProgress = findViewById(R.id.textViewProgress);
        allCandidatesModel = new ArrayList<>();

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
            displayProgressBar("Reading candidate count...");
            web3jHelper.getCandidateCount();
        }

    }

    @Override
    public void OnGetCandidateCountResult(int candidateCount) {
        hideProgressBar();
        displayProgressBar("Getting all candidates info...");
        web3jHelper.getAllCandidates(candidateCount);
    }

    @Override
    public void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo) {

        for(int i=0; i<allCandidatesInfo.size(); i++){

            allCandidatesModel.add(new CandidateListModel(allCandidatesInfo.get(i).getValue1().intValue(), allCandidatesInfo.get(i).getValue2(), allCandidatesInfo.get(i).getValue3(),
                    allCandidatesInfo.get(i).getValue4().intValue()));

        }
        hideProgressBar();
        setListViewCandidates();

    }

    @Override
    public void OnCastVoteResult(Boolean isVotedSuccessfully) {

        if(isVotedSuccessfully){
            hideProgressBar();
            Intent intent = new Intent(VoteScreenActivity.this, VoterActivity.class);
            startActivity(intent);
            finish();
        }else{
            hideProgressBar();
            Toast.makeText(VoteScreenActivity.this, "Error casting vote!!! Please try again later...", Toast.LENGTH_SHORT).show();
        }

    }
}
