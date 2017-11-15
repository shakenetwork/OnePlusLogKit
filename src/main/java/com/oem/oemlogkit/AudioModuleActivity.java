package com.oem.oemlogkit;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;

public class AudioModuleActivity extends PreferenceActivity {
    private static final int CREATE_AUDIO_CODEC_FILE = 100;
    private static final String RESULT_RENAME = "RESULT_RENAME";
    private static final String TAG = "AudioModuleActivity";
    private static String debug_file_name = "/sys/kernel/debug/dynamic_debug/control";
    private String[] filesNames;
    private Messenger mMessenger;
    private Handler mResultHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle res = msg.getData();
            Log.d(AudioModuleActivity.TAG, "mResultHandler");
            if (res.getBoolean(AudioModuleActivity.RESULT_RENAME)) {
                Toast.makeText(AudioModuleActivity.this, AudioModuleActivity.this.getResources().getString(R.string.save_codec_finished), 0).show();
                return;
            }
            Toast.makeText(AudioModuleActivity.this, AudioModuleActivity.this.getResources().getString(R.string.save_codec_failed), 0).show();
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            Log.d(AudioModuleActivity.TAG, "onServiceDisconnected");
            AudioModuleActivity.this.mMessenger = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(AudioModuleActivity.TAG, "onServiceConnected");
            AudioModuleActivity.this.mMessenger = new Messenger(service);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.grab_audio);
        Intent intent = new Intent("com.oem.logkit.command");
        intent.setPackage("com.oem.logkitsdservice");
        bindService(intent, this.mServiceConnection, 1);
        this.filesNames = getResources().getStringArray(R.array.common_audio_filename);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey().toString();
        if (key.equals("register")) {
            Log.d(TAG, "start to copy codec register");
            copyCodec();
        } else if (key.equals("audio_debug")) {
            open_audio_debug_flag();
        } else if (key.equals("audio_qxdm")) {
            startActivity(new Intent("com.oem.oemlogkit.startAUDIOAction"));
        }
        return false;
    }

    private void copyCodec() {
        Message message = Message.obtain(null, 0);
        message.replyTo = new Messenger(this.mResultHandler);
        message.arg1 = 0;
        Bundle data = new Bundle();
        data.putInt("type", CREATE_AUDIO_CODEC_FILE);
        message.setData(data);
        try {
            this.mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void open_audio_debug_flag() {
        for (String str : this.filesNames) {
            writeContentToFile("file " + str + " +p", debug_file_name);
        }
    }

    private void writeContentToFile(String content, String FileName) {
        try {
            FileWriter writer = new FileWriter(FileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
