package com.ali.transaction.Activities.Master;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ali.transaction.Adapters.JobsAdapter;
import com.ali.transaction.Classes.Calculation;
import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.FirstItemMarginDecoration;
import com.ali.transaction.Classes.Internet;
import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Dialogs.ValueDialog;
import com.ali.transaction.Interfaces.ViewOnClickListener;
import com.ali.transaction.MVVM.ClientWithJobsViewModel;
import com.ali.transaction.Models.Client;
import com.ali.transaction.Models.ClientJobModel;
import com.ali.transaction.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClientJob extends AppCompatActivity implements ViewOnClickListener {

    private String clientID;
    private Bundle bundle;
    private EditText search;
    private List<ClientJobModel> clientJobs;
    private JobsAdapter adapter;
    private ClientWithJobsViewModel model;
    private TextView name, take, give, balance, active;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ConstraintLayout alert;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_job);

        getExtra();
        initializeViews();
        initializeButtons();
        setupHeader();
        setupSearch();
        setupSwipeRefreshLayout();
        setupViewModel();
        setupView();
    }

    private void initializeViews() {
        bundle = new Bundle();
        clientJobs = new ArrayList<>();

        name = findViewById(R.id.name);
        take = findViewById(R.id.take);
        give = findViewById(R.id.give);
        balance = findViewById(R.id.balance);
        active = findViewById(R.id.active);

        alert = findViewById(R.id.alert);
        imageView = findViewById(R.id.alert_image);
        textView = findViewById(R.id.alert_text);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.jobs_recycler);
        recyclerView.addItemDecoration(new FirstItemMarginDecoration(getResources().getDimensionPixelSize(R.dimen.margin)));
    }

    private void getExtra() {
        clientID = Objects.requireNonNull(getIntent().getExtras()).getString(Common.CLIENT_ID);
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.filter).setOnClickListener(view -> {
            if (clientJobs != null) {
                Collections.reverse(clientJobs);
                setupRecyclerViewData(clientJobs);
            }
        });
        findViewById(R.id.record).setOnClickListener(view -> {
            ValueDialog dialog = new ValueDialog(R.string.job_name, ValueDialog.InputType.TEXT, Common.NOT_EMPTY_REGEX,
                    text -> Firebase.addJob(clientID, ClientJobModel.getInstance(text)));
            dialog.show(getSupportFragmentManager(), null);
        });
    }

    private void setupHeader() {
        findViewById(R.id.header).setOnClickListener(v -> {
            Intent intent = new Intent(this, ClientProfile.class);
            bundle.putString(Common.CLIENT_ID, clientID);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void setupSearch() {
        search = findViewById(R.id.search_view);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }
        });
    }

    private void setupView() {
        if (Internet.isConnectedWithoutMessage(this)) {
            alert.setVisibility(View.GONE);
            if (Internet.isNetworkLimited(this)) {
                setupWifi(getString(R.string.internet_limited));
            } else {
                setRecyclerView();
            }
        } else {
            setupWifi(getString(R.string.no_internet));
        }
    }

    private void setupWifi(String message) {
        alert.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        imageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.wifi_off));
        recyclerView.setAdapter(null);
        textView.setText(message);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setupView();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setRecyclerView() {
        model.getClientsWithJobs().observe(this, data -> {
            progressBar.setVisibility(View.GONE);

            this.clientJobs = data.second;
            setupUserData(data.first);

            if (data.second.isEmpty()) {
                handleEmptyJobs();
            } else {
                handleNonEmptyJobs();
            }
        });
    }

    private void setupUserData(Client client) {
        name.setText(client.getName());

        give.setText(formatCurrency(client.getGive()));
        take.setText(formatCurrency(client.getTake()));

        Pair<String, Boolean> balance = Calculation.getBalance(client.getTake(), client.getGive());
        this.balance.setText(String.format("%s ج.م", balance.first));
        this.balance.setTextColor(balance.second ? getColor(R.color.green) : getColor(R.color.red));

        active.setText(String.format("(%s ,نشط)", clientJobs.size()));
    }

    private void handleEmptyJobs() {
        search.setEnabled(false);
        recyclerView.setAdapter(null);
        alert.setVisibility(View.VISIBLE);
        textView.setText(getString(R.string.data_not_found));
    }

    private void handleNonEmptyJobs() {
        search.setEnabled(true);
        alert.setVisibility(View.GONE);
        setupRecyclerViewData(clientJobs);
    }

    private void setupRecyclerViewData(List<ClientJobModel> clientJobs) {
        adapter = new JobsAdapter(clientJobs, this);
        recyclerView.setAdapter(adapter);
    }

    private String formatCurrency(double value) {
        return String.format("%s ج.م", Calculation.formatNumberWithCommas(value));
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(ClientWithJobsViewModel.class);
        model.initialize(clientID);
    }

    @Override
    public void onClickListener(String id) {
        Intent intent = new Intent(this, JobItems.class);
        bundle.putString(Common.CLIENT_ID, this.clientID);
        bundle.putString(Common.JOB_ID, id);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}