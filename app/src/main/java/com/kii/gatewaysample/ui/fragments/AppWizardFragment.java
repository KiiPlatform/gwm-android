package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.kii.cloud.storage.Kii;
import com.kii.gatewaysample.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppWizardFragment extends WizardFragment {

    @Bind(R.id.spinnerSite)
    Spinner spinnerSite;
    @Bind(R.id.editTextAppID)
    EditText editTextAppID;
    @Bind(R.id.editTextAppKey)
    EditText editTextAppKey;

    public static AppWizardFragment newFragment() {
        AppWizardFragment fragment = new AppWizardFragment();
        return fragment;
    }
    public AppWizardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_wizard_fragment, null);
        ButterKnife.bind(this, view);

        // FIXME
        editTextAppID.setText("c8e970a9");
        editTextAppKey.setText("d59ac1235c57875436f8b396d190fff7");

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
        this.editTextAppID.addTextChangedListener(watcher);
        this.editTextAppKey.addTextChangedListener(watcher);
        this.validateRequiredField();
        return view;
    }

    @Override
    public String getNextButtonText() {
        return "Next";
    }
    @Override
    public String getPreviousButtonText() {
        return "Exit";
    }

    @Override
    public void execute() throws Exception {
    }
    @Override
    public void onActivate() {
        this.validateRequiredField();
    }
    @Override
    public void onInactivate(int exitCode) {
        if (exitCode == EXIT_NEXT) {
            String appID = this.editTextAppID.getText().toString();
            String appKey = this.editTextAppKey.getText().toString();
            String site = (String)spinnerSite.getSelectedItem();
            Kii.initialize(getContext(), appID, appKey, Kii.Site.valueOf(site));
        }
    }

    private void validateRequiredField() {
        if (TextUtils.isEmpty(this.editTextAppID.getText()) || TextUtils.isEmpty(this.editTextAppKey.getText())) {
            this.setNextButtonEnabled(false);
        } else {
            this.setNextButtonEnabled(true);
        }
    }
}
