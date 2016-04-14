package com.kii.gatewaysample.ui.fragments.wizard;

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
import com.kii.gatewaysample.AppConst;
import com.kii.gatewaysample.BuildConfig;
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
        if (AppConst.FLAVOR_GATEWAY.equals(BuildConfig.FLAVOR)) {
            editTextAppID.setText("c8e970a9");
            editTextAppKey.setText("d59ac1235c57875436f8b396d190fff7");
        } else {
            editTextAppID.setText("75b59c89");
            editTextAppKey.setText("4b61c84c6dfbaa6f191305f342e6c950");
        }

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
            Kii.initialize(getActivity(), appID, appKey, Kii.Site.valueOf(site));
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
