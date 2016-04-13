package com.kii.gatewaysample.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.gatewaysample.R;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.PendingEndNode;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PendingNodesFragment extends Fragment implements PagerFragment {

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private PendingEndNodeArrayAdapter adapter;

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
        this.adapter = new PendingEndNodeArrayAdapter(getContext());
        this.listView.setAdapter(adapter);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    GatewayAPI api = GatewayAPI.loadFromStoredInstance(getContext());
                    new GatewayPromiseAPIWrapper(api).listPendingEndNodes().then(new DoneCallback<List<PendingEndNode>>() {
                        @Override
                        public void onDone(List<PendingEndNode> result) {
                            adapter.clear();
                            adapter.addAll(result);
                        }
                    }).fail(new FailCallback<Throwable>() {
                        @Override
                        public void onFail(Throwable result) {
                            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).always(new AlwaysCallback<List<PendingEndNode>, Throwable>() {
                        @Override
                        public void onAlways(Promise.State state, List<PendingEndNode> resolved, Throwable rejected) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        this.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    @Override
    public void onVisible(boolean visible) {

    }

    private static class PendingEndNodeViewHolder {
        private TextView text;
    }

    private class PendingEndNodeArrayAdapter extends ArrayAdapter<PendingEndNode> {
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
                convertView.setTag(holder);
            } else {
                holder = (PendingEndNodeViewHolder)convertView.getTag();
            }
            PendingEndNode item = this.getItem(position);
            holder.text.setText(item.getVendorThingID());
            return convertView;
        }
    }

}
