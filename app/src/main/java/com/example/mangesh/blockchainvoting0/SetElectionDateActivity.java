package com.example.mangesh.blockchainvoting0;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.web3j.tuples.generated.Tuple5;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class SetElectionDateActivity extends AppCompatActivity implements Web3jHelper.iWeb3jConnection, Web3jHelper.iSetElectionDates{

    private TextInputEditText tietStartDate;
    private TextInputEditText tietEndDate;
    private ImageView ivStartDate;
    private ImageView ivEndDate;
    private Button buttonConfirmDates;
    public String startD = "";
    private String endD = "";
    private SimpleDateFormat formatter6;
    private Web3jHelper web3jHelper;
    private LinearLayout linLayProBar;
    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_election_date);

        initUI();
        formatter6=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        displayProgressBar("Connecting to Blockchain...");
        web3jHelper = new Web3jHelper(SetElectionDateActivity.this, "SetElectionDateActivity");

        ivStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tietStartDate.setText("");
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SetElectionDateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(SetElectionDateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                Date date = new Date(year-1900, month, dayOfMonth, hourOfDay, minute);

                                startD = formatter6.format(date);
                                if(isValidDate(startD, false)){
                                    tietStartDate.setText(startD);
                                    tietStartDate.setError(null);
                                    tietEndDate.requestFocus();
                                }
                                else
                                    Toast.makeText(SetElectionDateActivity.this, "Invalid Date!!!", Toast.LENGTH_SHORT).show();

                            }
                        }, mHour, mMinute, false);
                        timePickerDialog.show();

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        ivEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tietEndDate.setText("");
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SetElectionDateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(SetElectionDateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                Date date = new Date(year-1900, month, dayOfMonth, hourOfDay, minute);
                                endD = formatter6.format(date);

                                if(isValidDate(endD, true)){
                                    tietEndDate.setText(endD);
                                    tietEndDate.setError(null);
                                }
                                else
                                    Toast.makeText(SetElectionDateActivity.this, "Invalid Date!!!", Toast.LENGTH_SHORT).show();

                            }
                        }, mHour, mMinute, false);
                        timePickerDialog.show();

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        buttonConfirmDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidFields()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(SetElectionDateActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Are you sure you want to proceed?");
                    builder.setMessage("Start Date : "+tietStartDate.getText().toString()+"\n  " +
                            "End Date : "+tietEndDate.getText().toString());
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            displayProgressBar("Setting election dates...");
                            web3jHelper.setElectionDates(tietStartDate.getText().toString(), tietEndDate.getText().toString());
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

            }
        });

    }

    private void initUI() {

        tietStartDate = findViewById(R.id.tietStartDate);
        tietStartDate.requestFocus();
        tietEndDate = findViewById(R.id.tietEndDate);
        ivStartDate = findViewById(R.id.imageViewStartDate);
        ivEndDate = findViewById(R.id.imageViewEndDate);
        buttonConfirmDates = findViewById(R.id.buttonConfirmDates);
        linLayProBar = findViewById(R.id.linLayProBarTextSetDate);
        linLayProBar.setVisibility(View.INVISIBLE);
        tvProgress = findViewById(R.id.textViewProgress);

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

    private Boolean isValidDate(String startDate, Boolean isEndDate) {

        Date date = null;
        try {
            date = formatter6.parse(startDate);

            Date todayD = new Date();

            if(isEndDate){
                if(date.before(todayD)){
                    tietEndDate.requestFocus();
                    tietEndDate.setError("End date cannot be before Today!!!");
                    return false;
                }
                if(tietStartDate.getText().toString().isEmpty()){
                    tietStartDate.requestFocus();
                    tietStartDate.setError("Please set Start date before End date!!!");
                    return false;
                }
                startD = tietStartDate.getText().toString();
                Date d = formatter6.parse(startD);
                d.setHours(d.getHours()+2);
                if(date.before(d)){
                    tietEndDate.requestFocus();
                    tietEndDate.setError("End date cannot be before Start Date!!!(At least 2Hrs difference)");
                    return false;
                }
            }else{
                if(date.before(todayD)){
                    tietStartDate.requestFocus();
                    tietStartDate.setError("Start date cannot be before Today!!!");
                    return false;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();

            if(isEndDate){
                tietEndDate.requestFocus();
                tietEndDate.setError("Please enter Valid date!!!\nPattern : dd-MMM-yyyy HH:mm:ss");
                return false;
            }
            tietStartDate.requestFocus();
            tietStartDate.setError("Please enter Valid date!!!\nPattern : dd-MMM-yyyy HH:mm:ss");
            return false;
        }


        return true;
    }

    private Boolean isValidFields(){

        if(tietStartDate.getText().toString().isEmpty()){
            tietStartDate.requestFocus();
            tietStartDate.setError("Please select some Start date!!!");
            return false;
        }
        if(tietEndDate.getText().toString().isEmpty()){
            tietEndDate.requestFocus();
            tietEndDate.setError("Please select some End date!!!");
            return false;
        }
        if(!isValidDate(tietStartDate.getText().toString(), false))
            return false;
        if(!isValidDate(tietEndDate.getText().toString(), true))
            return false;

        return true;
    }

    @Override
    public void connection(Boolean isConnected) {
        hideProgressBar();
    }

    @Override
    public void OnSetElectionDatesResult(Boolean isDateSetSuccessful) {
        hideProgressBar();
        if(isDateSetSuccessful){
            Intent intent = new Intent(SetElectionDateActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(SetElectionDateActivity.this, "Error setting dates!!!", Toast.LENGTH_SHORT).show();
        }
    }
}


