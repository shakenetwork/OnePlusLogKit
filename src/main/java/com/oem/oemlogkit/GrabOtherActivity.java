package com.oem.oemlogkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemService;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import libcore.io.IoUtils;

public class GrabOtherActivity extends Activity implements OnClickListener {
    private static final int BATTERY_DUMPSTATE = 6;
    private static final int ENABLE_BUTTON = 110;
    private static final int LOG_ATRACE = 9;
    private static final int LOG_BUGREPORT = 8;
    private static final int LOG_DUMPSTATE = 5;
    private static final int LOG_DUMPSYS = 4;
    private static final int LOG_PS = 1;
    private static final int LOG_RPM_INT = 10;
    private static final int LOG_SERVER = 3;
    private static final int LOG_TOP = 2;
    private static final int MEDIA_DB = 7;
    static final String TAG = "OEMLogKitMainActivity";
    private static Context mContext;
    private Button atrace_button;
    private Button battery_dumpstate;
    private final byte[] buf = new byte[1024];
    private Button bugreport_button;
    private Button dumpstate_button;
    private Button dumpsys_button;
    private Button fetch_media_db;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    GrabOtherActivity.sendMsg("ps");
                    Toast.makeText(GrabOtherActivity.this, R.string.ps_info, 0).show();
                    return;
                case 2:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 15000);
                            GrabOtherActivity.sendMsg("top -m 10 -t -d 1");
                        }
                    }).start();
                    Toast.makeText(GrabOtherActivity.this, R.string.top_info, 0).show();
                    return;
                case 3:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 1000);
                            GrabOtherActivity.sendMsg("service list");
                        }
                    }).start();
                    Toast.makeText(GrabOtherActivity.this, R.string.server_info, 0).show();
                    return;
                case 4:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 5000);
                            GrabOtherActivity.sendMsg("dumpsys");
                        }
                    }).start();
                    Toast.makeText(GrabOtherActivity.this, R.string.dumpsys_info, 0).show();
                    return;
                case 5:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    Toast.makeText(GrabOtherActivity.this, R.string.dumpstate_info, 0).show();
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 30000);
                            SystemService.start("dumpstate_log");
                            SystemClock.sleep(100);
                            while (!PropertiesUtil.get("init.svc.dumpstate_log", "").equals("stopped")) {
                                SystemClock.sleep(1000);
                                Slog.e(GrabOtherActivity.TAG, "getting dumpstate_log");
                            }
                            Slog.e(GrabOtherActivity.TAG, "getting dumpstate_log done : " + new File("/sdcard/dumpstate.log.txt"));
                            String fileName = GrabOtherActivity.getLogPath() + "/" + new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss")
                                    .format(new Date(System.currentTimeMillis())) + "_dumpstate.log";
                            File[] files = new File("/sdcard/").listFiles();
                            for (File file2 : files) {
                                Slog.e(GrabOtherActivity.TAG, "" + file2.getName());
                                if (!file2.isDirectory() && file2.getName().startsWith("dumpstate_log")) {
                                    file2.renameTo(new File(fileName));
                                    Slog.e(GrabOtherActivity.TAG, "file2.getName() : " + file2.getName());
                                }
                            }
                        }
                    }).start();
                    return;
                case 6:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 5000);
                            GrabOtherActivity.sendMsg("dumpsys batterystats");
                        }
                    }).start();
                    Toast.makeText(GrabOtherActivity.this, R.string.battery_dumpstate, 0).show();
                    return;
                case 7:
                    Toast.makeText(GrabOtherActivity.this, R.string.fetching_media_db, 0).show();
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.dumpMediaDatabases();
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 1000);
                        }
                    }).start();
                    return;
                case 8:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 70000);
                            GrabOtherActivity.sendMsg("bugreport");
                        }
                    }).start();
                    Toast.makeText(GrabOtherActivity.this, R.string.bugreport_info, 0).show();
                    return;
                case 9:
                    GrabOtherActivity.this.setButtonEnabled(false);
                    new Thread(new Runnable() {
                        public void run() {
                            GrabOtherActivity.this.handler.sendEmptyMessageDelayed(GrabOtherActivity.ENABLE_BUTTON, 30000);
                            SystemService.start("atrace");
                            SystemClock.sleep(100);
                            while (!PropertiesUtil.get("init.svc.atrace", "").equals("stopped")) {
                                SystemClock.sleep(1000);
                                Slog.e(GrabOtherActivity.TAG, "getting atrace_log");
                            }
                            Slog.e(GrabOtherActivity.TAG, "getting atrace_log done : " + new File("/sdcard/atrace.html"));
                            long currentTimeMillis = System.currentTimeMillis();
                            new File("/sdcard/atrace.html").renameTo(new File(GrabOtherActivity.getLogPath() + "/" +
                                    new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date(currentTimeMillis)) + "_atrace.html"));
                        }
                    }).start();
                    Toast.makeText(GrabOtherActivity.this, R.string.atrace_info, 0).show();
                    return;
                case 10:
                    GrabOtherActivity.sendMsg("cat /d/rpm_stats");
                    GrabOtherActivity.sendMsg("cat /proc/interrupts");
                    Toast.makeText(GrabOtherActivity.this, R.string.rpm_int_info, 0).show();
                    return;
                case GrabOtherActivity.ENABLE_BUTTON /*110*/:
                    GrabOtherActivity.this.setButtonEnabled(true);
                    Toast.makeText(GrabOtherActivity.this, R.string.grab_finish, 0).show();
                    return;
                default:
                    return;
            }
        }
    };
    private OutputStream mOut;
    private Button ps_button;
    private Button rpm_int_button;
    private Button server_button;
    private Button top_button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.grab_other);
        findViews();
        setListeners();
        connect();
    }

    private void findViews() {
        this.ps_button = (Button) findViewById(R.id.button_ps);
        this.top_button = (Button) findViewById(R.id.button_top);
        this.server_button = (Button) findViewById(R.id.button_server);
        this.dumpsys_button = (Button) findViewById(R.id.button_dump);
        this.dumpstate_button = (Button) findViewById(R.id.button_state);
        this.battery_dumpstate = (Button) findViewById(R.id.battery_history);
        this.fetch_media_db = (Button) findViewById(R.id.button_media_db);
        this.bugreport_button = (Button) findViewById(R.id.button_bugreport);
        this.atrace_button = (Button) findViewById(R.id.button_atrace);
        this.rpm_int_button = (Button) findViewById(R.id.button_rpm_int);
    }

    private void setListeners() {
        this.ps_button.setOnClickListener(this);
        this.top_button.setOnClickListener(this);
        this.server_button.setOnClickListener(this);
        this.dumpsys_button.setOnClickListener(this);
        this.dumpstate_button.setOnClickListener(this);
        this.battery_dumpstate.setOnClickListener(this);
        this.fetch_media_db.setOnClickListener(this);
        this.bugreport_button.setOnClickListener(this);
        this.atrace_button.setOnClickListener(this);
        this.rpm_int_button.setOnClickListener(this);
    }

    private void setButtonEnabled(boolean on) {
        this.ps_button.setEnabled(on);
        this.top_button.setEnabled(on);
        this.server_button.setEnabled(on);
        this.dumpsys_button.setEnabled(on);
        this.dumpstate_button.setEnabled(on);
        this.battery_dumpstate.setEnabled(on);
        this.fetch_media_db.setEnabled(on);
        this.bugreport_button.setEnabled(on);
        this.atrace_button.setEnabled(on);
        this.rpm_int_button.setEnabled(on);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ps /*2131361844*/:
                this.handler.sendEmptyMessage(1);
                return;
            case R.id.button_top /*2131361845*/:
                this.handler.sendEmptyMessage(2);
                return;
            case R.id.button_server /*2131361846*/:
                this.handler.sendEmptyMessage(3);
                return;
            case R.id.button_dump /*2131361847*/:
                this.handler.sendEmptyMessage(4);
                return;
            case R.id.button_state /*2131361848*/:
                this.handler.sendEmptyMessage(5);
                return;
            case R.id.battery_history /*2131361849*/:
                this.handler.sendEmptyMessage(6);
                return;
            case R.id.button_media_db /*2131361850*/:
                this.handler.sendEmptyMessage(7);
                return;
            case R.id.button_bugreport /*2131361851*/:
                this.handler.sendEmptyMessage(8);
                return;
            case R.id.button_atrace /*2131361852*/:
                this.handler.sendEmptyMessage(9);
                return;
            case R.id.button_rpm_int /*2131361853*/:
                this.handler.sendEmptyMessage(10);
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void getCmdContent(java.lang.String r18, java.lang.String r19) {
        /*
        r5 = new java.io.File;
        r0 = r18;
        r5.<init>(r0);
        if (r5 == 0) goto L_0x008d;
    L_0x0009:
        r13 = r5.createNewFile();	 Catch:{ IOException -> 0x00ce }
        if (r13 == 0) goto L_0x008d;
    L_0x000f:
        r10 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x00ae }
        r6 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00ae }
        r6.<init>(r5);	 Catch:{ Exception -> 0x00ae }
        r13 = new java.lang.ProcessBuilder;	 Catch:{ Exception -> 0x00ae }
        r14 = 0;
        r14 = new java.lang.String[r14];	 Catch:{ Exception -> 0x00ae }
        r13.<init>(r14);	 Catch:{ Exception -> 0x00ae }
        r14 = 3;
        r14 = new java.lang.String[r14];	 Catch:{ Exception -> 0x00ae }
        r15 = "/system/bin/sh";
        r16 = 0;
        r14[r16] = r15;	 Catch:{ Exception -> 0x00ae }
        r15 = "-c";
        r16 = 1;
        r14[r16] = r15;	 Catch:{ Exception -> 0x00ae }
        r15 = 2;
        r14[r15] = r19;	 Catch:{ Exception -> 0x00ae }
        r13 = r13.command(r14);	 Catch:{ Exception -> 0x00ae }
        r14 = 1;
        r13 = r13.redirectErrorStream(r14);	 Catch:{ Exception -> 0x00ae }
        r9 = r13.start();	 Catch:{ Exception -> 0x00ae }
        r7 = r9.getInputStream();	 Catch:{ Exception -> 0x00ae }
        r8 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x00ae }
        r8.<init>(r7);	 Catch:{ Exception -> 0x00ae }
        r2 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x00ae }
        r2.<init>(r8);	 Catch:{ Exception -> 0x00ae }
        r12 = "";
    L_0x0052:
        r12 = r2.readLine();	 Catch:{ Exception -> 0x00ae }
        if (r12 == 0) goto L_0x006d;
    L_0x0058:
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x00ae }
        r14 = r14 - r10;
        r16 = 180000; // 0x2bf20 float:2.52234E-40 double:8.8932E-319;
        r13 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r13 <= 0) goto L_0x008e;
    L_0x0064:
        r13 = "OEMLogKitMainActivity";
        r14 = "Over GRAB_LOG_MAX_TIME";
        android.util.Log.e(r13, r14);	 Catch:{ Exception -> 0x00ae }
    L_0x006d:
        r13 = r9.getOutputStream();	 Catch:{ Exception -> 0x00ae }
        r13.close();	 Catch:{ Exception -> 0x00ae }
        r13 = r9.getInputStream();	 Catch:{ Exception -> 0x00ae }
        r13.close();	 Catch:{ Exception -> 0x00ae }
        r13 = r9.getErrorStream();	 Catch:{ Exception -> 0x00ae }
        r13.close();	 Catch:{ Exception -> 0x00ae }
        r2.close();	 Catch:{ Exception -> 0x00ae }
        r2 = 0;
        r6.flush();	 Catch:{ Exception -> 0x00ae }
        r6.close();	 Catch:{ Exception -> 0x00ae }
        r6 = 0;
    L_0x008d:
        return;
    L_0x008e:
        r13 = "top -m 10 -t -d 1";
        r0 = r19;
        r13 = r0.equals(r13);	 Catch:{ Exception -> 0x00ae }
        if (r13 == 0) goto L_0x00ee;
    L_0x0099:
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x00ae }
        r14 = r14 - r10;
        r16 = 15000; // 0x3a98 float:2.102E-41 double:7.411E-320;
        r13 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r13 <= 0) goto L_0x00ee;
    L_0x00a4:
        r13 = "OEMLogKitMainActivity";
        r14 = "top cmd over GRAB_LOG_MAX_TIME";
        android.util.Log.e(r13, r14);	 Catch:{ Exception -> 0x00ae }
        goto L_0x006d;
    L_0x00ae:
        r4 = move-exception;
        r13 = "OEMLogKitMainActivity";
        r14 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00ce }
        r14.<init>();	 Catch:{ IOException -> 0x00ce }
        r15 = "getCmdContent Exception=";
        r14 = r14.append(r15);	 Catch:{ IOException -> 0x00ce }
        r15 = r4.getMessage();	 Catch:{ IOException -> 0x00ce }
        r14 = r14.append(r15);	 Catch:{ IOException -> 0x00ce }
        r14 = r14.toString();	 Catch:{ IOException -> 0x00ce }
        android.util.Log.e(r13, r14);	 Catch:{ IOException -> 0x00ce }
        goto L_0x008d;
    L_0x00ce:
        r3 = move-exception;
        r13 = "OEMLogKitMainActivity";
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "getCmdContent IOException=";
        r14 = r14.append(r15);
        r15 = r3.getMessage();
        r14 = r14.append(r15);
        r14 = r14.toString();
        android.util.Log.e(r13, r14);
        goto L_0x008d;
    L_0x00ee:
        r13 = r12.getBytes();	 Catch:{ Exception -> 0x00ae }
        r6.write(r13);	 Catch:{ Exception -> 0x00ae }
        r13 = "\r\n";
        r13 = r13.getBytes();	 Catch:{ Exception -> 0x00ae }
        r6.write(r13);	 Catch:{ Exception -> 0x00ae }
        goto L_0x0052;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oem.oemlogkit.GrabOtherActivity.getCmdContent(java.lang.String, java.lang.String):void");
    }

    public static String getLogPath() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/oem_log/");
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                return null;
            }
            for (File file2 : files) {
                if (file2.isDirectory() && file2.getAbsolutePath().contains("_current")) {
                    return file2.getAbsolutePath();
                }
            }
        } else {
            Log.d(TAG, "/sdcard/oem_log/ does not exist!");
        }
        return null;
    }

    public static void sendMsg(String cmd) {
        String path = getLogPath();
        if (path == null) {
            Log.d(TAG, "/sdcard/oem_log/ does not exist!");
            path = "/sdcard/oem_log/";
            new File(path).mkdirs();
        }
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        Date d = new Date(currentTimeMillis);
        String fileName = path + "/" + f.format(d) + "_" + cmd + ".log";
        if (cmd.contains("batterystats")) {
            cmd = "dumpsys batterystats --enable full-wake-history;" + cmd;
        }
        if (cmd.contains("rpm_stats")) {
            fileName = path + "/" + f.format(d) + "_" + "rpm_stats" + ".log";
        }
        if (cmd.contains("interrupts")) {
            fileName = path + "/" + f.format(d) + "_" + "interrupts" + ".log";
        }
        Log.d(TAG, "fileName: " + fileName);
        Log.d(TAG, "cmd: " + cmd);
        getCmdContent(fileName, cmd);
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
            Log.d(TAG, "cmd len is " + len);
            this.mOut.flush();
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

    public static void dumpMediaDBPublic(Context context) {
        mContext = context;
        dumpMediaDatabases();
    }

    private static void dumpMediaDatabases() {
        String logPath;
        Slog.d(TAG, "dumpMediaDatabases start");
        if (getLogPath() != null) {
            logPath = getLogPath() + "/media_db_dump/";
        } else {
            logPath = "/sdcard/oem_log_media_db_dump/";
        }
        File path = new File(logPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        long currentTimeMillis = System.currentTimeMillis();
        String nameTag = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date(currentTimeMillis));
        if (!(mContext == null || logPath == null)) {
            dumpDbLogs(Media.INTERNAL_CONTENT_URI, nameTag, logPath);
            dumpDbLogs(Media.EXTERNAL_CONTENT_URI, nameTag, logPath);
            dumpDbLogs(Images.Media.INTERNAL_CONTENT_URI, nameTag, logPath);
            dumpDbLogs(Images.Media.EXTERNAL_CONTENT_URI, nameTag, logPath);
            dumpDbLogs(Video.Media.INTERNAL_CONTENT_URI, nameTag, logPath);
            dumpDbLogs(Video.Media.EXTERNAL_CONTENT_URI, nameTag, logPath);
            dumpDbLogs(Files.getContentUri("internal"), nameTag, logPath);
            dumpDbLogs(Files.getContentUri("external"), nameTag, logPath);
            dumpDbDebugLogs(nameTag, logPath);
        }
        long fetch_time = System.currentTimeMillis() - currentTimeMillis;
        Slog.d(TAG, "dumpMediaDatabases: cost time = " + new SimpleDateFormat("mm:ss.SSS").format(new Date(fetch_time)));
    }

    private static void dumpDbLogs(Uri uri, String logNamePrefix, String logPath) {
        FileWriter fileWriter;
        boolean isFile = false;
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Slog.v(TAG, "dumpDbLogs: " + uri);
        if ("file".equals(uri.getPathSegments().get(1))) {
            isFile = true;
        }
        File file2 = new File(logPath + logNamePrefix + "_" + ((String) uri.getPathSegments().get(0)) + "_" + ((String) uri.getPathSegments().get(1)) + "_db.log");
        if (file2 != null) {
            AutoCloseable autoCloseable;
            try {
                if (file2.createNewFile()) {
                    fileWriter = new FileWriter(file2);
                    autoCloseable = null;
                    ArrayList<String> arrayList = new ArrayList(Arrays.asList(new String[]{"_id", "_data", "_size", "mime_type"}));
                    if (isFile) {
                        arrayList.add("format");
                        arrayList.add("media_type");
                    }
                    Uri uri2 = uri;
                    autoCloseable = mContext.getContentResolver().query(uri2, (String[]) arrayList.toArray(new String[arrayList.size()]), null, null, null);
                    fileWriter.write("[" + uri + "] - " + autoCloseable.getCount() + " rows. \r\n");
                    if (autoCloseable != null && autoCloseable.getCount() > 0 && autoCloseable.moveToFirst()) {
                        fileWriter.write("\r\n ====================== Start ====================== \r\n");
                        while (autoCloseable.moveToNext()) {
                            long id = autoCloseable.getLong(0);
                            String data = autoCloseable.getString(1);
                            long size = autoCloseable.getLong(2);
                            String mimetype = autoCloseable.getString(3);
                            fileWriter.write("    [" + id + "] - " + data + " - size(" + size + ") : ");
                            if (isFile) {
                                fileWriter = fileWriter;
                                fileWriter.write("format(" + autoCloseable.getLong(4) + ") : ");
                                fileWriter = fileWriter;
                                fileWriter.write("mediaType(" + autoCloseable.getLong(5) + ") : ");
                            }
                            fileWriter.write("mime(" + mimetype + ") \r\n");
                        }
                        fileWriter.write(" ====================== Finish ====================== \r\n");
                    }
                    IoUtils.closeQuietly(autoCloseable);
                    if (fileWriter != null) {
                        fileWriter.flush();
                        fileWriter.close();
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "dumpDbLogs Exception: ", ex);
            } catch (Throwable th) {
                IoUtils.closeQuietly(autoCloseable);
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
        }
    }

    private static void dumpDbDebugLogs(String logNamePrefix, String logPath) {
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Uri DBG_URI = Uri.parse("content://media/external/op_debug");
        Slog.v(TAG, "dumpDbDebugLogs: " + DBG_URI);
        File file2 = new File(logPath + logNamePrefix + "_op_debug.log");
        if (file2 != null) {
            FileWriter fileWriter;
            AutoCloseable autoCloseable;
            try {
                if (file2.createNewFile()) {
                    fileWriter = new FileWriter(file2);
                    autoCloseable = null;
                    ArrayList<String> arrayList = new ArrayList(Arrays.asList(new String[]{"_id", "_tag", "_action", "_uri", "sel", "sel_arg", "_pkg_name", "datetime"}));
                    autoCloseable = mContext.getContentResolver().query(DBG_URI, (String[]) arrayList.toArray(new String[arrayList.size()]), null, null, null);
                    fileWriter.write("[" + DBG_URI + "] - " + autoCloseable.getCount() + " rows. \r\n");
                    if (autoCloseable != null && autoCloseable.getCount() > 0 && autoCloseable.moveToFirst()) {
                        fileWriter.write("\r\n ====================== Start ====================== \r\n");
                        while (autoCloseable.moveToNext()) {
                            long id = autoCloseable.getLong(0);
                            String _tag = autoCloseable.getString(1);
                            String _action = autoCloseable.getString(2);
                            String _uri = autoCloseable.getString(3);
                            String sel = autoCloseable.getString(4);
                            String sel_arg = autoCloseable.getString(5);
                            String _pkg_name = autoCloseable.getString(6);
                            String datetime = autoCloseable.getString(7);
                            fileWriter.write("    [" + id + "] - " + _pkg_name + "[" + _action + "] : " + _uri + ", " + sel + ", " + sel_arg + ", [" + _tag + "] - ");
                            fileWriter.write("(" + datetime + ") \r\n");
                        }
                        fileWriter.write(" ====================== Finish ====================== \r\n");
                    }
                    IoUtils.closeQuietly(autoCloseable);
                    if (fileWriter != null) {
                        fileWriter.flush();
                        fileWriter.close();
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "dumpDbDebugLogs Exception: ", ex);
            } catch (Throwable th) {
                IoUtils.closeQuietly(autoCloseable);
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
        }
    }
}
