package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kii.gatewaysample.R;

import butterknife.ButterKnife;

public class OnboardedNodesFragment extends Fragment implements PagerFragment {

    public OnboardedNodesFragment() {
        // Required empty public constructor
    }

    public static OnboardedNodesFragment newFragment() {
        OnboardedNodesFragment fragment = new OnboardedNodesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarded_nodes_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onVisible(boolean visible) {

    }
}
