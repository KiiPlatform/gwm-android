package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kii.gatewaysample.R;
import com.kii.thingif.gateway.GatewayAPI;

public class GatewayFragment extends Fragment implements PagerFragment {

    private GatewayAPI api;

    public static GatewayFragment newFragment(GatewayAPI api) {
        GatewayFragment fragment = new GatewayFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("GatewayAPI", api);
        fragment.setArguments(arguments);
        return fragment;
    }
    public GatewayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.api = savedInstanceState.getParcelable("GatewayAPI");
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.api = arguments.getParcelable("GatewayAPI");
        }
        View view = inflater.inflate(R.layout.gateway_fragment, null);


        return view;
    }

    @Override
    public void onVisible(boolean visible) {

    }
}
