package com.oem.oemlogkit;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.Slog;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import libcore.io.IoUtils;

public class ConnectivityGrab extends PreferenceActivity implements OnPreferenceClickListener {
    private static final String BTLOG_KEY = "bluetooth";
    private static final String BTLOG_PROP = "persist.sys.btlog.enable";
    private static final String BT_SNOOP_LOG_KEY = "bt_snoop";
    private static final String GPS_KEY = "gps";
    private static final String NFC_KEY = "nfc";
    private static final String TAG = "ConnectivityGrab";
    private static final String WIFI_KEY = "wifi";
    private final byte[] buf = new byte[1024];
    private CheckBoxPreference mBtLog;
    private CheckBoxPreference mBt_snoop_Log;
    private CheckBoxPreference mGpsLog;
    WifiManager mManager;
    NfcAdapter mNfcAdapter;
    private CheckBoxPreference mNfcLog;
    private OutputStream mOut;
    private CheckBoxPreference mWifiLog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.grab_connect);
        this.mBtLog = (CheckBoxPreference) findPreference(BTLOG_KEY);
        this.mBtLog.setOnPreferenceClickListener(this);
        this.mBt_snoop_Log = (CheckBoxPreference) findPreference(BT_SNOOP_LOG_KEY);
        this.mBt_snoop_Log.setOnPreferenceClickListener(this);
        this.mWifiLog = (CheckBoxPreference) findPreference(WIFI_KEY);
        this.mWifiLog.setOnPreferenceClickListener(this);
        this.mManager = (WifiManager) getSystemService(WIFI_KEY);
        this.mGpsLog = (CheckBoxPreference) findPreference(GPS_KEY);
        this.mGpsLog.setOnPreferenceClickListener(this);
        if (hasNfcFeature()) {
            this.mNfcLog = (CheckBoxPreference) findPreference(NFC_KEY);
            this.mNfcLog.setChecked(SystemProperties.getInt("persist.oem.nfc.debug", 0) > 0);
            this.mNfcLog.setOnPreferenceClickListener(this);
            this.mNfcAdapter = NfcAdapter.getDefaultAdapter();
            if (this.mNfcAdapter == null) {
                getPreferenceScreen().removePreference(this.mNfcLog);
            }
        }
        if (PropertiesUtil.get("persist.oem.bt.debug", "0").equals("1")) {
            this.mBtLog.setChecked(true);
        } else {
            this.mBtLog.setChecked(false);
        }
        if (Secure.getInt(getContentResolver(), "bluetooth_hci_log", 0) == 1) {
            this.mBt_snoop_Log.setChecked(true);
        } else {
            this.mBt_snoop_Log.setChecked(false);
        }
        if (PropertiesUtil.get("persist.oem.wifi.debug", "0").equals("1")) {
            this.mWifiLog.setChecked(true);
        } else {
            this.mWifiLog.setChecked(false);
        }
        if (PropertiesUtil.get("persist.oem.gps.debug", "0").equals("1")) {
            this.mGpsLog.setChecked(true);
        } else {
            this.mGpsLog.setChecked(false);
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.mWifiLog.isChecked()) {
            this.mManager.enableVerboseLogging(1);
        } else {
            this.mManager.enableVerboseLogging(0);
        }
    }

    private static boolean hasNfcFeature() {
        return new File("/dev/pn544").exists();
    }

    private void writeBtHciSnoopLogOptions() {
        BluetoothAdapter.getDefaultAdapter().configHciSnoopLog(this.mBt_snoop_Log.isChecked());
        Log.d(TAG, "BT_SNOOP_LOG_KEY writeBtHciSnoopLogOptions = " + this.mBt_snoop_Log.isChecked());
        Secure.putInt(getContentResolver(), "bluetooth_hci_log", this.mBt_snoop_Log.isChecked() ? 1 : 0);
    }

    public boolean onPreferenceClick(Preference preference) {
        boolean isChecked;
        if (preference.getKey().equals(BTLOG_KEY)) {
            int check = this.mBtLog.isChecked() ? 1 : 0;
            int mode = SystemProperties.getInt(BTLOG_PROP, 0);
            Log.d(TAG, "BTLog_KEY--mode=" + mode + ", --isChecked=" + Integer.toString(check));
            if (check != mode) {
                SystemProperties.set(BTLOG_PROP, Integer.toString(check));
                PropertiesUtil.set("persist.oem.bt.debug", check == 1 ? "1" : "0");
                transact(check == 1 ? "OEM_BT_DEBUG_ENABLE" : "OEM_BT_DEBUG_DISABLE");
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter.isEnabled()) {
                    Log.d(TAG, "BTLog_KEY--disable =" + String.valueOf(adapter.disable()));
                }
            }
        }
        if (preference.getKey().equals(BT_SNOOP_LOG_KEY)) {
            writeBtHciSnoopLogOptions();
        }
        if (preference.getKey().equals(WIFI_KEY)) {
            isChecked = this.mWifiLog.isChecked();
            Log.d(TAG, "WIFILog_KEY--isChecked=" + isChecked);
            if (isChecked) {
                this.mManager.enableVerboseLogging(1);
            } else {
                this.mManager.enableVerboseLogging(0);
            }
            boolean equals;
            if (PropertiesUtil.get("persist.sys.qxdm", "no").equals("yes")) {
                equals = PropertiesUtil.get("persist.sys.diag.type", "").equals("5");
            } else {
                equals = false;
            }
            if (isChecked || !r5) {
                PropertiesUtil.set("persist.oem.wifi.cnssdiag", isChecked ? "1" : "0");
            } else {
                Log.d(TAG, "Wlan QXDM is running");
            }
            PropertiesUtil.set("persist.oem.wifi.debug", isChecked ? "1" : "0");
            transact(isChecked ? "OEM_WIFI_DEBUG_ENABLE" : "OEM_WIFI_DEBUG_DISABLE");
        }
        if (preference.getKey().equals(NFC_KEY)) {
            isChecked = this.mNfcLog.isChecked();
            Log.d(TAG, "NFCLog_KEY--isChecked=" + isChecked);
            NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mNfcAdapter.isEnabled()) {
                Log.d(TAG, "NFC_KEY--disable =" + String.valueOf(mNfcAdapter.disable()));
            }
            PropertiesUtil.set("persist.oem.nfc.debug", isChecked ? "1" : "0");
            transact(isChecked ? "OEM_NFC_DEBUG_ENABLE" : "OEM_NFC_DEBUG_DISABLE");
        }
        if (preference.getKey().equals(GPS_KEY)) {
            isChecked = this.mGpsLog.isChecked();
            Log.d(TAG, "GPSLog_KEY--isChecked=" + isChecked);
            PropertiesUtil.set("persist.oem.gps.debug", isChecked ? "1" : "0");
            transact(isChecked ? "OEM_GPS_DEBUG_ENABLE" : "OEM_GPS_DEBUG_DISABLE");
        }
        return false;
    }

    private boolean connect() {
        try {
            this.mOut = OEMLogKitMainActivity.getmSocket().getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean writeCommand(String cmdString) {
        byte[] cmd = cmdString.getBytes();
        int len = cmd.length;
        if (len < 1 || len > this.buf.length) {
            return false;
        }
        this.buf[0] = (byte) (len & 255);
        this.buf[1] = (byte) ((len >> 8) & 255);
        try {
            this.mOut.write(this.buf, 0, 2);
            this.mOut.write(cmd, 0, len);
            return true;
        } catch (IOException e) {
            Slog.e(TAG, "write error");
            disconnect();
            return false;
        }
    }

    public synchronized String transact(String cmd) {
        if (connect()) {
            if (!writeCommand(cmd)) {
                Slog.e(TAG, "write command failed? reconnect!");
                if (!(connect() && writeCommand(cmd))) {
                    return "-1";
                }
            }
            Slog.i(TAG, "send: '" + cmd + "'");
            return "1";
        }
        Slog.e(TAG, "connection failed");
        return "-1";
    }

    public void disconnect() {
        Slog.i(TAG, "disconnecting...");
        IoUtils.closeQuietly(OEMLogKitMainActivity.getmSocket());
        IoUtils.closeQuietly(this.mOut);
        OEMLogKitMainActivity.setmSocket(null);
        this.mOut = null;
    }
}
