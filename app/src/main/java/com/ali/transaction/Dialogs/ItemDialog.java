package com.ali.transaction.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.DateAndTime;
import com.ali.transaction.Classes.Vibrate;
import com.ali.transaction.Models.JobItem;
import com.ali.transaction.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ItemDialog extends BottomSheetDialogFragment {
    ItemDialogListener listener;
    ConstraintLayout take, give;
    CheckBox takeCheck, giveCheck;
    EditText reason, price;

    public ItemDialog() {
    }

    public ItemDialog(ItemDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_item_dialog, container, false);
        initializeView(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialog_animation);
        }

        return dialog;
    }

    private void initializeView(View view) {
        initializeEditText(view);
        setupTakeOption(view);
        setupGiveOption(view);
        setupConfirmButton(view);
    }

    private void initializeEditText(View view) {
        reason = view.findViewById(R.id.reason);
        price = view.findViewById(R.id.price);
    }

    private void setupTakeOption(View view) {
        take = view.findViewById(R.id.take);
        takeCheck = view.findViewById(R.id.take_checkbox);

        take.setOnClickListener(v -> setOptionSelected(take, takeCheck, give, giveCheck));
    }

    private void setupGiveOption(View view) {
        give = view.findViewById(R.id.give);
        giveCheck = view.findViewById(R.id.give_checkbox);

        give.setOnClickListener(v -> setOptionSelected(give, giveCheck, take, takeCheck));
    }

    private void setOptionSelected(ConstraintLayout selectedLayout, CheckBox selectedCheckBox, ConstraintLayout deselectedLayout, CheckBox deselectedCheckBox) {
        selectedCheckBox.setChecked(true);
        selectedLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.green_stroke));

        deselectedCheckBox.setChecked(false);
        deselectedLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.gray_stroke));
    }

    private void setupConfirmButton(View view) {
        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(v -> {
            String reason = String.valueOf(this.reason.getText());
            String price = String.valueOf(this.price.getText());

            if (isValidInput(price)) {
                listener.onDataEntered(takeCheck.isChecked() ? JobItem.Type.TAKE : JobItem.Type.GIVE, DateAndTime.getCurrentDateTime(), reason.trim(), Double.parseDouble(price.trim()));
                dismiss();
            } else {
                handleInputErrors();
                Vibrate.vibrate(requireContext());
            }
        });
    }

    private boolean isValidInput(String value) {
        return value.matches(Common.DECIMAL_REGEX);
    }

    private void handleInputErrors() {
        if (!takeCheck.isChecked() && !giveCheck.isChecked()) {
            take.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.error_p));
            give.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.error_p));
        }

        if (price.getVisibility() != View.GONE) {
            if (!isValidInput(price.getText().toString().trim())) {
                price.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.error_p));
            } else {
                price.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.input_filed));
            }
        }
    }

    public interface ItemDialogListener {
        void onDataEntered(JobItem.Type type, String date, String reason, double price);
    }
}