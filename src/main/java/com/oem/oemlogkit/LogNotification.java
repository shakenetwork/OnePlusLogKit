package com.oem.oemlogkit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

public class LogNotification {
    public static final String ACTION_BUTTON = "com.oem.oemlogkit.bugreport";
    private static Context mContext;
    private static final Object mLock = new Object();
    public static LogNotification mLogNotification;
    private static NotificationManager mNotificationManager;

    public LogNotification(Context context) {
        mContext = context;
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService("notification");
        }
    }

    public void showNotification() {
        Intent mIntent = new Intent(mContext, OEMLogKitMainActivity.class);
        Intent buttonIntent = new Intent("com.oem.oemlogkit.bugreport");
        Builder mBuilder = new Builder(mContext);
        RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification);
        mBuilder.setContent(mRemoteViews).setPriority(0).setContentIntent(PendingIntent.getActivity(mContext, 0, mIntent, 0)).setOngoing(true).setSmallIcon(R.drawable.ic_launcher);
        Notification notify = mBuilder.build();
        notify.flags = 2;
        mNotificationManager.notify(1, notify);
    }

    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(1);
        }
        mNotificationManager.cancel(1);
    }
}
