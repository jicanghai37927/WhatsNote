package club.andnext.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TextViewUtils {

    public static final boolean canUndo(TextView view) {

        try {
            Method method = TextView.class.getDeclaredMethod("canUndo");
            method.setAccessible(true);
            boolean result = (Boolean)method.invoke(view);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static final void undo(TextView view) {
        int id = android.R.id.undo;

        view.onTextContextMenuItem(id);
    }

    public static final boolean canRedo(TextView view) {

        try {
            Method method = TextView.class.getDeclaredMethod("canRedo");
            method.setAccessible(true);
            boolean result = (Boolean)method.invoke(view);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static final void redo(TextView view) {
        int id = android.R.id.redo;

        view.onTextContextMenuItem(id);
    }

    public static final void forgetUndoRedo(TextView view) {

        try {

            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(view);

            Class<?> clazz = editor.getClass();
            Method method = clazz.getDeclaredMethod("forgetUndoRedo");
            method.setAccessible(true);

            method.invoke(editor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable[] getCursorDrawable(TextView view) {
        Drawable[] drawables = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                Field fEditor = TextView.class.getDeclaredField("mEditor");
                fEditor.setAccessible(true);
                Object editor = fEditor.get(view);

                Class<?> clazz = editor.getClass();
                Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
                fCursorDrawable.setAccessible(true);

                Object cursorDrawable = fCursorDrawable.get(editor);
                drawables = (Drawable[]) cursorDrawable;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Field fEditor = TextView.class.getDeclaredField("mEditor");
                fEditor.setAccessible(true);
                Object editor = fEditor.get(view);

                Class<?> clazz = editor.getClass();
                Field fCursorDrawable = clazz.getDeclaredField("mDrawableForCursor");
                fCursorDrawable.setAccessible(true);

                Object cursorDrawable = fCursorDrawable.get(editor);
                Drawable d = (Drawable) cursorDrawable;

                if (d != null) {
                    drawables = new Drawable[] { d };
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return drawables;
    }

    public static void setCursorDrawableColor(TextView view, int color) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                Field fCursorDrawableRes =
                        TextView.class.getDeclaredField("mCursorDrawableRes");
                fCursorDrawableRes.setAccessible(true);
                int mCursorDrawableRes = fCursorDrawableRes.getInt(view);

                Field fEditor = TextView.class.getDeclaredField("mEditor");
                fEditor.setAccessible(true);
                Object editor = fEditor.get(view);
                Class<?> clazz = editor.getClass();
                Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
                fCursorDrawable.setAccessible(true);

                Drawable[] drawables = new Drawable[2];
                Resources res = view.getContext().getResources();

                drawables[0] = res.getDrawable(mCursorDrawableRes, null);
                drawables[1] = res.getDrawable(mCursorDrawableRes, null);

                drawables[0].setTint(color);
                drawables[1].setTint(color);

                fCursorDrawable.set(editor, drawables);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            try {
                Field fCursorDrawableRes =
                        TextView.class.getDeclaredField("mCursorDrawableRes");
                fCursorDrawableRes.setAccessible(true);
                int mCursorDrawableRes = fCursorDrawableRes.getInt(view);

                Field fEditor = TextView.class.getDeclaredField("mEditor");
                fEditor.setAccessible(true);

                Object editor = fEditor.get(view);
                Class<?> clazz = editor.getClass();
                Field fCursorDrawable = clazz.getDeclaredField("mDrawableForCursor");
                fCursorDrawable.setAccessible(true);

                Resources res = view.getContext().getResources();
                Drawable drawable = res.getDrawable(mCursorDrawableRes, null);
                drawable.setTint(color);

                fCursorDrawable.set(editor, drawable);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void fitCursor(TextView view) {
        Drawable[] d = getCursorDrawable(view);
        if (d == null || d.length == 0) {
            return;
        }

        float textHeight;
        float lineHeight;

        {
            TextPaint paint = view.getPaint();

            float add = view.getLineSpacingExtra();
            float mult = view.getLineSpacingMultiplier();

            textHeight = paint.getFontMetricsInt(null);
            lineHeight = textHeight * mult + add;
        }

        for (Drawable drawable : d) {
            if (drawable == null) {
                continue;
            }

            if (!(drawable instanceof GradientDrawable)) {
                continue;
            }


            GradientDrawable gd = (GradientDrawable)drawable;
            Rect rect = getPadding(gd);
            if (rect == null) {
                continue;
            }

            float offset = lineHeight - textHeight;

            offset = -offset;
            rect.bottom = (int)offset;
        }
    }

    static Rect getPadding(GradientDrawable drawable) {
        Rect rect = null;

        try {
            Field fEditor = GradientDrawable.class.getDeclaredField("mPadding");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(drawable);

            rect = (Rect)editor;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rect;
    }
}
