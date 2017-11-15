package android.support.v4.media;

import android.content.Context;
import android.media.browse.MediaBrowser.MediaItem;
import android.os.Bundle;
import android.os.Parcel;
import android.service.media.MediaBrowserService;
import android.service.media.MediaBrowserService.Result;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class MediaBrowserServiceCompatApi24 {
    private static final String TAG = "MBSCompatApi24";
    private static Field sResultFlags;

    public interface ServiceCompatProxy extends android.support.v4.media.MediaBrowserServiceCompatApi23.ServiceCompatProxy {
        void onLoadChildren(String str, ResultWrapper resultWrapper, Bundle bundle);
    }

    static class MediaBrowserServiceAdaptor extends MediaBrowserServiceAdaptor {
        MediaBrowserServiceAdaptor(Context context, ServiceCompatProxy serviceWrapper) {
            super(context, serviceWrapper);
        }

        public void onLoadChildren(String parentId, Result<List<MediaItem>> result, Bundle options) {
            ((ServiceCompatProxy) this.mServiceProxy).onLoadChildren(parentId, new ResultWrapper(result), options);
        }
    }

    static class ResultWrapper {
        Result mResultObj;

        ResultWrapper(Result result) {
            this.mResultObj = result;
        }

        public void sendResult(List<Parcel> result, int flags) {
            try {
                MediaBrowserServiceCompatApi24.sResultFlags.setInt(this.mResultObj, flags);
            } catch (IllegalAccessException e) {
                Log.w(MediaBrowserServiceCompatApi24.TAG, e);
            }
            this.mResultObj.sendResult(parcelListToItemList(result));
        }

        public void detach() {
            this.mResultObj.detach();
        }

        List<MediaItem> parcelListToItemList(List<Parcel> parcelList) {
            if (parcelList == null) {
                return null;
            }
            List<MediaItem> items = new ArrayList();
            for (Parcel parcel : parcelList) {
                parcel.setDataPosition(0);
                items.add((MediaItem) MediaItem.CREATOR.createFromParcel(parcel));
                parcel.recycle();
            }
            return items;
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.media.MediaBrowserServiceCompatApi24.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:116)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:249)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
Caused by: java.lang.NullPointerException
	at jadx.core.dex.nodes.MethodNode.addJump(MethodNode.java:370)
	at jadx.core.dex.nodes.MethodNode.initJumps(MethodNode.java:360)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:106)
	... 7 more
*/
        /*
        r1 = android.service.media.MediaBrowserService.Result.class;	 Catch:{ NoSuchFieldException -> 0x000e }
        r2 = "mFlags";	 Catch:{ NoSuchFieldException -> 0x000e }
        sResultFlags = r1;	 Catch:{ NoSuchFieldException -> 0x000e }
        r1 = sResultFlags;	 Catch:{ NoSuchFieldException -> 0x000e }
        r2 = 1;	 Catch:{ NoSuchFieldException -> 0x000e }
    L_0x000e:
        r0 = move-exception;
        r1 = "MBSCompatApi24";
        android.util.Log.w(r1, r0);
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.MediaBrowserServiceCompatApi24.<clinit>():void");
    }

    MediaBrowserServiceCompatApi24() {
    }

    public static Object createService(Context context, ServiceCompatProxy serviceProxy) {
        return new MediaBrowserServiceAdaptor(context, serviceProxy);
    }

    public static void notifyChildrenChanged(Object serviceObj, String parentId, Bundle options) {
        ((MediaBrowserService) serviceObj).notifyChildrenChanged(parentId, options);
    }

    public static Bundle getBrowserRootHints(Object serviceObj) {
        return ((MediaBrowserService) serviceObj).getBrowserRootHints();
    }
}
