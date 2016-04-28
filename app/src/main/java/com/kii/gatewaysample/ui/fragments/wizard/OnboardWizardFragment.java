package com.kii.gatewaysample.ui.fragments.wizard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kii.cloud.storage.KiiThing;
import com.kii.cloud.storage.KiiUser;
import com.kii.gatewaysample.R;
import com.kii.gatewaysample.model.ApiBuilder;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.gateway.GatewayAPI;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OnboardWizardFragment  extends WizardFragment {

    public static OnboardWizardFragment newFragment() {
        OnboardWizardFragment fragment = new OnboardWizardFragment();
        return fragment;
    }

    @Bind(R.id.editTextGatewayPassword)
    EditText editTextGatewayPassword;

    private Gateway gateway = null;
    public OnboardWizardFragment() {
    }

    public Gateway getGateway() {
        return this.gateway;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboard_wizard_fragment, null);
        ButterKnife.bind(this, view);

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
        this.editTextGatewayPassword.addTextChangedListener(watcher);
        return view;
    }

    @Override
    public void onActivate() {
        this.validateRequiredField();
    }
    @Override
    public void execute() throws Exception {
        GatewayAPI gatewayAPI = GatewayAPI.loadFromStoredInstance(getActivity());
        this.gateway = gatewayAPI.onboardGateway();
        ThingIFAPI thingAPI = ApiBuilder.buildApi(getActivity(), KiiUser.getCurrentUser(), "gateway");
        thingAPI.onboard(this.gateway.getThingID(), this.editTextGatewayPassword.getText().toString());
    }
    private void validateRequiredField() {
        if (TextUtils.isEmpty(this.editTextGatewayPassword.getText())) {
            this.setNextButtonEnabled(false);
        } else {
            this.setNextButtonEnabled(true);
        }
    }

    @Override
    public String getNextButtonText() {
        return "Onboard";
    }
    @Override
    public String getPreviousButtonText() {
        return "Previous";
    }
}

