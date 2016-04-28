package com.kii.gatewaysample.utils;

import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.GatewayInformation;
import com.kii.thingif.gateway.PendingEndNode;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.List;

public class GatewayPromiseAPIWrapper {

    private AndroidDeferredManager adm;
    private GatewayAPI api;

    public GatewayPromiseAPIWrapper(GatewayAPI api) {
        this.adm = new AndroidDeferredManager();
        this.api = api;
    }

    public GatewayPromiseAPIWrapper(AndroidDeferredManager manager, GatewayAPI api) {
        this.adm = manager;
        this.api = api;
    }

    public Promise<Void, Throwable, Void> login(final String username, final String password) {
        return adm.when(new DeferredAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackgroundSafe(Void... voids) throws Exception {
                api.login(username, password);
                return null;
            }
        });
    }
    public Promise<Gateway, Throwable, Void> onboardGateway() {
        return adm.when(new DeferredAsyncTask<Void, Void, Gateway>() {
            @Override
            protected Gateway doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboardGateway();
            }
        });
    }
    public Promise<String, Throwable, Void> getGatewayID() {
        return adm.when(new DeferredAsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackgroundSafe(Void... voids) throws Exception {
                return api.getGatewayID();
            }
        });
    }
    public Promise<List<PendingEndNode>, Throwable, Void> listPendingEndNodes() {
        return adm.when(new DeferredAsyncTask<Void, Void, List<PendingEndNode>>() {
            @Override
            protected List<PendingEndNode> doInBackgroundSafe(Void... voids) throws Exception {
                return api.listPendingEndNodes();
            }
        });
    }
    public Promise<Void, Throwable, Void> notifyOnboardingCompletion(final EndNode endNode) {
        return adm.when(new DeferredAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackgroundSafe(Void... voids) throws Exception {
                api.notifyOnboardingCompletion(endNode);
                return null;
            }
        });
    }
    public Promise<Void, Throwable, Void> restore() {
        return adm.when(new DeferredAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackgroundSafe(Void... voids) throws Exception {
                api.restore();
                return null;
            }
        });
    }
    public Promise<Void, Throwable, Void> replaceEndNode(final String endNodeThingID, final String endNodeVenderThingID) {
        return adm.when(new DeferredAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackgroundSafe(Void... voids) throws Exception {
                api.replaceEndNode(endNodeThingID, endNodeVenderThingID);
                return null;
            }
        });
    }
    public Promise<GatewayInformation, Throwable, Void> getGatewayInformation() {
        return adm.when(new DeferredAsyncTask<Void, Void, GatewayInformation>() {
            @Override
            protected GatewayInformation doInBackgroundSafe(Void... voids) throws Exception {
                return api.getGatewayInformation();
            }
        });
    }
}
