package com.oem.oemlogkit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryInfoShow extends Activity {
    public static final String ACTION_ADDITIONAL_BATTERY_CHANGED = "android.intent.action.ADDITIONAL_BATTERY_CHANGED";
    public static final String BATTERY_LEVEL = "level";
    public static final String BATTERY_SCALE = "scale";
    public static final String BATTERY_TEMP = "temperature";
    private static final String BATTERY_TYPE = "/sys/class/power_supply/bms/battery_type";
    private static final String BATTERY_TYPE_PATH = "/sys/class/power_supply/battery/authenticate";
    public static final String BATTERY_VOL = "voltage";
    private static final int BOUNDARY_TEMP = -2;
    private static String CHAGREE_VOL_PATH = "/sys/class/power_supply/battery/charge_now";
    private static final String CHARGER_VOL = "chargervoltage";
    private static final int DURRING = 4000;
    private static final String ELEC = "batterycurrent";
    private static final String ELEC_PATH = "/sys/class/power_supply/battery/current_now";
    private static final String ENGINEERING_UPDATE = "oppo.intent.action.engineering.mode.update.battery";
    public static final String EXTRA_CHARGE_FAST_CHARGER = "chargefastcharger";
    private static final String IBAT_ELEC_PATH = "/sys/class/power_supply/battery/constant_charge_current_max";
    private static final String IBUS_ELEC_PATH = "/sys/class/power_supply/battery/input_current_max";
    private static final String PLUG = "plugged";
    private static final int REFRESH_RESULT = 2;
    private static final String STATUS = "status";
    private static final String TAG = "BatteryInfoShow";
    private static final String TEMP_PATH = "/sys/class/power_supply/battery/temp";
    private static final String THERMAL_TEMP_PATH = "/sys/class/power_supply/battery/system_temp_level";
    private static final int UPDATE_BATTERY_STATUS = 1;
    private static final int UPDATE_TEXTVIEW = 3;
    private boolean isInModelTest = false;
    private boolean mAuthiticate = false;
    private TextView mBatteryCapShow;
    IntentFilter mBatteryFilter = null;
    BroadcastReceiver mBatteryStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BatteryInfoShow.ACTION_ADDITIONAL_BATTERY_CHANGED.equals(action)) {
                boolean fastcharger = intent.getBooleanExtra(BatteryInfoShow.EXTRA_CHARGE_FAST_CHARGER, false);
                if (fastcharger != BatteryInfoShow.this.mIsFastCharger) {
                    BatteryInfoShow.this.mHasShowResult = false;
                    if (fastcharger) {
                        BatteryInfoShow.this.mFastchargerShow.setText(BatteryInfoShow.this.getString(R.string.fastcharger_yes));
                    } else {
                        BatteryInfoShow.this.mFastchargerShow.setText(BatteryInfoShow.this.getString(R.string.fastcharger_no));
                    }
                }
                BatteryInfoShow.this.mIsFastCharger = fastcharger;
                Log.i(BatteryInfoShow.TAG, "current is fastcharger ? " + BatteryInfoShow.this.mIsFastCharger);
                return;
            }
            if (BatteryInfoShow.this.mIsFastCharger) {
                BatteryInfoShow.this.mFastchargerShow.setText(BatteryInfoShow.this.getString(R.string.fastcharger_yes));
            } else {
                BatteryInfoShow.this.mFastchargerShow.setText(BatteryInfoShow.this.getString(R.string.fastcharger_no));
            }
            BatteryInfoShow.this.mBatteryTypeInfo.setVisibility(0);
            BatteryInfoShow.this.mBatteryTypeShow.setVisibility(0);
            if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.BATTERY_TYPE)) {
                BatteryInfoShow.this.mBatteryTypeShow.setText(BatteryInfoShow.this.readStringFromFile(BatteryInfoShow.BATTERY_TYPE));
            }
            if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.THERMAL_TEMP_PATH)) {
                BatteryInfoShow.this.mThermalTempShow.setText(BatteryInfoShow.this.readStringFromFile(BatteryInfoShow.THERMAL_TEMP_PATH));
            }
            if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.IBUS_ELEC_PATH)) {
                BatteryInfoShow.this.mIUSBElecShow.setText(String.valueOf(BatteryInfoShow.readFileByLines(BatteryInfoShow.IBUS_ELEC_PATH) / 1000) + " " + BatteryInfoShow.this.getString(R.string.battery_ma));
            }
            if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.IBAT_ELEC_PATH)) {
                BatteryInfoShow.this.mIBATElecShow.setText(String.valueOf(BatteryInfoShow.readFileByLines(BatteryInfoShow.IBAT_ELEC_PATH) / 1000) + " " + BatteryInfoShow.this.getString(R.string.battery_ma));
            }
            int authiticate = 0;
            if (BatteryInfoShow.this.mIsOppoFind7) {
                int batteryHealth = intent.getIntExtra("health", 1);
                Log.i(BatteryInfoShow.TAG, "batteryHealth:" + batteryHealth);
                BatteryInfoShow.this.mBatteryTypeInfo.setVisibility(0);
                BatteryInfoShow.this.mBatteryTypeShow.setVisibility(0);
                if (batteryHealth == 1) {
                    BatteryInfoShow.this.mBatteryTypeShow.setText(BatteryInfoShow.this.getString(R.string.battery_bad_contact));
                } else if (BatteryInfoShow.this.mAuthiticate) {
                    authiticate = BatteryInfoShow.readFileByLines(BatteryInfoShow.BATTERY_TYPE_PATH);
                    Log.i(BatteryInfoShow.TAG, "authiticate:" + authiticate);
                    if (authiticate == 1) {
                        BatteryInfoShow.this.mBatteryTypeShow.setText(BatteryInfoShow.this.getString(R.string.standard_battery));
                    } else if (authiticate == 0) {
                        BatteryInfoShow.this.mBatteryTypeShow.setText(BatteryInfoShow.this.getString(R.string.unstandard_battery));
                    }
                } else {
                    BatteryInfoShow.this.mBatteryTypeInfo.setVisibility(8);
                    BatteryInfoShow.this.mBatteryTypeShow.setVisibility(8);
                }
            }
            if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                int nLevel = intent.getIntExtra("level", 0);
                BatteryInfoShow.this.mBatteryCapShow.setText(String.valueOf((nLevel * 100) / intent.getIntExtra(BatteryInfoShow.BATTERY_SCALE, 100)) + "%");
                BatteryInfoShow.this.mBatteryVolShow.setText(String.valueOf(((double) intent.getIntExtra(BatteryInfoShow.BATTERY_VOL, 0)) / 1000.0d) + " " + BatteryInfoShow.this.getString(R.string.battery_vol_mv));
                int currentTemp = intent.getIntExtra(BatteryInfoShow.BATTERY_TEMP, 0);
                BatteryInfoShow.this.nTemp = (float) currentTemp;
                BatteryInfoShow.this.nTemp = (float) (((double) BatteryInfoShow.this.nTemp) / 10.0d);
                BatteryInfoShow.this.mBatteryTempShow.setText(String.valueOf(BatteryInfoShow.this.nTemp) + " " + BatteryInfoShow.this.getString(R.string.battery_ce));
                int elec = BatteryInfoShow.readFileByLines(BatteryInfoShow.ELEC_PATH) / 1000;
                BatteryInfoShow.this.mChargerElecShow.setText(String.valueOf(elec) + " " + BatteryInfoShow.this.getString(R.string.battery_ma));
                BatteryInfoShow.this.plugType = intent.getIntExtra(BatteryInfoShow.PLUG, 0);
                int status = intent.getIntExtra("status", 1);
                String statusString = null;
                int chargerVol = 0;
                if (status == 2 || status == 5) {
                    if (status == 5) {
                        statusString = BatteryInfoShow.this.getString(R.string.battery_info_status_full);
                    } else if (status == 2) {
                        statusString = BatteryInfoShow.this.getString(R.string.battery_info_status_charging);
                    }
                    if (BatteryInfoShow.this.plugType > 0) {
                        int i;
                        BatteryInfoShow batteryInfoShow = BatteryInfoShow.this;
                        if (BatteryInfoShow.this.plugType == 1) {
                            i = R.string.battery_info_status_charging_ac;
                        } else {
                            i = R.string.battery_info_status_charging_usb;
                        }
                        BatteryInfoShow.this.mChargerTypeShow.setText(batteryInfoShow.getString(i));
                        BatteryInfoShow.this.mShowResult = true;
                    }
                    chargerVol = BatteryInfoShow.readFileByLines(BatteryInfoShow.CHAGREE_VOL_PATH);
                    String type = SystemProperties.get("ro.prj_name", "");
                    if (type.equals("15055") || type.equals("14001")) {
                        chargerVol /= 1000;
                    }
                    BatteryInfoShow.this.mChargerVolShow.setText(String.valueOf(((double) chargerVol) / 1000.0d) + " " + BatteryInfoShow.this.getString(R.string.battery_vol_mv));
                } else {
                    BatteryInfoShow.this.mChargerTypeShow.setText("");
                    BatteryInfoShow.this.mChargerVolShow.setText("");
                    if (status == 3) {
                        statusString = BatteryInfoShow.this.getString(R.string.battery_info_status_discharging);
                    } else if (status == 4) {
                        statusString = BatteryInfoShow.this.getString(R.string.battery_info_status_not_charging);
                    } else {
                        statusString = BatteryInfoShow.this.getString(R.string.battery_info_status_unknown);
                    }
                    BatteryInfoShow.this.mHasShowResult = false;
                    BatteryInfoShow.this.mLastTemp = currentTemp;
                    BatteryInfoShow.this.mResult.setVisibility(4);
                }
                Log.e(BatteryInfoShow.TAG, "mShowResult:" + BatteryInfoShow.this.mShowResult + "  mHasShowResult:" + BatteryInfoShow.this.mHasShowResult);
                if (!(BatteryInfoShow.this.mIsFastCharger || BatteryInfoShow.this.mHasShowResult)) {
                    if (elec < -200) {
                        BatteryInfoShow.this.mChargerElecShow.setTextColor(-1);
                    } else {
                        BatteryInfoShow.this.mChargerElecShow.setTextColor(SupportMenu.CATEGORY_MASK);
                    }
                    if (BatteryInfoShow.this.nTemp < 20.0f || BatteryInfoShow.this.nTemp > 40.0f) {
                        BatteryInfoShow.this.mBatteryTempShow.setTextColor(SupportMenu.CATEGORY_MASK);
                    } else {
                        BatteryInfoShow.this.mBatteryTempShow.setTextColor(-1);
                    }
                    if (!BatteryInfoShow.this.isFileExist(BatteryInfoShow.BATTERY_TYPE) || BatteryInfoShow.this.readStringFromFile(BatteryInfoShow.BATTERY_TYPE).equals("Unknown Battery")) {
                        BatteryInfoShow.this.mBatteryTypeShow.setTextColor(SupportMenu.CATEGORY_MASK);
                    } else {
                        BatteryInfoShow.this.mBatteryTypeShow.setTextColor(-1);
                    }
                    if (chargerVol < 4500 || chargerVol > 5500) {
                        BatteryInfoShow.this.mChargerVolShow.setTextColor(SupportMenu.CATEGORY_MASK);
                    } else {
                        BatteryInfoShow.this.mChargerVolShow.setTextColor(-1);
                    }
                    if (BatteryInfoShow.this.plugType == 1) {
                        BatteryInfoShow.this.mChargerTypeShow.setTextColor(-1);
                    } else {
                        BatteryInfoShow.this.mChargerTypeShow.setTextColor(SupportMenu.CATEGORY_MASK);
                    }
                }
                if (BatteryInfoShow.this.plugType == 1 && BatteryInfoShow.this.mShowResult) {
                    Log.e(BatteryInfoShow.TAG, "plugType is AC");
                    if (!BatteryInfoShow.this.mHasShowResult) {
                        Log.e(BatteryInfoShow.TAG, "mIsFastCharger:" + BatteryInfoShow.this.mIsFastCharger + "  elec:" + elec + "  nTemp:" + BatteryInfoShow.this.nTemp + "  chargerVol:" + chargerVol);
                        Log.e(BatteryInfoShow.TAG, "curTemp:" + intent.getIntExtra(BatteryInfoShow.BATTERY_TEMP, 0) + "  mLastTemp:" + BatteryInfoShow.this.mLastTemp);
                        if (!BatteryInfoShow.this.mIsFastCharger || elec >= -2500 || BatteryInfoShow.this.nTemp < 20.0f || BatteryInfoShow.this.nTemp > 40.0f) {
                            if (!BatteryInfoShow.this.mIsFastCharger && elec < -200 && BatteryInfoShow.this.nTemp >= 20.0f && BatteryInfoShow.this.nTemp <= 40.0f && chargerVol >= 4500 && chargerVol <= 5500 && BatteryInfoShow.this.isFileExist(BatteryInfoShow.BATTERY_TYPE)) {
                                if (BatteryInfoShow.this.readStringFromFile(BatteryInfoShow.BATTERY_TYPE).equals("Unknown Battery")) {
                                }
                            }
                        }
                        Message msg = BatteryInfoShow.this.mHander.obtainMessage(2);
                        if (intent.getIntExtra(BatteryInfoShow.BATTERY_TEMP, 0) - BatteryInfoShow.this.mLastTemp <= -20) {
                            msg.arg1 = 0;
                            Log.e(BatteryInfoShow.TAG, "test fail");
                        } else if (BatteryInfoShow.this.mIsOppoFind7) {
                            msg.arg1 = authiticate;
                            Log.e(BatteryInfoShow.TAG, "test res:" + authiticate);
                        } else {
                            msg.arg1 = 1;
                            Log.e(BatteryInfoShow.TAG, "test success");
                        }
                        BatteryInfoShow.this.mHander.sendMessage(msg);
                        BatteryInfoShow.this.mShowResult = false;
                        BatteryInfoShow.this.mHasShowResult = true;
                    }
                }
                BatteryInfoShow.this.mChargerStatusShow.setText(statusString);
                Log.w(BatteryInfoShow.TAG, "Receice Broadcast: ACTION_BATTERY_CHANGED");
            }
        }
    };
    private TextView mBatteryTempShow;
    private TextView mBatteryTypeInfo;
    private TextView mBatteryTypeShow;
    private TextView mBatteryVolShow;
    private TextView mChargerElecShow;
    private TextView mChargerStatusShow;
    private TextView mChargerTypeShow;
    private TextView mChargerVolShow;
    private TextView mFastchargerShow;
    private TextView mFastchargerType;
    private Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                BatteryInfoShow.this.sendBroadcast(new Intent(BatteryInfoShow.ENGINEERING_UPDATE));
                BatteryInfoShow.this.mHander.sendEmptyMessageDelayed(1, 4000);
            } else if (2 == msg.what) {
                BatteryInfoShow.this.mResult.setVisibility(0);
                if (msg.arg1 == 0) {
                    BatteryInfoShow.this.mResult.setText(BatteryInfoShow.this.getResources().getString(R.string.fail));
                } else if (BatteryInfoShow.this.mIsFastCharger) {
                    BatteryInfoShow.this.mResult.setText(BatteryInfoShow.this.getResources().getString(R.string.fastcharger_pass));
                } else {
                    BatteryInfoShow.this.mResult.setText(BatteryInfoShow.this.getResources().getString(R.string.pass));
                }
            } else if (3 == msg.what) {
                int elec = BatteryInfoShow.readFileByLines(BatteryInfoShow.ELEC_PATH) / 1000;
                BatteryInfoShow.this.mChargerElecShow.setText(String.valueOf(elec) + " " + BatteryInfoShow.this.getString(R.string.battery_ma));
                int chargerVol = BatteryInfoShow.readFileByLines(BatteryInfoShow.CHAGREE_VOL_PATH);
                String type = SystemProperties.get("ro.prj_name", "");
                if (type.equals("15055") || type.equals("14001")) {
                    chargerVol /= 1000;
                }
                if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.THERMAL_TEMP_PATH)) {
                    BatteryInfoShow.this.mThermalTempShow.setText(BatteryInfoShow.this.readStringFromFile(BatteryInfoShow.THERMAL_TEMP_PATH));
                }
                if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.IBUS_ELEC_PATH)) {
                    BatteryInfoShow.this.mIUSBElecShow.setText(String.valueOf(BatteryInfoShow.readFileByLines(BatteryInfoShow.IBUS_ELEC_PATH) / 1000) + " " + BatteryInfoShow.this.getString(R.string.battery_ma));
                }
                if (BatteryInfoShow.this.isFileExist(BatteryInfoShow.IBAT_ELEC_PATH)) {
                    BatteryInfoShow.this.mIBATElecShow.setText(String.valueOf(BatteryInfoShow.readFileByLines(BatteryInfoShow.IBAT_ELEC_PATH) / 1000) + " " + BatteryInfoShow.this.getString(R.string.battery_ma));
                }
                if (((double) chargerVol) / 1000.0d > 1.0d) {
                    BatteryInfoShow.this.mChargerVolShow.setText(String.valueOf(((double) chargerVol) / 1000.0d) + " " + BatteryInfoShow.this.getString(R.string.battery_vol_mv));
                }
                if (elec < -200) {
                    BatteryInfoShow.this.mChargerElecShow.setTextColor(-1);
                } else {
                    BatteryInfoShow.this.mChargerElecShow.setTextColor(SupportMenu.CATEGORY_MASK);
                }
                if (chargerVol < 4500 || chargerVol > 5500) {
                    BatteryInfoShow.this.mChargerVolShow.setTextColor(SupportMenu.CATEGORY_MASK);
                } else {
                    BatteryInfoShow.this.mChargerVolShow.setTextColor(-1);
                }
                if (BatteryInfoShow.this.plugType == 1 && BatteryInfoShow.this.mShowResult) {
                    Log.e(BatteryInfoShow.TAG, "plugType is AC");
                    if (!BatteryInfoShow.this.mHasShowResult) {
                        Log.e(BatteryInfoShow.TAG, "mIsFastCharger:" + BatteryInfoShow.this.mIsFastCharger + "  elec:" + elec + "  nTemp:" + BatteryInfoShow.this.nTemp + "  chargerVol:" + chargerVol);
                        Log.e(BatteryInfoShow.TAG, "curTemp:" + (BatteryInfoShow.readFileByLines(BatteryInfoShow.TEMP_PATH) / 10) + "  mLastTemp:" + BatteryInfoShow.this.mLastTemp);
                        if (!BatteryInfoShow.this.mIsFastCharger || elec >= -2500 || BatteryInfoShow.this.nTemp < 20.0f || BatteryInfoShow.this.nTemp > 40.0f) {
                            if (!BatteryInfoShow.this.mIsFastCharger && elec < -200 && BatteryInfoShow.this.nTemp >= 20.0f && BatteryInfoShow.this.nTemp <= 40.0f && chargerVol >= 4500 && chargerVol <= 5500 && BatteryInfoShow.this.isFileExist(BatteryInfoShow.BATTERY_TYPE)) {
                                if (BatteryInfoShow.this.readStringFromFile(BatteryInfoShow.BATTERY_TYPE).equals("Unknown Battery")) {
                                    return;
                                }
                            }
                            return;
                        }
                        Message msg1 = BatteryInfoShow.this.mHander.obtainMessage(2);
                        if (BatteryInfoShow.readFileByLines(BatteryInfoShow.TEMP_PATH) - BatteryInfoShow.this.mLastTemp <= -20) {
                            msg1.arg1 = 0;
                            Log.e(BatteryInfoShow.TAG, "test fail");
                        } else if (BatteryInfoShow.this.mIsOppoFind7) {
                            int authiticate = BatteryInfoShow.readFileByLines(BatteryInfoShow.BATTERY_TYPE_PATH);
                            msg1.arg1 = authiticate;
                            Log.e(BatteryInfoShow.TAG, "test res:" + authiticate);
                        } else {
                            msg1.arg1 = 1;
                            Log.e(BatteryInfoShow.TAG, "test success");
                        }
                        BatteryInfoShow.this.mHander.sendMessage(msg1);
                        BatteryInfoShow.this.mShowResult = false;
                        BatteryInfoShow.this.mHasShowResult = true;
                    }
                }
            }
        }
    };
    private boolean mHasShowResult = false;
    private TextView mIBATElecShow;
    private TextView mIUSBElecShow;
    private boolean mIsFastCharger = false;
    private boolean mIsOppoFind7 = false;
    private int mLastTemp = 0;
    private TextView mResult;
    private boolean mShowResult = false;
    private TextView mThermalTempShow;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private float nTemp = 0.0f;
    private int plugType = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_infoshow);
        setTitle(R.string.battery_title);
        if (SystemProperties.get("ro.prj_name", "").equals("15055")) {
            CHAGREE_VOL_PATH = "/sys/class/power_supply/usb/voltage_now";
        }
        Log.d(TAG, "CHAGREE_VOL_PATH:" + CHAGREE_VOL_PATH);
        this.mBatteryCapShow = (TextView) findViewById(R.id.battery_cap_show);
        this.mBatteryVolShow = (TextView) findViewById(R.id.battery_vol_show);
        this.mBatteryTempShow = (TextView) findViewById(R.id.battery_temp_show);
        this.mChargerVolShow = (TextView) findViewById(R.id.charger_vol_show);
        this.mChargerElecShow = (TextView) findViewById(R.id.charger_elec_show);
        this.mChargerTypeShow = (TextView) findViewById(R.id.charger_type_show);
        this.mChargerStatusShow = (TextView) findViewById(R.id.charger_status_show);
        this.mBatteryTypeInfo = (TextView) findViewById(R.id.battery_type_info);
        this.mBatteryTypeShow = (TextView) findViewById(R.id.battery_type_show);
        this.mFastchargerType = (TextView) findViewById(R.id.fastcharger_type_info);
        this.mFastchargerShow = (TextView) findViewById(R.id.fastcharger_type_show);
        this.mFastchargerType.setVisibility(8);
        this.mFastchargerShow.setVisibility(8);
        this.mIBATElecShow = (TextView) findViewById(R.id.ibat_elec_show);
        this.mIUSBElecShow = (TextView) findViewById(R.id.iusb_elec_show);
        this.mThermalTempShow = (TextView) findViewById(R.id.thermal_temp_show);
        this.mResult = (TextView) findViewById(R.id.charge_pass);
        this.mBatteryFilter = new IntentFilter();
        this.mBatteryFilter.addAction("android.intent.action.BATTERY_CHANGED");
        this.mBatteryFilter.addAction(ACTION_ADDITIONAL_BATTERY_CHANGED);
        this.mAuthiticate = isFileExist(BATTERY_TYPE_PATH);
    }

    private boolean isFileExist(String fileName) {
        return new File(fileName).exists();
    }

    private String readStringFromFile(String fileName) {
        IOException e;
        Throwable th;
        BufferedReader bufferedReader = null;
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
                    return tempString;
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
        } catch (IOException e3) {
            e = e3;
            Log.e(TAG, "readFileByLines io exception:" + e.getMessage());
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            return tempString;
        }
        return tempString;
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

    public void onResume() {
        registerReceiver(this.mBatteryStatusReceiver, this.mBatteryFilter);
        this.mHander.removeMessages(1);
        this.mHander.sendEmptyMessageDelayed(1, 4000);
        this.mResult.setVisibility(4);
        this.mShowResult = false;
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
        if (this.mTimerTask == null) {
            this.mTimerTask = new TimerTask() {
                public void run() {
                    BatteryInfoShow.this.mHander.sendEmptyMessage(3);
                }
            };
        }
        if (!(this.mTimer == null || this.mTimerTask == null)) {
            this.mTimer.schedule(this.mTimerTask, 0, 5000);
        }
        super.onResume();
    }

    public void onPause() {
        this.mHander.removeMessages(1);
        unregisterReceiver(this.mBatteryStatusReceiver);
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
        super.onPause();
    }

    public void onDestroy() {
        Calendar now = Calendar.getInstance();
        String content = (now.get(1) + "-" + (now.get(2) + 1) + "-" + now.get(5) + "-" + now.get(11) + "-" + now.get(12) + "-" + now.get(13)) + "--BatteryInfoShow--" + "has entered it";
        this.mHander.removeMessages(1);
        this.mHander = null;
        super.onDestroy();
    }
}
