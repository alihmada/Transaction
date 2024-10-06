package com.ali.transaction.Activities.Master;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ali.transaction.Classes.Calculation;
import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.DateAndTime;
import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Dialogs.Confirmation;
import com.ali.transaction.Dialogs.ValueDialog;
import com.ali.transaction.MVVM.SelectedItemViewModel;
import com.ali.transaction.Models.JobItem;
import com.ali.transaction.R;

import java.util.Objects;

public class MoreItemInfo extends AppCompatActivity {

    private JobItem item;
    private SelectedItemViewModel model;
    private String clientID, jobID, itemID;
    private TextView date, label, reason, balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_item_info);

        startPoint();
    }

    private void startPoint() {
        getExtras();
        setupTextView();
        initializeButtons();
        setupViewModel();
        setupViewsData();
    }

    private void getExtras() {
        clientID = Objects.requireNonNull(getIntent().getExtras()).getString(Common.CLIENT_ID);
        jobID = Objects.requireNonNull(getIntent().getExtras()).getString(Common.JOB_ID);
        itemID = Objects.requireNonNull(getIntent().getExtras()).getString(Common.ITEM_ID);
    }

    private void setupTextView() {
        date = findViewById(R.id.date_time);
        label = findViewById(R.id.label);
        balance = findViewById(R.id.balance);
        reason = findViewById(R.id.reason);
    }

    private void initializeButtons() {
        findViewById(R.id.back).setOnClickListener(view -> finish());

        findViewById(R.id.delete).setOnClickListener(view -> handleDeleteButton());

        findViewById(R.id.edit_balance).setOnClickListener(view -> handleEditButton(
                R.string.price,
                ValueDialog.InputType.DECIMAL,
                Common.DECIMAL_REGEX,
                text -> {
//                    item.setDate(DateAndTime.getCurrentDateTime());
                    item.setBalance(Double.parseDouble(text));
                    Firebase.editItem(clientID, jobID, itemID, item);
                }
        ));

        findViewById(R.id.edit_reason).setOnClickListener(view -> handleEditButton(
                R.string.reason,
                ValueDialog.InputType.TEXT,
                "",
                text -> {
//                    item.setDate(DateAndTime.getCurrentDateTime());
                    item.setReason(text);
                    Firebase.editItem(clientID, jobID, itemID, item);
                }
        ));
    }

    private void handleDeleteButton() {
        Confirmation confirmation = new Confirmation(R.drawable.wrong, getString(R.string.delete_operation), () -> {
            Firebase.deleteItem(clientID, jobID, itemID);
            finish();
        });
        confirmation.show(getSupportFragmentManager(), "");
    }

    private void handleEditButton(int titleResId, ValueDialog.InputType inputType, String regex, ValueDialog.OnValueSetListener onValueSetListener) {
        ValueDialog dialog = new ValueDialog(titleResId, inputType, regex, onValueSetListener::onValueSet);
        dialog.show(getSupportFragmentManager(), "");
    }

    private void setupViewModel() {
        model = new ViewModelProvider(this).get(SelectedItemViewModel.class);
        model.initialize(clientID, jobID, itemID);
    }

    private void setupViewsData() {
        model.getItem().observe(this, item -> {
            if (item != null) {
                this.item = item;

                date.setText(DateAndTime.getDate(this, item.getDate()));
                label.setText(item.getType() == JobItem.Type.TAKE ? getString(R.string.take) : getString(R.string.give));
                balance.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(item.getBalance())));
                balance.setTextColor(item.getType() == JobItem.Type.TAKE ? getColor(R.color.green) : getColor(R.color.red));
                reason.setText(item.getReason());
            }
        });
    }
}