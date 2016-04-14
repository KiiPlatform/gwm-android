package com.kii.gatewaysample.model;

import com.kii.thingif.command.ActionResult;

public class TurnPowerResult extends ActionResult {
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
