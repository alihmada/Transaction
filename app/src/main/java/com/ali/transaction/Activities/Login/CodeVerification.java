package com.ali.transaction.Activities.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.Dialogs.Loading;
import com.ali.transaction.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CodeVerification extends AppCompatActivity {

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken token;
    EditText[] verificationDigits;
    String verificationId;
    FirebaseAuth mAuth;
    Button otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        Loading.progressDialogConstructor(this);

        otp = findViewById(R.id.otp);

        verificationDigits = new EditText[]{findViewById(R.id.phone_verification_1st_digit), findViewById(R.id.phone_verification_2nd_digit), findViewById(R.id.phone_verification_3rd_digit), findViewById(R.id.phone_verification_4th_digit), findViewById(R.id.phone_verification_5th_digit), findViewById(R.id.phone_verification_6th_digit)};

        setupTextWatchers();

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ar");

        String phone = Objects.requireNonNull(getIntent().getExtras()).getString(Common.PHONE_NUMBER);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    codeVerification(code);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException error) {
//                ConfirmationDialog dialog = new ConfirmationDialog(error.getLocalizedMessage(), new ConfirmationDialog.ConfirmationDialogListener() {
//                    @Override
//                    public void onConfirm() {
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//
//                dialog.show(getSupportFragmentManager(), "");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                CodeVerification.this.verificationId = verificationId;
                CodeVerification.this.token = token;
            }
        };

        assert phone != null;
        if (!phone.isEmpty()) {
            sendVerificationCode(phone);
        }

        otp.setOnClickListener(v -> {
            StringBuilder code = new StringBuilder();
            for (EditText editText : verificationDigits) {
                code.append(editText.getText());
            }
            if (!code.toString().isEmpty()) {
                codeVerification(code.toString());
                Loading.showProgressDialog();
            }
        });
    }

    private void setupTextWatchers() {
        for (int i = 0; i < verificationDigits.length; i++) {
            final int currentPosition = i;
            verificationDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && currentPosition < verificationDigits.length - 1) {
                        setFocusable(verificationDigits[currentPosition + 1]);
                    } else if (count == 0 && currentPosition > 0) {
                        setFocusable(verificationDigits[currentPosition - 1]);
                        setSelectAll(verificationDigits[currentPosition - 1]);
                    } else if (currentPosition == verificationDigits.length - 1) {
                        otp.performClick();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private void setFocusable(EditText editText) {
        editText.requestFocus();
    }

    private void setSelectAll(EditText editText) {
        editText.selectAll();
    }

    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(getBaseContext(), ID.class);
                startActivity(intent);
                finish();
                Loading.dismissProgressDialog();
            } else {
                for (EditText editText : verificationDigits) {
                    editText.setBackground(AppCompatResources.getDrawable(this, R.drawable.error_p));
                }
            }
        });
    }

    private void codeVerification(String code) {
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, code));
    }
}