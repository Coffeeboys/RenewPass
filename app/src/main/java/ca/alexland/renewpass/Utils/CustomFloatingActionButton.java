package ca.alexland.renewpass.Utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Wrapper for Floating Action Button with progress bar and animation helper functions
 */
public class CustomFloatingActionButton {
    FloatingActionButton fab;
    MaterialProgressBar fabProgressBar;

    public CustomFloatingActionButton(FloatingActionButton fab, Drawable fabIcon, MaterialProgressBar fabProgressBar) {
        this.fab = fab;
        fabIcon = DrawableCompat.wrap(fabIcon);
        DrawableCompat.setTint(fabIcon, Color.WHITE);
        this.fabProgressBar = fabProgressBar;
    }

    public void setOnClickListener(View.OnClickListener fabListener) {
        this.fab.setOnClickListener(fabListener);
    }

    public void startLoading() {
        fabProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        fabProgressBar.setVisibility(View.INVISIBLE);
    }
}
