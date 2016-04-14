package com.kii.gatewaysample.model;

import com.kii.thingif.command.ActionResult;

public class SetColorResult extends ActionResult {
    @Override
    public String getActionName() {
        return "setColor";
    }
}
