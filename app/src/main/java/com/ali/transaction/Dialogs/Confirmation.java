package com.ali.transaction.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Confirmation extends BottomSheetDialogFragment {

    private String message;
    private int image;
    private OnOkCLickedListener listener;

    public Confirmation() {
    }

    public Confirmation(int image, String message, OnOkCLickedListener listener) {
        this.listener = listener;
        this.image = image;
        this.message = message;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmation_dialog, container, false);
        initializeView(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (message == null && image == 0 && savedInstanceState != null) {
            message = savedInstanceState.getString(Common.MESSAGE);
            image = savedInstanceState.getInt(Common.IMAGE);
        }

        Window window = dialog.getWindow();

        if (window != null) {
            window.setWindowAnimations(R.style.dialog_animation);
        }

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Common.MESSAGE, message);
        outState.putInt(Common.IMAGE, image);
    }

    private void initializeView(View view) {
        if (image != 0 && message != null) {
            ImageView image = view.findViewById(R.id.alert_shape);
            image.setImageResource(this.image);
            TextView message = view.findViewById(R.id.alert_message);
            message.setText(this.message);
        }

        Button ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(view1 -> {
            if (listener != null)
                listener.onOkClicked();
            dismiss();
        });

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(view1 -> dismiss());

    }

    public interface OnOkCLickedListener {
        void onOkClicked();
    }
}
