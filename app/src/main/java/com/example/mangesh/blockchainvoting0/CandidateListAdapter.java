package com.example.mangesh.blockchainvoting0;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CandidateListAdapter extends ArrayAdapter<CandidateListModel> {

    private Context context;
    private ArrayList<CandidateListModel> allCandidates;
    private RadioButton currentlyChecked;
    private int currentPosition;

    public CandidateListAdapter(@NonNull Context context, @NonNull ArrayList<CandidateListModel> allCandidates) {
        super(context, 0, allCandidates);
        this.context = context;
        this.allCandidates = allCandidates;
        currentPosition = -1;

    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View candidateListView = convertView;
        final LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(candidateListView == null)
            candidateListView = layoutInflater.inflate(R.layout.listview_candidates_voter_screen, parent, false);

        final CandidateListModel currentPos = getItem(position);

        RadioButton rbCandidate = candidateListView.findViewById(R.id.radioButton);
        TextView tvCandidateName = candidateListView.findViewById(R.id.textViewCandidate);
        TextView tvPartyName = candidateListView.findViewById(R.id.textViewPartyName);
        ImageView ivLogo = candidateListView.findViewById(R.id.imageViewLogo);

        if(position == 0)
            currentlyChecked = rbCandidate;

        tvCandidateName.setText(currentPos.getCandidateName());
        tvPartyName.setText(currentPos.getPartyName());
        rbCandidate.setChecked(false);
        if(currentPos.getLogo() == 0)
            ivLogo.setImageResource(R.drawable.iron_man);
        else
            ivLogo.setImageResource(currentPos.getLogo());
        ivLogo.setEnabled(true);

        rbCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rbCandidate.isChecked()){
                    rbCandidate.setChecked(true);
                    if(!currentlyChecked.equals(rbCandidate)) {
                        currentlyChecked.setChecked(false);
                        currentlyChecked = rbCandidate;
                    }
                    currentPosition = position;
                }
                else{
                    rbCandidate.setChecked(false);
                }

            }
        });

        return candidateListView;

    }
}
