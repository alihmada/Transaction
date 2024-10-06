package com.ali.transaction.Activities.Master;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.ali.transaction.Classes.Calculation;
import com.ali.transaction.Classes.Common;
import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Dialogs.Confirmation;
import com.ali.transaction.Dialogs.ValueDialog;
import com.ali.transaction.MVVM.JobViewModel;
import com.ali.transaction.R;

import java.util.Objects;

public class JobProfile extends AppCompatActivity {

    private JobViewModel model;
    private ConstraintLayout editJobName;
    private TextView name, take, give;
    private String clientID, jobID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_profile);

        getExtra();
        setupViewModel();
        initializeViews();
        setTextViews();
        initializeButtons();
        setupListeners();
    }

    private void getExtra() {
        clientID = Objects.requireNonNull(getIntent().getExtras()).getString(Common.CLIENT_ID);
        jobID = Objects.requireNonNull(getIntent().getExtras()).getString(Common.JOB_ID);
    }

    private void initializeViews() {
        editJobName = findViewById(R.id.edit_name_of_job);

        name = findViewById(R.id.job_name);
        take = findViewById(R.id.job_take);
        give = findViewById(R.id.job_give);
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.delete).setOnClickListener(view -> {
            Confirmation confirmation = new Confirmation(R.drawable.wrong, getString(R.string.delete_operation), () -> {
                Firebase.deleteJob(clientID, jobID);
                goToJobsScreen();
            });
            confirmation.show(getSupportFragmentManager(), "");
        });
    }

    private void goToJobsScreen() {
        Intent intent = new Intent(this, ClientJob.class);
        intent.putExtra(Common.CLIENT_ID, this.clientID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setupListeners() {
        editJobName.setOnClickListener(view -> {
            ValueDialog dialog = new ValueDialog(
                    R.string.job_name,
                    ValueDialog.InputType.TEXT,
                    Common.NOT_EMPTY_REGEX,
                    text -> Firebase.updateJobName(clientID, jobID, text));

            dialog.show(getSupportFragmentManager(), null);
        });
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(JobViewModel.class);
        model.initialize(clientID, jobID);
    }

    private void setTextViews() {
        model.getJob().observe(this, job -> {
            if (job != null) {
                name.setText(job.getName());
                take.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(job.getTake())));
                take.setTextColor(getColor(R.color.green));
                give.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(job.getGive())));
                give.setTextColor(getColor(R.color.red));
            }
        });
    }
}