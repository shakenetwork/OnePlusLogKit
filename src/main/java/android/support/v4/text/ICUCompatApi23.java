package android.support.v4.text;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

class ICUCompatApi23 {
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod = "addLikelySubtags";

    ICUCompatApi23() {
    }

    static {
        try {
            Class<?> clazz = Class.forName("libcore.icu.ICU");
            new Class[1][0] = Locale.class;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String maximizeAndGetScript(Locale locale) {
        try {
            return ((Locale) sAddLikelySubtagsMethod.invoke(null, new Object[]{locale})).getScript();
        } catch (InvocationTargetException e) {
            Log.w(TAG, e);
            return locale.getScript();
        } catch (IllegalAccessException e2) {
            Log.w(TAG, e2);
            return locale.getScript();
        }
    }
}
