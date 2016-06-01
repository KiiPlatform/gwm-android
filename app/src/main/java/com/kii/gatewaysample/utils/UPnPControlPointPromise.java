package com.kii.gatewaysample.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.kii.gatewaysample.model.UPnPService;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class UPnPControlPointPromise {
    private AndroidDeferredManager adm;
    private Context ctx;
    public static final int BUFFER_SIZE = 1024;


    public UPnPControlPointPromise(Context ctx){
        this.adm = new AndroidDeferredManager();
        this.ctx = ctx;
    }

    public Promise<UPnPService[], Throwable, Void> discover(final String serviceType){
        return adm.when(new DeferredAsyncTask<Void, Void, UPnPService[]>() {
            @Override
            protected UPnPService[] doInBackgroundSafe(Void... params) throws Exception {
                ArrayList<UPnPService> services = new ArrayList<UPnPService>();
                WifiManager wifi = (WifiManager)ctx.getSystemService( ctx.getApplicationContext().WIFI_SERVICE );

                if(wifi != null) {

                    WifiManager.MulticastLock lock = wifi.createMulticastLock("The Lock");
                    lock.acquire();

                    DatagramSocket socket = null;

                    try {
                        // group address for SSDP
                        InetAddress group = InetAddress.getByName("239.255.255.250");
                        int port = 1900;
                        String ST;
                        if (serviceType != null){
                            ST = serviceType;
                        }else{
                            ST = "ssdp:all";
                        }
                        String query =
                                "M-SEARCH * HTTP/1.1\r\n" +
                                        "HOST: "+group+":"+port+"\r\n"+
                                        "MAN: \"ssdp:discover\"\r\n"+
                                        "MX: 1\r\n"+
                                        "ST: "+ST+"\r\n"+
                                        "\r\n";

                        socket = new DatagramSocket(port);
                        socket.setReuseAddress(true);

                        DatagramPacket dgram = new DatagramPacket(query.getBytes(), query.length(),
                                group, port);
                        socket.send(dgram);

                        long time = System.currentTimeMillis();
                        long curTime = System.currentTimeMillis();

                        // Let's consider all the responses we can get in 1 second
                        while (curTime - time < 1000) {
                            DatagramPacket p = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                            socket.receive(p);
                            byte[] data = p.getData();

                            // check status first
                            String status = new String(data, 0, 12);
                            if (!status.toUpperCase().equals("HTTP/1.1 200")) {
                                continue;
                            }

                            // get the content data
                            int i ;
                            for(i = 0; i<data.length; i++){
                                if(data[i] == '\0'){
                                    break;
                                }
                            }

                            // Skip status string
                            String contentString = new String(data, 12, i);
                            UPnPService foundService = parseService(contentString);
                            if (foundService != null) {
                                services.add(foundService);
                            }
                            curTime = System.currentTimeMillis();
                        }

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        socket.close();
                    }
                    lock.release();
                }
                UPnPService[] array = services.toArray(new UPnPService[0]);
                return array;
            }
        });
    }

    private UPnPService parseService(String data) {
        UPnPService service = null;

        InputStream is = new ByteArrayInputStream(data.getBytes());
        HashMap<String, String> headers = new HashMap<String, String>();
        try {
            headers = HttpParser.parseHeaders(is, "UTF-8");
            if (headers.size() > 0) {
                service = UPnPService.serviceWithHeaders(headers);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return service;
    }
}
