package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

public final class PointerIconCompat {
    static final PointerIconCompatImpl IMPL = null;
    public static final int TYPE_ALIAS = 1010;
    public static final int TYPE_ALL_SCROLL = 1013;
    public static final int TYPE_ARROW = 1000;
    public static final int TYPE_CELL = 1006;
    public static final int TYPE_CONTEXT_MENU = 1001;
    public static final int TYPE_COPY = 1011;
    public static final int TYPE_CROSSHAIR = 1007;
    public static final int TYPE_DEFAULT = 1000;
    public static final int TYPE_GRAB = 1020;
    public static final int TYPE_GRABBING = 1021;
    public static final int TYPE_HAND = 1002;
    public static final int TYPE_HELP = 1003;
    public static final int TYPE_HORIZONTAL_DOUBLE_ARROW = 1014;
    public static final int TYPE_NO_DROP = 1012;
    public static final int TYPE_NULL = 0;
    public static final int TYPE_TEXT = 1008;
    public static final int TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW = 1017;
    public static final int TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW = 1016;
    public static final int TYPE_VERTICAL_DOUBLE_ARROW = 1015;
    public static final int TYPE_VERTICAL_TEXT = 1009;
    public static final int TYPE_WAIT = 1004;
    public static final int TYPE_ZOOM_IN = 1018;
    public static final int TYPE_ZOOM_OUT = 1019;
    private Object mPointerIcon;

    interface PointerIconCompatImpl {
        Object create(Bitmap bitmap, float f, float f2);

        Object getSystemIcon(Context context, int i);

        Object load(Resources resources, int i);
    }

    static class BasePointerIconCompatImpl implements PointerIconCompatImpl {
        BasePointerIconCompatImpl() {
        }

        public Object getSystemIcon(Context context, int style) {
            return null;
        }

        public Object create(Bitmap bitmap, float hotSpotX, float hotSpotY) {
            return null;
        }

        public Object load(Resources resources, int resourceId) {
            return null;
        }
    }

    static class Api24PointerIconCompatImpl extends BasePointerIconCompatImpl {
        Api24PointerIconCompatImpl() {
        }

        public Object getSystemIcon(Context context, int style) {
            return PointerIconCompatApi24.getSystemIcon(context, style);
        }

        public Object create(Bitmap bitmap, float hotSpotX, float hotSpotY) {
            return PointerIconCompatApi24.create(bitmap, hotSpotX, hotSpotY);
        }

        public Object load(Resources resources, int resourceId) {
            return PointerIconCompatApi24.load(resources, resourceId);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.view.PointerIconCompat.<clinit>():void
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
        r0 = new android.support.v4.view.PointerIconCompat$Api24PointerIconCompatImpl;
        r0.<init>();
        IMPL = r0;
    L_0x000e:
        r0 = new android.support.v4.view.PointerIconCompat$BasePointerIconCompatImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.PointerIconCompat.<clinit>():void");
    }

    private PointerIconCompat(Object pointerIcon) {
        this.mPointerIcon = pointerIcon;
    }

    @RestrictTo({Scope.GROUP_ID})
    public Object getPointerIcon() {
        return this.mPointerIcon;
    }

    public static PointerIconCompat getSystemIcon(Context context, int style) {
        return new PointerIconCompat(IMPL.getSystemIcon(context, style));
    }

    public static PointerIconCompat create(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        return new PointerIconCompat(IMPL.create(bitmap, hotSpotX, hotSpotY));
    }

    public static PointerIconCompat load(Resources resources, int resourceId) {
        return new PointerIconCompat(IMPL.load(resources, resourceId));
    }
}
