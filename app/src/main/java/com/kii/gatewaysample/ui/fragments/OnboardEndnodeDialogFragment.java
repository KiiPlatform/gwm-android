package com.kii.gatewaysample.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.gatewaysample.GatewaySampleApplication;
import com.kii.gatewaysample.R;
import com.kii.gatewaysample.db.DatabaseHelper;
import com.kii.gatewaysample.db.dao.OnboardedNodesDao;
import com.kii.gatewaysample.model.ApiBuilder;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.gatewaysample.utils.IoTCloudPromiseAPIWrapper;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.exception.StoredGatewayAPIInstanceNotFoundException;
import com.kii.thingif.gateway.GatewayAPI;

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.concurrent.atomic.AtomicReference;

public class OnboardEndnodeDialogFragment extends DialogFragment {

    public static OnboardEndnodeDialogFragment newFragment(String gatewayThingID, String vendorThingID) {
        OnboardEndnodeDialogFragment fragment = new OnboardEndnodeDialogFragment();
        Bundle args = new Bundle();
        args.putString("gatewayThingID", gatewayThingID);
        args.putString("vendorThingID", vendorThingID);
        fragment.setArguments(args);
        return fragment;
    }

    public OnboardEndnodeDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AtomicReference<String> thingID = new AtomicReference<String>();
        final String gatewayThingID = getArguments().getString("gatewayThingID");
        final String vendorThingID = getArguments().getString("vendorThingID");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.onboard_endnode_dialog_fragment, null, false);

        ((TextView)view.findViewById(R.id.textVendorThingID)).setText(vendorThingID);
        final EditText editTextThingType = ((EditText)view.findViewById(R.id.editTextThingType));
        final EditText editTextPassword = ((EditText)view.findViewById(R.id.editTextPassword));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton("Onboard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                String thingType = editTextThingType.getText().toString();
                String password = editTextPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                ThingIFAPI thingAPI = ApiBuilder.buildApi(getActivity(), KiiUser.getCurrentUser());
                new IoTCloudPromiseAPIWrapper(thingAPI).onboardEndnodeWithGatewayThingID(gatewayThingID, vendorThingID, password, thingType, null).then(new DonePipe<Target, Void, Throwable, Void>() {
                    @Override
                    public Promise<Void, Throwable, Void> pipeDone(Target result) {
                        GatewayAPI gatewayAPI = null;
                        try {
                            gatewayAPI = GatewayAPI.loadFromStoredInstance(GatewaySampleApplication.getInstance());
                            thingID.set(result.getTypedID().getID());
                            return new GatewayPromiseAPIWrapper(gatewayAPI).notifyOnboardingCompletion(thingID.get(), vendorThingID);
                        } catch (StoredGatewayAPIInstanceNotFoundException e) {
                            return new DeferredObject<Void, Throwable, Void>().reject(e);
                        }
                    }
                }).then(new DonePipe<Void, Void, Throwable, Void>() {
                    @Override
                    public Promise<Void, Throwable, Void> pipeDone(Void result) {
                        try {
                            new OnboardedNodesDao(DatabaseHelper.getInstance()).insert(Kii.getAppId(), thingID.get(), vendorThingID);
                            return new DeferredObject<Void, Throwable, Void>().resolve(null);
                        } catch (Exception e) {
                            return new DeferredObject<Void, Throwable, Void>().reject(e);
                        }
                    }
                }).done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void result) {
                        Toast.makeText(GatewaySampleApplication.getInstance(), "Endnode is onboarded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        if (getArguments() != null) {
                            intent.putExtras(getArguments());
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), which, intent);
                        dismiss();
                    }
                }).fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        Toast.makeText(GatewaySampleApplication.getInstance(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

}
