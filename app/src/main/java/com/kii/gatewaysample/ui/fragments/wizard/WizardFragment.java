package com.kii.gatewaysample.ui.fragments.wizard;

import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

public abstract class WizardFragment extends Fragment {

    public static final int EXIT_NEXT = 1;
    public static final int EXIT_PREVIOUS = 2;

    public interface WizardController {
        public void setNextButtonEnabled(boolean enabled);
    }

    private WeakReference<WizardController> controller;

    public void setController(WizardController controller) {
        this.controller = new WeakReference<WizardController>(controller);
    }
    protected void setNextButtonEnabled(boolean enabled) {
        if (this.controller != null && this.controller.get() != null) {
            this.controller.get().setNextButtonEnabled(enabled);
        }
    }

    public abstract String getNextButtonText();
    public abstract String getPreviousButtonText();
    public abstract void execute() throws Exception;
    public void onActivate() {
    }
    public void onInactivate(int exitCode) {
    }
}
