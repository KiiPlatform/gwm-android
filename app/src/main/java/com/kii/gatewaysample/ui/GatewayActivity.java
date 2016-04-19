package com.kii.gatewaysample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.thingif.exception.StoredGatewayAPIInstanceNotFoundException;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.GatewayInformation;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GatewayActivity extends AppCompatActivity {

    @Bind(R.id.textHost)
    TextView textHost;
    @Bind(R.id.textVendorThingID)
    TextView textVendorThingID;
    @Bind(R.id.textThingID)
    TextView textThingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        ButterKnife.bind(this);
        try {
            GatewayAPI api = GatewayAPI.loadFromStoredInstance(this);
            this.textHost.setText(api.getGatewayAddress().toString());
            GatewayPromiseAPIWrapper wrapper = new GatewayPromiseAPIWrapper(api);
            wrapper.getGatewayInformation().then(new DoneCallback<GatewayInformation>() {
                @Override
                public void onDone(GatewayInformation result) {
                    textVendorThingID.setText(result.getVendorThingID());
                }
            }).fail(new FailCallback<Throwable>() {
                @Override
                public void onFail(Throwable result) {
                }
            });
            wrapper.getGatewayID().then(new DoneCallback<String>() {
                @Override
                public void onDone(String result) {
                    textThingID.setText(result);
                }
            }).fail(new FailCallback<Throwable>() {
                @Override
                public void onFail(Throwable result) {
                }
            });

        } catch (StoredGatewayAPIInstanceNotFoundException ignore) {
        }
    }

}
