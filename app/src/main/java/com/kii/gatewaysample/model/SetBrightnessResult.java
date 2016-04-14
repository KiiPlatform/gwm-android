package com.kii.gatewaysample.model;

import com.kii.thingif.command.ActionResult;

public class SetBrightnessResult extends ActionResult {
    @Override
    public String getActionName() {
        return "setBrightness";
    }
}
