package android.support.v4.view;

import android.view.VelocityTracker;

public final class VelocityTrackerCompat {
    static final VelocityTrackerVersionImpl IMPL = null;

    interface VelocityTrackerVersionImpl {
        float getXVelocity(VelocityTracker velocityTracker, int i);

        float getYVelocity(VelocityTracker velocityTracker, int i);
    }

    static class BaseVelocityTrackerVersionImpl implements VelocityTrackerVersionImpl {
        BaseVelocityTrackerVersionImpl() {
        }

        public float getXVelocity(VelocityTracker tracker, int pointerId) {
            return tracker.getXVelocity();
        }

        public float getYVelocity(VelocityTracker tracker, int pointerId) {
            return tracker.getYVelocity();
        }
    }

    static class HoneycombVelocityTrackerVersionImpl implements VelocityTrackerVersionImpl {
        HoneycombVelocityTrackerVersionImpl() {
        }

        public float getXVelocity(VelocityTracker tracker, int pointerId) {
            return VelocityTrackerCompatHoneycomb.getXVelocity(tracker, pointerId);
        }

        public float getYVelocity(VelocityTracker tracker, int pointerId) {
            return VelocityTrackerCompatHoneycomb.getYVelocity(tracker, pointerId);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.view.VelocityTrackerCompat.<clinit>():void
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
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 11;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r0 = new android.support.v4.view.VelocityTrackerCompat$HoneycombVelocityTrackerVersionImpl;
        r0.<init>();
        IMPL = r0;
    L_0x000e:
        r0 = new android.support.v4.view.VelocityTrackerCompat$BaseVelocityTrackerVersionImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.VelocityTrackerCompat.<clinit>():void");
    }

    public static float getXVelocity(VelocityTracker tracker, int pointerId) {
        return IMPL.getXVelocity(tracker, pointerId);
    }

    public static float getYVelocity(VelocityTracker tracker, int pointerId) {
        return IMPL.getYVelocity(tracker, pointerId);
    }

    private VelocityTrackerCompat() {
    }
}
