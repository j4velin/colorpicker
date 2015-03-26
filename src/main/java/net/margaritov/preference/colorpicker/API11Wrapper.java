package net.margaritov.preference.colorpicker;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class API11Wrapper {

    public static void copy(final Context c, final String text) {
        ClipboardManager cm = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(text, text));
    }

    public static String paste(final Context c) {
        String re = null;
        ClipboardManager cm = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm.hasPrimaryClip() && cm.getPrimaryClip().getItemCount() > 0) {
            re = cm.getPrimaryClip().getItemAt(0).getText().toString();
        }
        return re;
    }
}
