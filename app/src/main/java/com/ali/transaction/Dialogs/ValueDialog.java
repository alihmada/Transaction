package com.ali.transaction.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.Vibrate;
import com.ali.transaction.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ValueDialog extends BottomSheetDialogFragment {

    private BottomSheetListener listener;
    private int headerId, hintId;
    private InputType type;
    private String regex;
    private EditText textInput;

    public ValueDialog() {
    }

    public ValueDialog(int hintId, InputType type, String regex, BottomSheetListener listener) {
        this.hintId = hintId;
        this.type = type;
        this.regex = regex;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_person_dialog, container, false);
        initialize(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (headerId == 0 && hintId == 0 && regex == null) {
            if (savedInstanceState != null) {
                int[] ints = savedInstanceState.getIntArray(Common.VALUE);
                if (ints != null) {
                    headerId = ints[0];
                    hintId = ints[1];
                }
                String regex = savedInstanceState.getString(Common.REGEX);
                assert regex != null;
                this.regex = regex;
            }
        }

        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialog_animation);
        }

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putIntArray(Common.VALUE, new int[]{headerId, hintId});
        outState.putString(Common.REGEX, regex);
        super.onSaveInstanceState(outState);
    }

    private void initialize(View view) {
        setupEditText(view);
        setupButton(view);
    }

    private void setupEditText(View view) {
        textInput = view.findViewById(R.id.input_field);

        if (type == InputType.PHONE) {
            textInput.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        } else if (type == InputType.TEXT) {
            textInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        } else if (type == InputType.NUMBER) {
            textInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        } else {
            textInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        textInput.setHint(getString(hintId));
        textInput.requestFocus();
    }

    private void setupButton(View view) {
        view.findViewById(R.id.done).setOnClickListener(view1 -> {
            String edited = String.valueOf(textInput.getText());
            if (!edited.isEmpty() && validateInput(edited)) {
                listener.onDataEntered(edited.trim());
                dismiss();
            } else if (regex.isEmpty()) {
                listener.onDataEntered(edited.trim());
                dismiss();
            } else {
                textInput.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.error_p));
                Vibrate.vibrate(requireContext());
            }
        });
    }

    private boolean validateInput(String inputText) {
        return inputText.matches(regex);
    }

    public enum InputType {
        PHONE, DECIMAL, TEXT, NUMBER
    }

    public interface BottomSheetListener {
        void onDataEntered(String text);
    }

    public interface OnValueSetListener {
        void onValueSet(String text);
    }
}