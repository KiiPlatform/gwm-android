package com.kii.gatewaysample.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.GatewayAPI;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PendingNodesFragment extends Fragment implements PagerFragment {

    private static int REQUEST_CODE_ONBOARD = 100;

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private String gatewayThingID;

    private PendingEndNodeArrayAdapter adapter;

    public PendingNodesFragment() {
        // Required empty public constructor
    }

    public static PendingNodesFragment newFragment(String gatewayThingID) {
        PendingNodesFragment fragment = new PendingNodesFragment();
        Bundle arguments = new Bundle();
        arguments.putString("gatewayThingID", gatewayThingID);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_nodes_fragment, null);
        ButterKnife.bind(this, view);
        this.gatewayThingID = getArguments().getString("gatewayThingID");
        this.adapter = new PendingEndNodeArrayAdapter(getActivity());
        this.listView.setAdapter(adapter);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPendingNodes();
            }
        });
        // Configure the refreshing colors
        this.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        this.loadPendingNodes();
        return view;
    }

    @Override
    public void onVisible(boolean visible) {
    }
    private void loadPendingNodes() {
        try {
            GatewayAPI api = GatewayAPI.loadFromStoredInstance(getActivity());
            new GatewayPromiseAPIWrapper(api).listPendingEndNodes().then(new DoneCallback<List<EndNode>>() {
                @Override
                public void onDone(List<EndNode> result) {
                    adapter.clear();
                    adapter.addAll(result);
                }
            }).fail(new FailCallback<Throwable>() {
                @Override
                public void onFail(Throwable result) {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).always(new AlwaysCallback<List<EndNode>, Throwable>() {
                @Override
                public void onAlways(Promise.State state, List<EndNode> resolved, Throwable rejected) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } catch (Exception e) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private static class PendingEndNodeViewHolder {
        private TextView text;
        private Button buttonOnboard;
    }
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_ONBOARD && resultCode == DialogInterface.BUTTON_POSITIVE) {
            loadPendingNodes();
        }
    }
    private class PendingEndNodeArrayAdapter extends ArrayAdapter<EndNode> {
        private final LayoutInflater inflater;
        private PendingEndNodeArrayAdapter(Context context) {
            super(context, R.layout.pending_node_item);
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PendingEndNodeViewHolder holder = null;
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.pending_node_item, parent, false);
                holder = new PendingEndNodeViewHolder();
                holder.text = (TextView)convertView.findViewById(R.id.row_text);
                holder.buttonOnboard = (Button)convertView.findViewById(R.id.row_button_onboard);
                convertView.setTag(holder);
            } else {
                holder = (PendingEndNodeViewHolder)convertView.getTag();
            }
            final EndNode item = this.getItem(position);
            holder.text.setText(item.getVendorThingID());
            holder.buttonOnboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnboardEndnodeDialogFragment dialog = OnboardEndnodeDialogFragment.newFragment(gatewayThingID, item.getVendorThingID());
                    dialog.setTargetFragment(PendingNodesFragment.this, REQUEST_CODE_ONBOARD);
                    dialog.show(getActivity().getSupportFragmentManager(), "OnboardEndnodeDialogFragment");
                }
            });
            return convertView;
        }
    }

}
