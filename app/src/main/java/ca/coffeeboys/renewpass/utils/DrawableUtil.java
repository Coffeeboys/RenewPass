package ca.coffeeboys.renewpass.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by Trevor on 1/9/2016.
 */
public class DrawableUtil {
    public static void tint(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
    }
}
