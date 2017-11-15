package android.support.v4.media;

import android.media.browse.MediaBrowser.MediaItem;
import java.lang.reflect.Constructor;
import java.util.List;

class ParceledListSliceAdapterApi21 {
    private static Constructor sConstructor;

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.media.ParceledListSliceAdapterApi21.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:116)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:249)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
Caused by: java.lang.NullPointerException
	at jadx.core.dex.nodes.MethodNode.addJump(MethodNode.java:370)
	at jadx.core.dex.nodes.MethodNode.initJumps(MethodNode.java:360)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:106)
	... 5 more
*/
        /*
        r2 = "android.content.pm.ParceledListSlice";	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        r1 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        r2 = 1;	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        r2 = new java.lang.Class[r2];	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        r3 = java.util.List.class;	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        r4 = 0;	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        r2[r4] = r3;	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
        sConstructor = r2;	 Catch:{ ClassNotFoundException -> 0x0014, ClassNotFoundException -> 0x0014 }
    L_0x0014:
        r0 = move-exception;
        goto L_0x0013;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.ParceledListSliceAdapterApi21.<clinit>():void");
    }

    ParceledListSliceAdapterApi21() {
    }

    static Object newInstance(List<MediaItem> itemList) {
        Object result = null;
        try {
            result = sConstructor.newInstance(new Object[]{itemList});
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return result;
    }
}
