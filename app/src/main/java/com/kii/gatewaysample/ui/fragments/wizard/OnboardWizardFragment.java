package com.kii.gatewaysample.ui.fragments.wizard;

import android.os.Bundle;
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
    private String gatewayThingID = null;
    public OnboardWizardFragment() {
    }

    public String getGatewayThingID() {
        return this.gatewayThingID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboard_wizard_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void execute() throws Exception {
        GatewayAPI gatewayAPI = GatewayAPI.loadFromStoredInstance(getActivity());
        this.gatewayThingID = gatewayAPI.onboardGateway();
        KiiThing.thingWithID(this.gatewayThingID).registerOwner(KiiUser.getCurrentUser());
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

