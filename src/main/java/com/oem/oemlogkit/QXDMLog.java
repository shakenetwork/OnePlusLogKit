package com.oem.oemlogkit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.SystemService;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import java.io.File;

public class QXDMLog extends Activity implements OnClickListener, OnCheckedChangeListener {
    private static final int COMMAND_WRITELOG = 0;
    public static final boolean DBG = true;
    private static final int IDLE = -1;
    private static final String KEY_FROM = "KEY_RENAME_FROM";
    private static final String KEY_TO = "KEY_RENAME_TO";
    private static final String MDIAG_EXEC_PATH = "/system/bin/diag_mdlog";
    private static final String MDIAG_SERVICE_START = "diag_mdlog_start";
    private static final String MDIAG_SERVICE_STOP = "diag_mdlog_stop";
    private static final int MTYPE_DELETE_OLD_LOG = 10;
    private static final String RESULT_RENAME = "RESULT_RENAME";
    public static final String TAG = QXDMLog.class;
    private Button mDeviceLogBt;
    private Button mDeviceLogCloseBt;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Message message = Message.obtain(null, 0);
                    message.replyTo = new Messenger(QXDMLog.this.mResultHandler);
                    message.arg1 = 0;
                    Bundle data = new Bundle();
                    data.putString(QXDMLog.KEY_FROM, Environment.getExternalStorageDirectory().getPath() + "/oem_log/diag_logs/Diag.cfg");
                    data.putInt("type", QXDMLog.this.type);
                    message.setData(data);
                    try {
                        QXDMLog.this.mMessenger.send(message);
                        return;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return;
                    }
                case 2:
                    Log.d(QXDMLog.TAG, "message2");
                    Message message2 = Message.obtain(null, 0);
                    message2.replyTo = new Messenger(QXDMLog.this.mResultHandler2);
                    message2.arg1 = 0;
                    Bundle data2 = new Bundle();
                    data2.putString(QXDMLog.KEY_FROM, Environment.getExternalStorageDirectory().getPath() + "/oem_log/diag_logs");
                    data2.putInt("type", 10);
                    message2.setData(data2);
                    try {
                        QXDMLog.this.mMessenger.send(message2);
                        return;
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                        return;
                    }
                default:
                    return;
            }
        }
    };
    private Messenger mMessenger;
    private Handler mResultHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle res = msg.getData();
            Log.d(QXDMLog.TAG, "mResultHandler");
            if (res.getBoolean(QXDMLog.RESULT_RENAME)) {
                Toast.makeText(QXDMLog.this, QXDMLog.this.getResources().getString(R.string.createfile_log_success), 0).show();
                if (QXDMLog.this.isFileExists(QXDMLog.MDIAG_EXEC_PATH)) {
                    SystemService.start(QXDMLog.MDIAG_SERVICE_START);
                    return;
                } else {
                    Toast.makeText(QXDMLog.this, "Feture not avaiable as exec file not exists", 0).show();
                    return;
                }
            }
            Toast.makeText(QXDMLog.this, QXDMLog.this.getResources().getString(R.string.createfile_log_fail), 0).show();
        }
    };
    private Handler mResultHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle res = msg.getData();
            Log.d(QXDMLog.TAG, "mResultHandler2");
            if (res.getBoolean(QXDMLog.RESULT_RENAME)) {
                Toast.makeText(QXDMLog.this, QXDMLog.this.getResources().getString(R.string.delete_log_success), 0).show();
            } else {
                Toast.makeText(QXDMLog.this, QXDMLog.this.getResources().getString(R.string.delete_log_fail), 0).show();
            }
            QXDMLog.this.mRmOldLogBt.setEnabled(true);
            QXDMLog.this.mDeviceLogBt.setEnabled(true);
        }
    };
    private Button mRmOldLogBt;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            QXDMLog.this.mMessenger = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            QXDMLog.this.mMessenger = new Messenger(service);
        }
    };
    private EditText numberEt;
    private RadioButton[] radioButton = new RadioButton[10];
    private EditText sizeEt;
    private Button sizeEtCancelBt;
    private Button sizeEtConfirmBt;
    int type = 0;
    private RadioGroup typeRG;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modem_log);
        setupUI();
        initService();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (isDeviceLogEnabled()) {
            this.mDeviceLogBt.setEnabled(false);
            this.mDeviceLogCloseBt.setEnabled(true);
            this.mRmOldLogBt.setEnabled(false);
            this.sizeEt.setEnabled(false);
            this.numberEt.setEnabled(false);
            setTypeButtonEnabled(false);
            return;
        }
        this.mDeviceLogBt.setEnabled(true);
        this.mDeviceLogCloseBt.setEnabled(false);
        this.mRmOldLogBt.setEnabled(true);
        this.sizeEt.setEnabled(true);
        this.numberEt.setEnabled(true);
        setTypeButtonEnabled(true);
    }

    protected void onStop() {
        super.onStop();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setupUI() {
        this.mRmOldLogBt = (Button) findViewById(R.id.remove_grab);
        this.mDeviceLogBt = (Button) findViewById(R.id.select_grab);
        this.mDeviceLogCloseBt = (Button) findViewById(R.id.close_grab);
        this.sizeEt = (EditText) findViewById(R.id.size_et);
        this.numberEt = (EditText) findViewById(R.id.number_et);
        this.mRmOldLogBt.setOnClickListener(this);
        this.mDeviceLogBt.setOnClickListener(this);
        this.mDeviceLogCloseBt.setOnClickListener(this);
        this.typeRG = (RadioGroup) findViewById(R.id.radioGroup1);
        this.typeRG.setOnCheckedChangeListener(this);
        this.radioButton[0] = (RadioButton) findViewById(R.id.common_bu);
        this.radioButton[1] = (RadioButton) findViewById(R.id.voice_bu);
        this.radioButton[2] = (RadioButton) findViewById(R.id.sim_bu);
        this.radioButton[3] = (RadioButton) findViewById(R.id.data_bu);
        this.radioButton[4] = (RadioButton) findViewById(R.id.wifi_bu);
        this.radioButton[5] = (RadioButton) findViewById(R.id.audio_bu);
        this.radioButton[6] = (RadioButton) findViewById(R.id.bt_bu);
        this.radioButton[7] = (RadioButton) findViewById(R.id.sensor_bu);
        this.radioButton[8] = (RadioButton) findViewById(R.id.gps_bu);
        this.radioButton[9] = (RadioButton) findViewById(R.id.antenna_bu);
        int sizeFromFile = getSizeFromFile();
        int numberFromFile = getNumberFromFile();
        this.type = getTypeFromFile();
        if (sizeFromFile == -1 || sizeFromFile < 0 || sizeFromFile > 9999) {
            this.sizeEt.setText("32");
        } else {
            this.sizeEt.setText(sizeFromFile + "");
        }
        this.numberEt.setText("500");
        if (this.type == -1 || this.type < 0 || this.type > 9) {
            ((RadioButton) this.typeRG.getChildAt(1)).setChecked(true);
        } else {
            ((RadioButton) this.typeRG.getChildAt(this.type)).setChecked(true);
        }
    }

    private void setTypeButtonEnabled(boolean br) {
        for (int i = 0; i < 9; i++) {
            this.radioButton[i].setEnabled(br);
        }
    }

    private int getSizeFromFile() {
        Log.i(TAG, "get size from file");
        return FileTransactionUtil.readDataFromFile(FileTransactionUtil.SIZE_FLAG, this);
    }

    private int getNumberFromFile() {
        Log.i(TAG, "get number from file");
        return SystemProperties.getInt("persist.sys.diag.max.size", 200);
    }

    private int getTypeFromFile() {
        Log.i(TAG, "get type from file");
        return SystemProperties.getInt("persist.sys.diag.type", -1);
    }

    private boolean isDeviceLogEnabled() {
        return PropertiesUtil.get("persist.sys.qxdm", "no").equals("yes");
    }

    private boolean checkSizeFormat() {
        if (this.sizeEt == null || TextUtils.isEmpty(this.sizeEt.getText().toString().trim())) {
            return false;
        }
        return true;
    }

    private boolean checkNumberFormat() {
        if (this.numberEt == null || TextUtils.isEmpty(this.numberEt.getText().toString().trim())) {
            return false;
        }
        return true;
    }

    private void writeSizeToFile() {
        if (checkSizeFormat()) {
            String str;
            if (this.sizeEt.getText().toString().trim().equals(getString(R.string.device_log_size_notice))) {
                str = "-1";
            } else {
                str = this.sizeEt.getText().toString().trim();
            }
            FileTransactionUtil.writeDataToFile(FileTransactionUtil.SIZE_FLAG, Integer.parseInt(str), this);
            return;
        }
        Toast.makeText(this, getString(R.string.device_log_size_format_error), 0).show();
    }

    private void clearSizeEt() {
        if (this.sizeEt == null) {
            this.sizeEt.setText("");
        }
    }

    private void writeNumberToFile() {
        if (checkNumberFormat()) {
            String str;
            if (this.numberEt.getText().toString().trim().equals(getString(R.string.device_log_number_notice))) {
                str = "-1";
            } else {
                str = this.numberEt.getText().toString().trim();
            }
            FileTransactionUtil.writeDataToFile(FileTransactionUtil.NUMBER_FLAG, Integer.parseInt(str), this);
            return;
        }
        Toast.makeText(this, getString(R.string.device_log_number_format_error), 0).show();
    }

    private void clearNumberEt() {
        if (this.sizeEt == null) {
            this.sizeEt.setText("");
        }
    }

    private void writeTypeToFile() {
        SystemProperties.set("persist.sys.diag.type", this.type + "");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove_grab /*2131361808*/:
                if (this.mMessenger == null) {
                    Log.d(TAG, "mMessenger==null, re-bind service");
                    initService();
                    this.mHandler.sendEmptyMessageDelayed(2, 2000);
                } else {
                    Log.d(TAG, "mMessenger!=null");
                    this.mHandler.sendEmptyMessageDelayed(2, 2000);
                }
                this.mDeviceLogBt.setEnabled(false);
                this.mRmOldLogBt.setEnabled(false);
                return;
            case R.id.select_grab /*2131361809*/:
                writeSizeToFile();
                this.sizeEt.setEnabled(false);
                writeNumberToFile();
                this.numberEt.setEnabled(false);
                setTypeButtonEnabled(false);
                if (this.type == 5) {
                    if (PropertiesUtil.get("persist.oem.wifi.cnssdiag", "").equals("1")) {
                        Log.d(TAG, "Cnss diag service is already running");
                    } else {
                        Log.d(TAG, "Start cnss diag service");
                        PropertiesUtil.set("persist.oem.wifi.cnssdiag", "1");
                    }
                } else if (this.type == 7) {
                    Log.d(TAG, "bt qxdm log info enabled");
                    PropertiesUtil.set("persist.service.bdroid.soclog", "true");
                    PropertiesUtil.set("persist.service.bdroid.snooplog", "true");
                }
                PropertiesUtil.set("persist.sys.qxdm", "yes");
                this.mDeviceLogBt.setEnabled(false);
                this.mDeviceLogCloseBt.setEnabled(true);
                this.mRmOldLogBt.setEnabled(false);
                this.mHandler.sendEmptyMessageDelayed(1, 2000);
                return;
            case R.id.close_grab /*2131361810*/:
                Toast.makeText(this, "device_log_close", 0).show();
                if (this.type == 5) {
                    if (PropertiesUtil.get("persist.oem.wifi.debug", "").equals("1")) {
                        Log.d(TAG, "Wifi log info enabled");
                    } else {
                        PropertiesUtil.set("persist.oem.wifi.cnssdiag", "0");
                        Log.d(TAG, "Stop cnss diag service");
                    }
                } else if (this.type == 7) {
                    Log.d(TAG, "bt qxdm log info disable");
                    PropertiesUtil.set("persist.service.bdroid.soclog", "false");
                    PropertiesUtil.set("persist.service.bdroid.snooplog", "false");
                }
                PropertiesUtil.set("persist.sys.qxdm", "no");
                this.mDeviceLogCloseBt.setEnabled(false);
                this.mDeviceLogBt.setEnabled(true);
                this.mRmOldLogBt.setEnabled(true);
                this.sizeEt.setEnabled(true);
                this.numberEt.setEnabled(true);
                setTypeButtonEnabled(true);
                FileTransactionUtil.writeDisableMsg(this);
                if (isFileExists(MDIAG_EXEC_PATH)) {
                    SystemService.start(MDIAG_SERVICE_STOP);
                    return;
                } else {
                    Toast.makeText(this, "Feture not avaiable as exec file not exists", 0).show();
                    return;
                }
            default:
                return;
        }
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, "checkedId = " + checkedId);
        switch (((RadioButton) findViewById(checkedId)).getId()) {
            case R.id.common_bu /*2131361802*/:
                this.type = 1;
                break;
            case R.id.voice_bu /*2131361803*/:
                this.type = 2;
                break;
            case R.id.sim_bu /*2131361804*/:
                this.type = 3;
                break;
            case R.id.data_bu /*2131361805*/:
                this.type = 4;
                break;
            case R.id.wifi_bu /*2131361861*/:
                this.type = 5;
                break;
            case R.id.audio_bu /*2131361862*/:
                this.type = 6;
                break;
            case R.id.bt_bu /*2131361863*/:
                this.type = 7;
                break;
            case R.id.sensor_bu /*2131361864*/:
                this.type = 8;
                break;
            case R.id.gps_bu /*2131361865*/:
                this.type = 9;
                break;
            case R.id.antenna_bu /*2131361866*/:
                this.type = 11;
                break;
            default:
                this.type = 1;
                break;
        }
        Log.d(TAG, "type=" + this.type);
        writeTypeToFile();
    }

    private void initService() {
        Log.d(TAG, "QXDMLog---initService");
        Intent intent = new Intent("com.oem.logkit.command");
        intent.setPackage("com.oem.logkitsdservice");
        bindService(intent, this.mServiceConnection, 1);
    }

    protected void onDestroy() {
        Log.d(TAG, "QXDMLog---unbindService(mServiceConnection)");
        unbindService(this.mServiceConnection);
        super.onDestroy();
    }

    private boolean isFileExists(String filePath) {
        return new File(filePath).exists();
    }
}
