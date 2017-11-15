package com.oem.oemlogkit;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OPRecordNmea {
    private static final String NMEA_LAT1 = ",N,";
    private static final String NMEA_LAT2 = ",S,";
    private static final String NMEA_LOCATION = "GPGGA";
    private static final String NMEA_LOCATION1 = "GNGGA";
    private static final String NMEA_LON1 = ",E,";
    private static final String NMEA_LON2 = ",W,";
    private static final String TAG = "OPRecordNmea";
    private boolean D = true;
    private boolean hasError = false;
    private Context mContext;
    private SimpleDateFormat mDateFormat;
    private LocationManager mLocationManager;
    private NmeaListener mNmeaListener;
    private GpsDataSaveUtils mSaveData;
    private int needRecordNmea = 2;

    private class GpsDataSaveUtils {
        private String FILE_PATH = "/sdcard/oem_log/gps_info";
        DataOutputStream mDataOutputStream;
        File mFile;
        FileOutputStream mFileOutputStream;
        PrintWriter mPrintWriter;

        public GpsDataSaveUtils() {
            File file = new File(this.FILE_PATH);
            if (!file.exists()) {
                Log.d(OPRecordNmea.TAG, "Path not exists,create it!!");
                file.mkdirs();
            }
        }

        public void deleteOldestFile() {
            List<File> list = new ArrayList();
            File[] subFiles = new File(this.FILE_PATH).listFiles();
            if (subFiles == null) {
                Log.d(OPRecordNmea.TAG, "no subFiles, return!");
                return;
            }
            for (File file : subFiles) {
                list.add(file);
            }
            if (list != null && list.size() > 0) {
                Collections.sort(list, new Comparator<File>() {
                    public int compare(File file, File newFile) {
                        if (file.lastModified() < newFile.lastModified()) {
                            return 1;
                        }
                        if (file.lastModified() == newFile.lastModified()) {
                            return 0;
                        }
                        return -1;
                    }
                });
            }
        }

        public void createFile(String name) throws IOException {
            File parentPath = new File(this.FILE_PATH);
            if (parentPath.exists() || parentPath.mkdir()) {
                deleteOldestFile();
                this.mFile = new File(this.FILE_PATH + "/" + name + ".nmea");
                if (!this.mFile.exists()) {
                    try {
                        this.mFile.createNewFile();
                    } catch (IOException e) {
                        OPRecordNmea.this.hasError = true;
                        Log.d(OPRecordNmea.TAG, "nmea file won't create, so miss record this time");
                        return;
                    }
                }
                this.mFileOutputStream = new FileOutputStream(this.mFile);
                this.mPrintWriter = new PrintWriter(new BufferedOutputStream(this.mFileOutputStream));
                this.mPrintWriter.println(name);
                this.mPrintWriter.flush();
                return;
            }
            throw new IOException("creat directory:" + this.FILE_PATH + " unsuccessfully !");
        }

        public void writeData(String data) throws IOException {
            this.mPrintWriter.print(data);
        }

        public void flush() {
            if (this.mPrintWriter != null) {
                this.mPrintWriter.flush();
            }
        }

        public void close() {
            if (this.mPrintWriter != null) {
                this.mPrintWriter.close();
            }
        }
    }

    private class NmeaListener implements android.location.GpsStatus.NmeaListener {
        private NmeaListener() {
        }

        public void onNmeaReceived(long timestamp, String nmea) {
            if (!OPRecordNmea.this.hasError) {
                OPRecordNmea.this.checkNmeaDataHasLocation(nmea);
                if (OPRecordNmea.this.needRecordNmea != 2) {
                    try {
                        OPRecordNmea.this.mSaveData.writeData(nmea);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public OPRecordNmea(Context context) {
        this.mContext = context;
        this.mLocationManager = (LocationManager) this.mContext.getSystemService("location");
        this.mNmeaListener = new NmeaListener();
    }

    void startRecord() {
        Log.d(TAG, "startRecord!!");
        createNmeaFile();
        this.mLocationManager.addNmeaListener(this.mNmeaListener);
    }

    void stopRecord() {
        Log.d(TAG, "stopRecord!!");
        this.mLocationManager.removeNmeaListener(this.mNmeaListener);
        this.mSaveData.flush();
        this.mSaveData.close();
    }

    void createNmeaFile() {
        this.mSaveData = new GpsDataSaveUtils();
        this.mDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        try {
            this.mSaveData.createFile(this.mDateFormat.format(Long.valueOf(System.currentTimeMillis())));
        } catch (IOException e) {
            Log.d(TAG, "createNmeaFile fail");
            this.hasError = true;
            e.printStackTrace();
        }
    }

    void checkNmeaDataHasLocation(String nmea) {
        if (!(nmea.indexOf(NMEA_LOCATION) == -1 && nmea.indexOf(NMEA_LOCATION1) == -1)) {
            if (nmea.indexOf(NMEA_LAT1) == -1 && nmea.indexOf(NMEA_LAT2) == -1) {
                if (this.needRecordNmea != 2) {
                    this.needRecordNmea++;
                }
            } else if (nmea.indexOf(NMEA_LON1) == -1 && nmea.indexOf(NMEA_LON2) == -1) {
                Log.d(TAG, "nmea data format is wrong");
                this.needRecordNmea++;
            } else {
                this.needRecordNmea = 0;
            }
        }
    }
}
