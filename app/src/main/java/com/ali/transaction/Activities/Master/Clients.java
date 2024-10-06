package com.ali.transaction.Activities.Master;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.ali.transaction.Adapters.ClientsAdapter;
import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.FirstItemMarginDecoration;
import com.ali.transaction.Classes.Internet;
import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Dialogs.Confirmation;
import com.ali.transaction.Dialogs.ValueDialog;
import com.ali.transaction.Interfaces.ViewOnClickListener;
import com.ali.transaction.MVVM.ClientsViewModel;
import com.ali.transaction.Models.Client;
import com.ali.transaction.R;

import java.util.Collections;
import java.util.List;

public class Clients extends AppCompatActivity implements ViewOnClickListener {

    private Bundle bundle;
    private EditText search;
    private List<Client> clients;
    private ClientsAdapter adapter;
    private ClientsViewModel model;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ConstraintLayout alert;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        initializeViews();
        initializeButtons();
        setupSearch();
        setupSwipeRefreshLayout();
        setupViewModel();
        setupView();
    }

    private void initializeViews() {
        bundle = new Bundle();

        alert = findViewById(R.id.alert);
        imageView = findViewById(R.id.alert_image);
        textView = findViewById(R.id.alert_text);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.person_recycler);
        recyclerView.addItemDecoration(new FirstItemMarginDecoration(getResources().getDimensionPixelSize(R.dimen.margin)));
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> {
            Confirmation confirmation = new Confirmation(R.drawable.logout, getString(R.string.exit), this::finish);
            confirmation.show(getSupportFragmentManager(), "");
        });
        findViewById(R.id.filter).setOnClickListener(view -> {
            if (clients != null) {
                Collections.reverse(clients);
                setupRecyclerViewData(clients);
            }
        });
        findViewById(R.id.record).setOnClickListener(view -> {
            ValueDialog dialog = new ValueDialog(R.string.full_name, ValueDialog.InputType.TEXT, Common.NOT_EMPTY_REGEX,
                    (text) -> Firebase.addClient(Client.getInstance(text)));
            dialog.show(getSupportFragmentManager(), null);
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
        model.getClients().observe(this, clients -> {
            progressBar.setVisibility(View.GONE);

            if (clients == null || clients.isEmpty()) {
                handleEmptyClients();
            } else {
                search.setEnabled(true);
                setupRecyclerViewData(clients);
                alert.setVisibility(View.GONE);
            }
        });
    }

    private void handleEmptyClients() {
        search.setEnabled(false);
        recyclerView.setAdapter(null);
        alert.setVisibility(View.VISIBLE);
        textView.setText(getString(R.string.data_not_found));
    }

    private void setupRecyclerViewData(List<Client> clients) {
        this.clients = clients;
        adapter = new ClientsAdapter(clients, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(ClientsViewModel.class);
        model.initialize();
    }

    @Override
    public void onClickListener(String id) {
        Intent intent = new Intent(this, ClientJob.class);
        bundle.putString(Common.CLIENT_ID, id);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}