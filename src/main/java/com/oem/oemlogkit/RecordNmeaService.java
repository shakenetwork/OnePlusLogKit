package com.oem.oemlogkit;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class RecordNmeaService extends Service {
    private boolean D = true;
    private final String TAG = "RecordNmeaService";
    private LocationManager mLocationManager;
    private OPRecordNmea mOPRecordNmea;

    public void onCreate() {
        super.onCreate();
        if (this.D) {
            Log.d("RecordNmeaService", "RecordNmeaService onCreate");
        }
        this.mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.mOPRecordNmea = new OPRecordNmea(this);
        this.mOPRecordNmea.startRecord();
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.D) {
            Log.d("RecordNmeaService", "RecordNmeaService onDestory");
        }
        this.mOPRecordNmea.stopRecord();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
