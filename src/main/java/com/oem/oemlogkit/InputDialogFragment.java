package com.oem.oemlogkit;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InputDialogFragment extends DialogFragment {
    static final String TAG = "OEMLogKitMainActivity";
    private int layoutID;
    private EditText logFilter;
    private EditText logNum;
    private EditText logSize;
    private EditText rawData;
    private int titleID;

    public interface DialogInputListener {
        void onLoginInputComplete(String... strArr);
    }

    public InputDialogFragment(int layoutResourceID, int titleResourceID) {
        this.layoutID = layoutResourceID;
        this.titleID = titleResourceID;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(this.layoutID, null);
        builder.setTitle(this.titleID);
        builder.setView(view);
        builder.setPositiveButton(R.string.clean_ok, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                DialogInputListener listener = (DialogInputListener) InputDialogFragment.this.getActivity();
                switch (InputDialogFragment.this.layoutID) {
                    case R.layout.filter_dialog /*2130903045*/:
                        InputDialogFragment.this.logFilter = (EditText) view.findViewById(R.id.filter_log_edit);
                        listener.onLoginInputComplete(InputDialogFragment.this.logFilter.getText().toString());
                        return;
                    case R.layout.logsize_dialog /*2130903051*/:
                        InputDialogFragment.this.logNum = (EditText) view.findViewById(R.id.log_file_num_edit);
                        InputDialogFragment.this.logSize = (EditText) view.findViewById(R.id.log_file_size_edit);
                        String num = InputDialogFragment.this.logNum.getText().toString();
                        String size = InputDialogFragment.this.logSize.getText().toString();
                        listener.onLoginInputComplete(num, size);
                        return;
                    case R.layout.raw_data_dialog /*2130903057*/:
                        InputDialogFragment.this.rawData = (EditText) view.findViewById(R.id.rawdata_edit);
                        Log.d(InputDialogFragment.TAG, "rawData: " + InputDialogFragment.this.rawData.getText().toString());
                        if (!InputDialogFragment.this.rawData.getText().toString().equals("")) {
                            if (Integer.parseInt(InputDialogFragment.this.rawData.getText().toString()) > 50 || Integer.parseInt(InputDialogFragment.this.rawData.getText().toString()) < 1) {
                                Toast.makeText(InputDialogFragment.this.getActivity(), R.string.raw_data_error, 0).show();
                                return;
                            } else {
                                PropertiesUtil.set("persist.sys.rawdata", InputDialogFragment.this.rawData.getText().toString());
                                return;
                            }
                        }
                        return;
                    default:
                        return;
                }
            }
        });
        builder.setNegativeButton(R.string.clean_cancel, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        return builder.create();
    }
}
