package com.kii.gatewaysample.model;

import com.kii.thingif.command.ActionResult;

public class SetColorTemperatureResult extends ActionResult{
    @Override
    public String getActionName() {
        return "setColorTemperature";
    }
}
