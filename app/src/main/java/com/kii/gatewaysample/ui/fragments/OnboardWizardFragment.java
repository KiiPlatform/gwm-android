package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kii.cloud.storage.KiiThing;
import com.kii.cloud.storage.KiiUser;
import com.kii.gatewaysample.R;
import com.kii.thingif.gateway.GatewayAPI;

import butterknife.ButterKnife;

public class OnboardWizardFragment  extends WizardFragment {

    public static OnboardWizardFragment newFragment() {
        OnboardWizardFragment fragment = new OnboardWizardFragment();
        return fragment;
    }
    public OnboardWizardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboard_wizard_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void execute() throws Exception {
        GatewayAPI gatewayAPI = GatewayAPI.loadFromStoredInstance(getContext());
        String thingID = gatewayAPI.onboardGateway();
        KiiThing.thingWithID(thingID).registerOwner(KiiUser.getCurrentUser());
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

