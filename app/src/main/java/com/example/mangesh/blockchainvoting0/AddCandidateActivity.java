package com.example.mangesh.blockchainvoting0;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.web3j.tuples.generated.Tuple5;

import java.math.BigInteger;
import java.util.ArrayList;

public class AddCandidateActivity extends AppCompatActivity implements Web3jHelper.iWeb3jConnection, Web3jHelper.iAddCandidates{

    private ListView lvCandidates;
    private FloatingActionButton fabAddCandidate;
    private ImageButton imageButtonDone;
    public ImageView ivUpload;
    private LinearLayout linLayProBar;
    private TextView tvProgress;
    private Web3jHelper web3jHelper;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ArrayList<CandidateModel> candidateModelArrayList;
    private ArrayList<PartyLogoModel> logoModelArrayList;
    private String[] parties;
    private int pos = 0, candidateCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);

        initUI();
        initPartyNames();

        displayProgressBar("Connecting to blockchain...");
        web3jHelper = new Web3jHelper(AddCandidateActivity.this, "AddCandidateActivity");

        fabAddCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new AlertDialog.Builder(AddCandidateActivity.this);
                View view = getLayoutInflater().inflate(R.layout.popup_add_candidate, null);
                builder.setView(view);
                dialog = builder.create();
                dialog.show();

                TextInputEditText tietCandidateName = view.findViewById(R.id.tietCandidateName);
                Spinner spinnerParty = view.findViewById(R.id.spinnerParty);
                Button buttonAdd = view.findViewById(R.id.buttonAddCandidate);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCandidateActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, parties);
                spinnerParty.setAdapter(arrayAdapter);

                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tietCandidateName.getText().toString().isEmpty()){
                            tietCandidateName.setError("Please enter candidate name!");
                        }else{
                            pos = spinnerParty.getSelectedItemPosition();
                            candidateModelArrayList.add(new CandidateModel(tietCandidateName.getText().toString(),
                                    logoModelArrayList.get(pos).getPartyName(),
                                    logoModelArrayList.get(pos).getLogoResource()));
                            updateListViewCandidates();
                            tietCandidateName.setText("");
                            tietCandidateName.setHint("Add more candidates...");
                        }
                    }
                });

            }
        });

        imageButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCandidateActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Done");
                builder.setMessage("Are you sure you want to proceed?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddCandidateActivity.this, SetElectionDateActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        lvCandidates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                builder = new AlertDialog.Builder(AddCandidateActivity.this);
                View viewPopup = getLayoutInflater().inflate(R.layout.popup_add_candidate, null);
                builder.setView(viewPopup);
                dialog = builder.create();
                dialog.show();

                TextInputEditText tietCandidateName = viewPopup.findViewById(R.id.tietCandidateName);
                Spinner spinnerParty = viewPopup.findViewById(R.id.spinnerParty);
                Button buttonAdd = viewPopup.findViewById(R.id.buttonAddCandidate);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCandidateActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, parties);
                spinnerParty.setAdapter(arrayAdapter);

                tietCandidateName.setHint("Enter candidates name");
                tietCandidateName.setText(candidateModelArrayList.get(position).getCandidateName());
                buttonAdd.setText("Save Changes");
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tietCandidateName.getText().toString().isEmpty()){
                            tietCandidateName.setError("Please enter candidate name!");
                        }else{
                            pos = spinnerParty.getSelectedItemPosition();
                            candidateModelArrayList.get(position).setCandidateName(tietCandidateName.getText().toString());
                            candidateModelArrayList.get(position).setCandidateParty(logoModelArrayList.get(pos).getPartyName());
                            candidateModelArrayList.get(position).setPartyLogo(logoModelArrayList.get(pos).getLogoResource());
                            updateListViewCandidates();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

    }

    private void initPartyNames() {

        logoModelArrayList = getAllParties();
        parties = new String[logoModelArrayList.size()];
        for(int i=0; i<logoModelArrayList.size(); i++){
            parties[i] = logoModelArrayList.get(i).getPartyName();
        }

    }

    private void initUI() {

        lvCandidates = findViewById(R.id.listViewCandidates);
        fabAddCandidate = findViewById(R.id.fabAddCandidate);
        imageButtonDone = findViewById(R.id.imageButtonDone);
        linLayProBar = findViewById(R.id.linLayProBarTextAddCandidate);
        tvProgress = findViewById(R.id.textViewProgress);
        linLayProBar.setVisibility(View.INVISIBLE);
        candidateModelArrayList = new ArrayList<>();
        logoModelArrayList = new ArrayList<>();

    }

    private void updateListViewCandidates(){

        AddCandidateAdapter addCandidateAdapter = new AddCandidateAdapter(this,
                candidateModelArrayList,
                web3jHelper,
                candidateCount);
        lvCandidates.setAdapter(addCandidateAdapter);

    }

    public ArrayList<PartyLogoModel> getAllParties(){

        ArrayList<PartyLogoModel> partyLogoModelArrayList = new ArrayList<>();

        partyLogoModelArrayList.add(new PartyLogoModel("Iron Man", R.drawable.iron_man));
        partyLogoModelArrayList.add(new PartyLogoModel("Dr Strange", R.drawable.dr_strange));
        partyLogoModelArrayList.add(new PartyLogoModel("Captain America", R.drawable.captain_america));
        partyLogoModelArrayList.add(new PartyLogoModel("Captain Marvel", R.drawable.captain_marvel));
        partyLogoModelArrayList.add(new PartyLogoModel("Hulk", R.drawable.hulk));
        partyLogoModelArrayList.add(new PartyLogoModel("Black Panther", R.drawable.black_panther));
        partyLogoModelArrayList.add(new PartyLogoModel("Spider Man", R.drawable.spider_man));
        partyLogoModelArrayList.add(new PartyLogoModel("Ant Man", R.drawable.ant_man));


        return partyLogoModelArrayList;

    }

    public void displayProgressBar(String status) {
        linLayProBar.setVisibility(View.VISIBLE);
        tvProgress.setText(status);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgressBar(){
        linLayProBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void connection(Boolean isConnected) {
        if(isConnected){
            hideProgressBar();
            displayProgressBar("Reading uploaded candidates...");
            web3jHelper.getCandidateCount();
        }
    }

    @Override
    public void OnGetCandidateCountResult(int candidateCount) {

        if(candidateCount!=0){
            this.candidateCount = candidateCount;
            web3jHelper.getAllCandidates(candidateCount);
        }else{
            updateListViewCandidates();
            hideProgressBar();
        }

    }

    @Override
    public void OnGetAllCandidatesResult(ArrayList<Tuple5<BigInteger, String, String, BigInteger, BigInteger>> allCandidatesInfo) {

        candidateModelArrayList = new ArrayList<>();
        int cCount = allCandidatesInfo.size();
        candidateCount = cCount;
        for(int i=0; i<cCount; i++){
            candidateModelArrayList.add(new CandidateModel(
                    allCandidatesInfo.get(i).getValue2(),
                    allCandidatesInfo.get(i).getValue3(),
                    allCandidatesInfo.get(i).getValue4().intValue()));
        }
        hideProgressBar();
        updateListViewCandidates();

    }

    @Override
    public void OnAddCandidateResult(Boolean isAddedSuccessfully) {

        if(isAddedSuccessfully){
            candidateCount++;
            hideProgressBar();
            ivUpload.setBackgroundColor(Color.WHITE);
            ivUpload.setEnabled(false);
        }

    }
}

class PartyLogoModel {

    private String partyName;
    private int logoResource;

    public PartyLogoModel(String partyName, int logoResource) {
        this.partyName = partyName;
        this.logoResource = logoResource;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getLogoResource() {
        return logoResource;
    }
}

class CandidateModel {

    private String candidateName;
    private String candidateParty;
    private int partyLogo;

    public CandidateModel(String candidateName, String candidateParty, int partyLogo) {
        this.candidateName = candidateName;
        this.candidateParty = candidateParty;
        this.partyLogo = partyLogo;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getCandidateParty() {
        return candidateParty;
    }

    public int getPartyLogo() {
        return partyLogo;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setCandidateParty(String candidateParty) {
        this.candidateParty = candidateParty;
    }

    public void setPartyLogo(int partyLogo) {
        this.partyLogo = partyLogo;
    }
}

class AddCandidateAdapter extends ArrayAdapter<CandidateModel>{

    private AddCandidateActivity addCandidateActivity;
    private ArrayList<CandidateModel> candidateModelArrayList;
    private Web3jHelper web3jHelper;
    private int candidateCount;

    public AddCandidateAdapter(@NonNull AddCandidateActivity addCandidateActivity,
                               ArrayList<CandidateModel> candidateModelArrayList,
                               Web3jHelper web3jHelper,
                               int candidateCount) {
        super((Context)addCandidateActivity, 0, candidateModelArrayList);
        this.addCandidateActivity = addCandidateActivity;
        this.candidateModelArrayList = candidateModelArrayList;
        this.web3jHelper = web3jHelper;
        this.candidateCount = candidateCount;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View addCandidateListView = convertView;
        final LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(addCandidateListView == null)
            addCandidateListView = layoutInflater.inflate(R.layout.listview_add_candidate_activity, parent, false);

        final CandidateModel currentPos = getItem(position);

        TextView tvCandidateName = addCandidateListView.findViewById(R.id.textViewCandidate);
        TextView tvPartyName = addCandidateListView.findViewById(R.id.textViewPartyName);
        ImageView ivLogo = addCandidateListView.findViewById(R.id.imageViewLogo);
        ImageView ivUpload = addCandidateListView.findViewById(R.id.imageViewUpload);

        tvCandidateName.setText(currentPos.getCandidateName());
        tvPartyName.setText(currentPos.getCandidateParty());

        ivLogo.setImageResource(currentPos.getPartyLogo());

        if(candidateCount!=0){
            candidateCount--;
            ivUpload.setBackgroundColor(Color.WHITE);
            ivUpload.setEnabled(false);
        }else{
            ivUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCandidateActivity.displayProgressBar("Adding candidate onto Blockchain...");
                    addCandidateActivity.ivUpload = ivUpload;
                    web3jHelper.addCandidate(currentPos.getCandidateName(),
                            currentPos.getPartyLogo(),
                            currentPos.getCandidateParty());
                }
            });
        }

        return addCandidateListView;
    }
}
