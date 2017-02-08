package org.ttnmapper.ttnmapperv2;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jpmeijers on 28-1-17.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication singleton;
    public ArrayList<TTNApplication> ttnApplications = new ArrayList<>();
    public TTNApplication chosenTtnApplication = null;
    public ArrayList<Measurement> measurements = new ArrayList<>();
    private boolean shouldUpload;
    private boolean isExperiment;
    private boolean saveToFile;
    private String fileName;
    private String experimentName;
    private String ttnApplicationId = "";
    private String ttnDeviceId = "";
    private String ttnAccessKey = "";
    private String ttnBroker = "";
    private double latestLat = 0;
    private double latestLon = 0;
    private double latestAlt = 0;
    private double latestAcc = 0;
    private boolean lordriveMode = true;
    private boolean autoCenter = true;
    private boolean autoZoom = true;
    private String latestProvider = "none";
    private OkHttpClient httpClient = new OkHttpClient();

    public static MyApplication getInstance(){
        return singleton;
    }

    public boolean isLordriveMode() {
        return lordriveMode;
    }

    public void setLordriveMode(boolean lordriveMode) {
        this.lordriveMode = lordriveMode;
    }

    public boolean isAutoCenter() {
        return autoCenter;
    }

    public void setAutoCenter(boolean autoCenter) {
        this.autoCenter = autoCenter;
    }

    public boolean isAutoZoom() {
        return autoZoom;
    }

    public void setAutoZoom(boolean autoZoom) {
        this.autoZoom = autoZoom;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        ttnApplicationId = myPrefs.getString("ttnApplicationId", "");
        ttnDeviceId = myPrefs.getString("ttnDeviceId", "");
        ttnAccessKey = myPrefs.getString("ttnAccessKey", "");
        ttnBroker = myPrefs.getString("ttnBroker", "");

        shouldUpload = myPrefs.getBoolean("shouldUpload", true);
        isExperiment = myPrefs.getBoolean("isExperiment", false);
        saveToFile = myPrefs.getBoolean("saveToFile", true);

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        experimentName = myPrefs.getString("experimentName", "experiment_" + nowAsISO);
        fileName = "ttnmapper-" + nowAsISO + ".log";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getTtnApplicationId() {
        return ttnApplicationId;
    }

    public void setTtnApplicationId(String ttnApplicationId) {
        this.ttnApplicationId = ttnApplicationId;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("ttnApplicationId", ttnApplicationId);
        prefsEditor.apply();
    }

    public String getTtnDeviceId() {
        return ttnDeviceId;
    }

    public void setTtnDeviceId(String ttnDeviceId) {
        this.ttnDeviceId = ttnDeviceId;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("ttnDeviceId", ttnDeviceId);
        prefsEditor.apply();
    }

    public String getTtnAccessKey() {
        return ttnAccessKey;
    }

    public void setTtnAccessKey(String ttnAccessKey) {
        this.ttnAccessKey = ttnAccessKey;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("ttnAccessKey", ttnAccessKey);
        prefsEditor.apply();
    }

    public String getTtnBroker() {
        return ttnBroker;
    }

    public void setTtnBroker(String ttnBroker) {
        this.ttnBroker = ttnBroker;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("ttnBroker", ttnBroker);
        prefsEditor.apply();
    }

    public boolean isShouldUpload() {
        return shouldUpload;
    }

    public void setShouldUpload(boolean shouldUpload) {
        this.shouldUpload = shouldUpload;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean("shouldUpload", shouldUpload);
        prefsEditor.apply();
    }

    public boolean isExperiment() {
        return isExperiment;
    }

    public void setExperiment(boolean experiment) {
        isExperiment = experiment;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean("isExperiment", isExperiment);
        prefsEditor.apply();
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("experimentName", experimentName);
        prefsEditor.apply();
    }

    public double getLatestAcc() {
        return latestAcc;
    }

    public void setLatestAcc(double latestAcc) {
        this.latestAcc = latestAcc;
    }

    public double getLatestAlt() {
        return latestAlt;
    }

    public void setLatestAlt(double latestAlt) {
        this.latestAlt = latestAlt;
    }

    public double getLatestLat() {
        return latestLat;
    }

    public void setLatestLat(double latestLat) {
        this.latestLat = latestLat;
    }

    public double getLatestLon() {
        return latestLon;
    }

    public void setLatestLon(double latestLon) {
        this.latestLon = latestLon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSaveToFile() {
        return saveToFile;
    }

    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean("saveToFile", saveToFile);
        prefsEditor.apply();
    }

    public String getLatestProvider() {
        return latestProvider;
    }

    public void setLatestProvider(String latestProvider) {
        this.latestProvider = latestProvider;
    }

    //is configured
    public boolean isConfigured()
    {
        return !(ttnApplicationId.equals("") || ttnDeviceId.equals("") || ttnAccessKey.equals("") || ttnBroker.equals(""));
    }

    //check if we have all the neccesary permissions
    public boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (saveToFile && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public void logPacket(String topic, String payload) {
        Log.d(TAG, "Logging rx packet");

        if (latestLat == 0 || latestLon == 0) {
            //we do not know our location yet
            return;
        }

        /*
        {
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
            "gateways":
              [
                {
                  "gtw_id":"eui-1dee039aac75c307",
                  "timestamp":1401010363,
                  "time":"",
                  "channel":0,
                  "rssi":-108,
                  "snr":-5,
                  "rf_chain":1,
                  "latitude":52.2388,
                  "longitude":6.8551,
                  "altitude":6
                }
              ]
            }
          }
         */
        try {
            JSONObject packetData = new JSONObject(payload);
            JSONObject metadata = packetData.getJSONObject("metadata");
            JSONArray gateways = metadata.getJSONArray("gateways");

            double maxRssi = 0;
            for (int i = 0; i < gateways.length(); i++) {
                JSONObject gateway = gateways.getJSONObject(i);
                if (maxRssi == 0 || maxRssi < gateway.getDouble("rssi")) {
                    maxRssi = gateway.getDouble("rssi");
                }
            }

            for (int i = 0; i < gateways.length(); i++) {
                JSONObject gateway = gateways.getJSONObject(i);

                Measurement measurement = new Measurement();
                measurement.setTime(metadata.getString("time"));
                measurement.setNodeaddr(packetData.getString("dev_id"));
                measurement.setGwaddr(gateway.getString("gtw_id"));
                measurement.setSnr(gateway.getDouble("snr"));
                measurement.setRssi(gateway.getDouble("rssi"));
                measurement.setFreq(metadata.getDouble("frequency"));
                measurement.setLat(latestLat);
                measurement.setLon(latestLon);
                measurement.setDatarate(metadata.getString("data_rate"));
                measurement.setAppeui(packetData.getString("app_id"));
                measurement.setAlt(latestAlt);
                measurement.setAccuracy(latestAcc);
                measurement.setProvider(latestProvider);
                measurement.setMqtt_topic(topic);

                //save details for plotting on map
                measurement.setMaxRssi(maxRssi);
                measurement.setGwlat(gateway.getDouble("latitude"));
                measurement.setGwlon(gateway.getDouble("longitude"));

                measurements.add(measurement);

                if (shouldUpload) {
                    uploadMeasurement(measurement);
                    uploadGateway(gateway);
                }
                if (saveToFile) {
                    saveMeasurementToFIle(measurement);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Parsing packet payload failed with a json error");
        }
    }

    private void saveMeasurementToFIle(Measurement measurement) {
        Log.d(TAG, "Saving to file");

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
        File root = android.os.Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
        File dir = new File(root.getAbsolutePath() + "/ttnmapper_logs");
        dir.mkdirs();
        File file = new File(dir, fileName);

        try {
            final FileOutputStream f = new FileOutputStream(file, true);
            final PrintWriter pw = new PrintWriter(f);
            pw.println(measurement.getJSON().toString().trim());
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Call postToServer(String url, String json, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void uploadMeasurement(Measurement measurement) {
        Log.d(TAG, "Uploading: " + measurement.getJSON().toString());

        JSONObject toPost = measurement.getJSON();

        //set the app instance ID (https://developers.google.com/instance-id/)
        try {
            toPost.put("iid", InstanceID.getInstance(getApplicationContext()).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //add version name of this app
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            final String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            try {
                toPost.put("user_agent", "Android" + android.os.Build.VERSION.RELEASE + " App" + verCode + ":" + version);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //the only difference between a normal upload and an experiment is the experiment name parameter
        if (isExperiment) {
            try {
                toPost.put("experiment", experimentName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //post packet
        try {
            postToServer(getString(R.string.ttnmapper_api_upload_packet), toPost.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "Error uploading");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String returnedString = response.body().string();
                        System.out.println("HTTP response: " + returnedString);
                        if (!returnedString.contains("New records created successfully")) {
                            // Request not successful
                            Log.d(TAG, "server error: " + returnedString);
                        }
                        // Do what you want to do with the response.
                    } else {
                        // Request not successful
                        Log.d(TAG, "server error");
                    }
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "HTTP call IO exception");
            e.printStackTrace();
        }
    }

    private void uploadGateway(JSONObject toPost) {
        //set the app instance ID (https://developers.google.com/instance-id/)
        try {
            toPost.put("iid", InstanceID.getInstance(getApplicationContext()).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //add version name of this app
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            final String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            try {
                toPost.put("user_agent", "Android" + android.os.Build.VERSION.RELEASE + " App" + verCode + ":" + version);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //the only difference between a normal upload and an experiment is the experiment name parameter
        if (isExperiment) {
            try {
                toPost.put("experiment", experimentName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //post packet
        try {
            postToServer(getString(R.string.ttnmapper_api_upload_gateway), toPost.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "Error uploading");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String returnedString = response.body().string();
                        System.out.println("HTTP response: " + returnedString);
                        if (!returnedString.contains("New records created successfully")) {
                            // Request not successful
                            Log.d(TAG, "server error: " + returnedString);
                        }
                        // Do what you want to do with the response.
                    } else {
                        // Request not successful
                        Log.d(TAG, "server error");
                    }
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "HTTP call IO exception");
            e.printStackTrace();
        }
    }


}
