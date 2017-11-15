package android.support.v4.widget;

import android.util.Log;
import android.widget.PopupWindow;
import java.lang.reflect.Field;

class PopupWindowCompatApi21 {
    private static final String TAG = "PopupWindowCompatApi21";
    private static Field sOverlapAnchorField;

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.widget.PopupWindowCompatApi21.<clinit>():void
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
        r1 = android.widget.PopupWindow.class;	 Catch:{ NoSuchFieldException -> 0x000e }
        r2 = "mOverlapAnchor";	 Catch:{ NoSuchFieldException -> 0x000e }
        sOverlapAnchorField = r1;	 Catch:{ NoSuchFieldException -> 0x000e }
        r1 = sOverlapAnchorField;	 Catch:{ NoSuchFieldException -> 0x000e }
        r2 = 1;	 Catch:{ NoSuchFieldException -> 0x000e }
    L_0x000e:
        r0 = move-exception;
        r1 = "PopupWindowCompatApi21";
        r2 = "Could not fetch mOverlapAnchor field from PopupWindow";
        android.util.Log.i(r1, r2, r0);
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.PopupWindowCompatApi21.<clinit>():void");
    }

    PopupWindowCompatApi21() {
    }

    static void setOverlapAnchor(PopupWindow popupWindow, boolean overlapAnchor) {
        if (sOverlapAnchorField != null) {
            try {
                sOverlapAnchorField.set(popupWindow, Boolean.valueOf(overlapAnchor));
            } catch (IllegalAccessException e) {
                Log.i(TAG, "Could not set overlap anchor field in PopupWindow", e);
            }
        }
    }

    static boolean getOverlapAnchor(PopupWindow popupWindow) {
        if (sOverlapAnchorField != null) {
            try {
                return ((Boolean) sOverlapAnchorField.get(popupWindow)).booleanValue();
            } catch (IllegalAccessException e) {
                Log.i(TAG, "Could not get overlap anchor field in PopupWindow", e);
            }
        }
        return false;
    }
}
