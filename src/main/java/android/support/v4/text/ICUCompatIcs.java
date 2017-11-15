package android.support.v4.text;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

class ICUCompatIcs {
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.text.ICUCompatIcs.<clinit>():void
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
	at jadx.core.dex.nodes.MethodNode.initJumps(MethodNode.java:356)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:106)
	... 7 more
*/
        /*
        r6 = 0;
        r2 = "libcore.icu.ICU";	 Catch:{ Exception -> 0x0029 }
        r0 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x0029 }
        if (r0 == 0) goto L_0x0028;	 Catch:{ Exception -> 0x0029 }
    L_0x000a:
        r2 = "getScript";	 Catch:{ Exception -> 0x0029 }
        r3 = 1;	 Catch:{ Exception -> 0x0029 }
        r3 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0029 }
        r4 = java.lang.String.class;	 Catch:{ Exception -> 0x0029 }
        r5 = 0;	 Catch:{ Exception -> 0x0029 }
        r3[r5] = r4;	 Catch:{ Exception -> 0x0029 }
        sGetScriptMethod = r2;	 Catch:{ Exception -> 0x0029 }
        r2 = "addLikelySubtags";	 Catch:{ Exception -> 0x0029 }
        r3 = 1;	 Catch:{ Exception -> 0x0029 }
        r3 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0029 }
        r4 = java.lang.String.class;	 Catch:{ Exception -> 0x0029 }
        r5 = 0;	 Catch:{ Exception -> 0x0029 }
        r3[r5] = r4;	 Catch:{ Exception -> 0x0029 }
        sAddLikelySubtagsMethod = r2;	 Catch:{ Exception -> 0x0029 }
    L_0x0029:
        r1 = move-exception;
        sGetScriptMethod = r6;
        sAddLikelySubtagsMethod = r6;
        r2 = "ICUCompatIcs";
        android.util.Log.w(r2, r1);
        goto L_0x0028;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.text.ICUCompatIcs.<clinit>():void");
    }

    ICUCompatIcs() {
    }

    public static String maximizeAndGetScript(Locale locale) {
        String localeWithSubtags = addLikelySubtags(locale);
        if (localeWithSubtags != null) {
            return getScript(localeWithSubtags);
        }
        return null;
    }

    private static String getScript(String localeStr) {
        try {
            if (sGetScriptMethod != null) {
                return (String) sGetScriptMethod.invoke(null, new Object[]{localeStr});
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e2) {
            Log.w(TAG, e2);
        }
        return null;
    }

    private static String addLikelySubtags(Locale locale) {
        String localeStr = locale.toString();
        try {
            if (sAddLikelySubtagsMethod != null) {
                return (String) sAddLikelySubtagsMethod.invoke(null, new Object[]{localeStr});
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e2) {
            Log.w(TAG, e2);
        }
        return localeStr;
    }
}
