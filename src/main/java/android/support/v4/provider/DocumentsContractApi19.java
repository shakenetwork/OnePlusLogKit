package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;

class DocumentsContractApi19 {
    private static final String TAG = "DocumentFile";

    DocumentsContractApi19() {
    }

    public static boolean isDocumentUri(Context context, Uri self) {
        return DocumentsContract.isDocumentUri(context, self);
    }

    public static String getName(Context context, Uri self) {
        return queryForString(context, self, "_display_name", null);
    }

    private static String getRawType(Context context, Uri self) {
        return queryForString(context, self, "mime_type", null);
    }

    public static String getType(Context context, Uri self) {
        String rawType = getRawType(context, self);
        if ("vnd.android.document/directory".equals(rawType)) {
            return null;
        }
        return rawType;
    }

    public static boolean isDirectory(Context context, Uri self) {
        return "vnd.android.document/directory".equals(getRawType(context, self));
    }

    public static boolean isFile(Context context, Uri self) {
        String type = getRawType(context, self);
        if ("vnd.android.document/directory".equals(type) || TextUtils.isEmpty(type)) {
            return false;
        }
        return true;
    }

    public static long lastModified(Context context, Uri self) {
        return queryForLong(context, self, "last_modified", 0);
    }

    public static long length(Context context, Uri self) {
        return queryForLong(context, self, "_size", 0);
    }

    public static boolean canRead(Context context, Uri self) {
        return context.checkCallingOrSelfUriPermission(self, 1) == 0 && !TextUtils.isEmpty(getRawType(context, self));
    }

    public static boolean canWrite(Context context, Uri self) {
        if (context.checkCallingOrSelfUriPermission(self, 2) != 0) {
            return false;
        }
        String type = getRawType(context, self);
        int flags = queryForInt(context, self, "flags", 0);
        if (TextUtils.isEmpty(type)) {
            return false;
        }
        if ((flags & 4) != 0) {
            return true;
        }
        if (!"vnd.android.document/directory".equals(type) || (flags & 8) == 0) {
            return (TextUtils.isEmpty(type) || (flags & 2) == 0) ? false : true;
        } else {
            return true;
        }
    }

    public static boolean delete(Context context, Uri self) {
        return DocumentsContract.deleteDocument(context.getContentResolver(), self);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean exists(android.content.Context r10, android.net.Uri r11) {
        /*
        r8 = 1;
        r9 = 0;
        r0 = r10.getContentResolver();
        r6 = 0;
        r1 = 1;
        r2 = new java.lang.String[r1];	 Catch:{ Exception -> 0x0025 }
        r1 = "document_id";
        r3 = 0;
        r2[r3] = r1;	 Catch:{ Exception -> 0x0025 }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r1 = r11;
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x0025 }
        r1 = r6.getCount();	 Catch:{ Exception -> 0x0025 }
        if (r1 <= 0) goto L_0x0023;
    L_0x001e:
        r1 = r8;
    L_0x001f:
        closeQuietly(r6);
        return r1;
    L_0x0023:
        r1 = r9;
        goto L_0x001f;
    L_0x0025:
        r7 = move-exception;
        r1 = "DocumentFile";
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0044 }
        r2.<init>();	 Catch:{ all -> 0x0044 }
        r3 = "Failed query: ";
        r2 = r2.append(r3);	 Catch:{ all -> 0x0044 }
        r2 = r2.append(r7);	 Catch:{ all -> 0x0044 }
        r2 = r2.toString();	 Catch:{ all -> 0x0044 }
        android.util.Log.w(r1, r2);	 Catch:{ all -> 0x0044 }
        closeQuietly(r6);
        return r9;
    L_0x0044:
        r1 = move-exception;
        closeQuietly(r6);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.DocumentsContractApi19.exists(android.content.Context, android.net.Uri):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String queryForString(android.content.Context r8, android.net.Uri r9, java.lang.String r10, java.lang.String r11) {
        /*
        r0 = r8.getContentResolver();
        r6 = 0;
        r1 = 1;
        r2 = new java.lang.String[r1];	 Catch:{ Exception -> 0x002d }
        r1 = 0;
        r2[r1] = r10;	 Catch:{ Exception -> 0x002d }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r1 = r9;
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x002d }
        r1 = r6.moveToFirst();	 Catch:{ Exception -> 0x002d }
        if (r1 == 0) goto L_0x0020;
    L_0x0019:
        r1 = 0;
        r1 = r6.isNull(r1);	 Catch:{ Exception -> 0x002d }
        if (r1 == 0) goto L_0x0024;
    L_0x0020:
        closeQuietly(r6);
        return r11;
    L_0x0024:
        r1 = 0;
        r1 = r6.getString(r1);	 Catch:{ Exception -> 0x002d }
        closeQuietly(r6);
        return r1;
    L_0x002d:
        r7 = move-exception;
        r1 = "DocumentFile";
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004c }
        r2.<init>();	 Catch:{ all -> 0x004c }
        r3 = "Failed query: ";
        r2 = r2.append(r3);	 Catch:{ all -> 0x004c }
        r2 = r2.append(r7);	 Catch:{ all -> 0x004c }
        r2 = r2.toString();	 Catch:{ all -> 0x004c }
        android.util.Log.w(r1, r2);	 Catch:{ all -> 0x004c }
        closeQuietly(r6);
        return r11;
    L_0x004c:
        r1 = move-exception;
        closeQuietly(r6);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.DocumentsContractApi19.queryForString(android.content.Context, android.net.Uri, java.lang.String, java.lang.String):java.lang.String");
    }

    private static int queryForInt(Context context, Uri self, String column, int defaultValue) {
        return (int) queryForLong(context, self, column, (long) defaultValue);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long queryForLong(android.content.Context r9, android.net.Uri r10, java.lang.String r11, long r12) {
        /*
        r0 = r9.getContentResolver();
        r6 = 0;
        r1 = 1;
        r2 = new java.lang.String[r1];	 Catch:{ Exception -> 0x002d }
        r1 = 0;
        r2[r1] = r11;	 Catch:{ Exception -> 0x002d }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r1 = r10;
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x002d }
        r1 = r6.moveToFirst();	 Catch:{ Exception -> 0x002d }
        if (r1 == 0) goto L_0x0020;
    L_0x0019:
        r1 = 0;
        r1 = r6.isNull(r1);	 Catch:{ Exception -> 0x002d }
        if (r1 == 0) goto L_0x0024;
    L_0x0020:
        closeQuietly(r6);
        return r12;
    L_0x0024:
        r1 = 0;
        r2 = r6.getLong(r1);	 Catch:{ Exception -> 0x002d }
        closeQuietly(r6);
        return r2;
    L_0x002d:
        r7 = move-exception;
        r1 = "DocumentFile";
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004c }
        r2.<init>();	 Catch:{ all -> 0x004c }
        r3 = "Failed query: ";
        r2 = r2.append(r3);	 Catch:{ all -> 0x004c }
        r2 = r2.append(r7);	 Catch:{ all -> 0x004c }
        r2 = r2.toString();	 Catch:{ all -> 0x004c }
        android.util.Log.w(r1, r2);	 Catch:{ all -> 0x004c }
        closeQuietly(r6);
        return r12;
    L_0x004c:
        r1 = move-exception;
        closeQuietly(r6);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.DocumentsContractApi19.queryForLong(android.content.Context, android.net.Uri, java.lang.String, long):long");
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }
}
