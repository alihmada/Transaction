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

import com.ali.transaction.Adapters.ItemsAdapter;
import com.ali.transaction.Classes.Calculation;
import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.FirstItemMarginDecoration;
import com.ali.transaction.Classes.Internet;
import com.ali.transaction.Classes.UniqueIdGenerator;
import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Dialogs.ItemDialog;
import com.ali.transaction.Interfaces.ViewOnClickListener;
import com.ali.transaction.MVVM.CustomerWithItemsViewModel;
import com.ali.transaction.Models.Customer;
import com.ali.transaction.Models.Item;
import com.ali.transaction.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomerData extends AppCompatActivity implements ViewOnClickListener {

    private String id;
    private Bundle bundle;
    private EditText search;
    private List<Item> items;
    private ItemsAdapter adapter;
    private CustomerWithItemsViewModel model;
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
        setContentView(R.layout.activity_customer_data);

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
        items = new ArrayList<>();

        name = findViewById(R.id.name);
        take = findViewById(R.id.take);
        give = findViewById(R.id.give);
        balance = findViewById(R.id.balance);
        active = findViewById(R.id.active);

        alert = findViewById(R.id.alert);
        imageView = findViewById(R.id.alert_image);
        textView = findViewById(R.id.alert_text);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.items_recycler);
        recyclerView.addItemDecoration(new FirstItemMarginDecoration(getResources().getDimensionPixelSize(R.dimen.margin)));
    }

    private void getExtra() {
        id = Objects.requireNonNull(getIntent().getExtras()).getString(Common.ID);
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.filter).setOnClickListener(view -> {
            if (items != null) {
                Collections.reverse(items);
                setupRecyclerViewData(items);
            }
        });
        findViewById(R.id.record).setOnClickListener(view -> {
            ItemDialog dialog = new ItemDialog((type, date, reason, price) -> {
                Firebase.addItem(id, new Item(date, UniqueIdGenerator.generateUniqueId(), type, reason, price));
                if (type == Item.Type.TAKE) {
                    Firebase.updateTakeValue(id, price);
                } else {
                    Firebase.updateGiveValue(id, price);
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        });
    }

    private void setupHeader() {
        findViewById(R.id.header).setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerProfile.class);
            bundle.putString(Common.ID, id);
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
        model.getCustomersWithItems().observe(this, items -> {
            progressBar.setVisibility(View.GONE);

            this.items = items.second;
            setupUserData(items.first);

            if (items.second.isEmpty()) {
                handleEmptyItems();
            } else {
                handleNonEmptyItems();
            }
        });
    }

    private void setupUserData(Customer person) {
        name.setText(person.getName());

        give.setText(formatCurrency(person.getGive()));
        take.setText(formatCurrency(person.getTake()));

        Pair<String, Boolean> balance = Calculation.getBalance(person.getTake(), person.getGive());
        this.balance.setText(String.format("%s ج.م", balance.first));
        this.balance.setTextColor(balance.second ? getColor(R.color.green) : getColor(R.color.red));

        active.setText(String.format("(%s ,نشط)", items.size()));
    }

    private void handleEmptyItems() {
        search.setEnabled(false);
        recyclerView.setAdapter(null);
        alert.setVisibility(View.VISIBLE);
        textView.setText(getString(R.string.data_not_found));
    }

    private void handleNonEmptyItems() {
        search.setEnabled(true);
        alert.setVisibility(View.GONE);
        setupRecyclerViewData(items);
    }

    private void setupRecyclerViewData(List<Item> items) {
        adapter = new ItemsAdapter(items, this);
        recyclerView.setAdapter(adapter);
    }

    private String formatCurrency(double value) {
        return String.format("%s ج.م", Calculation.formatNumberWithCommas(value));
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(CustomerWithItemsViewModel.class);
        model.initialize(id);
    }

    @Override
    public void onClickListener(String id) {
        Intent intent = new Intent(this, MoreItemInfo.class);
        bundle.putString(Common.PARENT_ID, this.id);
        bundle.putString(Common.ITEM_ID, id);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}