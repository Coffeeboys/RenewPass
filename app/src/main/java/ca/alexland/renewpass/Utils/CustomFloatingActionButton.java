package ca.alexland.renewpass.Utils;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import ca.alexland.renewpass.R;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Wrapper for Floating Action Button with progress bar and animation helper functions
 */
public class CustomFloatingActionButton extends FrameLayout {
    private FloatingActionButton fab;
    private MaterialProgressBar fabProgressBar;
    private Drawable completeIcon;

    public CustomFloatingActionButton(Context context) {
        this(context, null);
    }

    public CustomFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public CustomFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        inflate();
        initAttributes(attributeSet, defStyleAttr, defStyleRes);
    }

    private void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_fab, this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabProgressBar = (MaterialProgressBar) findViewById(R.id.fabProgressBar);
    }

    private void initAttributes(AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        if (attributeSet == null) {
            return;
        }
        TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.LoadingFab,
                defStyleAttr, defStyleRes);

        //TODO: move the tinting of this icon outside the view so that the icon color can be set by the user of the view from xml or code
        completeIcon = styledAttributes.getDrawable(R.styleable.LoadingFab_completeIcon);
        if (completeIcon != null) {
            tintDrawable(completeIcon, Color.WHITE);
        }

        int progressColor = styledAttributes.getColor(
                R.styleable.LoadingFab_progressColor,
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        tintDrawable(fabProgressBar.getDrawable(), progressColor);

        Drawable fabIcon = styledAttributes.getDrawable(R.styleable.LoadingFab_fabIcon);
        if (fabIcon != null) {
            fab.setImageDrawable(fabIcon);
        }
    }

    private void tintDrawable(Drawable fabIcon, int color) {
        fabIcon = DrawableCompat.wrap(fabIcon);
        DrawableCompat.setTint(fabIcon, color);
        fabIcon = DrawableCompat.unwrap(fabIcon);
    }

    public void setOnClickListener(View.OnClickListener fabListener) {
        this.fab.setOnClickListener(fabListener);
    }

    public void startLoading() {
        fabProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopLoading() {
        fabProgressBar.setVisibility(View.INVISIBLE);

        CompleteFABView completeFABView = new CompleteFABView(getContext(), completeIcon, ContextCompat.getColor(getContext(), R.color.colorSuccess));
        ViewCompat.setElevation(completeFABView, ViewCompat.getElevation(fab) + 1);

        int fabSize = getResources().getDimensionPixelSize(R.dimen.fab_size);
        addView(completeFABView, new FrameLayout.LayoutParams(fabSize, fabSize, Gravity.CENTER));

        completeFABView.animate(new AnimatorSet());
    }
}
