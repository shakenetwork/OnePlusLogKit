package android.support.v4.view.accessibility;

import android.graphics.Rect;

public class AccessibilityWindowInfoCompat {
    private static final AccessibilityWindowInfoImpl IMPL = null;
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
    public static final int TYPE_APPLICATION = 1;
    public static final int TYPE_INPUT_METHOD = 2;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
    public static final int TYPE_SYSTEM = 3;
    private static final int UNDEFINED = -1;
    private Object mInfo;

    private interface AccessibilityWindowInfoImpl {
        Object getAnchor(Object obj);

        void getBoundsInScreen(Object obj, Rect rect);

        Object getChild(Object obj, int i);

        int getChildCount(Object obj);

        int getId(Object obj);

        int getLayer(Object obj);

        Object getParent(Object obj);

        Object getRoot(Object obj);

        CharSequence getTitle(Object obj);

        int getType(Object obj);

        boolean isAccessibilityFocused(Object obj);

        boolean isActive(Object obj);

        boolean isFocused(Object obj);

        Object obtain();

        Object obtain(Object obj);

        void recycle(Object obj);
    }

    private static class AccessibilityWindowInfoStubImpl implements AccessibilityWindowInfoImpl {
        AccessibilityWindowInfoStubImpl() {
        }

        public Object obtain() {
            return null;
        }

        public Object obtain(Object info) {
            return null;
        }

        public int getType(Object info) {
            return -1;
        }

        public int getLayer(Object info) {
            return -1;
        }

        public Object getRoot(Object info) {
            return null;
        }

        public Object getParent(Object info) {
            return null;
        }

        public int getId(Object info) {
            return -1;
        }

        public void getBoundsInScreen(Object info, Rect outBounds) {
        }

        public boolean isActive(Object info) {
            return true;
        }

        public boolean isFocused(Object info) {
            return true;
        }

        public boolean isAccessibilityFocused(Object info) {
            return true;
        }

        public int getChildCount(Object info) {
            return 0;
        }

        public Object getChild(Object info, int index) {
            return null;
        }

        public void recycle(Object info) {
        }

        public CharSequence getTitle(Object info) {
            return null;
        }

        public Object getAnchor(Object info) {
            return null;
        }
    }

    private static class AccessibilityWindowInfoApi21Impl extends AccessibilityWindowInfoStubImpl {
        AccessibilityWindowInfoApi21Impl() {
        }

        public Object obtain() {
            return AccessibilityWindowInfoCompatApi21.obtain();
        }

        public Object obtain(Object info) {
            return AccessibilityWindowInfoCompatApi21.obtain(info);
        }

        public int getType(Object info) {
            return AccessibilityWindowInfoCompatApi21.getType(info);
        }

        public int getLayer(Object info) {
            return AccessibilityWindowInfoCompatApi21.getLayer(info);
        }

        public Object getRoot(Object info) {
            return AccessibilityWindowInfoCompatApi21.getRoot(info);
        }

        public Object getParent(Object info) {
            return AccessibilityWindowInfoCompatApi21.getParent(info);
        }

        public int getId(Object info) {
            return AccessibilityWindowInfoCompatApi21.getId(info);
        }

        public void getBoundsInScreen(Object info, Rect outBounds) {
            AccessibilityWindowInfoCompatApi21.getBoundsInScreen(info, outBounds);
        }

        public boolean isActive(Object info) {
            return AccessibilityWindowInfoCompatApi21.isActive(info);
        }

        public boolean isFocused(Object info) {
            return AccessibilityWindowInfoCompatApi21.isFocused(info);
        }

        public boolean isAccessibilityFocused(Object info) {
            return AccessibilityWindowInfoCompatApi21.isAccessibilityFocused(info);
        }

        public int getChildCount(Object info) {
            return AccessibilityWindowInfoCompatApi21.getChildCount(info);
        }

        public Object getChild(Object info, int index) {
            return AccessibilityWindowInfoCompatApi21.getChild(info, index);
        }

        public void recycle(Object info) {
            AccessibilityWindowInfoCompatApi21.recycle(info);
        }
    }

    private static class AccessibilityWindowInfoApi24Impl extends AccessibilityWindowInfoApi21Impl {
        AccessibilityWindowInfoApi24Impl() {
        }

        public CharSequence getTitle(Object info) {
            return AccessibilityWindowInfoCompatApi24.getTitle(info);
        }

