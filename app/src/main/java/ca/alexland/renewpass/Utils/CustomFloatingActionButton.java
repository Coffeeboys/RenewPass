package ca.alexland.renewpass.Utils;

import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import ca.alexland.renewpass.R;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Wrapper for Floating Action Button with progress bar and animation helper functions
 */
public class CustomFloatingActionButton {
    FloatingActionButton fab;
    MaterialProgressBar fabProgressBar;
    Drawable fabIcon;
    FrameLayout fabRoot;

    public CustomFloatingActionButton(FloatingActionButton fab, Drawable fabIcon, MaterialProgressBar fabProgressBar, FrameLayout fabRoot) {
        this.fab = fab;
        this.fabIcon = fabIcon;
        tintIconWhite(fabIcon);
        this.fabProgressBar = fabProgressBar;
        this.fabRoot = fabRoot;
    }

    private void tintIconWhite(Drawable fabIcon) {
        fabIcon = DrawableCompat.wrap(fabIcon);
        DrawableCompat.setTint(fabIcon, Color.WHITE);
        fabIcon = DrawableCompat.unwrap(fabIcon);
    }

    public void setOnClickListener(View.OnClickListener fabListener) {
        this.fab.setOnClickListener(fabListener);
    }

    public void startLoading() {
        fabProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopLoading(View view) {
        fabProgressBar.setVisibility(View.INVISIBLE);

        Drawable doneIcon = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_done);
        tintIconWhite(doneIcon);
        CompleteFABView completeFABView = new CompleteFABView(view.getContext(), doneIcon, ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));

        ViewCompat.setElevation(completeFABView, ViewCompat.getElevation(fabRoot.getChildAt(1)) + 1);

        int fabWidth = view.getResources().getDimensionPixelSize(R.dimen.fab_width) - 75;
        int fabHeight = view.getResources().getDimensionPixelSize(R.dimen.fab_height) - 75;
        fabRoot.addView(completeFABView, new FrameLayout.LayoutParams(fabWidth, fabHeight, Gravity.CENTER));

        completeFABView.animate(new AnimatorSet());
    }
}
