package com.kii.gatewaysample.ui.fragments.wizard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.kii.cloud.storage.Kii;
import com.kii.gatewaysample.R;
import com.kii.gatewaysample.model.UPnPService;
import com.kii.gatewaysample.ui.fragments.GatewayServicesListFragment;
import com.kii.gatewaysample.utils.UPnPControlPointPromise;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Site;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.GatewayAPIBuilder;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayWizardFragment  extends WizardFragment {
    private static int REQUEST_CODE_SELECT_GATEWAY = 100;


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
    public void onActivate() {
        this.validateRequiredField();
    }
    @Override
    public void execute() throws Exception {
        // TODO:Gets Site from KiiCloudSDK directly
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
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        if (TextUtils.isEmpty(port)) {
            builder.encodedAuthority(ip);
        } else {
            builder.encodedAuthority(ip + ":" + port);
        }
        GatewayAPI api = GatewayAPIBuilder.newBuilder(getActivity(), app, builder.build()).build();
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

    @OnClick(R.id.buttonSearchGateway)
    public void searchGateway(View v) {
        loadGatewayServices();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SELECT_GATEWAY &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(GatewayServicesListFragment.GATEWAY_SERVICE)) {
            UPnPService selectedGateway =
                    (UPnPService) data.getParcelableExtra(GatewayServicesListFragment.GATEWAY_SERVICE);
            String location = selectedGateway.getLocation();
            if(location.startsWith("http://")){
                location = location.substring(7);
            }else if (location.startsWith("https://")){
                location = location.substring(8);
            }
            String[] parts = location.split(":");
            String ip = parts[0];
            String port = parts[1];
            editTextIP.setText(ip);
            editTextPortNo.setText(port);
        }
    }

    private void loadGatewayServices() {
        new UPnPControlPointPromise(this.getContext()).discover("urn:kii:service:iot-gateway:1").then(new DoneCallback<UPnPService[]>() {
            @Override
            public void onDone(UPnPService[] result) {
                if(result.length == 0){
                    Toast.makeText(getActivity(), "No gateway found", Toast.LENGTH_SHORT).show();
                }else {
                    GatewayServicesListFragment dialog = GatewayServicesListFragment.newFragment(result);
                    dialog.setTargetFragment(GatewayWizardFragment.this, REQUEST_CODE_SELECT_GATEWAY);
                    dialog.show(getActivity().getSupportFragmentManager(), "GatewayServicesListFragment");
                }

            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

