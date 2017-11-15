package android.support.v4.text;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import java.util.Locale;

public final class TextUtilsCompat {
    static String ARAB_SCRIPT_SUBTAG = "Arab";
    static String HEBR_SCRIPT_SUBTAG = "Hebr";
    private static final TextUtilsCompatImpl IMPL;
    public static final Locale ROOT = new Locale("", "");

    private static class TextUtilsCompatImpl {
        TextUtilsCompatImpl() {
        }

        @NonNull
        public String htmlEncode(@NonNull String s) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case MotionEventCompat.AXIS_GENERIC_3 /*34*/:
                        sb.append("&quot;");
                        break;
                    case MotionEventCompat.AXIS_GENERIC_7 /*38*/:
                        sb.append("&amp;");
                        break;
                    case MotionEventCompat.AXIS_GENERIC_8 /*39*/:
                        sb.append("&#39;");
                        break;
                    case '<':
                        sb.append("&lt;");
                        break;
                    case '>':
                        sb.append("&gt;");
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        }

        public int getLayoutDirectionFromLocale(@Nullable Locale locale) {
            if (!(locale == null || locale.equals(TextUtilsCompat.ROOT))) {
                String scriptSubtag = ICUCompat.maximizeAndGetScript(locale);
                if (scriptSubtag == null) {
                    return getLayoutDirectionFromFirstChar(locale);
                }
                if (scriptSubtag.equalsIgnoreCase(TextUtilsCompat.ARAB_SCRIPT_SUBTAG) || scriptSubtag.equalsIgnoreCase(TextUtilsCompat.HEBR_SCRIPT_SUBTAG)) {
                    return 1;
                }
            }
            return 0;
        }

        private static int getLayoutDirectionFromFirstChar(@NonNull Locale locale) {
            switch (Character.getDirectionality(locale.getDisplayName(locale).charAt(0))) {
                case (byte) 1:
                case (byte) 2:
                    return 1;
                default:
                    return 0;
            }
        }
    }

    private static class TextUtilsCompatJellybeanMr1Impl extends TextUtilsCompatImpl {
        TextUtilsCompatJellybeanMr1Impl() {
        }

        @NonNull
        public String htmlEncode(@NonNull String s) {
            return TextUtilsCompatJellybeanMr1.htmlEncode(s);
        }

        public int getLayoutDirectionFromLocale(@Nullable Locale locale) {
            return TextUtilsCompatJellybeanMr1.getLayoutDirectionFromLocale(locale);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: java.lang.OutOfMemoryError: Java heap space
	at java.base/java.util.Arrays.copyOf(Arrays.java:3688)
	at java.base/java.util.ArrayList.grow(ArrayList.java:236)
	at java.base/java.util.ArrayList.grow(ArrayList.java:241)
	at java.base/java.util.ArrayList.add(ArrayList.java:466)
	at java.base/java.util.ArrayList.add(ArrayList.java:479)
	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:146)
	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:94)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 17;
        if (r0 < r1) goto L_0x0025;
    L_0x0006:
        r1 = new android.support.v4.text.TextUtilsCompat$TextUtilsCompatJellybeanMr1Impl;
        r1.<init>();
        IMPL = r1;
    L_0x000d:
        r1 = new java.util.Locale;
        r2 = "";
        r3 = "";
        r1.<init>(r2, r3);
        ROOT = r1;
        r1 = "Arab";
        ARAB_SCRIPT_SUBTAG = r1;
        r1 = "Hebr";
        HEBR_SCRIPT_SUBTAG = r1;
    L_0x0025:
        r1 = new android.support.v4.text.TextUtilsCompat$TextUtilsCompatImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.text.TextUtilsCompat.<clinit>():void");
    }

    @NonNull
    public static String htmlEncode(@NonNull String s) {
        return IMPL.htmlEncode(s);
    }

    public static int getLayoutDirectionFromLocale(@Nullable Locale locale) {
        return IMPL.getLayoutDirectionFromLocale(locale);
    }

    private TextUtilsCompat() {
    }
}
