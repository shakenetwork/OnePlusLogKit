package com.oem.oemlogkit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class RecordBugreportService extends Service {
    public static final String BATTERY_LEVEL = "level";
    private boolean D = true;
    private final String TAG = "RecordNmeaService";
    private boolean getBugreportDone = false;
    private MyReceiver myRec = null;

    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i("RecordNmeaService", "intent" + intent);
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int nLevel = intent.getIntExtra("level", 0);
                Log.i("RecordNmeaService", "battery level : " + nLevel);
                if (nLevel == 2 && !RecordBugreportService.this.getBugreportDone) {
                    Log.i("RecordNmeaService", "get  bugreport begin");
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.sendMsg("bugreport");
                        }
                    }).start();
                    RecordBugreportService.this.getBugreportDone = true;
                    Log.i("RecordNmeaService", "get  bugreport end");
                }
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        if (this.D) {
            Log.d("RecordNmeaService", "RecordBugreportService onCreate");
        }
        if (PropertiesUtil.get("persist.sys.bugreport", "no").equals("yes")) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            this.myRec = new MyReceiver();
            registerReceiver(this.myRec, filter);
        }
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.D) {
            Log.d("RecordNmeaService", "RecordBugreportService onDestory");
        }
        if (this.myRec != null) {
            unregisterReceiver(this.myRec);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
