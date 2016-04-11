package com.kii.gatewaysample.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.thingif.gateway.GatewayAPI;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayFragment extends Fragment implements PagerFragment {

    private GatewayAPI api;

    @Bind(R.id.layout_gateway_onboarded)
    LinearLayout onboardedLayout;
    @Bind(R.id.layout_gateway_unonboarded)
    LinearLayout unonboardedLayout;
    @Bind(R.id.text_thing_id)
    TextView textThingId;

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
        ButterKnife.bind(this, view);

        new GatewayPromiseAPIWrapper(api).getGatewayID().done(new DoneCallback<String>() {
            @Override
            public void onDone(final String result) {
                onboardedLayout.setVisibility(View.VISIBLE);
                unonboardedLayout.setVisibility(View.GONE);
                textThingId.setText(result);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(final Throwable tr) {
                onboardedLayout.setVisibility(View.GONE);
                unonboardedLayout.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
    @OnClick(R.id.button_onboard_gateway)
    void onboardGateway() {
        new GatewayPromiseAPIWrapper(api).onboardGateway().done(new DoneCallback<String>() {
            @Override
            public void onDone(String result) {
                onboardedLayout.setVisibility(View.VISIBLE);
                unonboardedLayout.setVisibility(View.GONE);
                textThingId.setText(result);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(getContext(), "Failed to onboard gateway -- " + result.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onVisible(boolean visible) {

    }
}
