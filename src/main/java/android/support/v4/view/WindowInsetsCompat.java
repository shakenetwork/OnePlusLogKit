package android.support.v4.view;

import android.graphics.Rect;

public class WindowInsetsCompat {
    private static final WindowInsetsCompatImpl IMPL = null;
    private final Object mInsets;

    private interface WindowInsetsCompatImpl {
        WindowInsetsCompat consumeStableInsets(Object obj);

        WindowInsetsCompat consumeSystemWindowInsets(Object obj);

        Object getSourceWindowInsets(Object obj);

        int getStableInsetBottom(Object obj);

        int getStableInsetLeft(Object obj);

        int getStableInsetRight(Object obj);

        int getStableInsetTop(Object obj);

        int getSystemWindowInsetBottom(Object obj);

        int getSystemWindowInsetLeft(Object obj);

        int getSystemWindowInsetRight(Object obj);

        int getSystemWindowInsetTop(Object obj);

        boolean hasInsets(Object obj);

        boolean hasStableInsets(Object obj);

        boolean hasSystemWindowInsets(Object obj);

        boolean isConsumed(Object obj);

        boolean isRound(Object obj);

        WindowInsetsCompat replaceSystemWindowInsets(Object obj, int i, int i2, int i3, int i4);

        WindowInsetsCompat replaceSystemWindowInsets(Object obj, Rect rect);
    }

    private static class WindowInsetsCompatBaseImpl implements WindowInsetsCompatImpl {
        WindowInsetsCompatBaseImpl() {
        }

        public int getSystemWindowInsetLeft(Object insets) {
            return 0;
        }

        public int getSystemWindowInsetTop(Object insets) {
            return 0;
        }

        public int getSystemWindowInsetRight(Object insets) {
            return 0;
        }

        public int getSystemWindowInsetBottom(Object insets) {
            return 0;
        }

        public boolean hasSystemWindowInsets(Object insets) {
            return false;
        }

        public boolean hasInsets(Object insets) {
            return false;
        }

        public boolean isConsumed(Object insets) {
            return false;
        }

        public boolean isRound(Object insets) {
            return false;
        }

        public WindowInsetsCompat consumeSystemWindowInsets(Object insets) {
            return null;
        }

        public WindowInsetsCompat replaceSystemWindowInsets(Object insets, int left, int top, int right, int bottom) {
            return null;
        }

        public WindowInsetsCompat replaceSystemWindowInsets(Object insets, Rect systemWindowInsets) {
            return null;
        }

        public int getStableInsetTop(Object insets) {
            return 0;
        }

        public int getStableInsetLeft(Object insets) {
            return 0;
        }

        public int getStableInsetRight(Object insets) {
            return 0;
        }

        public int getStableInsetBottom(Object insets) {
            return 0;
        }

        public boolean hasStableInsets(Object insets) {
            return false;
        }

        public WindowInsetsCompat consumeStableInsets(Object insets) {
            return null;
        }

        public Object getSourceWindowInsets(Object src) {
            return null;
        }
    }

    private static class WindowInsetsCompatApi20Impl extends WindowInsetsCompatBaseImpl {
        WindowInsetsCompatApi20Impl() {
        }

        public WindowInsetsCompat consumeSystemWindowInsets(Object insets) {
            return new WindowInsetsCompat(WindowInsetsCompatApi20.consumeSystemWindowInsets(insets));
        }

        public int getSystemWindowInsetBottom(Object insets) {
            return WindowInsetsCompatApi20.getSystemWindowInsetBottom(insets);
        }

        public int getSystemWindowInsetLeft(Object insets) {
            return WindowInsetsCompatApi20.getSystemWindowInsetLeft(insets);
        }

        public int getSystemWindowInsetRight(Object insets) {
            return WindowInsetsCompatApi20.getSystemWindowInsetRight(insets);
        }

        public int getSystemWindowInsetTop(Object insets) {
            return WindowInsetsCompatApi20.getSystemWindowInsetTop(insets);
        }

        public boolean hasInsets(Object insets) {
            return WindowInsetsCompatApi20.hasInsets(insets);
        }

        public boolean hasSystemWindowInsets(Object insets) {
            return WindowInsetsCompatApi20.hasSystemWindowInsets(insets);
        }

        public boolean isRound(Object insets) {
            return WindowInsetsCompatApi20.isRound(insets);
        }

        public WindowInsetsCompat replaceSystemWindowInsets(Object insets, int left, int top, int right, int bottom) {
            return new WindowInsetsCompat(WindowInsetsCompatApi20.replaceSystemWindowInsets(insets, left, top, right, bottom));
        }

        public Object getSourceWindowInsets(Object src) {
            return WindowInsetsCompatApi20.getSourceWindowInsets(src);
        }
    }

