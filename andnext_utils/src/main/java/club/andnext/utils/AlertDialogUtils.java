package club.andnext.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtils {

    public static final AlertDialog showMessage(Context context, CharSequence title, CharSequence msg) {
        AlertDialog dialog = showMessage(context, title, msg, null);
        return dialog;
    }

    public static final AlertDialog showMessage(Context context, CharSequence title, CharSequence msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setPositiveButton(android.R.string.ok, listener);
        return builder.show();
    }

    public static final AlertDialog showConfirm(Context context,
                                                CharSequence title, CharSequence msg,
                                                CharSequence negativeButton, CharSequence positiveButton,
                                                DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setNegativeButton(negativeButton, listener);
        builder.setPositiveButton(positiveButton, listener);

        return builder.show();
    }
}
