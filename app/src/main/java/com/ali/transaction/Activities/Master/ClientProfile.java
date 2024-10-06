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
import com.ali.transaction.MVVM.ClientViewModel;
import com.ali.transaction.R;

import java.util.Objects;

public class ClientProfile extends AppCompatActivity {

    private ClientViewModel model;
    private ConstraintLayout editCustomerName;
    private TextView name, take, give;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        getExtra();
        setupViewModel();
        initializeViews();
        setTextViews();
        initializeButtons();
        setupListeners();
    }

    private void getExtra() {
        id = Objects.requireNonNull(getIntent().getExtras()).getString(Common.CLIENT_ID);
    }

    private void initializeViews() {
        editCustomerName = findViewById(R.id.edit_name_of_customer);

        name = findViewById(R.id.customer_name);
        take = findViewById(R.id.customer_take);
        give = findViewById(R.id.customer_give);
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.delete).setOnClickListener(view -> {
            Confirmation confirmation = new Confirmation(R.drawable.wrong, getString(R.string.delete_operation), () -> {
                Firebase.deleteClient(id);
                goToMainScreen();
            });
            confirmation.show(getSupportFragmentManager(), "");
        });
    }

    private void goToMainScreen() {
        Intent intent = new Intent(this, Clients.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setupListeners() {
        editCustomerName.setOnClickListener(view -> {
            ValueDialog dialog = new ValueDialog(
                    R.string.name_of_customer,
                    ValueDialog.InputType.TEXT,
                    Common.NOT_EMPTY_REGEX,
                    text -> Firebase.updateClientName(id, text));

            dialog.show(getSupportFragmentManager(), null);
        });
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(ClientViewModel.class);
        model.initialize(id);
    }

    private void setTextViews() {
        model.getClient().observe(this, client -> {
            if (client != null) {
                name.setText(client.getName());
                take.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(client.getTake())));
                take.setTextColor(getColor(R.color.green));
                give.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(client.getGive())));
                give.setTextColor(getColor(R.color.red));
            }
        });
    }
}