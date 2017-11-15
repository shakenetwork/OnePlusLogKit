package com.oem.oemlogkit;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.util.Slog;
import android.widget.Toast;

import com.oem.oemlogkit.InputDialogFragment.DialogInputListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import libcore.io.IoUtils;

public class AdvancedActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener, DialogInputListener {
    static final String TAG = "OEMLogKitMainActivity";
    private static String logLevel = "V";
    private static String logRaw = "threadtime";
    private final byte[] buf = new byte[1024];
    private String logFilter = "";
    private String logNum = "";
    private String logSize = "";
    private Preference mClearCache;
    private Preference mFilterLog;
    private ListPreference mLogLevel;
    private ListPreference mLogRaw;
    private Preference mLogSize;
    private MultiSelectListPreference mLogType;
    private OutputStream mOut;
    private Preference mRawData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreat start");
        addPreferencesFromResource(R.xml.advanced);
        findViews();
        setListeners();
        connect();
    }

    private void findViews() {
        CharSequence charSequence;
        String str;
        this.mClearCache = findPreference("clear_cache");
        this.mFilterLog = findPreference("filter_log");
        String[] filter = PropertiesUtil.get("persist.sys.loglevel", "").split(":");
        Preference preference = this.mFilterLog;
        if (PropertiesUtil.get("persist.sys.loglevel", "").contains("*")) {
            charSequence = "";
        } else {
            charSequence = filter[0];
        }
        preference.setSummary(charSequence);
        this.mRawData = findPreference("raw_data");
        this.mLogSize = findPreference("log_size_and_num");
        this.mLogSize.setSummary("Size:" + (Integer.parseInt(PropertiesUtil.get("persist.sys.logsize", "20480")) / 1024) + " " + "Num:" + PropertiesUtil.get("persist.sys.lognum", "40"));
        this.mLogType = (MultiSelectListPreference) findPreference("Log_type");
        boolean getKernelLog = PropertiesUtil.get("persist.sys.kernel", "no").equals("yes");
        boolean getSystemLog = PropertiesUtil.get("persist.sys.system", "no").equals("yes");
        boolean getMainLog = PropertiesUtil.get("persist.sys.main", "no").equals("yes");
        boolean getRadioLog = PropertiesUtil.get("persist.sys.radio", "no").equals("yes");
        boolean getEventLog = PropertiesUtil.get("persist.sys.event", "no").equals("yes");
        boolean getTcpdumpLog = PropertiesUtil.get("persist.sys.tcpdump", "no").equals("yes");
        MultiSelectListPreference multiSelectListPreference = this.mLogType;
        StringBuilder append = new StringBuilder().append(getKernelLog ? "kernel" : "").append(getSystemLog ? " system" : "").append(getMainLog ? " main" : "").append(getRadioLog ? " radio" : "").append(getEventLog ? " event" : "");
        if (getTcpdumpLog) {
            str = " tcpdump";
        } else {
            str = "";
        }
        multiSelectListPreference.setSummary(append.append(str).toString());
        HashSet<String> mySet = new HashSet();
        if (getKernelLog) {
            mySet.add("0");
        }
        if (getSystemLog) {
            mySet.add("1");
        }
        if (getMainLog) {
            mySet.add("2");
        }
        if (getRadioLog) {
            mySet.add("3");
        }
        if (getEventLog) {
            mySet.add("4");
        }
        if (getTcpdumpLog) {
            mySet.add("5");
        }
        this.mLogType.setValues(mySet);
        this.mLogLevel = (ListPreference) findPreference("loglevel");
        this.mLogLevel.setSummary(PropertiesUtil.get("persist.sys.loglevel", "").equals("") ? "V" : filter[1]);
        this.mLogRaw = (ListPreference) findPreference("lograw");
        this.mLogRaw.setSummary(PropertiesUtil.get("persist.sys.lograw", "").equals("") ? "threadtime" : PropertiesUtil.get("persist.sys.lograw", ""));
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

    private void setListeners() {
        this.mLogType.setOnPreferenceChangeListener(this);
        this.mLogLevel.setOnPreferenceChangeListener(this);
        this.mLogRaw.setOnPreferenceChangeListener(this);
        this.mClearCache.setOnPreferenceClickListener(this);
        this.mFilterLog.setOnPreferenceClickListener(this);
        this.mLogSize.setOnPreferenceClickListener(this);
        this.mRawData.setOnPreferenceClickListener(this);
    }

    public void setNormalLogProperty(Iterator<String> iterator) {
        String str;
        PropertiesUtil.set("persist.sys.kernel", "no");
        PropertiesUtil.set("persist.sys.main", "no");
        PropertiesUtil.set("persist.sys.system", "no");
        PropertiesUtil.set("persist.sys.radio", "no");
        PropertiesUtil.set("persist.sys.event", "no");
        PropertiesUtil.set("persist.sys.tcpdump", "no");
        while (iterator.hasNext()) {
            switch (Integer.parseInt((String) iterator.next())) {
                case 0:
                    PropertiesUtil.set("persist.sys.kernel", "yes");
                    break;
                case 1:
                    PropertiesUtil.set("persist.sys.main", "yes");
                    break;
                case 2:
                    PropertiesUtil.set("persist.sys.system", "yes");
                    break;
                case 3:
                    PropertiesUtil.set("persist.sys.radio", "yes");
                    break;
                case 4:
                    PropertiesUtil.set("persist.sys.event", "yes");
                    break;
                case 5:
                    PropertiesUtil.set("persist.sys.tcpdump", "yes");
                    break;
                default:
                    break;
            }
        }
        MultiSelectListPreference multiSelectListPreference = this.mLogType;
        StringBuilder append = new StringBuilder().append(PropertiesUtil.get("persist.sys.kernel", "no").equals("yes") ? "kernel" : "");
        if (PropertiesUtil.get("persist.sys.system", "no").equals("yes")) {
            str = " system";
        } else {
            str = "";
        }
        append = append.append(str);
        if (PropertiesUtil.get("persist.sys.main", "no").equals("yes")) {
            str = " main";
        } else {
            str = "";
        }
        append = append.append(str);
        if (PropertiesUtil.get("persist.sys.radio", "no").equals("yes")) {
            str = " radio";
        } else {
            str = "";
        }
        append = append.append(str);
        if (PropertiesUtil.get("persist.sys.event", "no").equals("yes")) {
            str = " event";
        } else {
            str = "";
        }
        multiSelectListPreference.setSummary(append.append(str).append(PropertiesUtil.get("persist.sys.tcpdump", "no").equals("yes") ? " tcpdump" : "").toString());
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if ("loglevel".equals(key)) {
            logLevel = (String) newValue;
            PropertiesUtil.set("persist.sys.loglevel", (this.logFilter.equals("") ? "*" : this.logFilter) + ":" + logLevel);
            this.mLogLevel.setSummary(PropertiesUtil.get("persist.sys.loglevel", "V").split(":")[1]);
        }
        if ("lograw".equals(key)) {
            logRaw = (String) newValue;
            PropertiesUtil.set("persist.sys.lograw", logRaw);
            this.mLogRaw.setSummary(PropertiesUtil.get("persist.sys.lograw", "threadtime"));
        }
        if ("Log_type".equals(key)) {
            Log.d(TAG, "newValue: " + newValue);
            setNormalLogProperty(((Set) newValue).iterator());
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("clear_cache".equals(key)) {
            dialog(R.string.cache_clean);
        }
        if ("filter_log".equals(key)) {
            new InputDialogFragment(R.layout.filter_dialog, R.string.filter_log).show(getFragmentManager(), "filterDialog");
        }
        if ("log_size_and_num".equals(key)) {
            new InputDialogFragment(R.layout.logsize_dialog, R.string.log_size).show(getFragmentManager(), "logsizeDialog");
        }
        if ("raw_data".equals(key)) {
            new InputDialogFragment(R.layout.raw_data_dialog, R.string.raw_screenshot).show(getFragmentManager(), "raw_screenshot");
        }
        return false;
    }

    private void cacheClear() {
        transact("CACHECLEAR");
    }

    protected void dialog(final int promptResource) {
        Builder builder = new Builder(this);
        builder.setTitle(promptResource);
        builder.setPositiveButton(R.string.clean_ok, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (promptResource == R.string.cache_clean) {
                    AdvancedActivity.this.cacheClear();
                }
            }
        });
        builder.setNegativeButton(R.string.clean_cancel, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.create().show();
    }

    public void onLoginInputComplete(String... data) {
        if (data.length == 1) {
            this.logFilter = data[0];
            int i = 0;
            while (i < this.logFilter.length()) {
                if ((this.logFilter.charAt(i) < 'A' || this.logFilter.charAt(i) > 'Z') && ((this.logFilter.charAt(i) < 'a' || this.logFilter.charAt(i) > 'z') && !Character.isDigit(this.logFilter.charAt(i)))) {
                    Toast.makeText(this, R.string.filter_error, 0).show();
                    return;
                }
                i++;
            }
            if (this.logFilter.equals("")) {
                this.logFilter = "*";
                PropertiesUtil.set("persist.sys.loglevel", "*:" + logLevel);
                PropertiesUtil.set("persist.sys.maskothers", "");
                this.mFilterLog.setSummary("");
            } else {
                PropertiesUtil.set("persist.sys.loglevel", this.logFilter + ":" + logLevel);
                String[] filter = PropertiesUtil.get("persist.sys.loglevel", "").split(":");
                PropertiesUtil.set("persist.sys.maskothers", "*:S");
                this.mFilterLog.setSummary(filter[0]);
            }
        }
        if (data.length == 2) {
            this.logNum = data[0];
            this.logSize = data[1];
            Log.e(TAG, "num=" + this.logNum + "size=" + this.logSize);
            if (!(this.logSize.equals("") || this.logNum.equals(""))) {
                this.mLogSize.setSummary("Size:" + this.logSize + " " + "Num:" + this.logNum);
                PropertiesUtil.set("persist.sys.logsize", Integer.toString(Integer.parseInt(this.logSize) * 1024));
                PropertiesUtil.set("persist.sys.lognum", this.logNum);
            }
        }
    }

    protected void onResume() {
        super.onResume();
    }
}
