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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class AudioQXDM extends Activity implements OnClickListener, OnCheckedChangeListener {
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
    public static final String TAG = AudioQXDM.class;
    private static final String[] typeRGItems = new String[]{"ALL_CONFIG"};
    private Button mDeviceLogBt;
    private Button mDeviceLogCloseBt;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Message message = Message.obtain(null, 0);
                    message.replyTo = new Messenger(AudioQXDM.this.mResultHandler);
                    message.arg1 = 0;
                    Bundle data = new Bundle();
                    data.putString(AudioQXDM.KEY_FROM, Environment.getExternalStorageDirectory().getPath() + "/diag_logs/Diag.cfg");
                    data.putInt("type", AudioQXDM.this.typeRG.getCheckedRadioButtonId());
                    message.setData(data);
                    try {
                        AudioQXDM.this.mMessenger.send(message);
                        return;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return;
                    }
                case 2:
                    Log.d(AudioQXDM.TAG, "message2");
                    Message message2 = Message.obtain(null, 0);
                    message2.replyTo = new Messenger(AudioQXDM.this.mResultHandler2);
                    message2.arg1 = 0;
                    Bundle data2 = new Bundle();
                    data2.putString(AudioQXDM.KEY_FROM, Environment.getExternalStorageDirectory().getPath() + "/diag_logs");
                    data2.putInt("type", 10);
                    message2.setData(data2);
                    try {
                        AudioQXDM.this.mMessenger.send(message2);
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
            Log.d(AudioQXDM.TAG, "mResultHandler");
            if (res.getBoolean(AudioQXDM.RESULT_RENAME)) {
                Toast.makeText(AudioQXDM.this, AudioQXDM.this.getResources().getString(R.string.createfile_log_success), 0).show();
                if (AudioQXDM.this.isFileExists(AudioQXDM.MDIAG_EXEC_PATH)) {
                    SystemService.start(AudioQXDM.MDIAG_SERVICE_START);
                    return;
                } else {
                    Toast.makeText(AudioQXDM.this, "Feture not avaiable as exec file not exists", 0).show();
                    return;
                }
            }
            Toast.makeText(AudioQXDM.this, AudioQXDM.this.getResources().getString(R.string.createfile_log_fail), 0).show();
        }
    };
    private Handler mResultHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle res = msg.getData();
            Log.d(AudioQXDM.TAG, "mResultHandler2");
            if (res.getBoolean(AudioQXDM.RESULT_RENAME)) {
                Toast.makeText(AudioQXDM.this, AudioQXDM.this.getResources().getString(R.string.delete_log_success), 0).show();
            } else {
                Toast.makeText(AudioQXDM.this, AudioQXDM.this.getResources().getString(R.string.delete_log_fail), 0).show();
            }
            AudioQXDM.this.mRmOldLogBt.setEnabled(true);
        }
    };
    private Button mRmOldLogBt;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            AudioQXDM.this.mMessenger = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioQXDM.this.mMessenger = new Messenger(service);
        }
    };
    private EditText numberEt;
    private EditText sizeEt;
    private Button sizeEtCancelBt;
    private Button sizeEtConfirmBt;
    private RadioGroup typeRG;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_qxdm_log);
        setupUI();
        initService();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (FileTransactionUtil.readDataFromFile(FileTransactionUtil.ENABLE_FLAG, this) != -1) {
            this.mDeviceLogBt.setEnabled(false);
            this.mDeviceLogCloseBt.setEnabled(true);
            this.mRmOldLogBt.setEnabled(false);
            this.sizeEt.setEnabled(false);
            this.numberEt.setEnabled(false);
            this.typeRG.setVisibility(4);
            return;
        }
        this.mDeviceLogBt.setEnabled(true);
        this.mDeviceLogCloseBt.setEnabled(false);
        this.mRmOldLogBt.setEnabled(true);
        this.sizeEt.setEnabled(true);
        this.numberEt.setEnabled(true);
        this.typeRG.setVisibility(0);
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
        insertTypes();
        int sizeFromFile = getSizeFromFile();
        int numberFromFile = getNumberFromFile();
        int typeFromFile = getTypeFromFile();
        if (sizeFromFile != -1) {
            this.sizeEt.setText(sizeFromFile + "");
        } else {
            this.sizeEt.setText("100");
        }
        if (numberFromFile != -1) {
            this.numberEt.setText(numberFromFile + "");
        } else {
            this.numberEt.setText("10");
        }
        if (typeFromFile != -1) {
            ((RadioButton) this.typeRG.getChildAt(typeFromFile)).setChecked(true);
        } else {
            ((RadioButton) this.typeRG.getChildAt(1)).setChecked(true);
        }
    }

    private void insertTypes() {
        this.typeRG.removeAllViews();
        TextView textView1 = new TextView(this);
        textView1.setText(R.string.save);
        this.typeRG.addView(textView1);
        for (int i = 0; i < typeRGItems.length; i++) {
            RadioButton typeButton = new RadioButton(this);
            typeButton.setChecked(false);
            typeButton.setText(typeRGItems[i]);
            typeButton.setId(i + 1);
            if (i > typeRGItems.length - 3) {
                typeButton.setVisibility(4);
            }
            this.typeRG.addView(typeButton);
        }
        this.typeRG.invalidate();
    }

    private int getSizeFromFile() {
        Log.i(TAG, "get size from file");
        return FileTransactionUtil.readDataFromFile(FileTransactionUtil.SIZE_FLAG, this);
    }

    private int getNumberFromFile() {
        Log.i(TAG, "get number from file");
        return FileTransactionUtil.readDataFromFile(FileTransactionUtil.NUMBER_FLAG, this);
    }

    private int getTypeFromFile() {
        Log.i(TAG, "get type from file");
        return FileTransactionUtil.readDataFromFile(FileTransactionUtil.TYPE_FLAG, this);
    }

    private boolean isDeviceLogEnabled() {
        return FileTransactionUtil.readDataFromFile(FileTransactionUtil.ENABLE_FLAG, this) == 1;
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
        FileTransactionUtil.writeDataToFile(FileTransactionUtil.TYPE_FLAG, this.typeRG.getCheckedRadioButtonId(), this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remove_grab /*2131361808*/:
                this.mHandler.sendEmptyMessageDelayed(2, 2000);
                this.mRmOldLogBt.setEnabled(false);
                return;
            case R.id.select_grab /*2131361809*/:
                writeSizeToFile();
                this.sizeEt.setEnabled(false);
                writeNumberToFile();
                this.numberEt.setEnabled(false);
                this.typeRG.setVisibility(4);
                FileTransactionUtil.writeDataToFile(FileTransactionUtil.ENABLE_FLAG, 1, this);
                this.mDeviceLogBt.setEnabled(false);
                this.mDeviceLogCloseBt.setEnabled(true);
                this.mRmOldLogBt.setEnabled(false);
                this.mHandler.sendEmptyMessageDelayed(1, 2000);
                return;
            case R.id.close_grab /*2131361810*/:
                Toast.makeText(this, "device_log_close", 0).show();
                this.mDeviceLogCloseBt.setEnabled(false);
                this.mDeviceLogBt.setEnabled(true);
                this.mRmOldLogBt.setEnabled(true);
                this.sizeEt.setEnabled(true);
                this.numberEt.setEnabled(true);
                this.typeRG.setVisibility(0);
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
        writeTypeToFile();
    }

    private void initService() {
        Log.d(TAG, "QXDMLog---initService");
        bindService(new Intent("com.oppo.logkit.command"), this.mServiceConnection, 1);
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
