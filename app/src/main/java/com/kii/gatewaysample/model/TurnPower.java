package com.kii.gatewaysample.model;

import com.kii.thingif.command.Action;

public class TurnPower extends Action {
    public boolean power;
    public TurnPower() {
    }
    public TurnPower(boolean power) {
        this.power = power;
    }
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
