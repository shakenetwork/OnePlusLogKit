package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityEvent;

public final class AccessibilityEventCompat {
    public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4;
    public static final int CONTENT_CHANGE_TYPE_SUBTREE = 1;
    public static final int CONTENT_CHANGE_TYPE_TEXT = 2;
    public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0;
    private static final AccessibilityEventVersionImpl IMPL = null;
    public static final int TYPES_ALL_MASK = -1;
    public static final int TYPE_ANNOUNCEMENT = 16384;
    public static final int TYPE_ASSIST_READING_CONTEXT = 16777216;
    public static final int TYPE_GESTURE_DETECTION_END = 524288;
    public static final int TYPE_GESTURE_DETECTION_START = 262144;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
    public static final int TYPE_TOUCH_INTERACTION_END = 2097152;
    public static final int TYPE_TOUCH_INTERACTION_START = 1048576;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536;
    public static final int TYPE_VIEW_CONTEXT_CLICKED = 8388608;
    public static final int TYPE_VIEW_HOVER_ENTER = 128;
    public static final int TYPE_VIEW_HOVER_EXIT = 256;
    public static final int TYPE_VIEW_SCROLLED = 4096;
    public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;
    public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072;
    public static final int TYPE_WINDOWS_CHANGED = 4194304;
    public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;

    interface AccessibilityEventVersionImpl {
        void appendRecord(AccessibilityEvent accessibilityEvent, Object obj);

        int getAction(AccessibilityEvent accessibilityEvent);

        int getContentChangeTypes(AccessibilityEvent accessibilityEvent);

        int getMovementGranularity(AccessibilityEvent accessibilityEvent);

        Object getRecord(AccessibilityEvent accessibilityEvent, int i);

        int getRecordCount(AccessibilityEvent accessibilityEvent);

        void setAction(AccessibilityEvent accessibilityEvent, int i);

        void setContentChangeTypes(AccessibilityEvent accessibilityEvent, int i);

        void setMovementGranularity(AccessibilityEvent accessibilityEvent, int i);
    }

    static class AccessibilityEventStubImpl implements AccessibilityEventVersionImpl {
        AccessibilityEventStubImpl() {
        }

        public void appendRecord(AccessibilityEvent event, Object record) {
        }

        public Object getRecord(AccessibilityEvent event, int index) {
            return null;
        }

        public void setContentChangeTypes(AccessibilityEvent event, int types) {
        }

        public int getRecordCount(AccessibilityEvent event) {
            return 0;
        }

        public int getContentChangeTypes(AccessibilityEvent event) {
            return 0;
        }

        public void setMovementGranularity(AccessibilityEvent event, int granularity) {
        }

        public int getMovementGranularity(AccessibilityEvent event) {
            return 0;
        }

        public void setAction(AccessibilityEvent event, int action) {
        }

        public int getAction(AccessibilityEvent event) {
            return 0;
        }
    }

    static class AccessibilityEventIcsImpl extends AccessibilityEventStubImpl {
        AccessibilityEventIcsImpl() {
        }

        public void appendRecord(AccessibilityEvent event, Object record) {
            AccessibilityEventCompatIcs.appendRecord(event, record);
        }

        public Object getRecord(AccessibilityEvent event, int index) {
            return AccessibilityEventCompatIcs.getRecord(event, index);
        }

        public int getRecordCount(AccessibilityEvent event) {
            return AccessibilityEventCompatIcs.getRecordCount(event);
        }
    }

    static class AccessibilityEventJellyBeanImpl extends AccessibilityEventIcsImpl {
        AccessibilityEventJellyBeanImpl() {
        }

        public void setMovementGranularity(AccessibilityEvent event, int granularity) {
            AccessibilityEventCompatJellyBean.setMovementGranularity(event, granularity);
        }

        public int getMovementGranularity(AccessibilityEvent event) {
            return AccessibilityEventCompatJellyBean.getMovementGranularity(event);
        }

        public void setAction(AccessibilityEvent event, int action) {
            AccessibilityEventCompatJellyBean.setAction(event, action);
        }

        public int getAction(AccessibilityEvent event) {
            return AccessibilityEventCompatJellyBean.getAction(event);
        }
    }

    static class AccessibilityEventKitKatImpl extends AccessibilityEventJellyBeanImpl {
        AccessibilityEventKitKatImpl() {
        }

        public void setContentChangeTypes(AccessibilityEvent event, int types) {
            AccessibilityEventCompatKitKat.setContentChangeTypes(event, types);
        }

        public int getContentChangeTypes(AccessibilityEvent event) {
            return AccessibilityEventCompatKitKat.getContentChangeTypes(event);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.view.accessibility.AccessibilityEventCompat.<clinit>():void
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
        r1 = 19;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r0 = new android.support.v4.view.accessibility.AccessibilityEventCompat$AccessibilityEventKitKatImpl;
        r0.<init>();
        IMPL = r0;
    L_0x000e:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 16;
        if (r0 < r1) goto L_0x001c;
    L_0x0014:
        r0 = new android.support.v4.view.accessibility.AccessibilityEventCompat$AccessibilityEventJellyBeanImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
    L_0x001c:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 14;
        if (r0 < r1) goto L_0x002a;
        r0 = new android.support.v4.view.accessibility.AccessibilityEventCompat$AccessibilityEventIcsImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        r0 = new android.support.v4.view.accessibility.AccessibilityEventCompat$AccessibilityEventStubImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.accessibility.AccessibilityEventCompat.<clinit>():void");
    }

    private AccessibilityEventCompat() {
    }

    public static int getRecordCount(AccessibilityEvent event) {
        return IMPL.getRecordCount(event);
    }

    public static void appendRecord(AccessibilityEvent event, AccessibilityRecordCompat record) {
        IMPL.appendRecord(event, record.getImpl());
    }

    public static AccessibilityRecordCompat getRecord(AccessibilityEvent event, int index) {
        return new AccessibilityRecordCompat(IMPL.getRecord(event, index));
    }

    public static AccessibilityRecordCompat asRecord(AccessibilityEvent event) {
        return new AccessibilityRecordCompat(event);
    }

    public static void setContentChangeTypes(AccessibilityEvent event, int changeTypes) {
        IMPL.setContentChangeTypes(event, changeTypes);
    }

    public static int getContentChangeTypes(AccessibilityEvent event) {
        return IMPL.getContentChangeTypes(event);
    }

    public void setMovementGranularity(AccessibilityEvent event, int granularity) {
        IMPL.setMovementGranularity(event, granularity);
    }

    public int getMovementGranularity(AccessibilityEvent event) {
        return IMPL.getMovementGranularity(event);
    }

    public void setAction(AccessibilityEvent event, int action) {
        IMPL.setAction(event, action);
    }

    public int getAction(AccessibilityEvent event) {
        return IMPL.getAction(event);
    }
}
