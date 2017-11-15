package android.support.v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;

public final class CompoundButtonCompat {
    private static final CompoundButtonCompatImpl IMPL = null;

    interface CompoundButtonCompatImpl {
        Drawable getButtonDrawable(CompoundButton compoundButton);

        ColorStateList getButtonTintList(CompoundButton compoundButton);

        Mode getButtonTintMode(CompoundButton compoundButton);

        void setButtonTintList(CompoundButton compoundButton, ColorStateList colorStateList);

        void setButtonTintMode(CompoundButton compoundButton, Mode mode);
    }

    static class BaseCompoundButtonCompat implements CompoundButtonCompatImpl {
        BaseCompoundButtonCompat() {
        }

        public void setButtonTintList(CompoundButton button, ColorStateList tint) {
            CompoundButtonCompatGingerbread.setButtonTintList(button, tint);
        }

        public ColorStateList getButtonTintList(CompoundButton button) {
            return CompoundButtonCompatGingerbread.getButtonTintList(button);
        }

        public void setButtonTintMode(CompoundButton button, Mode tintMode) {
            CompoundButtonCompatGingerbread.setButtonTintMode(button, tintMode);
        }

        public Mode getButtonTintMode(CompoundButton button) {
            return CompoundButtonCompatGingerbread.getButtonTintMode(button);
        }

        public Drawable getButtonDrawable(CompoundButton button) {
            return CompoundButtonCompatGingerbread.getButtonDrawable(button);
        }
    }

    static class LollipopCompoundButtonImpl extends BaseCompoundButtonCompat {
        LollipopCompoundButtonImpl() {
        }

        public void setButtonTintList(CompoundButton button, ColorStateList tint) {
            CompoundButtonCompatLollipop.setButtonTintList(button, tint);
        }

        public ColorStateList getButtonTintList(CompoundButton button) {
            return CompoundButtonCompatLollipop.getButtonTintList(button);
        }

        public void setButtonTintMode(CompoundButton button, Mode tintMode) {
            CompoundButtonCompatLollipop.setButtonTintMode(button, tintMode);
        }

        public Mode getButtonTintMode(CompoundButton button) {
            return CompoundButtonCompatLollipop.getButtonTintMode(button);
        }
    }

    static class Api23CompoundButtonImpl extends LollipopCompoundButtonImpl {
        Api23CompoundButtonImpl() {
        }

        public Drawable getButtonDrawable(CompoundButton button) {
            return CompoundButtonCompatApi23.getButtonDrawable(button);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.widget.CompoundButtonCompat.<clinit>():void
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
        r1 = 23;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r1 = new android.support.v4.widget.CompoundButtonCompat$Api23CompoundButtonImpl;
        r1.<init>();
        IMPL = r1;
    L_0x000e:
        r1 = 21;
        if (r0 < r1) goto L_0x001a;
    L_0x0012:
        r1 = new android.support.v4.widget.CompoundButtonCompat$LollipopCompoundButtonImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
    L_0x001a:
        r1 = new android.support.v4.widget.CompoundButtonCompat$BaseCompoundButtonCompat;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.CompoundButtonCompat.<clinit>():void");
    }

    private CompoundButtonCompat() {
    }

    public static void setButtonTintList(@NonNull CompoundButton button, @Nullable ColorStateList tint) {
        IMPL.setButtonTintList(button, tint);
    }

    @Nullable
    public static ColorStateList getButtonTintList(@NonNull CompoundButton button) {
        return IMPL.getButtonTintList(button);
    }

    public static void setButtonTintMode(@NonNull CompoundButton button, @Nullable Mode tintMode) {
        IMPL.setButtonTintMode(button, tintMode);
    }

    @Nullable
    public static Mode getButtonTintMode(@NonNull CompoundButton button) {
        return IMPL.getButtonTintMode(button);
    }

    @Nullable
    public static Drawable getButtonDrawable(@NonNull CompoundButton button) {
        return IMPL.getButtonDrawable(button);
    }
}
