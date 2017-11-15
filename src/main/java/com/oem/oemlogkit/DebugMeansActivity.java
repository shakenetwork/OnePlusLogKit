package com.oem.oemlogkit;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DebugMeansActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.debug_means);
    }
}
