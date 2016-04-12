package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.gatewaysample.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KiiUserWizardFragment  extends WizardFragment {

    @Bind(R.id.editTextEmail)
    EditText editTextEmail;
    @Bind(R.id.editTextPassword)
    EditText editTextPassword;
    @Bind(R.id.checkboxNewUser)
    CheckBox checkboxNewUser;

    public static KiiUserWizardFragment newFragment() {
        KiiUserWizardFragment fragment = new KiiUserWizardFragment();
        return fragment;
    }
    public KiiUserWizardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_wizard_fragment, null);
        ButterKnife.bind(this, view);

        // TODO
        editTextEmail.setText("u" + System.currentTimeMillis() + "@kii.com");
        editTextPassword.setText("pass");

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateRequiredField();
            }
        };
        this.editTextEmail.addTextChangedListener(watcher);
        this.editTextPassword.addTextChangedListener(watcher);
        this.validateRequiredField();
        return view;
    }

    @Override
    public void execute() throws Exception {
        String email = this.editTextEmail.getText().toString();
        String password = this.editTextPassword.getText().toString();
        if (this.checkboxNewUser.isChecked()) {
            KiiUser user = KiiUser.builderWithEmail(email).build();
            user.register(password);
        } else {
            KiiUser.logIn(email, password);
        }
    }
    @Override
    public void onActivate() {
        this.validateRequiredField();
    }
    @Override
    public void onInactivate(int exitCode) {
    }

    @Override
    public String getNextButtonText() {
        return "Next";
    }
    @Override
    public String getPreviousButtonText() {
        return "Previous";
    }

    private void validateRequiredField() {
        if (TextUtils.isEmpty(this.editTextEmail.getText()) || TextUtils.isEmpty(this.editTextPassword.getText())) {
            this.setNextButtonEnabled(false);
        } else {
            this.setNextButtonEnabled(true);
        }
    }

}
