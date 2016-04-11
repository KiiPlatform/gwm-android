package com.kii.gatewaysample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Site;
import com.kii.thingif.exception.StoredGatewayAPIInstanceNotFoundException;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.GatewayAPIBuilder;
import com.kii.thingif.gateway.GatewayAddress;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewaySettingsActivity extends AppCompatActivity {

    @Bind(R.id.editTextIP)
    EditText editTextIP;
    @Bind(R.id.editTextPortNo)
    EditText editTextPortNo;
    @Bind(R.id.spinnerSite)
    Spinner spinnerSite;
    @Bind(R.id.editTextAppID)
    EditText editTextAppID;
    @Bind(R.id.editTextAppKey)
    EditText editTextAppKey;
    @Bind(R.id.editTextUsername)
    EditText editTextUsername;
    @Bind(R.id.editTextPassword)
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_settings);
        ButterKnife.bind(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            GatewayAPI api = GatewayAPI.loadFromStoredInstance(this, "gateway");
            editTextIP.setText(api.getGatewayAddress().getHostName());
            if (api.getGatewayAddress().getPort() >= 0) {
                editTextPortNo.setText(String.valueOf(api.getGatewayAddress().getPort()));
            }
            int siteIndex = 0;
            SpinnerAdapter adapter = (SpinnerAdapter)spinnerSite.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(api.getApp().getSiteName())) {
                    siteIndex = i;
                    break;
                }
            }
            spinnerSite.setSelection(siteIndex);
            editTextAppID.setText(api.getAppID());
            editTextAppKey.setText(api.getAppKey());
        } catch (StoredGatewayAPIInstanceNotFoundException ignore) {
            // FIXME:For Text
            editTextIP.setText("10.5.250.92");
            editTextPortNo.setText("4001");
            editTextAppID.setText("c8e970a9");
            editTextAppKey.setText("d59ac1235c57875436f8b396d190fff7");
            editTextUsername.setText("gateway_user");
            editTextPassword.setText("gateway_pass");
        }
    }

    @OnClick(R.id.button_ok)
    public void onClickOK() {
        String ip = editTextIP.getText().toString();
        String port = editTextPortNo.getText().toString();
        String site = (String)spinnerSite.getSelectedItem();
        String appID = editTextAppID.getText().toString();
        String appKey = editTextAppKey.getText().toString();
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(ip)) {
            Toast.makeText(this, "IP is required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(appID)) {
            Toast.makeText(this, "App ID is required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(appKey)) {
            Toast.makeText(this, "App Key is required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        GatewayAddress gatewayAddress = null;
        if (TextUtils.isEmpty(port)) {
            gatewayAddress = new GatewayAddress("http", ip);
        } else {
            gatewayAddress = new GatewayAddress("http", ip, Integer.valueOf(port));
        }
        KiiApp app = new KiiApp(appID, appKey, Site.valueOf(site));
        final GatewayAPI api = GatewayAPIBuilder.newBuilder(getApplicationContext(), app, gatewayAddress).setTag("gateway").build();
        new GatewayPromiseAPIWrapper(api).login(username, password).done(new DoneCallback<Void>() {
            @Override
            public void onDone(final Void result) {
                finish();
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(final Throwable tr) {
                Log.d("GatewaySettingsActivity", tr.getMessage());
                Toast.makeText(GatewaySettingsActivity.this, tr.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @OnClick(R.id.button_cancel)
    public void onClickCancel() {
        finish();
    }
}
