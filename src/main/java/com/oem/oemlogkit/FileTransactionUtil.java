package com.oem.oemlogkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.util.EncodingUtils;

public class FileTransactionUtil {
    private static final String DEFAULT_MSG = "#-1:-1:-1:-1#";
    public static final int ENABLE_FLAG = 655360;
    public static final int IDLE = -1;
    private static final String LOG_TAG = "FileTransactionUtil";
    public static final int NUMBER_FLAG = 655362;
    public static final int SIZE_FLAG = 655361;
    public static final int TYPE_FLAG = 655363;
    private static final boolean debug = true;
    private static Object lock = new Object();
    private static final String mPerferenceTag = "MSG";
    private static final String mSharedPerferenceName = "DeviceLogPerference";

    public static void writeDataToFile(int flag, int data, Context context) {
        Log.i(LOG_TAG, "Incoming flag = " + flag + "; data = " + data);
        synchronized (lock) {
            Editor mEditor = context.getSharedPreferences(mSharedPerferenceName, 0).edit();
            String[] items;
            switch (flag) {
                case ENABLE_FLAG /*655360*/:
                    items = msgParser(context).split(":");
                    items[0] = data + "";
                    mEditor.putString(mPerferenceTag, "#" + items[0] + ":" + items[1] + ":" + items[2] + ":" + items[3] + "#");
                    mEditor.commit();
                    break;
                case SIZE_FLAG /*655361*/:
                    items = msgParser(context).split(":");
                    items[1] = data + "";
                    mEditor.putString(mPerferenceTag, "#" + items[0] + ":" + items[1] + ":" + items[2] + ":" + items[3] + "#");
                    mEditor.commit();
                    break;
                case NUMBER_FLAG /*655362*/:
                    items = msgParser(context).split(":");
                    items[2] = data + "";
                    mEditor.putString(mPerferenceTag, "#" + items[0] + ":" + items[1] + ":" + items[2] + ":" + items[3] + "#");
                    mEditor.commit();
                    SystemProperties.set("persist.sys.diag.max.size", String.valueOf(data));
                    break;
                case TYPE_FLAG /*655363*/:
                    items = msgParser(context).split(":");
                    items[3] = data + "";
                    mEditor.putString(mPerferenceTag, "#" + items[0] + ":" + items[1] + ":" + items[2] + ":" + items[3] + "#");
                    mEditor.commit();
                    break;
            }
        }
    }

    public static int readDataFromFile(int flag, Context context) {
        Log.i(LOG_TAG, "Incoming flag = " + flag);
        String msg = "";
        String[] items;
        switch (flag) {
            case ENABLE_FLAG /*655360*/:
                items = msgParser(context).split(":");
                if (Integer.parseInt(items[0]) != -1) {
                    Log.i(LOG_TAG, "Size, item = " + items[0]);
                    return Integer.parseInt(items[0]);
                }
                break;
            case SIZE_FLAG /*655361*/:
                items = msgParser(context).split(":");
                if (Integer.parseInt(items[1]) != -1) {
                    Log.i(LOG_TAG, "Size, item = " + items[1]);
                    return Integer.parseInt(items[1]);
                }
                break;
            case NUMBER_FLAG /*655362*/:
                items = msgParser(context).split(":");
                if (Integer.parseInt(items[2]) != -1) {
                    Log.i(LOG_TAG, "Size, item = " + items[2]);
                    return Integer.parseInt(items[2]);
                }
                break;
            case TYPE_FLAG /*655363*/:
                items = msgParser(context).split(":");
                if (Integer.parseInt(items[3]) != -1) {
                    Log.i(LOG_TAG, "Size, item = " + items[3]);
                    return Integer.parseInt(items[3]);
                }
                break;
        }
        return -1;
    }

    public static void writeDisableMsg(Context context) {
        synchronized (lock) {
            SharedPreferences mSharedPreferences = context.getSharedPreferences(mSharedPerferenceName, 0);
            String[] items = msgParser(context).split(":");
            items[0] = "-1";
            String msg = "#" + items[0] + ":" + items[1] + ":" + items[2] + ":" + items[3] + "#";
            Log.i(LOG_TAG, "Write Disable Msg = " + msg);
            Editor mEditor = mSharedPreferences.edit();
            mEditor.putString(mPerferenceTag, msg);
            mEditor.commit();
        }
    }