        public Object getAnchor(Object info) {
            return AccessibilityWindowInfoCompatApi24.getAnchor(info);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.view.accessibility.AccessibilityWindowInfoCompat.<clinit>():void
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
        r1 = 24;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r0 = new android.support.v4.view.accessibility.AccessibilityWindowInfoCompat$AccessibilityWindowInfoApi24Impl;
        r0.<init>();
        IMPL = r0;
    L_0x000e:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x001c;
    L_0x0014:
        r0 = new android.support.v4.view.accessibility.AccessibilityWindowInfoCompat$AccessibilityWindowInfoApi21Impl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
    L_0x001c:
        r0 = new android.support.v4.view.accessibility.AccessibilityWindowInfoCompat$AccessibilityWindowInfoStubImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.accessibility.AccessibilityWindowInfoCompat.<clinit>():void");
    }

    static AccessibilityWindowInfoCompat wrapNonNullInstance(Object object) {
        if (object != null) {
            return new AccessibilityWindowInfoCompat(object);
        }
        return null;
    }

    private AccessibilityWindowInfoCompat(Object info) {
        this.mInfo = info;
    }

    public int getType() {
        return IMPL.getType(this.mInfo);
    }

    public int getLayer() {
        return IMPL.getLayer(this.mInfo);
    }

    public AccessibilityNodeInfoCompat getRoot() {
        return AccessibilityNodeInfoCompat.wrapNonNullInstance(IMPL.getRoot(this.mInfo));
    }

    public AccessibilityWindowInfoCompat getParent() {
        return wrapNonNullInstance(IMPL.getParent(this.mInfo));
    }

    public int getId() {
        return IMPL.getId(this.mInfo);
    }

    public void getBoundsInScreen(Rect outBounds) {
        IMPL.getBoundsInScreen(this.mInfo, outBounds);
    }

    public boolean isActive() {
        return IMPL.isActive(this.mInfo);
    }

    public boolean isFocused() {
        return IMPL.isFocused(this.mInfo);
    }

    public boolean isAccessibilityFocused() {
        return IMPL.isAccessibilityFocused(this.mInfo);
    }

    public int getChildCount() {
        return IMPL.getChildCount(this.mInfo);
    }

    public AccessibilityWindowInfoCompat getChild(int index) {
        return wrapNonNullInstance(IMPL.getChild(this.mInfo, index));
    }

    public CharSequence getTitle() {
        return IMPL.getTitle(this.mInfo);
    }

    public AccessibilityNodeInfoCompat getAnchor() {
        return AccessibilityNodeInfoCompat.wrapNonNullInstance(IMPL.getAnchor(this.mInfo));
    }

    public static AccessibilityWindowInfoCompat obtain() {
        return wrapNonNullInstance(IMPL.obtain());
    }

    public static AccessibilityWindowInfoCompat obtain(AccessibilityWindowInfoCompat info) {
        return info == null ? null : wrapNonNullInstance(IMPL.obtain(info.mInfo));
    }

    public void recycle() {
        IMPL.recycle(this.mInfo);
    }

    public int hashCode() {
        return this.mInfo == null ? 0 : this.mInfo.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityWindowInfoCompat other = (AccessibilityWindowInfoCompat) obj;
        if (this.mInfo == null) {
            if (other.mInfo != null) {
                return false;
            }
        } else if (!this.mInfo.equals(other.mInfo)) {
            return false;
        }
        return true;
    }

    public String toString() {
        boolean z;
        boolean z2 = true;
        StringBuilder builder = new StringBuilder();
        Rect bounds = new Rect();
        getBoundsInScreen(bounds);
        builder.append("AccessibilityWindowInfo[");
        builder.append("id=").append(getId());
        builder.append(", type=").append(typeToString(getType()));
        builder.append(", layer=").append(getLayer());
        builder.append(", bounds=").append(bounds);
        builder.append(", focused=").append(isFocused());
        builder.append(", active=").append(isActive());
        StringBuilder append = builder.append(", hasParent=");
        if (getParent() != null) {
            z = true;
        } else {
            z = false;
        }
        append.append(z);
        StringBuilder append2 = builder.append(", hasChildren=");
        if (getChildCount() <= 0) {
            z2 = false;
        }
        append2.append(z2);
        builder.append(']');
        return builder.toString();
    }

    private static String typeToString(int type) {
        switch (type) {
            case 1:
                return "TYPE_APPLICATION";
            case 2:
                return "TYPE_INPUT_METHOD";
            case 3:
                return "TYPE_SYSTEM";
            case 4:
                return "TYPE_ACCESSIBILITY_OVERLAY";
            default:
                return "<UNKNOWN>";
        }
    }
}
