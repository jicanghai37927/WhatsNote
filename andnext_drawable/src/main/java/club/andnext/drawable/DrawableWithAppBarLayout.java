package club.andnext.drawable;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.appbar.AppBarLayout;

public class DrawableWithAppBarLayout extends ColorDrawable implements AppBarLayout.OnOffsetChangedListener{

    AppBarLayout layout;
    int fromColor;
    int toColor;

    public DrawableWithAppBarLayout(AppBarLayout layout, int from, int to) {
        this.layout = layout;

        this.fromColor = from;
        this.toColor = to;

        layout.addOnOffsetChangedListener(this);

        this.setColor(fromColor);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int height = appBarLayout.getHeight();
        if (height <= 0) {
            return;
        }

        i = Math.abs(i);

        int a1 = Color.alpha(fromColor);
        int r1 = Color.red(fromColor);
        int g1 = Color.green(fromColor);
        int b1 = Color.blue(fromColor);

        int a2 = Color.alpha(toColor);
        int r2 = Color.red(toColor);
        int g2 = Color.green(toColor);
        int b2 = Color.blue(toColor);

        int a = a1 + (a2 - a1) * i / height;
        int r = r1 + (r2 - r1) * i / height;
        int g = g1 + (g2 - g1) * i / height;
        int b = b1 + (b2 - b1) * i / height;

        this.setColor(Color.argb(a, r, g, b));

    }
}
