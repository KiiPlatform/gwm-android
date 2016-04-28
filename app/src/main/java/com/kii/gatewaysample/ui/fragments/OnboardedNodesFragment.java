package com.kii.gatewaysample.ui.fragments;

import android.content.Context;
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

import com.kii.cloud.storage.Kii;
import com.kii.gatewaysample.R;
import com.kii.gatewaysample.db.DatabaseHelper;
import com.kii.gatewaysample.db.dao.OnboardedNodesDao;
import com.kii.gatewaysample.model.ApiBuilder;
import com.kii.gatewaysample.model.TurnPower;
import com.kii.gatewaysample.utils.IoTCloudPromiseAPIWrapper;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OnboardedNodesFragment extends Fragment implements PagerFragment {

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private OnboardedEndNodeArrayAdapter adapter;

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
        this.adapter = new OnboardedEndNodeArrayAdapter(getActivity());
        this.listView.setAdapter(adapter);
        this.listView.setAdapter(adapter);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOnboardedNodes();
            }
        });
        // Configure the refreshing colors
        this.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        this.loadOnboardedNodes();
        return view;
    }

    @Override
    public void onVisible(boolean visible) {
    }

    private void loadOnboardedNodes() {
        try {
            new AndroidDeferredManager().when(new DeferredAsyncTask<Void, Void, List<OnboardedNodesDao.Endnode>>() {
                @Override
                protected List<OnboardedNodesDao.Endnode> doInBackgroundSafe(Void... params) throws Exception {
                    return new OnboardedNodesDao(DatabaseHelper.getInstance()).selectByApp(Kii.getAppId());
                }
            }).done(new DoneCallback<List<OnboardedNodesDao.Endnode>>() {
                @Override
                public void onDone(List<OnboardedNodesDao.Endnode> result) {
                    adapter.clear();
                    adapter.addAll(result);
                }
            }).fail(new FailCallback<Throwable>() {
                @Override
                public void onFail(Throwable result) {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).always(new AlwaysCallback<List<OnboardedNodesDao.Endnode>, Throwable>() {
                @Override
                public void onAlways(Promise.State state, List<OnboardedNodesDao.Endnode> resolved, Throwable rejected) {
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

    private static class OnboardedEndNodeViewHolder {
        private TextView text;
        private Button buttonSendCommand;
    }

    private class OnboardedEndNodeArrayAdapter extends ArrayAdapter<OnboardedNodesDao.Endnode> {
        private final LayoutInflater inflater;
        private OnboardedEndNodeArrayAdapter(Context context) {
            super(context, R.layout.onboarded_node_item);
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OnboardedEndNodeViewHolder holder = null;
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.onboarded_node_item, parent, false);
                holder = new OnboardedEndNodeViewHolder();
                holder.text = (TextView)convertView.findViewById(R.id.row_text);
                holder.buttonSendCommand = (Button)convertView.findViewById(R.id.row_button_send_command);
                convertView.setTag(holder);
            } else {
                holder = (OnboardedEndNodeViewHolder)convertView.getTag();
            }
            final OnboardedNodesDao.Endnode item = this.getItem(position);
            holder.text.setText(item.vendorThingID);
            holder.buttonSendCommand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                try {
                    ThingIFAPI api = ThingIFAPI.loadFromStoredInstance(getActivity(), "endnode");
                    List<Action> actions = new ArrayList<Action>();
                    actions.add(new TurnPower(true));
                    new IoTCloudPromiseAPIWrapper(api).postNewCommand(ApiBuilder.SCHEMA_NAME, ApiBuilder.SCHEMA_VERSION, actions).done(new DoneCallback<Command>() {
                        @Override
                        public void onDone(Command result) {
                            Toast.makeText(getActivity(), "Command is submitted", Toast.LENGTH_SHORT).show();
                        }
                    }).fail(new FailCallback<Throwable>() {
                        @Override
                        public void onFail(Throwable e) {
                            Toast.makeText(getActivity(), e.getClass().toString() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getClass().toString() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                }
            });
            return convertView;
        }
    }

}
