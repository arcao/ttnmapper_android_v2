package org.ttnmapper.ttnmapperv2;

import android.location.Location;
import android.text.TextUtils;

import org.ttnmapper.ttnmapperv2.util.ISO8601DateParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jpmeijers on 7-2-17.
 */

public class Packet {
    static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    /*
          "app_id":"jpm_testing",
          "dev_id":"arduino_uno_rn2483",
          "hardware_serial":"00999B8A917DBB71",
          "port":1,
          "counter":123,
          "payload_raw":"IQ==",
          "metadata":{
            "time":"2017-02-07T11:03:29.086549185Z",
            "frequency":868.1,
            "modulation":"LORA",
            "data_rate":"SF7BW125",
            "coding_rate":"4/5",
     */
    private String appID;
    private String deviceID;

    private String time;
    private double frequency;
    private String modulation;
    private String dataRate;
    private String codingRate;

    private ArrayList<Gateway> gateways = new ArrayList<>();
    private double maxRssi = 0;
    private double maxSnr = 100;
    private double maxDistance = 0;

    private double latitude;
    private double longitude;
    private double altitude;
    private double accuracy;
    private String provider;
    private String mqttTopic;
    private String formattedTime;

    public String getModulation() {
        return modulation;
    }

    public void setModulation(String modulation) {
        this.modulation = modulation;
    }

    public String getCodingRate() {
        return codingRate;
    }

    public void setCodingRate(String codingRate) {
        this.codingRate = codingRate;
    }

    public double getMaxRssi() {
        //we assume here that the max rssi only needs to be calculated once
        //as no more gateways will be added than was added at the start
        if (maxRssi == 0) {
            for (Gateway gateway : gateways) {
                if (maxRssi == 0 || gateway.getRssi() > maxRssi) {
                    maxRssi = gateway.getRssi();
                }
            }
        }
        return maxRssi;
    }

    public double getMaxSnr() {
        if (maxSnr == 100) {
            for (Gateway gateway : gateways) {
                if (maxSnr == 100 || gateway.getSnr() > maxSnr) {
                    maxSnr = gateway.getSnr();
                }
            }
        }
        return maxSnr;
    }

    public double getMaxDistance() {
        if (maxDistance == 0) {
            for (Gateway gateway : gateways) {
                double distance;
                if (gateway.getLatitude() == 0 || gateway.getLongitude() == 0 || latitude == 0 || longitude == 0) {
                    distance = 0;
                } else {
                    Location locationA = new Location("");
                    locationA.setLatitude(gateway.getLatitude());
                    locationA.setLongitude(gateway.getLongitude());

                    Location locationB = new Location("");
                    locationB.setLatitude(latitude);
                    locationB.setLongitude(longitude);

                    distance = locationA.distanceTo(locationB);
                }
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }
        }
        return maxDistance;
    }

    public ArrayList<Gateway> getGateways() {
        return gateways;
    }

    public void setGateways(ArrayList<Gateway> gateways) {
        this.gateways = gateways;
    }

    public void addGateway(Gateway gateway) {
        if (gateways == null) {
            gateways = new ArrayList<>();
        }
        gateways.add(gateway);
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;

        if (TextUtils.isEmpty(time)) {
            this.formattedTime = DATE_TIME_FORMAT.format(new Date());
            return;
        }

        try {
            this.formattedTime = DATE_TIME_FORMAT.format(ISO8601DateParser.parse(time));
        } catch (ParseException e) {
            this.formattedTime = DATE_TIME_FORMAT.format(new Date());
        }
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDataRate() {
        return dataRate;
    }

    public void setDataRate(String dataRate) {
        this.dataRate = dataRate;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }


}
