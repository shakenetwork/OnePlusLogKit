package com.oem.oemlogkit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.util.Slog;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import libcore.io.IoUtils;

public class OEMLogKitMainActivity extends PreferenceActivity implements OnPreferenceClickListener {
    public static final String ACTION_BUTTON = "com.oem.oemlogkit.bugreport";
    public static final String ACTION_START_LOG = "com.oem.oemlogkit.startlog";
    private static final int LOG_CLEAN = 0;
    private static final int OLD_LOG_SAVE = 1;
    static final String TAG = "OEMLogKitMainActivity";
    private static byte[] buf = new byte[1024];
    private static Context mContext;
    private static InputStream mIn;
    private static OutputStream mOut;
    private static LocalSocket mSocket;
    private Preference mAdvanced;
    private CheckBoxPreference mCheckBoxBugreport;
    private CheckBoxPreference mCheckBoxSaveLog;
    private CheckBoxPreference mCheckBoxSystrace;
    private CheckBoxPreference mCheckBoxTcpdump;
    private CheckBoxPreference mCheckMemLog;
    private DataInputStream mDis;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!OEMLogKitMainActivity.this.mCheckBoxSaveLog.isEnabled()) {
                        OEMLogKitMainActivity.this.mCheckBoxSaveLog.setEnabled(true);
                        Toast.makeText(OEMLogKitMainActivity.this, R.string.pack_success, 0).show();
                        break;
                    }
                    OEMLogKitMainActivity.this.enableButtons();
                    break;
                case 1:
                    break;
            }
            new Thread(new Runnable() {
                public void run() {
                    OEMLogKitMainActivity.this.saveOldLog();
                }
            }).start();
            super.handleMessage(msg);
        }
    };
    private Preference mOtherLog;
    private Preference mPackLog;
    private Preference mRemoveLog;
    private Preference mSwitch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreat start");
        addPreferencesFromResource(R.xml.normal_log);
        mContext = getApplicationContext();
        findViews();
        restoreLastState();
        setListeners();
        connect();
    }

    private void restoreLastState() {
        if (PropertiesUtil.get("persist.sys.assert.panic", "").equals("true")) {
            this.mCheckBoxSaveLog.setChecked(true);
            disableButtons();
        } else {
            this.mCheckBoxSaveLog.setChecked(false);
            enableButtons();
        }
        if (PropertiesUtil.get("persist.sys.tcpdump", "no").equals("yes")) {
            this.mCheckBoxTcpdump.setChecked(true);
            this.mCheckBoxTcpdump.setSummary(R.string.tcpdump_opened);
        } else {
            this.mCheckBoxTcpdump.setChecked(false);
            this.mCheckBoxTcpdump.setSummary(R.string.tcpdump_closed);
        }
        if (PropertiesUtil.get("persist.sys.bugreport", "no").equals("yes")) {
            this.mCheckBoxBugreport.setChecked(true);
        } else {
            this.mCheckBoxBugreport.setChecked(false);
        }
        if (PropertiesUtil.get("persist.sys.systrace.enable", "false").equals("true")) {
            this.mCheckBoxSystrace.setChecked(true);
        } else {
            this.mCheckBoxSystrace.setChecked(false);
        }
        if (PropertiesUtil.get("persist.sys.memorylog", "false").equals("true")) {
            this.mCheckMemLog.setChecked(true);
        } else {
            this.mCheckMemLog.setChecked(false);
        }
    }

    @Deprecated
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference chkpref = (CheckBoxPreference) preference;
            if ("save_log".equals(chkpref.getKey())) {
                if (chkpref.isChecked()) {
                    Toast.makeText(this, R.string.save_info, 0).show();
                    disableButtons();
                    startSaveLog();
                    startRecordNmea();
                    startNfclog();
                    setRootAlerDialog();
                } else {
                    Toast.makeText(this, R.string.stop_info, 0).show();
                    enableButtons();
                    stopRecordNmea();
                    stopSaveLog();
                    stopNfclog();
                    Log.d(TAG, "dumpsys power log switch 0. result: " + shellRun("dumpsys power log switch 0"));
                }
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    void setRootAlerDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.reboot_confirm).setMessage(R.string.reboot_message).setPositiveButton(R.string.reboot_OK, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((PowerManager) OEMLogKitMainActivity.this.getSystemService("power")).reboot(null);
            }
        }).setNegativeButton(R.string.reboot_cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d(OEMLogKitMainActivity.TAG, "dumpsys power log switch 1. result: " + OEMLogKitMainActivity.this.shellRun("dumpsys power log switch 1"));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(2003);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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

    private void startRecordNmea() {
        Log.d(TAG, "startRecordNmea!!");
        startService(new Intent(this, RecordNmeaService.class));
    }

    private void stopRecordNmea() {
        Log.d(TAG, "stopRecordNmea!!");
        stopService(new Intent(this, RecordNmeaService.class));
    }

    private void startNfclog() {
        Log.d(TAG, "startNfclog!!");
        PropertiesUtil.set("persist.oem.nfc.debug", "1");
        transact("OEM_NFC_DEBUG_ENABLE");
    }

    private void stopNfclog() {
        Log.d(TAG, "stopNfclog!!");
    }

    private void findViews() {
        this.mCheckBoxSaveLog = (CheckBoxPreference) findPreference("save_log");
        this.mRemoveLog = findPreference("remove_grab");
        this.mOtherLog = findPreference("other_log");
        this.mSwitch = findPreference("switches");
        this.mPackLog = findPreference("pack_grab");
        this.mPackLog.setSummary(R.string.pack_summary);
        this.mCheckBoxTcpdump = (CheckBoxPreference) findPreference("save_tcpdump");
        this.mCheckBoxBugreport = (CheckBoxPreference) findPreference("save_bugreport");
        this.mCheckBoxSystrace = (CheckBoxPreference) findPreference("save_systrace");
        this.mCheckMemLog = (CheckBoxPreference) findPreference("save_memLog");
        this.mAdvanced = findPreference("advanced");
        if (getIntent() != null && getIntent().getAction().equals(ACTION_START_LOG)) {
            getPreferenceScreen().removePreference(findPreference("switches"));
        }
    }

    private void enableButtons() {
        this.mRemoveLog.setEnabled(true);
        this.mAdvanced.setEnabled(true);
        this.mCheckBoxTcpdump.setEnabled(true);
        this.mCheckBoxBugreport.setEnabled(true);
        this.mCheckBoxSystrace.setEnabled(true);
        this.mCheckMemLog.setEnabled(true);
    }

    private void disableButtons() {
        this.mRemoveLog.setEnabled(false);
        this.mAdvanced.setEnabled(false);
        this.mCheckBoxTcpdump.setEnabled(false);
        this.mCheckBoxBugreport.setEnabled(false);
        this.mCheckBoxSystrace.setEnabled(false);
        this.mCheckMemLog.setEnabled(false);
    }

    private static boolean connect() {
        if (mSocket != null) {
            return true;
        }
        Slog.i(TAG, "connecting...");
        try {
            setmSocket(new LocalSocket());
            mSocket.connect(new LocalSocketAddress("oemlogkit", Namespace.RESERVED));
            mIn = mSocket.getInputStream();
            mOut = mSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            disconnect();
            return false;
        }
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

    public static void disconnect() {
        Slog.i(TAG, "disconnecting...");
        IoUtils.closeQuietly(mSocket);
        IoUtils.closeQuietly(mOut);
        IoUtils.closeQuietly(mIn);
        setmSocket(null);
        mOut = null;
        mIn = null;
    }

    private void startSaveLog() {
        PropertiesUtil.set("persist.sys.assert.panic", "true");
        transact("LOGBEGIN");
        this.mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    private void stopSaveLog() {
        PropertiesUtil.set("persist.sys.assert.panic", "false");
        transact("LOGSTOP");
    }

    private void setListeners() {
        this.mRemoveLog.setOnPreferenceClickListener(this);
        this.mAdvanced.setOnPreferenceClickListener(this);
        this.mOtherLog.setOnPreferenceClickListener(this);
        this.mSwitch.setOnPreferenceClickListener(this);
        this.mPackLog.setOnPreferenceClickListener(this);
        this.mCheckBoxTcpdump.setOnPreferenceClickListener(this);
        this.mCheckBoxBugreport.setOnPreferenceClickListener(this);
        this.mCheckBoxSystrace.setOnPreferenceClickListener(this);
        this.mCheckMemLog.setOnPreferenceClickListener(this);
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("clear_cache".equals(key)) {
            dialog(R.string.cache_clean);
        } else if ("remove_grab".equals(key)) {
            dialog(R.string.clean_title);
        } else if ("pack_grab".equals(key)) {
            dialog(R.string.pack_log);
        } else if ("advanced".equals(key)) {
            startActivity(new Intent(this, AdvancedActivity.class));
        } else if ("switches".equals(key)) {
            startActivity(new Intent(this, SwitchActivity.class));
        } else if ("save_tcpdump".equals(key)) {
            isChecked = this.mCheckBoxTcpdump.isChecked();
            this.mCheckBoxTcpdump.setChecked(isChecked);
            if (isChecked) {
                PropertiesUtil.set("persist.sys.tcpdump", "yes");
                this.mCheckBoxTcpdump.setSummary(R.string.tcpdump_opened);
            } else {
                PropertiesUtil.set("persist.sys.tcpdump", "no");
                this.mCheckBoxTcpdump.setSummary(R.string.tcpdump_closed);
            }
        } else if ("save_bugreport".equals(key)) {
            isChecked = this.mCheckBoxBugreport.isChecked();
            this.mCheckBoxBugreport.setChecked(isChecked);
            if (isChecked) {
                PropertiesUtil.set("persist.sys.bugreport", "yes");
                startService(new Intent(this, RecordBugreportService.class));
            } else {
                PropertiesUtil.set("persist.sys.bugreport", "no");
                stopService(new Intent(this, RecordBugreportService.class));
            }
        } else if ("save_systrace".equals(key)) {
            isChecked = this.mCheckBoxSystrace.isChecked();
            this.mCheckBoxSystrace.setChecked(isChecked);
            if (isChecked) {
                PropertiesUtil.set("persist.sys.systrace.enable", "true");
            } else {
                PropertiesUtil.set("persist.sys.systrace.enable", "false");
            }
        } else if ("save_memLog".equals(key)) {
            isChecked = this.mCheckMemLog.isChecked();
            this.mCheckMemLog.setChecked(isChecked);
            if (isChecked) {
                PropertiesUtil.set("persist.sys.memorylog", "true");
            } else {
                PropertiesUtil.set("persist.sys.memorylog", "false");
            }
        }
        return false;
    }

    private void saveOldLog() {
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
            try {
                copyFolder(new File("/data/anr"), fileAnr);
                copyFolder(new File("/data/system/dropbox"), fileDropbox);
                File recovery = new File("/cache/recovery/");
                if (recovery.exists()) {
                    copyFolder(recovery, fileRecovery);
                }
                File kmsg = new File("/cache/kmsg/");
                if (kmsg.exists()) {
                    copyFolder(kmsg, fileChargeLog);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void cleanLog() {
        disableButtons();
        transact("DELETELOG");
        new Thread(new Runnable() {
            public void run() {
                OEMLogKitMainActivity.this.mDis = new DataInputStream(OEMLogKitMainActivity.mIn);
                try {
                    OEMLogKitMainActivity.this.mDis.read(OEMLogKitMainActivity.buf);
                    Slog.i(OEMLogKitMainActivity.TAG, "cleanLog: '" + new String(OEMLogKitMainActivity.buf));
                    OEMLogKitMainActivity.this.mHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(System.currentTimeMillis()));
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

    public void zipFile(String baseDir, String fileName) throws Exception {
        Log.d(TAG, "zipFile, begin to collect file...");
        List fileList = getSubFiles(new File(baseDir));
        fileList.addAll(getSubFiles(new File("/cache/recovery/")));
        Log.d(TAG, "zipFile, finish to collect file!");
        Log.d(TAG, "zipFile, begin to zip file...");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName));
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
        Log.d(TAG, "zipFile, finish to zip file!");
        shareFiles(fileName);
    }

    protected void dialog(final int promptResource) {
        Builder builder = new Builder(this);
        builder.setTitle(promptResource);
        if (promptResource == R.string.clean_title) {
            builder.setPositiveButton(R.string.clean_ok, new OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    OEMLogKitMainActivity.this.cleanLog();
                }
            });
        }
        if (promptResource == R.string.pack_log) {
            builder.setPositiveButton(R.string.pack_ok, new OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    OEMLogKitMainActivity.this.mCheckBoxSaveLog.setEnabled(false);
                    Thread mThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                String currentLogPath = GrabOtherActivity.getLogPath();
                                Log.d(OEMLogKitMainActivity.TAG, "begin to dump media db");
                                GrabOtherActivity.dumpMediaDBPublic(OEMLogKitMainActivity.mContext);
                                Log.d(OEMLogKitMainActivity.TAG, "finish to dump media db");
                                Log.d(OEMLogKitMainActivity.TAG, "begin to get ps log");
                                GrabOtherActivity.sendMsg("ps");
                                Log.d(OEMLogKitMainActivity.TAG, "finish to get ps log");
                                Log.d(OEMLogKitMainActivity.TAG, "begin to get dumpsys batterystats log");
                                GrabOtherActivity.sendMsg("dumpsys batterystats");
                                Log.d(OEMLogKitMainActivity.TAG, "finish to get dumpsys batterystats log");
                                OEMLogKitMainActivity.this.zipFile(currentLogPath, Environment.getExternalStorageDirectory().getPath() + "/" + OEMLogKitMainActivity.getCurrentTime() + "_oemlog.zip");
                                OEMLogKitMainActivity.this.mHandler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mThread.setPriority(1);
                    mThread.start();
                }
            });
        }
        builder.setNegativeButton(R.string.clean_cancel, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (promptResource == R.string.clean_title) {
                    OEMLogKitMainActivity.this.enableButtons();
                }
            }
        });
        builder.create().show();
    }

    protected void onResume() {
        Log.d(TAG, "onResume start");
        restoreLastState();
        super.onResume();
    }

    public static LocalSocket getmSocket() {
        if (mSocket == null) {
            connect();
        }
        return mSocket;
    }

    public static void setmSocket(LocalSocket mSocket) {
        mSocket = mSocket;
    }
}
