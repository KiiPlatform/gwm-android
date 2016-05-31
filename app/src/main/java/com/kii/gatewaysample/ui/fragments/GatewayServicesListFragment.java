package com.kii.gatewaysample.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.model.UPnPService;

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
    private UPnPService selectedService;
    private UPnPService[] foundServices;

    public GatewayServicesListFragment() {
        // Required empty public constructor
    }

    public static GatewayServicesListFragment newFragment(UPnPService[] foundServices){
        GatewayServicesListFragment fragment = new GatewayServicesListFragment();
        fragment.foundServices = foundServices;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gateway_services_list_fragment, null);
        getDialog().setTitle("Found Gateway Services");
        ButterKnife.bind(this, view);
        this.adapter = new GatewayServiceArrayAdapter(getActivity());
        this.listView.setAdapter(adapter);
        adapter.addAll(this.foundServices);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String location = ((TextView) view.findViewById(R.id.gateway_description) ).getText().toString();
                selectedService = (UPnPService)adapter.getItem(position);
            }
        });

        return view;
    }


    private class GatewayServiceArrayAdapter extends ArrayAdapter<UPnPService> {
        private final LayoutInflater inflater;
        private GatewayServiceArrayAdapter(Context context) {
            super(context, R.layout.gateway_service_layout);
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView description = null;
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.gateway_service_layout, parent, false);
                description = (TextView)convertView.findViewById(R.id.gateway_description);
                convertView.setTag(description);
            } else {
                description = (TextView) convertView.getTag();
            }
            final UPnPService item = this.getItem(position);
            description.setText("Address:"+item.getLocation()+"\nService Type:"+item.getSt()+"\nMax age:"+item.getMaxAge());
            return convertView;
        }
    }

    @OnClick(R.id.select_btn)
    public void onSelect(View v){
        Intent intent = new Intent();
        if (selectedService != null) {
            intent.putExtra(GATEWAY_SERVICE, selectedService);
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }

    @OnClick(R.id.cancel_btn)
    public void onCancel(View v){
        dismiss();
    }
}
