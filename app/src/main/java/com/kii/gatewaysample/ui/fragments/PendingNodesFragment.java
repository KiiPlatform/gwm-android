package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kii.gatewaysample.R;

import butterknife.ButterKnife;

public class PendingNodesFragment extends Fragment implements PagerFragment {

    public PendingNodesFragment() {
        // Required empty public constructor
    }

    public static PendingNodesFragment newFragment() {
        PendingNodesFragment fragment = new PendingNodesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_nodes_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onVisible(boolean visible) {

    }
}
