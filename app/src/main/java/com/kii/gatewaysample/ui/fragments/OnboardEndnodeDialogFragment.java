package com.kii.gatewaysample.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.gatewaysample.GatewaySampleApplication;
import com.kii.gatewaysample.R;
import com.kii.gatewaysample.utils.GatewayPromiseAPIWrapper;
import com.kii.gatewaysample.utils.IoTCloudPromiseAPIWrapper;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.exception.StoredGatewayAPIInstanceNotFoundException;
import com.kii.thingif.exception.StoredThingIFAPIInstanceNotFoundException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.PendingEndNode;

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.concurrent.atomic.AtomicReference;

public class OnboardEndnodeDialogFragment extends DialogFragment {

    public static OnboardEndnodeDialogFragment newFragment(PendingEndNode pendingEndNode) {
        OnboardEndnodeDialogFragment fragment = new OnboardEndnodeDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("pendingEndNode", pendingEndNode);
        fragment.setArguments(args);
        return fragment;
    }

    public OnboardEndnodeDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AtomicReference<String> thingID = new AtomicReference<String>();
        final PendingEndNode pendingEndNode = getArguments().getParcelable("pendingEndNode");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.onboard_endnode_dialog_fragment, null, false);

        ((TextView)view.findViewById(R.id.textVendorThingID)).setText(pendingEndNode.getVendorThingID());
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
                final ThingIFAPI thingAPI;
                try {
                    thingAPI = ThingIFAPI.loadFromStoredInstance(getContext(), "gateway");
                } catch (StoredThingIFAPIInstanceNotFoundException e) {
                    Log.e("OnboardEndnodeDialog", e.getMessage());
                    return;
                }
                new IoTCloudPromiseAPIWrapper(thingAPI).onboardEndnodeWithGateway(pendingEndNode, password).then(new DonePipe<EndNode, Void, Throwable, Void>() {
                    @Override
                    public Promise<Void, Throwable, Void> pipeDone(EndNode result) {
                        GatewayAPI gatewayAPI = null;
                        try {
                            gatewayAPI = GatewayAPI.loadFromStoredInstance(GatewaySampleApplication.getInstance());
                            thingID.set(result.getTypedID().getID());
                            thingAPI.copyWithTarget(result, "endnode"); // Create ThingIFAPI instance for endnode
                            return new GatewayPromiseAPIWrapper(gatewayAPI).notifyOnboardingCompletion(result);
                        } catch (StoredGatewayAPIInstanceNotFoundException e) {
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
