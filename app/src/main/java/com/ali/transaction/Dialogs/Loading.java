package com.ali.transaction.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;

import com.ali.transaction.R;

import java.util.Objects;

public class Loading {
    private static ProgressDialog progressDialog;

    public static void progressDialogConstructor(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    public static void showProgressDialog() {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        Objects.requireNonNull(progressDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
