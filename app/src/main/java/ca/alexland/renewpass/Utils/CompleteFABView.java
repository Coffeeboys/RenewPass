package ca.alexland.renewpass.Utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ca.alexland.renewpass.R;

/**
 * This view represents the fake FAB that will be displayed at the end of the animation.
 *
 * @author Jorge Castillo Pérez
 * @author Alex Land
 */
public class CompleteFABView extends FrameLayout {

    private final int RESET_DELAY = 3000;

    private Drawable iconDrawable;
    private int arcColor;
    private boolean viewsAdded;

    public CompleteFABView(Context context, Drawable iconDrawable, int arcColor) {
        super(context);
        this.iconDrawable = iconDrawable;
        this.arcColor = arcColor;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.complete_fab, this);
    }

    private void tintCompleteFabWithArcColor() {
        Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.oval_complete);
        background.setColorFilter(arcColor, PorterDuff.Mode.SRC_ATOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.completeFabRoot).setBackground(background);
        } else {
            findViewById(R.id.completeFabRoot).setBackgroundDrawable(background);
        }
    }

    private void setIcon() {
        ImageView iconView = (ImageView) findViewById(R.id.completeFabIcon);
        iconView.setImageDrawable(
                iconDrawable != null ? iconDrawable : ContextCompat.getDrawable(getContext(), R.drawable.ic_done));
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!viewsAdded) {
            setupContentSize();
            tintCompleteFabWithArcColor();
            setIcon();
            viewsAdded = true;
        }
    }

    private void setupContentSize() {
        int contentSize = (int) getResources().getDimension(R.dimen.fab_width);
        int mContentPadding = (getChildAt(0).getMeasuredWidth() - contentSize) / 2;
        getChildAt(0).setPadding(mContentPadding, mContentPadding, mContentPadding, mContentPadding);
    }

    public void animate(AnimatorSet progressArcAnimator) {
        animate(progressArcAnimator, false);
    }

    private void animate(AnimatorSet progressArcAnimator, boolean inverse) {
        ValueAnimator completeFabAnim = ObjectAnimator.ofFloat(getChildAt(0), "alpha", inverse ? 0 : 1);
        completeFabAnim.setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());

        View icon = findViewById(R.id.completeFabIcon);

        ValueAnimator iconScaleAnimX = ObjectAnimator.ofFloat(icon, "scaleX", 0, 1);
        ValueAnimator iconScaleAnimY = ObjectAnimator.ofFloat(icon, "scaleY", 0, 1);

        Interpolator iconAnimInterpolator = new LinearInterpolator();
        iconScaleAnimX.setDuration(250).setInterpolator(iconAnimInterpolator);
        iconScaleAnimY.setDuration(250).setInterpolator(iconAnimInterpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        if (inverse) {
            animatorSet.playTogether(completeFabAnim);
        } else {
            animatorSet.playTogether(completeFabAnim, progressArcAnimator, iconScaleAnimX,
                    iconScaleAnimY);
        }

        if (inverse) {
            animatorSet.setStartDelay(RESET_DELAY);
        }
        animatorSet.start();
    }

    public void reset() {
        animate(null, true);
    }

    /**
     * This view must block every touch event so the user cannot click on fab anymore if this view
     * is visible.
     */
    @Override public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
