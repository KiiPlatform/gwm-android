package com.kii.gatewaysample.utils;

import android.util.Pair;

import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.Target;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.PendingEndNode;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggeredServerCodeResult;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class IoTCloudPromiseAPIWrapper {

    private AndroidDeferredManager adm;
    private ThingIFAPI api;

    public IoTCloudPromiseAPIWrapper(ThingIFAPI api) {
        this.adm = new AndroidDeferredManager();
        this.api = api;
    }

    public IoTCloudPromiseAPIWrapper(AndroidDeferredManager manager, ThingIFAPI api) {
        this.adm = manager;
        this.api = api;
    }

    public Promise<Target, Throwable, Void> onboard(final String thingID, final String thingPassword) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboard(thingID, thingPassword);
            }
        });
    }
    public Promise<EndNode, Throwable, Void> onboardEndnodeWithGateway(final PendingEndNode pendingEndNode, final String thingPassword) {
        return adm.when(new DeferredAsyncTask<Void, Void, EndNode>() {
            @Override
            protected EndNode doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboardEndnodeWithGateway(pendingEndNode, thingPassword);
            }
        });
    }
    public Promise<List<Command>, Throwable, Void> listCommands() {
        return adm.when(new DeferredAsyncTask<Void, Void, List<Command>>() {
            @Override
            protected List<Command> doInBackgroundSafe(Void... voids) throws Exception {
                List<Command> commands = new ArrayList<Command>();
                String paginationKey = null;
                do {
                    Pair<List<Command>, String> result = api.listCommands(0, paginationKey);
                    commands.addAll(result.first);
                    paginationKey = result.second;
                } while (paginationKey != null);
                return commands;
            }
        });
    }
    public Promise<Command, Throwable, Void> postNewCommand(final String schemaName, final int schemaVersion, final List<Action> actions) {
        return adm.when(new DeferredAsyncTask<Void, Void, Command>() {
            @Override
            protected Command doInBackgroundSafe(Void... voids) throws Exception {
                return api.postNewCommand(schemaName, schemaVersion, actions);
            }
        });
    }

    public Promise<List<Trigger>, Throwable, Void> listTriggers() {
        return adm.when(new DeferredAsyncTask<Void, Void, List<Trigger>>() {
            @Override
            protected List<Trigger> doInBackgroundSafe(Void... voids) throws Exception {
                List<Trigger> triggers = new ArrayList<Trigger>();
                String paginationKey = null;
                do {
                    Pair<List<Trigger>, String> result = api.listTriggers(0, paginationKey);
                    triggers.addAll(result.first);
                    paginationKey = result.second;
                } while (paginationKey != null);
                return triggers;
            }
        });
    }

    public Promise<Trigger, Throwable, Void> postNewTrigger(
            final String schemaName,
            final int schemaVersion,
            final List<Action> actions,
            final Predicate predicate) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.postNewTrigger(schemaName, schemaVersion, actions, predicate);
            }
        });
    }
    public Promise<Trigger, Throwable, Void> postNewTrigger(
            final ServerCode serverCode,
            final Predicate predicate) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.postNewTrigger(serverCode, predicate);
            }
        });
    }

    public Promise<Trigger, Throwable, Void> patchTrigger(
            final String triggerID,
            final String schemaName,
            final int schemaVersion,
            final List<Action> actions,
            final Predicate predicate) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.patchTrigger(triggerID, schemaName, schemaVersion, actions, predicate);
            }
        });
    }
    public Promise<Trigger, Throwable, Void> patchTrigger(
            final String triggerID,
            final ServerCode serverCode,
            final Predicate predicate) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.patchTrigger(triggerID, serverCode, predicate);
            }
        });
    }

    public Promise<Trigger, Throwable, Void> enableTrigger(
            final String triggerID,
            final boolean enable) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.enableTrigger(triggerID, enable);
            }
        });
    }

    public Promise<String, Throwable, Void> deleteTrigger(
            final String triggerID) {
        return adm.when(new DeferredAsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackgroundSafe(Void... voids) throws Exception {
                return api.deleteTrigger(triggerID);
            }
        });
    }

    public Promise<List<TriggeredServerCodeResult>, Throwable, Void> listTriggeredServerCodeResults(final String triggerID) {
        return adm.when(new DeferredAsyncTask<Void, Void, List<TriggeredServerCodeResult>>() {
            @Override
            protected List<TriggeredServerCodeResult> doInBackgroundSafe(Void... voids) throws Exception {
                List<TriggeredServerCodeResult> results = new ArrayList<TriggeredServerCodeResult>();
                String paginationKey = null;
                do {
                    Pair<List<TriggeredServerCodeResult>, String> result = api.listTriggeredServerCodeResults(triggerID, 0, paginationKey);
                    results.addAll(result.first);
                    paginationKey = result.second;
                } while (paginationKey != null);
                return results;
            }
        });
    }

}
