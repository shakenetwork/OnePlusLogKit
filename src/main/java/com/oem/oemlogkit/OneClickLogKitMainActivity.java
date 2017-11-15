package com.oem.oemlogkit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.SystemService;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings.Secure;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import libcore.io.IoUtils;

public class OneClickLogKitMainActivity extends PreferenceActivity implements OnPreferenceClickListener {
    private static final String BT_KEY = "bluetooth";
    private static final int BT_QXDM_LOG_TYPE = 7;
    private static final String CLEANLOG_KEY = "cleanlog";
    private static final String CLOSELOG_KEY = "closelog";
    private static final int COMMAND_WRITELOG = 0;
    private static final int COMMON_QXDM_LOG_TYPE = 1;
    static final boolean D = false;
    private static final String GPS_KEY = "gps";
    private static final int GPS_QXDM_LOG_TYPE = 9;
    private static final String HOT_KEY = "hot_power_issue";
    private static final String KEY_FROM = "KEY_RENAME_FROM";
    private static final String KEY_TO = "KEY_RENAME_TO";
    private static final String LAG_KEY = "lag_issue";
    private static final String MDIAG_EXEC_PATH = "/system/bin/diag_mdlog";
    private static final String MDIAG_SERVICE_START = "diag_mdlog_start";
    private static final String MDIAG_SERVICE_STOP = "diag_mdlog_stop";
    private static final String MODEM_DATA_KEY = "modem_data_issue";
    private static final String MODEM_SIGNAL_KEY = "modem_signal_issue";
    private static final int MTYPE_DELETE_OLD_LOG = 10;
    private static final String NFC_KEY = "nfc";
    private static final int ONECLICK_AP_LOG_CLEAN = 4;
    private static final int ONECLICK_FULLLOG = 102;
    private static final int ONECLICK_LOG_CLEAN = 0;
    private static final int ONECLICK_LOG_CLOSE = 8;
    private static final int ONECLICK_LOG_NULL = 6;
    private static final int ONECLICK_LOG_PACK = 5;
    private static final int ONECLICK_NOLOG = 100;
    private static final int ONECLICK_PARTLOG = 101;
    private static final int ONECLICK_QXDM_LOG_CLEAN = 3;
    private static final int ONECLICK_QXDM_LOG_START = 2;
    private static final int ONECLICK_RENAMEWLAN = 204;
    private static final int ONECLICK_SAVEOLDLOG = 200;
    private static final int ONECLICK_WRITEDISABLEMSG = 201;
    private static final int ONECLICK_WRITEENABLEMSG = 202;
    private static final String PACKAGELOG_KEY = "packagelog";
    private static final String RESULT_RENAME = "RESULT_RENAME";
    private static final int SLEEP_TIME = 20000;
    static final String TAG = "OneClickLogKitMainActivity";
    private static final String WARNMESSAGE_KEY = "warnmessage";
    private static final String WIFI_KEY = "wifi";
    private static Context mContext;
    private static InputStream mIn;
    private static OutputStream mOut;
    private static WifiSniffThread sniffthread = null;
    private final byte[] buf = new byte[1024];
    private Preference mBtLog;
    private Preference mCleanLog;
    private Preference mCloseLog;
    private DataInputStream mDis;
    private Preference mGpsLog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Message message = Message.obtain(null, 0);
                    message.replyTo = new Messenger(OneClickLogKitMainActivity.this.mResultHandler);
                    message.arg1 = 0;
                    Bundle data = new Bundle();
                    data.putString(OneClickLogKitMainActivity.KEY_FROM, Environment.getExternalStorageDirectory().getPath() + "/oem_log/diag_logs/Diag.cfg");
                    data.putInt("type", OneClickLogKitMainActivity.this.type);
                    message.setData(data);
                    try {
                        OneClickLogKitMainActivity.this.mMessenger.send(message);
                        return;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return;
                    }
                case 4:
                    Toast.makeText(OneClickLogKitMainActivity.this, R.string.delete_log_success, 0).show();
                    OneClickLogKitMainActivity.this.mWifiLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mBtLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mNfcLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mGpsLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mHotLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mLagLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemSignalLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemDataLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCloseLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mPackageLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCleanLog.setEnabled(true);
                    return;
                case 5:
                    Toast.makeText(OneClickLogKitMainActivity.this, R.string.oneclick_pack_success, 0).show();
                    OneClickLogKitMainActivity.this.mWifiLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mBtLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mNfcLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mGpsLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mHotLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mLagLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemSignalLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemDataLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCloseLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mPackageLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCleanLog.setEnabled(true);
                    return;
                case 6:
                    Toast.makeText(OneClickLogKitMainActivity.this, R.string.oneclick_nulllog, 0).show();
                    OneClickLogKitMainActivity.this.mWifiLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mBtLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mNfcLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mGpsLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mHotLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mLagLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemSignalLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemDataLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCloseLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mPackageLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCleanLog.setEnabled(true);
                    return;
                case 8:
                    PropertiesUtil.set("persist.oem.wifi.cnssdiag", "0");
                    OneClickLogKitMainActivity.this.mWifiLog.setSummary("");
                    OneClickLogKitMainActivity.this.mBtLog.setSummary("");
                    OneClickLogKitMainActivity.this.mNfcLog.setSummary("");
                    OneClickLogKitMainActivity.this.mGpsLog.setSummary("");
                    OneClickLogKitMainActivity.this.mHotLog.setSummary("");
                    OneClickLogKitMainActivity.this.mLagLog.setSummary("");
                    OneClickLogKitMainActivity.this.mModemSignalLog.setSummary("");
                    OneClickLogKitMainActivity.this.mModemDataLog.setSummary("");
                    OneClickLogKitMainActivity.this.mWifiLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mBtLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mNfcLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mGpsLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mHotLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mLagLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemSignalLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mModemDataLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCleanLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mCloseLog.setEnabled(true);
                    OneClickLogKitMainActivity.this.mPackageLog.setEnabled(true);
                    return;
                default:
                    return;
            }
        }
    };
    private Preference mHotLog;
    private Preference mLagLog;
    private Messenger mMessenger;
    private Preference mModemDataLog;
    private Preference mModemSignalLog;
    private Preference mNfcLog;
    private Handler mOffHandler;
    private Timer mOffTime;
    private Preference mPackageLog;
    private Handler mResultHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle res = msg.getData();
            Log.d(OneClickLogKitMainActivity.TAG, "mResultHandler");
            if (res.getBoolean(OneClickLogKitMainActivity.RESULT_RENAME)) {
                Toast.makeText(OneClickLogKitMainActivity.this, OneClickLogKitMainActivity.this.getResources().getString(R.string.createfile_log_success), 0).show();
                if (OneClickLogKitMainActivity.this.isFileExists(OneClickLogKitMainActivity.MDIAG_EXEC_PATH)) {
                    SystemService.start(OneClickLogKitMainActivity.MDIAG_SERVICE_START);
                    return;
                } else {
                    Toast.makeText(OneClickLogKitMainActivity.this, "Feture not avaiable as exec file not exists", 0).show();
                    return;
                }
            }
            Toast.makeText(OneClickLogKitMainActivity.this, OneClickLogKitMainActivity.this.getResources().getString(R.string.createfile_log_fail), 0).show();
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            OneClickLogKitMainActivity.this.mMessenger = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            OneClickLogKitMainActivity.this.mMessenger = new Messenger(service);
        }
    };
    private Preference mWarnMessage;
    private Preference mWifiLog;
    private WifiManager mWifiManager;
    private ProcessHandler mpHandler = null;
    private HandlerThread processthread = null;
    private TextView textview;
    private int type = 0;

    private class ProcessHandler extends Handler {
        public ProcessHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OneClickLogKitMainActivity.ONECLICK_SAVEOLDLOG /*200*/:
                    OneClickLogKitMainActivity.this.saveOldLog();
                    return;
                case OneClickLogKitMainActivity.ONECLICK_WRITEDISABLEMSG /*201*/:
                    FileTransactionUtil.writeDisableMsg(OneClickLogKitMainActivity.mContext);
                    return;
                case OneClickLogKitMainActivity.ONECLICK_WRITEENABLEMSG /*202*/:
                    FileTransactionUtil.writeDataToFile(FileTransactionUtil.ENABLE_FLAG, 1, OneClickLogKitMainActivity.mContext);
                    return;
                case OneClickLogKitMainActivity.ONECLICK_RENAMEWLAN /*204*/:
                    OneClickLogKitMainActivity.this.renameWlanLog();
                    return;
                default:
                    return;
            }
        }
    }

    public class WifiSniffThread extends Thread {
        public void run() {
            try {
                sleep(20000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PropertiesUtil.set("persist.oem.wifi.cnssdiag", "0");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreat start");
        addPreferencesFromResource(R.xml.oneclick_connectivity_log);
        mContext = getApplicationContext();
        this.mWifiManager = (WifiManager) getSystemService(WIFI_KEY);
        connect();
        InitQXDMLog();
        this.type = getTypeFromFile();
        if (sniffthread == null) {
            sniffthread = new WifiSniffThread();
        }
        if (this.processthread == null) {
            this.processthread = new HandlerThread("OneClickProcessThread");
            this.processthread.start();
        }
        if (this.mpHandler == null) {
            this.mpHandler = new ProcessHandler(this.processthread.getLooper());
        }
        InitView();
    }

    public void onStart() {
        super.onStart();
        if (this.processthread == null) {
            this.processthread = new HandlerThread("OneClickProcessThread");
            this.processthread.start();
        }
        if (this.mpHandler == null) {
            this.mpHandler = new ProcessHandler(this.processthread.getLooper());
        }
        if (sniffthread == null) {
            sniffthread = new WifiSniffThread();
        }
        initService();
    }

    public void onStop() {
        super.onStop();
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(5);
        this.mHandler.removeMessages(4);
        this.mHandler.removeMessages(6);
        this.mHandler.removeMessages(8);
        this.processthread.quit();
        this.processthread = null;
        finish();
    }

    private void InitView() {
        this.mWarnMessage = findPreference(WARNMESSAGE_KEY);
        this.mWarnMessage.setSelectable(D);
        this.mWifiLog = findPreference(WIFI_KEY);
        this.mBtLog = findPreference(BT_KEY);
        this.mNfcLog = findPreference(NFC_KEY);
        this.mGpsLog = findPreference(GPS_KEY);
        this.mHotLog = findPreference(HOT_KEY);
        this.mLagLog = findPreference(LAG_KEY);
        this.mModemSignalLog = findPreference(MODEM_SIGNAL_KEY);
        this.mModemDataLog = findPreference(MODEM_DATA_KEY);
        this.mCloseLog = findPreference(CLOSELOG_KEY);
        this.mCleanLog = findPreference(CLEANLOG_KEY);
        this.mPackageLog = findPreference(PACKAGELOG_KEY);
        this.mPackageLog.setOnPreferenceClickListener(this);
        this.mWifiLog.setOnPreferenceClickListener(this);
        this.mBtLog.setOnPreferenceClickListener(this);
        this.mNfcLog.setOnPreferenceClickListener(this);
        this.mGpsLog.setOnPreferenceClickListener(this);
        this.mHotLog.setOnPreferenceClickListener(this);
        this.mLagLog.setOnPreferenceClickListener(this);
        this.mModemSignalLog.setOnPreferenceClickListener(this);
        this.mModemDataLog.setOnPreferenceClickListener(this);
        this.mCloseLog.setOnPreferenceClickListener(this);
        this.mCleanLog.setOnPreferenceClickListener(this);
        if (getLogStatus() || isQXDMLogEnabled()) {
            this.mWifiLog.setEnabled(D);
            this.mBtLog.setEnabled(D);
            this.mNfcLog.setEnabled(D);
            this.mGpsLog.setEnabled(D);
            this.mHotLog.setEnabled(D);
            this.mLagLog.setEnabled(D);
            this.mModemSignalLog.setEnabled(D);
            this.mModemDataLog.setEnabled(D);
            this.mCloseLog.setEnabled(true);
            this.mPackageLog.setEnabled(D);
            this.mCleanLog.setEnabled(D);
            if (getWifiLogStatus() == ONECLICK_FULLLOG) {
                this.mWifiLog.setSummary(R.string.oneclick_logrunning);
            } else if (getWifiLogStatus() == ONECLICK_PARTLOG) {
                this.mWifiLog.setSummary(R.string.oneclick_partlogrunning);
            } else {
                this.mWifiLog.setSummary("");
            }
            if (getNfcLogStatus() == ONECLICK_FULLLOG) {
                this.mNfcLog.setSummary(R.string.oneclick_logrunning);
            } else {
                this.mNfcLog.setSummary("");
            }
            if (getBtLogStatus() == ONECLICK_FULLLOG) {
                this.mBtLog.setSummary(R.string.oneclick_logrunning);
            } else if (getBtLogStatus() == ONECLICK_PARTLOG) {
                this.mBtLog.setSummary(R.string.oneclick_partlogrunning);
            } else {
                this.mBtLog.setSummary("");
            }
            if (getGpsLogStatus() == ONECLICK_FULLLOG) {
                this.mGpsLog.setSummary(R.string.oneclick_logrunning);
            } else if (getGpsLogStatus() == ONECLICK_PARTLOG) {
                this.mGpsLog.setSummary(R.string.oneclick_partlogrunning);
            } else {
                this.mGpsLog.setSummary("");
            }
            if (getHotLogStatus()) {
                this.mHotLog.setSummary(R.string.oneclick_logrunning);
            } else {
                this.mHotLog.setSummary("");
            }
            if (getLagLogStatus()) {
                this.mLagLog.setSummary(R.string.oneclick_logrunning);
            } else {
                this.mLagLog.setSummary("");
            }
            if (getModemSignalLogStatus()) {
                this.mModemSignalLog.setSummary(R.string.oneclick_logrunning);
            } else {
                this.mModemSignalLog.setSummary("");
            }
            if (getModemDataLogStatus()) {
                this.mModemDataLog.setSummary(R.string.oneclick_logrunning);
            } else {
                this.mModemDataLog.setSummary("");
            }
        } else if (getWifiSniffStatus()) {
            this.mWifiLog.setEnabled(D);
            this.mBtLog.setEnabled(D);
            this.mNfcLog.setEnabled(D);
            this.mGpsLog.setEnabled(D);
            this.mHotLog.setEnabled(D);
            this.mLagLog.setEnabled(D);
            this.mModemSignalLog.setEnabled(D);
            this.mModemDataLog.setEnabled(D);
            this.mCloseLog.setEnabled(D);
            this.mPackageLog.setEnabled(D);
            this.mCleanLog.setEnabled(D);
            this.mWifiLog.setSummary(R.string.wifi_sniff_capture);
            this.mHandler.sendEmptyMessageDelayed(8, 20000);
            if (!sniffthread.isAlive()) {
                sniffthread.start();
            }
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        if ((getLogStatus() || isQXDMLogEnabled()) && !preference.getKey().equals(CLOSELOG_KEY)) {
            Toast.makeText(this, getResources().getString(R.string.oneclick_logopentoast), 0).show();
            return D;
        }
        this.type = 0;
        if (preference.getKey().equals(WIFI_KEY)) {
            this.mpHandler.sendEmptyMessage(ONECLICK_RENAMEWLAN);
            setWifiLog(true);
            openLog();
        } else if (preference.getKey().equals(BT_KEY)) {
            setBTLog(true);
            openLog();
        } else if (preference.getKey().equals(NFC_KEY)) {
            setNFCLog(true);
            openLog();
        } else if (preference.getKey().equals(GPS_KEY)) {
            setGPSLog(true);
            openLog();
        } else if (preference.getKey().equals(HOT_KEY)) {
            setHOTLog(true);
            openLog();
        } else if (preference.getKey().equals(LAG_KEY)) {
            setLAGLog(true);
            openLog();
        } else if (preference.getKey().equals(MODEM_SIGNAL_KEY)) {
            setModemSignalLog(true);
            openLog();
        } else if (preference.getKey().equals(MODEM_DATA_KEY)) {
            setModemDataLog(true);
            openLog();
        } else if (preference.getKey().equals(CLOSELOG_KEY)) {
            closeLog();
        } else if (preference.getKey().equals(PACKAGELOG_KEY)) {
            packageLog();
        } else if (preference.getKey().equals(CLEANLOG_KEY)) {
            cleanLog();
        } else {
            Log.d(TAG, "Preference Click Invalid!!");
        }
        return D;
    }

    private void setWifiLog(boolean b) {
        int i;
        Log.d(TAG, "setWifiLog:" + b);
        if (b) {
            this.mWifiLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mWifiLog.setSummary("");
        }
        setWifiSniffLog(b);
        PropertiesUtil.set("persist.oem.wifi.debug", b ? "1" : "0");
        transact(b ? "OEM_WIFI_DEBUG_ENABLE" : "OEM_WIFI_DEBUG_DISABLE");
        PropertiesUtil.set("persist.sys.tcpdump", b ? "yes" : "no");
        WifiManager wifiManager = this.mWifiManager;
        if (b) {
            i = 1;
        } else {
            i = 0;
        }
        wifiManager.enableVerboseLogging(i);
        if (b) {
            PropertiesUtil.set("persist.oem.wifi.cnssdiag", "1");
        } else if (getWifiSniffStatus()) {
            this.mWifiLog.setSummary(R.string.wifi_sniff_capture);
            Toast.makeText(this, getResources().getString(R.string.wifi_sniff_geting), 0).show();
            this.mHandler.sendEmptyMessageDelayed(8, 20000);
            sniffthread.start();
        }
    }

    private void setWifiSniffLog(boolean b) {
        if (this.mWifiManager.getWifiState() == 3) {
            if (b) {
                transact("WIFI_SNIFFLOG_START");
                Log.d(TAG, "WIFI_SNIFFLOG_START");
            } else {
                transact("WIFI_SNIFFLOG_STOP");
                Log.d(TAG, "WIFI_SNIFFLOG_STOP");
            }
        }
    }

    private void setBTLog(boolean b) {
        Log.d(TAG, "setBTLog:" + b);
        if (b) {
            this.type = 7;
            this.mBtLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mBtLog.setSummary("");
        }
        PropertiesUtil.set("persist.service.bdroid.soclog", b ? "true" : "false");
        PropertiesUtil.set("persist.service.bdroid.snooplog", b ? "true" : "false");
        selectBtLog(b);
        writeBtHciSnoopLogOptions(b);
    }

    private void setNFCLog(boolean b) {
        String str;
        Log.d(TAG, "setNFCLog:" + b);
        if (b) {
            this.mNfcLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mNfcLog.setSummary("");
        }
        PropertiesUtil.set("persist.oem.nfc.debug", b ? "1" : "0");
        if (b) {
            str = "OEM_NFC_DEBUG_ENABLE";
        } else {
            str = "OEM_NFC_DEBUG_DISABLE";
        }
        transact(str);
    }

    private void setGPSLog(boolean b) {
        String str;
        Log.d(TAG, "setGPSLog:" + b);
        if (b) {
            startRecordNmea();
            this.type = 9;
            this.mGpsLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mGpsLog.setSummary("");
            stopRecordNmea();
        }
        PropertiesUtil.set("persist.oem.gps.debug", b ? "1" : "0");
        if (b) {
            str = "OEM_GPS_DEBUG_ENABLE";
        } else {
            str = "OEM_GPS_DEBUG_DISABLE";
        }
        transact(str);
    }

    private void setHOTLog(boolean b) {
        String str;
        Log.d(TAG, "setHOTLog:" + b);
        if (b) {
            this.mHotLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mHotLog.setSummary("");
        }
        String str2 = "persist.sys.hotlog.enable";
        if (b) {
            str = "1";
        } else {
            str = "0";
        }
        PropertiesUtil.set(str2, str);
    }

    private void setLAGLog(boolean b) {
        String str;
        Log.d(TAG, "setLAGLog:" + b);
        if (b) {
            this.mLagLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mLagLog.setSummary("");
        }
        PropertiesUtil.set("persist.sys.systrace.enable", b ? "true" : "false");
        String str2 = "persist.sys.laglog.enable";
        if (b) {
            str = "1";
        } else {
            str = "0";
        }
        PropertiesUtil.set(str2, str);
    }

    private void setModemSignalLog(boolean b) {
        String str;
        Log.d(TAG, "setModemSignalLog:" + b);
        if (b) {
            this.type = 1;
            this.mModemSignalLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mModemSignalLog.setSummary("");
        }
        String str2 = "persist.sys.signallog.enable";
        if (b) {
            str = "1";
        } else {
            str = "0";
        }
        PropertiesUtil.set(str2, str);
    }

    private void setModemDataLog(boolean b) {
        String str;
        Log.d(TAG, "setModemDataLog:" + b);
        if (b) {
            this.type = 1;
            this.mModemDataLog.setSummary(R.string.oneclick_logrunning);
        } else {
            this.mModemDataLog.setSummary("");
        }
        PropertiesUtil.set("persist.sys.tcpdump", b ? "yes" : "no");
        String str2 = "persist.sys.signallog.enable";
        if (b) {
            str = "1";
        } else {
            str = "0";
        }
        PropertiesUtil.set(str2, str);
    }

    private boolean getLogStatus() {
        boolean bool = D;
        if (PropertiesUtil.get("persist.sys.assert.panic", "false").equals("true")) {
            bool = true;
        }
        Log.d(TAG, "getLogStatus:" + bool);
        return bool;
    }

    private boolean getWifiSniffStatus() {
        if (PropertiesUtil.get("persist.oem.wifi.cnssdiag", "0").equals("1")) {
            return true;
        }
        return D;
    }

    private boolean getHotLogStatus() {
        if (getLogStatus() && PropertiesUtil.get("persist.sys.hotlog.enable", "0").equals("1")) {
            return true;
        }
        return D;
    }

    private boolean getLagLogStatus() {
        if (getLogStatus() && PropertiesUtil.get("persist.sys.laglog.enable", "0").equals("1")) {
            return true;
        }
        return D;
    }

    private boolean getModemSignalLogStatus() {
        if (getLogStatus() && PropertiesUtil.get("persist.sys.signallog.enable", "0").equals("1")) {
            return true;
        }
        return D;
    }

    private boolean getModemDataLogStatus() {
        if (getLogStatus() && PropertiesUtil.get("persist.sys.datalog.enable", "0").equals("1")) {
            return true;
        }
        return D;
    }

    private int getWifiLogStatus() {
        int status = 0;
        if (getLogStatus() && PropertiesUtil.get("persist.oem.wifi.debug", "0").equals("1")) {
            status = 1;
        }
        if (PropertiesUtil.get("persist.sys.tcpdump", "no").equals("yes")) {
            status++;
        }
        if (getLogStatus() && this.mWifiManager.getVerboseLoggingLevel() == 1) {
            status++;
        }
        if (status == 0) {
            return ONECLICK_NOLOG;
        }
        if (status > 0 && status < 3) {
            return ONECLICK_PARTLOG;
        }
        if (status == 3) {
            return ONECLICK_FULLLOG;
        }
        return status;
    }

    private int getBtLogStatus() {
        int status = 0;
        if (getLogStatus() && PropertiesUtil.get("persist.oem.bt.debug", "0").equals("1")) {
            status = 1;
        }
        if (this.type == 7 && isQXDMLogEnabled()) {
            status++;
        }
        if (getLogStatus() && Secure.getInt(getContentResolver(), "bluetooth_hci_log", 0) == 1) {
            status++;
        }
        if (status == 0) {
            return ONECLICK_NOLOG;
        }
        if (status > 0 && status < 3) {
            return ONECLICK_PARTLOG;
        }
        if (status == 3) {
            return ONECLICK_FULLLOG;
        }
        return status;
    }

    private int getGpsLogStatus() {
        int status = 0;
        if (getLogStatus() && PropertiesUtil.get("persist.oem.gps.debug", "0").equals("1")) {
            status = 1;
        }
        if (this.type == 9 && isQXDMLogEnabled()) {
            status++;
        }
        if (status == 0) {
            return ONECLICK_NOLOG;
        }
        if (status > 0 && status < 2) {
            return ONECLICK_PARTLOG;
        }
        if (status == 2) {
            return ONECLICK_FULLLOG;
        }
        return status;
    }

    private int getNfcLogStatus() {
        int status = 0;
        if (getLogStatus() && PropertiesUtil.get("persist.oem.nfc.debug", "0").equals("1")) {
            status = 1;
        }
        if (status == 0) {
            return ONECLICK_NOLOG;
        }
        if (status == 1) {
            return ONECLICK_FULLLOG;
        }
        return status;
    }

    private void openLog() {
        this.mWifiLog.setEnabled(D);
        this.mBtLog.setEnabled(D);
        this.mNfcLog.setEnabled(D);
        this.mGpsLog.setEnabled(D);
        this.mHotLog.setEnabled(D);
        this.mLagLog.setEnabled(D);
        this.mModemSignalLog.setEnabled(D);
        this.mModemDataLog.setEnabled(D);
        this.mPackageLog.setEnabled(D);
        this.mCleanLog.setEnabled(D);
        this.mCloseLog.setEnabled(true);
        PropertiesUtil.set("persist.sys.assert.panic", "true");
        transact("LOGBEGIN");
        if (!(this.type == 7 || this.type == 9)) {
            if (this.type == 1) {
            }
            this.mpHandler.sendEmptyMessage(ONECLICK_SAVEOLDLOG);
            setRootAlerDialog();
        }
        this.mpHandler.sendEmptyMessage(ONECLICK_WRITEENABLEMSG);
        writeTypeToFile();
        PropertiesUtil.set("persist.sys.qxdm", "yes");
        this.mHandler.sendEmptyMessage(2);
        this.mpHandler.sendEmptyMessage(ONECLICK_SAVEOLDLOG);
        setRootAlerDialog();
    }

    private void closeLog() {
        this.mWifiManager.enableVerboseLogging(0);
        if (getLogStatus() || isQXDMLogEnabled() || getWifiSniffStatus()) {
            this.mCloseLog.setEnabled(D);
            if (getWifiSniffStatus()) {
                this.mHandler.sendEmptyMessageDelayed(8, 20000);
            } else if (getHotLogStatus()) {
                new Thread(new Runnable() {
                    public void run() {
                        GrabOtherActivity.sendMsg("bugreport");
                        OneClickLogKitMainActivity.this.mHandler.sendEmptyMessage(8);
                    }
                }).start();
                setHOTLog(D);
                Toast.makeText(this, getResources().getString(R.string.oneclick_closinglog), 1).show();
            } else {
                this.mHandler.sendEmptyMessageDelayed(8, 3200);
                Toast.makeText(this, getResources().getString(R.string.oneclick_closinglog), 0).show();
            }
            if (getModemSignalLogStatus()) {
                setModemSignalLog(D);
            }
            if (getLagLogStatus()) {
                setLAGLog(D);
            }
            if (getModemDataLogStatus()) {
                setModemDataLog(D);
            }
            if (getWifiLogStatus() != ONECLICK_NOLOG || getWifiSniffStatus()) {
                setWifiLog(D);
            }
            if (getBtLogStatus() != ONECLICK_NOLOG) {
                setBTLog(D);
            }
            if (getGpsLogStatus() != ONECLICK_NOLOG) {
                setGPSLog(D);
            }
            if (getNfcLogStatus() != ONECLICK_NOLOG) {
                setNFCLog(D);
            }
            this.mpHandler.sendEmptyMessage(ONECLICK_WRITEDISABLEMSG);
            if (isQXDMLogEnabled()) {
                if (isFileExists(MDIAG_EXEC_PATH)) {
                    PropertiesUtil.set("persist.sys.qxdm", "no");
                    SystemService.start(MDIAG_SERVICE_STOP);
                } else {
                    Toast.makeText(this, "Feture not avaiable as exec file not exists", 0).show();
                }
            }
            if (getLogStatus()) {
                transact("LOGSTOP");
                PropertiesUtil.set("persist.sys.assert.panic", "false");
            }
        }
    }

    private void packageLog() {
        Toast.makeText(this, getResources().getString(R.string.oneclick_startpackagelog), 0).show();
        Builder packbuilder = new Builder(this);
        packbuilder.setTitle(R.string.pack_log);
        packbuilder.setPositiveButton(R.string.pack_ok, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                OneClickLogKitMainActivity.this.mPackageLog.setEnabled(OneClickLogKitMainActivity.D);
                Thread mThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if (OneClickLogKitMainActivity.this.isLogExists()) {
                                String currentLogPath = Environment.getExternalStorageDirectory().getPath() + "/oem_log/";
                                Log.d(OneClickLogKitMainActivity.TAG, "begin to dump media db");
                                GrabOtherActivity.dumpMediaDBPublic(OneClickLogKitMainActivity.mContext);
                                Log.d(OneClickLogKitMainActivity.TAG, "finish to dump media db");
                                Log.d(OneClickLogKitMainActivity.TAG, "begin to get ps log");
                                GrabOtherActivity.sendMsg("ps");
                                Log.d(OneClickLogKitMainActivity.TAG, "finish to get ps log");
                                Log.d(OneClickLogKitMainActivity.TAG, "begin to get dumpsys batterystats log");
                                GrabOtherActivity.sendMsg("dumpsys batterystats");
                                Log.d(OneClickLogKitMainActivity.TAG, "finish to get dumpsys batterystats log");
                                try {
                                    File uidDir = new File(currentLogPath + "/uiderrors");
                                    uidDir.mkdir();
                                    OneClickLogKitMainActivity.this.copyFolder(new File("/data/system/uiderrors.txt"), new File(uidDir, "uiderrors.txt"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                OneClickLogKitMainActivity.this.zipFile(currentLogPath, Environment.getExternalStorageDirectory().getPath() + "/" + OneClickLogKitMainActivity.getCurrentTime() + "_oemlog.zip");
                                OneClickLogKitMainActivity.this.mHandler.sendEmptyMessage(5);
                                return;
                            }
                            OneClickLogKitMainActivity.this.mHandler.sendEmptyMessage(6);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                });
                mThread.setPriority(1);
                mThread.start();
            }
        });
        packbuilder.setNegativeButton(R.string.clean_cancel, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        packbuilder.create().show();
    }

    private void cleanLog() {
        this.mWifiLog.setEnabled(D);
        this.mBtLog.setEnabled(D);
        this.mNfcLog.setEnabled(D);
        this.mGpsLog.setEnabled(D);
        this.mHotLog.setEnabled(D);
        this.mLagLog.setEnabled(D);
        this.mModemSignalLog.setEnabled(D);
        this.mModemDataLog.setEnabled(D);
        this.mCloseLog.setEnabled(D);
        this.mPackageLog.setEnabled(D);
        this.mCleanLog.setEnabled(D);
        if (isLogExists()) {
            cleanApLog();
        } else {
            this.mHandler.sendEmptyMessageDelayed(6, 1000);
        }
    }

    private void cleanApLog() {
        transact("DELETELOG");
        new Thread(new Runnable() {
            public void run() {
                OneClickLogKitMainActivity.this.mDis = new DataInputStream(OneClickLogKitMainActivity.mIn);
                try {
                    OneClickLogKitMainActivity.this.mDis.read(OneClickLogKitMainActivity.this.buf);
                    Log.i(OneClickLogKitMainActivity.TAG, "cleanLog: '" + new String(OneClickLogKitMainActivity.this.buf));
                    OneClickLogKitMainActivity.this.mHandler.sendEmptyMessageDelayed(4, 2000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void onDestroy() {
        Log.d(TAG, "QXDMLog---unbindService(mServiceConnection)");
        unbindService(this.mServiceConnection);
        super.onDestroy();
    }

    public void zipFile(String baseDir, String fileName) throws Exception {
        Log.d(TAG, "zipFile, begin to collect file...");
        List fileList = getSubFiles(new File(baseDir));
        fileList.addAll(getSubFiles(new File("/cache/recovery/")));
        Log.d(TAG, "zipFile, finish to collect file!");
        Log.d(TAG, "zipFile, begin to zip file...");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName + ".intermediate"));
        byte[] buf = new byte[1024];
        for (int i = 0; i < fileList.size(); i++) {
            File f = (File) fileList.get(i);
            ZipEntry ze = new ZipEntry(getAbsFileName(baseDir, f));
            ze.setSize(f.length());
            ze.setTime(f.lastModified());
            zos.putNextEntry(ze);
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            while (true) {
                int readLen = is.read(buf, 0, 1024);
                if (readLen == -1) {
                    break;
                }
                zos.write(buf, 0, readLen);
            }
            is.close();
        }
        zos.close();
        new File(fileName + ".intermediate").renameTo(new File(fileName));
        Log.d(TAG, "zipFile, finish to zip file!");
        shareFiles(fileName);
    }

    private static List getSubFiles(File baseDir) {
        List ret = new ArrayList();
        File[] tmp = baseDir.listFiles();
        int i = 0;
        while (i < tmp.length) {
            if (tmp[i].isFile() && !tmp[i].getAbsolutePath().equals("/cache/recovery/block.map")) {
                ret.add(tmp[i]);
            } else if (tmp[i].isDirectory()) {
                ret.addAll(getSubFiles(tmp[i]));
            }
            i++;
        }
        return ret;
    }

    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        String ret = realFileName.getName();
        while (true) {
            real = real.getParentFile();
            if (!(real == null || real.equals(base))) {
                ret = real.getName() + "/" + ret;
            }
        }
        return ret;
    }

    private void shareFiles(String fileName) {
        Uri uri = Uri.fromFile(new File(fileName));
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("application/octet-stream");
        intent.putExtra("android.intent.extra.STREAM", uri);
        startActivity(Intent.createChooser(intent, "Choose a channel to share your files..."));
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(System.currentTimeMillis()));
    }

    void setRootAlerDialog() {
        this.textview = new TextView(this);
        this.textview.setText("");
        this.textview.setTextSize(16.0f);
        this.textview.setTextColor(SupportMenu.CATEGORY_MASK);
        this.textview.setGravity(17);
        Builder builder = new Builder(this);
        builder.setTitle(R.string.reboot_confirm).setMessage(R.string.oneclick_reboot_message).setView(this.textview).setPositiveButton(R.string.reboot_OK, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                OneClickLogKitMainActivity.this.mOffTime.cancel();
                ((PowerManager) OneClickLogKitMainActivity.this.getSystemService("power")).reboot(null);
            }
        }).setNegativeButton(R.string.reboot_cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                OneClickLogKitMainActivity.this.mOffTime.cancel();
                dialog.cancel();
                Log.d(OneClickLogKitMainActivity.TAG, "dumpsys power log switch 1. result: " + OneClickLogKitMainActivity.this.shellRun("dumpsys power log switch 1"));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(2003);
        dialog.setCancelable(D);
        dialog.setCanceledOnTouchOutside(D);
        dialog.show();
        this.mOffHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    OneClickLogKitMainActivity.this.textview.setText(OneClickLogKitMainActivity.this.getResources().getString(R.string.oneclick_reboot_timer) + msg.what + "");
                } else {
                    OneClickLogKitMainActivity.this.mOffTime.cancel();
                    ((PowerManager) OneClickLogKitMainActivity.this.getSystemService("power")).reboot(null);
                }
                super.handleMessage(msg);
            }
        };
        this.mOffTime = new Timer(true);
        this.mOffTime.schedule(new TimerTask() {
            int countTime = 30;

            public void run() {
                if (this.countTime > 0) {
                    this.countTime--;
                }
                Message msg = new Message();
                msg.what = this.countTime;
                OneClickLogKitMainActivity.this.mOffHandler.sendMessage(msg);
            }
        }, 1000, 1000);
    }

    private String shellRun(String command) {
        IOException e;
        InterruptedException e2;
        Throwable th;
        Process process = null;
        BufferedReader bufferedReader = null;
        String result = "";
        try {
            byte[] b = new byte[1024];
            process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                try {
                    String line = bufferedReader2.readLine();
                    if (line == null) {
                        break;
                    }
                    Log.e(TAG, "line:" + line);
                    result = result + line;
                } catch (IOException e3) {
                    e = e3;
                    bufferedReader = bufferedReader2;
                } catch (InterruptedException e4) {
                    e2 = e4;
                    bufferedReader = bufferedReader2;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = bufferedReader2;
                }
            }
            process.waitFor();
            if (bufferedReader2 != null) {
                try {
                    bufferedReader2.close();
                } catch (IOException e5) {
                }
            }
            if (process != null) {
                process.destroy();
            }
            bufferedReader = bufferedReader2;
        } catch (IOException e6) {
            e = e6;
            e.printStackTrace();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e7) {
                }
            }
            if (process != null) {
                process.destroy();
            }
            return result;
        } catch (InterruptedException e8) {
            e2 = e8;
            try {
                e2.printStackTrace();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e9) {
                    }
                }
                if (process != null) {
                    process.destroy();
                }
                return result;
            } catch (Throwable th3) {
                th = th3;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e10) {
                    }
                }
                if (process != null) {
                    process.destroy();
                }
                throw th;
            }
        }
        return result;
    }

    private void initService() {
        Log.d(TAG, "QXDMLog---initService");
        Intent intent = new Intent("com.oem.logkit.command");
        intent.setPackage("com.oem.logkitsdservice");
        bindService(intent, this.mServiceConnection, 1);
    }

    private boolean isFileExists(String filePath) {
        return new File(filePath).exists();
    }

    private boolean isLogExists() {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/oem_log/");
            if (file.exists() && file.listFiles().length > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return D;
    }

    private int getTypeFromFile() {
        return SystemProperties.getInt("persist.sys.diag.type", -1);
    }

    private boolean isQXDMLogEnabled() {
        return PropertiesUtil.get("persist.sys.qxdm", "no").equals("yes");
    }

    private void writeTypeToFile() {
        SystemProperties.set("persist.sys.diag.type", this.type + "");
    }

    private void InitQXDMLog() {
        if (FileTransactionUtil.readDataFromFile(FileTransactionUtil.NUMBER_FLAG, this) == -1) {
            FileTransactionUtil.writeDataToFile(FileTransactionUtil.NUMBER_FLAG, 500, this);
        }
        if (FileTransactionUtil.readDataFromFile(FileTransactionUtil.SIZE_FLAG, this) == -1) {
            FileTransactionUtil.writeDataToFile(FileTransactionUtil.SIZE_FLAG, 32, this);
        }
    }

    private void startRecordNmea() {
        Log.d(TAG, "startRecordNmea!!");
        startService(new Intent(this, RecordNmeaService.class));
    }

    private void stopRecordNmea() {
        Log.d(TAG, "stopRecordNmea!!");
        stopService(new Intent(this, RecordNmeaService.class));
    }

    private void selectBtLog(boolean isChecked) {
        int check = isChecked ? 1 : 0;
        int mode = SystemProperties.getInt("persist.sys.btlog.enable", 0);
        Log.d(TAG, "BTLog_KEY--mode=" + mode + ", --isChecked=" + Integer.toString(check));
        if (check != mode) {
            SystemProperties.set("persist.sys.btlog.enable", Integer.toString(check));
            SystemProperties.set("persist.oem.bt.debug", check == 1 ? "1" : "0");
            transact(check == 1 ? "OEM_BT_DEBUG_ENABLE" : "OEM_BT_DEBUG_DISABLE");
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter.isEnabled()) {
                Log.d(TAG, "BTLog_KEY--disable =" + String.valueOf(adapter.disable()));
            }
        }
    }

    private void writeBtHciSnoopLogOptions(boolean enable) {
        BluetoothAdapter.getDefaultAdapter().configHciSnoopLog(enable);
        Log.d(TAG, "BT_SNOOP_LOG_KEY writeBtHciSnoopLogOptions = " + enable);
        Secure.putInt(getContentResolver(), "bluetooth_hci_log", enable ? 1 : 0);
    }

    private void saveOldLog() {
        Thread mThread = new Thread(new Runnable() {
            public void run() {
                try {
                    String destPath = GrabOtherActivity.getLogPath();
                    if (destPath != null) {
                        String destPath_anr = destPath.concat("/old_anr");
                        String destPath_dropbox = destPath.concat("/old_dropbox");
                        String destPath_recovery = destPath.concat("/recovery");
                        String destPath_chargeLog = destPath.concat("/charge_log");
                        File fileAnr = new File(destPath_anr);
                        File fileDropbox = new File(destPath_dropbox);
                        File fileRecovery = new File(destPath_recovery);
                        File fileChargeLog = new File(destPath_chargeLog);
                        OneClickLogKitMainActivity.this.copyFolder(new File("/data/anr"), fileAnr);
                        OneClickLogKitMainActivity.this.copyFolder(new File("/data/system/dropbox"), fileDropbox);
                        File recovery = new File("/cache/recovery/");
                        if (recovery.exists()) {
                            OneClickLogKitMainActivity.this.copyFolder(recovery, fileRecovery);
                        }
                        File kmsg = new File("/cache/kmsg/");
                        if (kmsg.exists()) {
                            OneClickLogKitMainActivity.this.copyFolder(kmsg, fileChargeLog);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mThread.setPriority(1);
        mThread.start();
    }

    private void renameWlanLog() {
        String path1 = Environment.getExternalStorageDirectory().getPath() + "/wlan_logs";
        String path2 = Environment.getExternalStorageDirectory().getPath() + "/oem_log/wlan_logs";
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        File file1 = new File(path1);
        File file2 = new File(path2);
        if (file1.exists()) {
            file1.renameTo(new File(path1 + "_" + dateFormat.format(Long.valueOf(time)).toString()));
        }
        if (file2.exists()) {
            file2.renameTo(new File(path2 + "_" + dateFormat.format(Long.valueOf(time)).toString()));
        }
    }

    private void copyFolder(File src, File dest) throws IOException {
        int i = 0;
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] files = src.list();
            if (files != null) {
                int length = files.length;
                while (i < length) {
                    String file = files[i];
                    copyFolder(new File(src, file), new File(dest, file));
                    i++;
                }
            }
        } else if (!src.getAbsolutePath().equals("/cache/recovery/block.map")) {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            while (true) {
                int length2 = in.read(buffer);
                if (length2 <= 0) {
                    break;
                }
                out.write(buffer, 0, length2);
            }
            in.close();
            out.close();
        }
    }

    private void copyFile(File src, File dest) {
        try {
            if (src.exists()) {
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                File destFile = new File(dest, "data.dat");
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(destFile);
                byte[] buffer = new byte[1024];
                while (true) {
                    int length = in.read(buffer);
                    if (length > 0) {
                        out.write(buffer, 0, length);
                    } else {
                        in.close();
                        out.close();
                        return;
                    }
                }
            }
            Log.e(TAG, src + " not Exist!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean connect() {
        try {
            mOut = OEMLogKitMainActivity.getmSocket().getOutputStream();
            mIn = OEMLogKitMainActivity.getmSocket().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean writeCommand(String cmdString) {
        byte[] cmd = cmdString.getBytes();
        int len = cmd.length;
        if (len < 1 || len > this.buf.length) {
            return D;
        }
        this.buf[0] = (byte) (len & 255);
        this.buf[1] = (byte) ((len >> 8) & 255);
        try {
            mOut.write(this.buf, 0, 2);
            mOut.write(cmd, 0, len);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "write error");
            disconnect();
            return D;
        }
    }

    public synchronized String transact(String cmd) {
        if (connect()) {
            if (!writeCommand(cmd)) {
                Log.e(TAG, "write command failed? reconnect!");
                if (!(connect() && writeCommand(cmd))) {
                    return "-1";
                }
            }
            Log.i(TAG, "send: '" + cmd + "'");
            return "1";
        }
        Log.e(TAG, "connection failed");
        return "-1";
    }

    private static void disconnect() {
        Log.i(TAG, "disconnecting...");
        IoUtils.closeQuietly(OEMLogKitMainActivity.getmSocket());
        IoUtils.closeQuietly(mOut);
        IoUtils.closeQuietly(mIn);
        OEMLogKitMainActivity.disconnect();
        mOut = null;
        mIn = null;
    }
}
