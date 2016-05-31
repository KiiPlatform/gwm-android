package com.kii.gatewaysample.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.model.UPnPService;
import com.kii.gatewaysample.utils.UPnPControlPointPromise;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * List Gateway services found in LAN
 */
public class GatewayServicesListFragment extends DialogFragment {
    public final static String GATEWAY_SERVICE="gateway_service";


    @Bind(R.id.gatewayServicesListView)
    ListView listView;

    private GatewayServiceArrayAdapter adapter;
    private UPnPService selectedGateway;

    public GatewayServicesListFragment() {
        // Required empty public constructor
    }

    public static GatewayServicesListFragment newFragment(){
        GatewayServicesListFragment fragment = new GatewayServicesListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.loadGatewayServices();
        View view = inflater.inflate(R.layout.gateway_services_list_fragment, null);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);
        this.adapter = new GatewayServiceArrayAdapter(getActivity());
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String location = ((TextView) view.findViewById(R.id.gateway_ip) ).getText().toString();
                selectedGateway = (UPnPService)adapter.getItem(position);
            }
        });

        return view;
    }

    private void loadGatewayServices() {
        new UPnPControlPointPromise(this.getContext()).discover(null).then(new DoneCallback<UPnPService[]>() {
            @Override
            public void onDone(UPnPService[] result) {
                // FIXME: 5/31/16 Can not dismiss well
                if(result.length == 0){
                    dismiss();
                    Toast.makeText(getActivity(), "No gateway found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.clear();
                adapter.addAll(result);
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GatewayServiceArrayAdapter extends ArrayAdapter<UPnPService> {
        private final LayoutInflater inflater;
        private GatewayServiceArrayAdapter(Context context) {
            super(context, R.layout.gateway_service_layout);
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView ip = null;
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.gateway_service_layout, parent, false);
                ip = (TextView)convertView.findViewById(R.id.gateway_ip);
                convertView.setTag(ip);
            } else {
                ip = (TextView) convertView.getTag();
            }
            final UPnPService item = this.getItem(position);
            ip.setText(item.getLocation());
            return convertView;
        }
    }

    @OnClick(R.id.select_btn)
    public void onSelect(View v){
        Intent intent = new Intent();
        if (selectedGateway != null) {
            intent.putExtra(GATEWAY_SERVICE, selectedGateway);
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }

    @OnClick(R.id.cancel_btn)
    public void onCancel(View v){
        dismiss();
    }
}
