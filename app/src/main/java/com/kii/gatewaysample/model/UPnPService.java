package com.kii.gatewaysample.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UPnPService implements Parcelable {
    final String location;
    final String date;
    final String server;
    final String st;
    final String usn;
    final long maxAge;

    public UPnPService(long maxAge,
                       String date,
                       String location,
                       String server,
                       String st,
                       String usn){
        this.location = location;
        this.date = date;
        this.server = server;
        this.st = st;
        this.usn = usn;
        this.maxAge = maxAge;
    }

    protected UPnPService(Parcel in) {
        location = in.readString();
        date = in.readString();
        server = in.readString();
        st = in.readString();
        usn = in.readString();
        maxAge = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(server);
        dest.writeString(st);
        dest.writeString(usn);
        dest.writeLong(maxAge);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UPnPService> CREATOR = new Creator<UPnPService>() {
        @Override
        public UPnPService createFromParcel(Parcel in) {
            return new UPnPService(in);
        }

        @Override
        public UPnPService[] newArray(int size) {
            return new UPnPService[size];
        }
    };

    public static UPnPService serviceWithHeaders(HashMap<String, String> headers) {
        String date = headers.get("DATE");
        String location = headers.get("LOCATION");
        String server = headers.get("SERVER");
        String st = headers.get("ST");
        String usn = headers.get("USN");
        long maxAge = 0;
        String cacheControl = headers.get("CACHE-CONTROL");
        if(cacheControl != null){
            if (cacheControl.startsWith("max-age=")) {
                maxAge = Long.parseLong(cacheControl.substring(8));
            }
        }
        return new UPnPService(maxAge, date, location, server, st, usn);
    }

    public String getLocation() {
        return location;
    }

    public String getServer() {
        return server;
    }

    public String getDate() {
        return date;
    }

    public String getSt() {
        return st;
    }

    public String getUsn() {
        return usn;
    }

    public String toString() {
        HashMap<String, Object> serviceMap = new HashMap<String, Object>();
        serviceMap.put("MaxAge", maxAge);
        serviceMap.put("Location", location);
        serviceMap.put("Date", date);
        serviceMap.put("Server", server);
        serviceMap.put("ST", st);
        serviceMap.put("USN", usn);
        return serviceMap.toString();
    }
}
