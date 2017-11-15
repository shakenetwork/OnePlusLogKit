package android.support.v4.app;

import android.app.Service;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ServiceCompat {
    static final ServiceCompatImpl IMPL = null;
    public static final int START_STICKY = 1;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;

    interface ServiceCompatImpl {
        void stopForeground(Service service, int i);
    }

    static class Api24ServiceCompatImpl implements ServiceCompatImpl {
        Api24ServiceCompatImpl() {
        }

        public void stopForeground(Service service, int flags) {
            ServiceCompatApi24.stopForeground(service, flags);
        }
    }

    static class BaseServiceCompatImpl implements ServiceCompatImpl {
        BaseServiceCompatImpl() {
        }

        public void stopForeground(Service service, int flags) {
            boolean z = false;
            if ((flags & 1) != 0) {
                z = true;
            }
            service.stopForeground(z);
        }
    }

    @RestrictTo({Scope.GROUP_ID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StopForegroundFlags {
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.app.ServiceCompat.<clinit>():void
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
        r0 = android.support.v4.os.BuildCompat.isAtLeastN();
        if (r0 == 0) goto L_0x000e;
    L_0x0006:
        r0 = new android.support.v4.app.ServiceCompat$Api24ServiceCompatImpl;
        r0.<init>();
        IMPL = r0;
    L_0x000e:
        r0 = new android.support.v4.app.ServiceCompat$BaseServiceCompatImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.ServiceCompat.<clinit>():void");
    }

    private ServiceCompat() {
    }

    public static void stopForeground(Service service, int flags) {
        IMPL.stopForeground(service, flags);
    }
}
