package android.support.v4.view;

import android.view.ViewGroup.MarginLayoutParams;

public final class MarginLayoutParamsCompat {
    static final MarginLayoutParamsCompatImpl IMPL = null;

    interface MarginLayoutParamsCompatImpl {
        int getLayoutDirection(MarginLayoutParams marginLayoutParams);

        int getMarginEnd(MarginLayoutParams marginLayoutParams);

        int getMarginStart(MarginLayoutParams marginLayoutParams);

        boolean isMarginRelative(MarginLayoutParams marginLayoutParams);

        void resolveLayoutDirection(MarginLayoutParams marginLayoutParams, int i);

        void setLayoutDirection(MarginLayoutParams marginLayoutParams, int i);

        void setMarginEnd(MarginLayoutParams marginLayoutParams, int i);

        void setMarginStart(MarginLayoutParams marginLayoutParams, int i);
    }

    static class MarginLayoutParamsCompatImplBase implements MarginLayoutParamsCompatImpl {
        MarginLayoutParamsCompatImplBase() {
        }

        public int getMarginStart(MarginLayoutParams lp) {
            return lp.leftMargin;
        }

        public int getMarginEnd(MarginLayoutParams lp) {
            return lp.rightMargin;
        }

        public void setMarginStart(MarginLayoutParams lp, int marginStart) {
            lp.leftMargin = marginStart;
        }

        public void setMarginEnd(MarginLayoutParams lp, int marginEnd) {
            lp.rightMargin = marginEnd;
        }

        public boolean isMarginRelative(MarginLayoutParams lp) {
            return false;
        }

        public int getLayoutDirection(MarginLayoutParams lp) {
            return 0;
        }

        public void setLayoutDirection(MarginLayoutParams lp, int layoutDirection) {
        }

        public void resolveLayoutDirection(MarginLayoutParams lp, int layoutDirection) {
        }
    }

    static class MarginLayoutParamsCompatImplJbMr1 implements MarginLayoutParamsCompatImpl {
        MarginLayoutParamsCompatImplJbMr1() {
        }

        public int getMarginStart(MarginLayoutParams lp) {
            return MarginLayoutParamsCompatJellybeanMr1.getMarginStart(lp);
        }

        public int getMarginEnd(MarginLayoutParams lp) {
            return MarginLayoutParamsCompatJellybeanMr1.getMarginEnd(lp);
        }

        public void setMarginStart(MarginLayoutParams lp, int marginStart) {
            MarginLayoutParamsCompatJellybeanMr1.setMarginStart(lp, marginStart);
        }

        public void setMarginEnd(MarginLayoutParams lp, int marginEnd) {
            MarginLayoutParamsCompatJellybeanMr1.setMarginEnd(lp, marginEnd);
        }

        public boolean isMarginRelative(MarginLayoutParams lp) {
            return MarginLayoutParamsCompatJellybeanMr1.isMarginRelative(lp);
        }

        public int getLayoutDirection(MarginLayoutParams lp) {
            return MarginLayoutParamsCompatJellybeanMr1.getLayoutDirection(lp);
        }

        public void setLayoutDirection(MarginLayoutParams lp, int layoutDirection) {
            MarginLayoutParamsCompatJellybeanMr1.setLayoutDirection(lp, layoutDirection);
        }

        public void resolveLayoutDirection(MarginLayoutParams lp, int layoutDirection) {
            MarginLayoutParamsCompatJellybeanMr1.resolveLayoutDirection(lp, layoutDirection);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.view.MarginLayoutParamsCompat.<clinit>():void
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
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 17;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r1 = new android.support.v4.view.MarginLayoutParamsCompat$MarginLayoutParamsCompatImplJbMr1;
        r1.<init>();
        IMPL = r1;
    L_0x000e:
        r1 = new android.support.v4.view.MarginLayoutParamsCompat$MarginLayoutParamsCompatImplBase;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.MarginLayoutParamsCompat.<clinit>():void");
    }

    public static int getMarginStart(MarginLayoutParams lp) {
        return IMPL.getMarginStart(lp);
    }

    public static int getMarginEnd(MarginLayoutParams lp) {
        return IMPL.getMarginEnd(lp);
    }

    public static void setMarginStart(MarginLayoutParams lp, int marginStart) {
        IMPL.setMarginStart(lp, marginStart);
    }

    public static void setMarginEnd(MarginLayoutParams lp, int marginEnd) {
        IMPL.setMarginEnd(lp, marginEnd);
    }

    public static boolean isMarginRelative(MarginLayoutParams lp) {
        return IMPL.isMarginRelative(lp);
    }

    public static int getLayoutDirection(MarginLayoutParams lp) {
        int result = IMPL.getLayoutDirection(lp);
        if (result == 0 || result == 1) {
            return result;
        }
        return 0;
    }

    public static void setLayoutDirection(MarginLayoutParams lp, int layoutDirection) {
        IMPL.setLayoutDirection(lp, layoutDirection);
    }

    public static void resolveLayoutDirection(MarginLayoutParams lp, int layoutDirection) {
        IMPL.resolveLayoutDirection(lp, layoutDirection);
    }

    private MarginLayoutParamsCompat() {
    }
}