    private static String msgParser(Context context) {
        String msg = context.getSharedPreferences(mSharedPerferenceName, 0).getString(mPerferenceTag, DEFAULT_MSG);
        msg = msg.substring(msg.indexOf("#") + 1, msg.lastIndexOf("#"));
        Log.i(LOG_TAG, "Parser get msg = " + msg);
        return msg;
    }

    public static boolean readInstallSpecialApkFlag(Context context) {
        return context.getSharedPreferences("InstallSpecialApk", 0).getInt("APK_FLAG", 0) == 1;
    }

    public static void writeInstallSpecialApkFlag(int flag, Context context) {
        Editor mEditor = context.getSharedPreferences("InstallSpecialApk", 0).edit();
        mEditor.putInt("APK_FLAG", flag);
        mEditor.commit();
    }

    public static ArrayList<String> parentContents(String path, String quhao) {
        ArrayList<String> fileNames = null;
        File file = new File(path);
        if (file.isDirectory()) {
            fileNames = new ArrayList();
            String[] files = file.list();
            Arrays.sort(files);
            int i = 0;
            while (i < files.length) {
                if (files[i].contains("-") && files[i].split("-")[0].equals(quhao)) {
                    fileNames.add(files[i]);
                }
                i++;
            }
        }
        return fileNames;
    }

    private static void copyFiles(String name) {
        String path;
        InputStream inputStream;
        Throwable th;
        Log.d("cbt", "copyFiles-name:" + name);
        for (String d : name.split("-")) {
            Log.d("cbt", d);
        }
        String srcString = "system/oppo_app_bak";
        if (num == 2) {
            path = "data/oppo-app";
        } else {
            path = "data/oppo-sys-app";
        }
        try {
            InputStream inputStream2 = new FileInputStream(srcString + "/" + name);
            try {
                byte[] buffer = new byte[4096];
                File diagcfg_file = new File(path + "/" + name);
                if (diagcfg_file != null) {
                    try {
                        if (diagcfg_file.exists()) {
                            diagcfg_file.delete();
                        }
                    } catch (Exception e) {
                        inputStream = inputStream2;
                        try {
                            Log.d("DeviceLog", "isPassed=false");
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        inputStream = inputStream2;
                        throw th;
                    }
                }
                if (!diagcfg_file.exists()) {
                    diagcfg_file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(diagcfg_file);
                while (true) {
                    try {
                        int count = inputStream2.read(buffer);
                        if (count != -1) {
                            fos.write(buffer, 0, count);
                        } else {
                            fos.flush();
                            Log.d("cbt", "isSuccess=" + diagcfg_file.setReadable(true, false));
                            return;
                        }
                    } catch (Exception e2) {
                        File file = diagcfg_file;
                    } catch (Throwable th4) {
                        th = th4;
                    }
                }
            } catch (Exception e3) {
                Log.d("DeviceLog", "isPassed=false");
            } catch (Throwable th5) {
                th = th5;
                inputStream = inputStream2;
                throw th;
            }
        } catch (Exception e4) {
            Log.d("DeviceLog", "isPassed=false");
        }
    }

    public static void installSpecialApk(String zoneID, Context context) {
        ArrayList<String> specialApkName = parentContents("system/oppo_app_bak", zoneID);
        Log.d("cbt", "installSpecialApk ing");
        Log.d("cbt", "specialApkName.size=" + specialApkName.size());
        for (int i = 0; i < specialApkName.size(); i++) {
            Log.d("cbt", "specialApkName-i " + ((String) specialApkName.get(i)));
            copyFiles((String) specialApkName.get(i));
        }
        SystemProperties.set("persist.sys.pms.scanoppo-app", "true");
        Toast.makeText(context, "install " + specialApkName.size() + " apk", 0).show();
        writeInstallSpecialApkFlag(1, context);
        File persistFile = new File("persist/oppo_cbt.txt");
        try {
            if (persistFile.exists()) {
                persistFile.delete();
            }
            persistFile.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(persistFile));
            output.write(zoneID);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPersistFileExist() {
        if (new File("persist/oppo_cbt.txt").exists()) {
            return true;
        }
        return false;
    }

    public static String readPersistFile() {
        File persistFile = new File("persist/oppo_cbt.txt");
        String res = "";
        if (!persistFile.exists()) {
            return res;
        }
        try {
            FileInputStream fis = new FileInputStream(persistFile);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fis.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
    }
}
