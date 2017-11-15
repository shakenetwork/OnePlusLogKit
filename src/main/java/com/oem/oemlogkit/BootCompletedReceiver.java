package com.oem.oemlogkit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.SystemService;
import android.util.Log;
import android.util.Slog;

import java.io.IOException;
import java.io.OutputStream;

import libcore.io.IoUtils;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final int IDLE_REQUEST = 0;
    private static final String MDIAG_SERVICE = "diag_mdlog";
    private static final String MDIAG_SERVICE_START = "diag_mdlog_start";
    private static final String MDIAG_SERVICE_STOP = "diag_mdlog_stop";
    private static boolean WifiCloseStatus = false;
    private static PendingIntent mIdleIntent;
    private final String TAG = "OEMLogkit_BootCompletedReceiver";
    private final byte[] buf = new byte[1024];
    private AlarmManager mAlarmManager;
    private Context mContext;
    private OutputStream mOut;
    private WifiManager mWifiManager;
    private String res = "";

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.d("OEMLogkit_BootCompletedReceiver", "BOOT_COMPLETED");

        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("OEMLogkit_BootCompletedReceiver", "BOOT_COMPLETED");

            if (PropertiesUtil.get("persist.sys.assert.panic", "").equals("true")) {
                Log.d("OEMLogkit_BootCompletedReceiver", "persist.sys.assert.panic is true!!!");
                startRecordNmea();
            }

            mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            mIdleIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.android.alarm", null), 0);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, mIdleIntent);

            if (PropertiesUtil.get("persist.sys.bugreport", "no").equals("yes")) {
                context.startService(new Intent(context, RecordBugreportService.class));
            }

            if (PropertiesUtil.get("persist.sys.assert.panic", "").equals("true") &&
                    PropertiesUtil.get("persist.oem.wifi.debug", "0").equals("1")) {
                mWifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    Log.d("OEMLogkit_BootCompletedReceiver", "BOOT_COMPLETED---WIFI_SNIFFLOG_START");
                    new Thread(new Runnable() {
                        public void run() {
                            BootCompletedReceiver.transact("WIFI_SNIFFLOG_START");
                        }
                    }).start();
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    WifiCloseStatus = true;
                }
            }
        }

        if (action.equals("com.android.alarm")) {
            Log.d("OEMLogkit_BootCompletedReceiver", "com.android.alarm");
        }

        if (action.equals("com.oem.oemlogkit.getLog")) {
            Log.d("OEMLogkit_BootCompletedReceiver", "com.oem.oemlogkit.getLog");
            if (PropertiesUtil.get("persist.sys.assert.panic", "").equals("false")) {
                new Thread(new Runnable() {
                    public void run() {
                        PropertiesUtil.set("persist.sys.assert.panic", "true");
                        BootCompletedReceiver.this.transact("LOGBEGIN");
                        BootCompletedReceiver.this.startRecordNmea();
                        BootCompletedReceiver.this.startNfclog();
                    }
                }).start();
            }
            if (PropertiesUtil.get("persist.oem.dump", "").equals("0")) {
                PropertiesUtil.set("persist.oem.dump", "1");
            }
        }

        if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            int wifiState = intent.getIntExtra("wifi_state", -1);
            if (wifiState == 1) {
                WifiCloseStatus = true;
            } else if (wifiState == 3 && WifiCloseStatus &&
                    PropertiesUtil.get("persist.sys.assert.panic", "").equals("true") &&
                    PropertiesUtil.get("persist.oem.wifi.debug", "0").equals("1")) {
                Log.d("OEMLogkit_BootCompletedReceiver", "WiFi Enabled,WIFI_SNIFFLOG_START");
                WifiCloseStatus = false;
                new Thread(new Runnable() {
                    public void run() {
                        BootCompletedReceiver.this.transact("WIFI_SNIFFLOG_START");
                    }
                }).start();
            }
        }
    }

    private void startNfclog() {
        Log.d("OEMLogkit_BootCompletedReceiver", "startNfclog!!");
        PropertiesUtil.set("persist.oem.nfc.debug", "1");
        transact("OEM_NFC_DEBUG_ENABLE");
    }

    public synchronized String transact(String cmd) {
        if (connect()) {
            if (!writeCommand(cmd)) {
                Slog.e("OEMLogkit_BootCompletedReceiver", "write command failed? reconnect!");
                if (!(connect() && writeCommand(cmd))) {
                    return "-1";
                }
            }
            Slog.i("OEMLogkit_BootCompletedReceiver", "send: '" + cmd + "'");
            return "1";
        }
        Slog.e("OEMLogkit_BootCompletedReceiver", "connection failed");
        return "-1";
    }

    private boolean connect() {
        try {
            mOut = OEMLogKitMainActivity.getmSocket().getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean writeCommand(String cmdString) {
        byte[] cmd = cmdString.getBytes();
        int len = cmd.length;
        if (len < 1 || len > buf.length) {
            return false;
        }
        buf[0] = (byte) (len & 255);
        buf[1] = (byte) ((len >> 8) & 255);
        try {
            mOut.write(buf, 0, 2);
            mOut.write(cmd, 0, len);
            Log.d("OEMLogkit_BootCompletedReceiver", "cmd len is " + len);
            mOut.flush();
            return true;
        } catch (IOException e) {
            Slog.e("OEMLogkit_BootCompletedReceiver", "write error");
            disconnect();
            return false;
        }
    }

    public void disconnect() {
        Slog.i("OEMLogkit_BootCompletedReceiver", "disconnecting...");
        IoUtils.closeQuietly(OEMLogKitMainActivity.getmSocket());
        IoUtils.closeQuietly(mOut);
        OEMLogKitMainActivity.setmSocket(null);
        mOut = null;
    }

    private void startRecordNmea() {
        mContext.startService(new Intent(mContext, RecordNmeaService.class));
    }

    private void startOrStopService() {
        if (isDeviceLogEnabled()) {
            Log.d("OEMLogkit_BootCompletedReceiver", "DiagEnabled--true");
            SystemService.start(MDIAG_SERVICE_START);
            return;
        }
        Log.d("OEMLogkit_BootCompletedReceiver", "DiagEnabled--false");
        SystemService.stop(MDIAG_SERVICE_STOP);
    }

    private boolean isDeviceLogEnabled() {
        Log.d("OEMLogkit_BootCompletedReceiver", "isDeviceLogEnabled()");
        return PropertiesUtil.get("persist.sys.qxdm", "no").equals("yes");
    }
}
