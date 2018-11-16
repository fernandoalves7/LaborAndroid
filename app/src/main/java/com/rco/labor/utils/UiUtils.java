package com.rco.labor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/***
 *
 */
public class UiUtils {
    /***
     *
     * @param values
     * @return
     */
    public static boolean isNullOrWhitespacesAll(EditText... values) {
        for (EditText v : values)
            if (!isNullOrWhitespaces(v))
                return false;

        return true;
    }

    /***
     *
     * @param values
     * @return
     */
    public static boolean isNullOrWhitespacesAny(EditText... values) {
        for (EditText v : values)
            if (isNullOrWhitespaces(v))
                return true;

        return false;
    }

    /***
     *
     * @param editText
     * @return
     */
    public static boolean isNullOrWhitespaces(EditText editText) {
        return StringUtils.isNullOrWhitespaces(editText.getText().toString());
    }

    /***
     *
     * @param ctx
     * @param message
     */
    public static void showToast(Context ctx, String message) {
        try {
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param ctx
     * @param layoutResId
     * @return
     */
    public static AlertDialog showCustomLayoutDialog(Context ctx, int layoutResId, int customDialogStyleId) {
        try {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View v = inflater.inflate(layoutResId, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx, customDialogStyleId);
            builder.setView(v);

            AlertDialog alert = builder.create();

            if (alert == null || !alert.isShowing()) {
                alert = builder.create();
                alert.show();
            }

            return alert;
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();

            return null;
        }
    }

    /***
     *
     * @param ctx
     * @param title
     * @param message
     * @return
     */
    public static AlertDialog showExclamationDialog(Context ctx, String title, String message) {
        return showOkDialog(ctx, title, android.R.drawable.ic_dialog_alert, message,  false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.cancel();
                } catch (Throwable t) {
                    if (t != null)
                        t.printStackTrace();
                }
            }
        });
    }

    /***
     *
     * @param ctx
     * @param title
     * @param message
     * @param onClickListener
     * @return
     */
    public static AlertDialog showExclamationDialog(Context ctx, String title, String message, DialogInterface.OnClickListener onClickListener) {
        return showOkDialog(ctx, title, android.R.drawable.ic_dialog_alert, message,  false, onClickListener);
    }

    /***
     *
     * @param ctx
     * @param title
     * @param iconId
     * @param message
     * @param isCancellable
     * @param onClickListener
     * @return
     */
    public static AlertDialog showOkDialog(Context ctx, String title, int iconId, String message, boolean isCancellable, DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);

            alertBuilder.setTitle(title);
            alertBuilder.setIcon(iconId);
            alertBuilder.setMessage(message);
            alertBuilder.setCancelable(isCancellable);
            alertBuilder.setNeutralButton("Ok", onClickListener);

            AlertDialog alert = null;

            if (alert == null || !alert.isShowing()) {
                alert = alertBuilder.create();
                alert.show();
            }

            return alert;
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();

            return null;
        }
    }

    /***
     *
     * @param a
     * @param resId
     * @param onClickListener
     */
    public static void setOnClickListener(Activity a, int resId, View.OnClickListener onClickListener) {
        try {
            a.findViewById(resId).setOnClickListener(onClickListener);
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param a
     * @param resId
     * @param imageResourceId
     * @param onClickListener
     */
    public static ImageView setImageViewResource(Activity a, int resId, int imageResourceId, View.OnClickListener onClickListener) {
        try {
            ImageView img = (ImageView) a.findViewById(resId);
            img.setImageResource(imageResourceId);

            if (onClickListener != null)
                img.setOnClickListener(onClickListener);

            return img;
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }

        return null;
    }

    /***
     *
     * @param a
     * @param resId
     * @param imageResourceId
     */
    public static void setImageViewResource(Activity a, int resId, int imageResourceId) {
        setImageViewResource(a, resId, imageResourceId, null);
    }

    /***
     *
     * @param a
     * @param resId
     * @param value
     */
    public static void setTextView(Activity a, int resId, String value) {
        try {
            if (a == null)
                return;

            TextView textView = (TextView) a.findViewById(resId);

            if (textView != null)
                textView.setText(value != null ? value : "-");
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param v
     * @param resId
     * @param onClickListener
     */
    public static void setTextViewOnClickListener(View v, int resId, View.OnClickListener onClickListener) {
        try {
            if (v == null)
                return;

            TextView textView = (TextView) v.findViewById(resId);

            if (textView != null)
                textView.setOnClickListener(onClickListener);
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param a
     * @param resIds
     */
    public static void setViewVisibilityGone(Activity a, int... resIds) {
        try {
            if (resIds == null)
                return;

            for (int resId : resIds)
                setViewVisibility(a, resId, View.GONE);
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param a
     * @param resIds
     */
    public static void setViewVisibilityVisible(Activity a, int... resIds) {
        if (resIds == null)
            return;

        for (int resId : resIds)
            setViewVisibility(a, resId, View.VISIBLE);
    }

    /***
     *
     * @param a
     * @param resIds
     */
    public static void setViewVisibilityInvisible(Activity a, int... resIds) {
        if (resIds == null)
            return;

        for (int resId : resIds)
            setViewVisibility(a, resId, View.INVISIBLE);
    }

    /***
     *
     * @param a
     * @param resId
     * @param visibility
     */
    public static void setViewVisibility(Activity a, int resId, int visibility) {
        try {
            if (a == null || a.findViewById(resId) == null)
                return;

            a.findViewById(resId).setVisibility(visibility);
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param v
     * @param resId
     * @param visibility
     */
    public static void setViewVisibility(View v, int resId, int visibility) {
        try {
            if (v == null || v.findViewById(resId) == null)
                return;

            v.findViewById(resId).setVisibility(visibility);
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();
        }
    }

    /***
     *
     * @param context
     * @param defaultValue
     * @return
     */
    public static boolean isLargeScreen(Context context, boolean defaultValue) {
        try {
            //return false;
            return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        } catch (Throwable t) {
            if (t != null)
                t.printStackTrace();

            return defaultValue;
        }
    }

    /***
     *
     * @param ctx
     * @param pixelValue
     * @return
     */
    public static int getPixelFromDip(Context ctx, int pixelValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelValue, ctx.getResources().getDisplayMetrics());
    }

    /***
     *
     * @param ctx
     * @param pixelValue
     * @return
     */
    public static int getPixelFromSp(Context ctx, int pixelValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pixelValue, ctx.getResources().getDisplayMetrics());
    }
}
