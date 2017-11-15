package com.oem.oemlogkit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.oem.debug.OemAssertTip;

public class AssertControlActivity extends Activity implements OnClickListener {
    private Button clean_button;
    private OemAssertTip mFunctionProxy = null;
    private Button open_button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assert_control);
        findViews();
        setListeners();
        this.mFunctionProxy = OemAssertTip.getInstance();
        if (PropertiesUtil.get("persist.sys.assert.enable", "").equals("true")) {
            this.open_button.setText(R.string.assert_close);
        } else {
            this.open_button.setText(R.string.assert_open);
        }
    }

    private void findViews() {
        this.open_button = (Button) findViewById(R.id.open_assert);
        this.clean_button = (Button) findViewById(R.id.clean_assert);
    }

    private void setListeners() {
        this.open_button.setOnClickListener(this);
        this.clean_button.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_assert /*2131361793*/:
                if (PropertiesUtil.get("persist.sys.assert.enable", "").equals("true")) {
                    PropertiesUtil.set("persist.sys.assert.enable", "false");
                    this.open_button.setText(R.string.assert_open);
                    Toast.makeText(this, R.string.close_info, 0).show();
                    return;
                }
                PropertiesUtil.set("persist.sys.assert.enable", "true");
                this.open_button.setText(R.string.assert_close);
                Toast.makeText(this, R.string.open_info, 0).show();
                return;
            case R.id.clean_assert /*2131361795*/:
                int result = this.mFunctionProxy.hideAssertMessage();
                return;
            default:
                return;
        }
    }
}
