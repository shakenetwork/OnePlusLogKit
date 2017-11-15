package com.oem.oemlogkit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PowerMoniterCurrentActivity extends Activity {
    private static final String ELEC_PATH = "/sys/class/power_supply/battery/current_now";
    private static final String TAG = "PowerMoniterCurrentActivity";
    private static final int UPDATE_TEXTVIEW = 1;
    private TextView mAverageCurrent;
    private ArrayList<String> mDataList = new ArrayList();
    private Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                int elec = PowerMoniterCurrentActivity.readFileByLines(PowerMoniterCurrentActivity.ELEC_PATH) / 1000;
                PowerMoniterCurrentActivity.this.mWaveView.addPoint(elec / 10);
                PowerMoniterCurrentActivity.this.mWaveView.draw();
                PowerMoniterCurrentActivity.this.mDataList.add(elec + "");
                int allCurrent = 0;
                for (int i = 0; i < PowerMoniterCurrentActivity.this.mDataList.size(); i++) {
                    allCurrent += Integer.parseInt((String) PowerMoniterCurrentActivity.this.mDataList.get(i));
                }
                PowerMoniterCurrentActivity.this.mAverageCurrent.setText(PowerMoniterCurrentActivity.this.getString(R.string.average_current) + ": " + (allCurrent / PowerMoniterCurrentActivity.this.mDataList.size()) + " mA\n" + PowerMoniterCurrentActivity.this.getString(R.string.real_time_current) + ": " + elec + " mA");
            }
        }
    };
    private Button mStart;
    private Button mStop;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private WaveView mWaveView;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.current_graph);
        getWindow().addFlags(128);
        this.mWaveView = (WaveView) findViewById(R.id.wave);
        this.mAverageCurrent = (TextView) findViewById(R.id.average_current);
        this.mStart = (Button) findViewById(R.id.start_button);
        this.mStart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PowerMoniterCurrentActivity.this.mTimer == null) {
                    PowerMoniterCurrentActivity.this.mTimer = new Timer();
                }
                if (PowerMoniterCurrentActivity.this.mTimerTask == null) {
                    PowerMoniterCurrentActivity.this.mTimerTask = new TimerTask() {
                        public void run() {
                            PowerMoniterCurrentActivity.this.mHander.sendEmptyMessage(1);
                        }
                    };
                }
                if (!(PowerMoniterCurrentActivity.this.mTimer == null || PowerMoniterCurrentActivity.this.mTimerTask == null)) {
                    PowerMoniterCurrentActivity.this.mTimer.schedule(PowerMoniterCurrentActivity.this.mTimerTask, 0, 500);
                }
                PowerMoniterCurrentActivity.this.mStart.setEnabled(false);
                PowerMoniterCurrentActivity.this.mWaveView.clearPoint();
            }
        });
        this.mStop = (Button) findViewById(R.id.stop_button);
        this.mStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PowerMoniterCurrentActivity.this.mTimer != null) {
                    PowerMoniterCurrentActivity.this.mTimer.cancel();
                    PowerMoniterCurrentActivity.this.mTimer = null;
                }
                if (PowerMoniterCurrentActivity.this.mTimerTask != null) {
                    PowerMoniterCurrentActivity.this.mTimerTask.cancel();
                    PowerMoniterCurrentActivity.this.mTimerTask = null;
                }
                PowerMoniterCurrentActivity.this.mDataList.clear();
                PowerMoniterCurrentActivity.this.mStart.setEnabled(true);
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public static int readFileByLines(String fileName) {
        IOException e;
        Throwable th;
        BufferedReader bufferedReader = null;
        int result = 0;
        String tempString = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            try {
                tempString = reader.readLine();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        Log.e(TAG, "readFileByLines io close exception :" + e1.getMessage());
                    }
                }
                bufferedReader = reader;
            } catch (IOException e2) {
                e = e2;
                bufferedReader = reader;
                try {
                    Log.e(TAG, "readFileByLines io exception:" + e.getMessage());
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e12) {
                            Log.e(TAG, "readFileByLines io close exception :" + e12.getMessage());
                        }
                    }
                    try {
                        result = Integer.valueOf(tempString).intValue();
                    } catch (NumberFormatException e3) {
                        Log.e(TAG, "readFileByLines NumberFormatException:" + e3.getMessage());
                    }
                    return result;
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e122) {
                            Log.e(TAG, "readFileByLines io close exception :" + e122.getMessage());
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = reader;
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
        } catch (IOException e4) {
            e = e4;
            Log.e(TAG, "readFileByLines io exception:" + e.getMessage());
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            result = Integer.valueOf(tempString).intValue();
            return result;
        }
        if (!(tempString == null || "".equals(tempString))) {
            result = Integer.valueOf(tempString).intValue();
        }
        return result;
    }
}