    private static class WindowInsetsCompatApi21Impl extends WindowInsetsCompatApi20Impl {
        WindowInsetsCompatApi21Impl() {
        }

        public WindowInsetsCompat consumeStableInsets(Object insets) {
            return new WindowInsetsCompat(WindowInsetsCompatApi21.consumeStableInsets(insets));
        }

        public int getStableInsetBottom(Object insets) {
            return WindowInsetsCompatApi21.getStableInsetBottom(insets);
        }

        public int getStableInsetLeft(Object insets) {
            return WindowInsetsCompatApi21.getStableInsetLeft(insets);
        }

        public int getStableInsetRight(Object insets) {
            return WindowInsetsCompatApi21.getStableInsetRight(insets);
        }

        public int getStableInsetTop(Object insets) {
            return WindowInsetsCompatApi21.getStableInsetTop(insets);
        }

        public boolean hasStableInsets(Object insets) {
            return WindowInsetsCompatApi21.hasStableInsets(insets);
        }

        public boolean isConsumed(Object insets) {
            return WindowInsetsCompatApi21.isConsumed(insets);
        }

        public WindowInsetsCompat replaceSystemWindowInsets(Object insets, Rect systemWindowInsets) {
            return new WindowInsetsCompat(WindowInsetsCompatApi21.replaceSystemWindowInsets(insets, systemWindowInsets));
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.view.WindowInsetsCompat.<clinit>():void
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
        r1 = 21;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r1 = new android.support.v4.view.WindowInsetsCompat$WindowInsetsCompatApi21Impl;
        r1.<init>();
        IMPL = r1;
    L_0x000e:
        r1 = 20;
        if (r0 < r1) goto L_0x001a;
    L_0x0012:
        r1 = new android.support.v4.view.WindowInsetsCompat$WindowInsetsCompatApi20Impl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
    L_0x001a:
        r1 = new android.support.v4.view.WindowInsetsCompat$WindowInsetsCompatBaseImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.WindowInsetsCompat.<clinit>():void");
    }

    WindowInsetsCompat(Object insets) {
        this.mInsets = insets;
    }

    public WindowInsetsCompat(WindowInsetsCompat src) {
        Object obj = null;
        if (src != null) {
            obj = IMPL.getSourceWindowInsets(src.mInsets);
        }
        this.mInsets = obj;
    }

    public int getSystemWindowInsetLeft() {
        return IMPL.getSystemWindowInsetLeft(this.mInsets);
    }

    public int getSystemWindowInsetTop() {
        return IMPL.getSystemWindowInsetTop(this.mInsets);
    }

    public int getSystemWindowInsetRight() {
        return IMPL.getSystemWindowInsetRight(this.mInsets);
    }

    public int getSystemWindowInsetBottom() {
        return IMPL.getSystemWindowInsetBottom(this.mInsets);
    }

    public boolean hasSystemWindowInsets() {
        return IMPL.hasSystemWindowInsets(this.mInsets);
    }

    public boolean hasInsets() {
        return IMPL.hasInsets(this.mInsets);
    }

    public boolean isConsumed() {
        return IMPL.isConsumed(this.mInsets);
    }

    public boolean isRound() {
        return IMPL.isRound(this.mInsets);
    }

    public WindowInsetsCompat consumeSystemWindowInsets() {
        return IMPL.consumeSystemWindowInsets(this.mInsets);
    }

    public WindowInsetsCompat replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        return IMPL.replaceSystemWindowInsets(this.mInsets, left, top, right, bottom);
    }

    public WindowInsetsCompat replaceSystemWindowInsets(Rect systemWindowInsets) {
        return IMPL.replaceSystemWindowInsets(this.mInsets, systemWindowInsets);
    }

    public int getStableInsetTop() {
        return IMPL.getStableInsetTop(this.mInsets);
    }

    public int getStableInsetLeft() {
        return IMPL.getStableInsetLeft(this.mInsets);
    }

    public int getStableInsetRight() {
        return IMPL.getStableInsetRight(this.mInsets);
    }

    public int getStableInsetBottom() {
        return IMPL.getStableInsetBottom(this.mInsets);
    }

    public boolean hasStableInsets() {
        return IMPL.hasStableInsets(this.mInsets);
    }

    public WindowInsetsCompat consumeStableInsets() {
        return IMPL.consumeStableInsets(this.mInsets);
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WindowInsetsCompat other = (WindowInsetsCompat) o;
        if (this.mInsets != null) {
            z = this.mInsets.equals(other.mInsets);
        } else if (other.mInsets != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return this.mInsets == null ? 0 : this.mInsets.hashCode();
    }

    static WindowInsetsCompat wrap(Object insets) {
        return insets == null ? null : new WindowInsetsCompat(insets);
    }

    static Object unwrap(WindowInsetsCompat insets) {
        return insets == null ? null : insets.mInsets;
    }
}
