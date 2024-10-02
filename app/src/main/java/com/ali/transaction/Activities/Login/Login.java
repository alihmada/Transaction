package com.ali.transaction.Activities.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.R;
import com.hbb20.CountryCodePicker;

public class Login extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    ConstraintLayout inputText;
    Bundle bundle;
    boolean isError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bundle = new Bundle();

        inputText = findViewById(R.id.constraintLayout2);

        View view4 = findViewById(R.id.view4);

        countryCodePicker = findViewById(R.id.login_countryCodePicker);

        Button login = findViewById(R.id.login);
        EditText phone = findViewById(R.id.mobile_text);

        phone.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus) {
                if (!isError) {
                    changeBackground(R.drawable.stroke_with_2dp_width);
                    view4.setBackgroundColor(getPrimaryColor());
                } else {
                    changeBackground(R.drawable.red_stroke_with_2dp_width);
                    view4.setBackgroundColor(getColor(R.color.red));
                }
            } else {
                changeBackground(R.drawable.input_filed);
                view4.setBackgroundColor(getColor(R.color.red));
            }
        });

        login.setOnClickListener(view -> {
            String phoneNo = String.valueOf(phone.getText());
            if (countryCodePicker.getSelectedCountryNameCode().equals("EG")) {
                if (phoneNo.matches("^01[0125][0-9]{8}$")) {
                    Intent intent = new Intent(this, CodeVerification.class);
                    bundle.putString(Common.PHONE_NUMBER, normalizePhoneNumber(phoneNo));
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    isError = true;
                    Toast.makeText(this, getString(R.string.invalid_phone_no), Toast.LENGTH_LONG).show();
                    changeBackground(R.drawable.red_stroke_with_1dp_width);
                    view4.setBackgroundColor(getColor(R.color.red));
                }
            } else {
                isError = true;
                Toast.makeText(this, getString(R.string.invalid_country_code), Toast.LENGTH_LONG).show();
                changeBackground(R.drawable.red_stroke_with_1dp_width);
                view4.setBackgroundColor(getColor(R.color.red));
            }
        });
    }

    private void changeBackground(int stroke) {
        inputText.setBackground(AppCompatResources.getDrawable(this, stroke));
    }

    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private String normalizePhoneNumber(String phone) {
        if (phone.matches("^1[0125][0-9]{8}$")) {
            return String.format("%s%s", countryCodePicker.getSelectedCountryCodeWithPlus(), phone);
        } else if (phone.matches("^01[0125][0-9]{8}$")) {
            return String.format("%s%s", countryCodePicker.getSelectedCountryCodeWithPlus(), phone.substring(1));
        }
        return null;
    }
}