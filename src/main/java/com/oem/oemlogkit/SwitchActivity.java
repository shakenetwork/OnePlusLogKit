package com.oem.oemlogkit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Slog;

import java.io.IOException;
import java.io.OutputStream;

import libcore.io.IoUtils;

public class SwitchActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    static final String TAG = "OEMLogKitMainActivity";
    private final byte[] buf = new byte[1024];
    private CheckBoxPreference mCheckBoxCpuThermal;
    private CheckBoxPreference mCheckBoxDump;
    private OutputStream mOut;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        addPreferencesFromResource(R.xml.switches);
        this.mCheckBoxDump = (CheckBoxPreference) findPreference("dump_switch");
        this.mCheckBoxCpuThermal = (CheckBoxPreference) findPreference("cputhermal_switch");
        this.mCheckBoxDump.setOnPreferenceChangeListener(this);
        if (PropertiesUtil.get("ro.product.board", "").equals("msm8994")) {
            this.mCheckBoxCpuThermal.setEnabled(false);
        } else {
            this.mCheckBoxCpuThermal.setOnPreferenceChangeListener(this);
        }
    }

    void setRootAlertDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.reboot_confirm).setPositiveButton(R.string.reboot_OK, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((PowerManager) SwitchActivity.this.getSystemService("power")).reboot(null);
            }
        }).setNegativeButton(R.string.reboot_cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(2003);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        String key = arg0.getKey();
        if ("cputhermal_switch".equals(key)) {
            connect();
            if (this.mCheckBoxCpuThermal.isChecked()) {
                PropertiesUtil.set("sys.oem.cputhermal", "0");
                transact("STOPCPUTHERMAL");
            } else {
                PropertiesUtil.set("sys.oem.cputhermal", "1");
                transact("CPUTHERMAL");
            }
        }
        if ("dump_switch".equals(key)) {
            if (this.mCheckBoxDump.isChecked()) {
                PropertiesUtil.set("persist.oem.dump", "0");
            } else {
                PropertiesUtil.set("persist.oem.dump", "1");
                setRootAlertDialog();
            }
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        if (PropertiesUtil.get("persist.oem.dump", "").equals("1")) {
            this.mCheckBoxDump.setChecked(true);
        } else {
            this.mCheckBoxDump.setChecked(false);
        }
        if (PropertiesUtil.get("sys.oem.cputhermal", "").equals("1")) {
            this.mCheckBoxCpuThermal.setChecked(true);
        } else {
            this.mCheckBoxCpuThermal.setChecked(false);
        }
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
