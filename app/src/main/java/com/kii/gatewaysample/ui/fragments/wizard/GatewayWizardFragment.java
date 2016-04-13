package com.kii.gatewaysample.ui.fragments.wizard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kii.cloud.storage.Kii;
import com.kii.gatewaysample.R;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Site;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.GatewayAPIBuilder;
import com.kii.thingif.gateway.GatewayAddress;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GatewayWizardFragment  extends WizardFragment {

    @Bind(R.id.editTextIP)
    EditText editTextIP;
    @Bind(R.id.editTextPortNo)
    EditText editTextPortNo;
    @Bind(R.id.editTextUsername)
    EditText editTextUsername;
    @Bind(R.id.editTextPassword)
    EditText editTextPassword;

    public static GatewayWizardFragment newFragment() {
        GatewayWizardFragment fragment = new GatewayWizardFragment();
        return fragment;
    }
    public GatewayWizardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gateway_wizard_fragment, null);
        ButterKnife.bind(this, view);

        editTextIP.setText("10.5.250.92");
        editTextPortNo.setText("4001");
        editTextUsername.setText("gateway_user");
        editTextPassword.setText("gateway_pass");

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
        this.editTextIP.addTextChangedListener(watcher);
        this.editTextPortNo.addTextChangedListener(watcher);
        this.editTextUsername.addTextChangedListener(watcher);
        this.editTextPassword.addTextChangedListener(watcher);
        this.validateRequiredField();
        return view;
    }

    @Override
    public void execute() throws Exception {
        Site site = null;
        if ("https://api-jp.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.JP;
        } else if ("https://api.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.US;
        } else if ("https://api-sg.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.SG;
        } else if ("https://api-cn3.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.CN3;
        }

        String ip = this.editTextIP.getText().toString();
        String port = this.editTextPortNo.getText().toString();
        String username = this.editTextUsername.getText().toString();
        String password = this.editTextPassword.getText().toString();

        KiiApp app = new KiiApp(Kii.getAppId(), Kii.getAppKey(), site);
        GatewayAddress address = null;
        if (TextUtils.isEmpty(port)) {
            address = new GatewayAddress("http", ip);
        } else {
            address = new GatewayAddress("http", ip, Integer.valueOf(port));
        }
        GatewayAPI api = GatewayAPIBuilder.newBuilder(getContext(), app, address).build();
        api.login(username, password);
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
        if (TextUtils.isEmpty(this.editTextIP.getText()) || TextUtils.isEmpty(this.editTextUsername.getText()) || TextUtils.isEmpty(this.editTextPassword.getText())) {
            this.setNextButtonEnabled(false);
        } else {
            this.setNextButtonEnabled(true);
        }
    }
}

