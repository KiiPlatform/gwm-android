package com.kii.gatewaysample.model;

import android.content.Context;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Site;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.Owner;
import com.kii.thingif.TypedID;
import com.kii.thingif.schema.SchemaBuilder;

public class ApiBuilder {

    public static final String SCHEMA_NAME = "Smart-Light-Demo";
    public static final int SCHEMA_VERSION = 1;

    public static ThingIFAPI buildApi(Context context, KiiUser owner) {
        String appId = Kii.getAppId();
        String appKey = Kii.getAppKey();
        // TODO:Gets Site from KiiCloudSDK directly
        Site site = null;
        if ("https://api-jp.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.JP;
        } else if ("https://api.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.US;
        } else if ("https://api-sg.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.SG;
        } else if ("https://api-cn3.kii.com/api".equals(Kii.getBaseURL())) {
            site = Site.CN3;
        }
        KiiApp app = new KiiApp(appId, appKey, site);
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(context, app, new Owner(new TypedID(TypedID.Types.USER, owner.getID()), owner.getAccessToken()));
        SchemaBuilder schemaBuilder = SchemaBuilder.newSchemaBuilder("SmartLight-Demo", SCHEMA_NAME, SCHEMA_VERSION, LightState.class);
        schemaBuilder.addActionClass(TurnPower.class, TurnPowerResult.class);
        schemaBuilder.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        schemaBuilder.addActionClass(SetColor.class, SetColorResult.class);
        schemaBuilder.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        builder.addSchema(schemaBuilder.build());
        return builder.build();
    }

}
