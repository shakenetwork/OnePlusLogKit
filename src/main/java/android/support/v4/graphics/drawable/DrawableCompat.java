package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class DrawableCompat {
    static final DrawableImpl IMPL = null;

    interface DrawableImpl {
        void applyTheme(Drawable drawable, Theme theme);

        boolean canApplyTheme(Drawable drawable);

        void clearColorFilter(Drawable drawable);

        int getAlpha(Drawable drawable);

        ColorFilter getColorFilter(Drawable drawable);

        int getLayoutDirection(Drawable drawable);

        void inflate(Drawable drawable, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Theme theme) throws IOException, XmlPullParserException;

        boolean isAutoMirrored(Drawable drawable);

        void jumpToCurrentState(Drawable drawable);

        void setAutoMirrored(Drawable drawable, boolean z);

        void setHotspot(Drawable drawable, float f, float f2);

        void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4);

        boolean setLayoutDirection(Drawable drawable, int i);

        void setTint(Drawable drawable, int i);

        void setTintList(Drawable drawable, ColorStateList colorStateList);

        void setTintMode(Drawable drawable, Mode mode);

        Drawable wrap(Drawable drawable);
    }

    static class BaseDrawableImpl implements DrawableImpl {
        BaseDrawableImpl() {
        }

        public void jumpToCurrentState(Drawable drawable) {
        }

        public void setAutoMirrored(Drawable drawable, boolean mirrored) {
        }

        public boolean isAutoMirrored(Drawable drawable) {
            return false;
        }

        public void setHotspot(Drawable drawable, float x, float y) {
        }

        public void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
        }

        public void setTint(Drawable drawable, int tint) {
            DrawableCompatBase.setTint(drawable, tint);
        }

        public void setTintList(Drawable drawable, ColorStateList tint) {
            DrawableCompatBase.setTintList(drawable, tint);
        }

        public void setTintMode(Drawable drawable, Mode tintMode) {
            DrawableCompatBase.setTintMode(drawable, tintMode);
        }

        public Drawable wrap(Drawable drawable) {
            return DrawableCompatBase.wrapForTinting(drawable);
        }

        public boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
            return false;
        }

        public int getLayoutDirection(Drawable drawable) {
            return 0;
        }

        public int getAlpha(Drawable drawable) {
            return 0;
        }

        public void applyTheme(Drawable drawable, Theme t) {
        }

        public boolean canApplyTheme(Drawable drawable) {
            return false;
        }

        public ColorFilter getColorFilter(Drawable drawable) {
            return null;
        }

        public void clearColorFilter(Drawable drawable) {
            drawable.clearColorFilter();
        }

        public void inflate(Drawable drawable, Resources res, XmlPullParser parser, AttributeSet attrs, Theme t) throws IOException, XmlPullParserException {
            DrawableCompatBase.inflate(drawable, res, parser, attrs, t);
        }
    }

    static class HoneycombDrawableImpl extends BaseDrawableImpl {
        HoneycombDrawableImpl() {
        }

        public void jumpToCurrentState(Drawable drawable) {
            DrawableCompatHoneycomb.jumpToCurrentState(drawable);
        }

        public Drawable wrap(Drawable drawable) {
            return DrawableCompatHoneycomb.wrapForTinting(drawable);
        }
    }

    static class JellybeanMr1DrawableImpl extends HoneycombDrawableImpl {
        JellybeanMr1DrawableImpl() {
        }

        public boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
            return DrawableCompatJellybeanMr1.setLayoutDirection(drawable, layoutDirection);
        }

        public int getLayoutDirection(Drawable drawable) {
            int dir = DrawableCompatJellybeanMr1.getLayoutDirection(drawable);
            return dir >= 0 ? dir : 0;
        }
    }

    static class KitKatDrawableImpl extends JellybeanMr1DrawableImpl {
        KitKatDrawableImpl() {
        }

        public void setAutoMirrored(Drawable drawable, boolean mirrored) {
            DrawableCompatKitKat.setAutoMirrored(drawable, mirrored);
        }

        public boolean isAutoMirrored(Drawable drawable) {
            return DrawableCompatKitKat.isAutoMirrored(drawable);
        }

        public Drawable wrap(Drawable drawable) {
            return DrawableCompatKitKat.wrapForTinting(drawable);
        }

        public int getAlpha(Drawable drawable) {
            return DrawableCompatKitKat.getAlpha(drawable);
        }
    }

    static class LollipopDrawableImpl extends KitKatDrawableImpl {
        LollipopDrawableImpl() {
        }

        public void setHotspot(Drawable drawable, float x, float y) {
            DrawableCompatLollipop.setHotspot(drawable, x, y);
        }

        public void setHotspotBounds(Drawable drawable, int left, int top, int right, int bottom) {
            DrawableCompatLollipop.setHotspotBounds(drawable, left, top, right, bottom);
        }

        public void setTint(Drawable drawable, int tint) {
            DrawableCompatLollipop.setTint(drawable, tint);
        }

        public void setTintList(Drawable drawable, ColorStateList tint) {
            DrawableCompatLollipop.setTintList(drawable, tint);
        }

        public void setTintMode(Drawable drawable, Mode tintMode) {
            DrawableCompatLollipop.setTintMode(drawable, tintMode);
        }

        public Drawable wrap(Drawable drawable) {
            return DrawableCompatLollipop.wrapForTinting(drawable);
        }

        public void applyTheme(Drawable drawable, Theme t) {
            DrawableCompatLollipop.applyTheme(drawable, t);
        }

        public boolean canApplyTheme(Drawable drawable) {
            return DrawableCompatLollipop.canApplyTheme(drawable);
        }

        public ColorFilter getColorFilter(Drawable drawable) {
            return DrawableCompatLollipop.getColorFilter(drawable);
        }

        public void clearColorFilter(Drawable drawable) {
            DrawableCompatLollipop.clearColorFilter(drawable);
        }

        public void inflate(Drawable drawable, Resources res, XmlPullParser parser, AttributeSet attrs, Theme t) throws IOException, XmlPullParserException {
            DrawableCompatLollipop.inflate(drawable, res, parser, attrs, t);
        }
    }

    static class MDrawableImpl extends LollipopDrawableImpl {
        MDrawableImpl() {
        }

        public boolean setLayoutDirection(Drawable drawable, int layoutDirection) {
            return DrawableCompatApi23.setLayoutDirection(drawable, layoutDirection);
        }

        public int getLayoutDirection(Drawable drawable) {
            return DrawableCompatApi23.getLayoutDirection(drawable);
        }

        public Drawable wrap(Drawable drawable) {
            return drawable;
        }

        public void clearColorFilter(Drawable drawable) {
            drawable.clearColorFilter();
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.graphics.drawable.DrawableCompat.<clinit>():void
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
        r1 = new android.support.v4.graphics.drawable.DrawableCompat$MDrawableImpl;
        r1.<init>();
        IMPL = r1;
    L_0x000e:
        r1 = 21;
        if (r0 < r1) goto L_0x001a;
    L_0x0012:
        r1 = new android.support.v4.graphics.drawable.DrawableCompat$LollipopDrawableImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
    L_0x001a:
        r1 = 19;
        if (r0 < r1) goto L_0x0026;
        r1 = new android.support.v4.graphics.drawable.DrawableCompat$KitKatDrawableImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        r1 = 17;
        if (r0 < r1) goto L_0x0032;
        r1 = new android.support.v4.graphics.drawable.DrawableCompat$JellybeanMr1DrawableImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        r1 = 11;
        if (r0 < r1) goto L_0x003e;
        r1 = new android.support.v4.graphics.drawable.DrawableCompat$HoneycombDrawableImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        r1 = new android.support.v4.graphics.drawable.DrawableCompat$BaseDrawableImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.drawable.DrawableCompat.<clinit>():void");
    }

    public static void jumpToCurrentState(@NonNull Drawable drawable) {
        IMPL.jumpToCurrentState(drawable);
    }

    public static void setAutoMirrored(@NonNull Drawable drawable, boolean mirrored) {
        IMPL.setAutoMirrored(drawable, mirrored);
    }

    public static boolean isAutoMirrored(@NonNull Drawable drawable) {
        return IMPL.isAutoMirrored(drawable);
    }

    public static void setHotspot(@NonNull Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }

    public static void setHotspotBounds(@NonNull Drawable drawable, int left, int top, int right, int bottom) {
        IMPL.setHotspotBounds(drawable, left, top, right, bottom);
    }

    public static void setTint(@NonNull Drawable drawable, @ColorInt int tint) {
        IMPL.setTint(drawable, tint);
    }

    public static void setTintList(@NonNull Drawable drawable, @Nullable ColorStateList tint) {
        IMPL.setTintList(drawable, tint);
    }

    public static void setTintMode(@NonNull Drawable drawable, @Nullable Mode tintMode) {
        IMPL.setTintMode(drawable, tintMode);
    }

    public static int getAlpha(@NonNull Drawable drawable) {
        return IMPL.getAlpha(drawable);
    }

    public static void applyTheme(@NonNull Drawable drawable, @NonNull Theme t) {
        IMPL.applyTheme(drawable, t);
    }

    public static boolean canApplyTheme(@NonNull Drawable drawable) {
        return IMPL.canApplyTheme(drawable);
    }

    public static ColorFilter getColorFilter(@NonNull Drawable drawable) {
        return IMPL.getColorFilter(drawable);
    }

    public static void clearColorFilter(@NonNull Drawable drawable) {
        IMPL.clearColorFilter(drawable);
    }

    public static void inflate(@NonNull Drawable drawable, @NonNull Resources res, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Theme theme) throws XmlPullParserException, IOException {
        IMPL.inflate(drawable, res, parser, attrs, theme);
    }

    public static Drawable wrap(@NonNull Drawable drawable) {
        return IMPL.wrap(drawable);
    }

    public static <T extends Drawable> T unwrap(@NonNull Drawable drawable) {
        if (drawable instanceof DrawableWrapper) {
            return ((DrawableWrapper) drawable).getWrappedDrawable();
        }
        return drawable;
    }

    public static boolean setLayoutDirection(@NonNull Drawable drawable, int layoutDirection) {
        return IMPL.setLayoutDirection(drawable, layoutDirection);
    }

    public static int getLayoutDirection(@NonNull Drawable drawable) {
        return IMPL.getLayoutDirection(drawable);
    }

    private DrawableCompat() {
    }
}
