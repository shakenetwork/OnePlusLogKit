package com.oem.oemlogkit;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class CommonOptionActivity extends PreferenceActivity implements OnPreferenceClickListener {
    static final String TAG = "CommonOptionActivity";
    private CheckBoxPreference mCheckBoxLog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreat start");
        addPreferencesFromResource(R.xml.common_option);
        this.mCheckBoxLog = (CheckBoxPreference) findPreference("save_tcpdump");
        this.mCheckBoxLog.setOnPreferenceClickListener(this);
        if (PropertiesUtil.get("persist.sys.tcpdump", "no").equals("yes")) {
            this.mCheckBoxLog.setSummary(R.string.tcpdump_opened);
        } else {
            this.mCheckBoxLog.setSummary(R.string.tcpdump_closed);
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        if ("save_tcpdump".equals(preference.getKey())) {
            boolean isChecked = this.mCheckBoxLog.isChecked();
            this.mCheckBoxLog.setChecked(isChecked);
            if (isChecked) {
                PropertiesUtil.set("persist.sys.tcpdump", "yes");
                this.mCheckBoxLog.setSummary(R.string.tcpdump_opened);
            } else {
                PropertiesUtil.set("persist.sys.tcpdump", "no");
                this.mCheckBoxLog.setSummary(R.string.tcpdump_closed);
            }
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
    }
}
