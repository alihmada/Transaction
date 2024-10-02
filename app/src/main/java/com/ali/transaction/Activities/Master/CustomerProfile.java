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
import com.ali.transaction.MVVM.CustomerViewModel;
import com.ali.transaction.R;

import java.util.Objects;

public class CustomerProfile extends AppCompatActivity {

    private CustomerViewModel model;
    private ConstraintLayout editCustomerName;
    private TextView character, name, take, give;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        getExtra();
        setupViewModel();
        initializeViews();
        setTextViews();
        initializeButtons();
        setupListeners();
    }

    private void getExtra() {
        id = Objects.requireNonNull(getIntent().getExtras()).getString(Common.ID);
    }

    private void initializeViews() {
        editCustomerName = findViewById(R.id.edit_name_of_customer);

        character = findViewById(R.id.character);
        name = findViewById(R.id.customer_name);
        take = findViewById(R.id.customer_take);
        give = findViewById(R.id.customer_give);
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.delete).setOnClickListener(view -> {
            Confirmation confirmation = new Confirmation(R.drawable.wrong, getString(R.string.delete_operation), () -> {
                Firebase.deleteCustomer(id);
                goToMainScreen();
            });
            confirmation.show(getSupportFragmentManager(), "");
        });
    }

    private void goToMainScreen(){
        Intent intent = new Intent(this, Main.class);
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
                    text -> Firebase.updateCustomerName(id, text));

            dialog.show(getSupportFragmentManager(), null);
        });
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(CustomerViewModel.class);
        model.initialize(id);
    }

    private void setTextViews() {
        model.getCustomer().observe(this, customer -> {
            if (customer != null) {
                character.setText(customer.getName().substring(0, 1));
                name.setText(customer.getName());
                take.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(customer.getTake())));
                take.setTextColor(getColor(R.color.green));
                give.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(customer.getGive())));
                give.setTextColor(getColor(R.color.red));
            }
        });
    }
}