package com.moetutu.acg12.view.gamerefreshview;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moetutu.acg12.R;

/**
 * Created by Hitomis on 2016/3/1.
 */
public class FunGameHeader extends FrameLayout {

    private Context mContext;

    private int headerType;

    private FunGameView funGameView;

    private RelativeLayout curtainReLayout, maskReLayout;

    private TextView topMaskView, bottomMaskView;

    private int halfHitBlockHeight;

    private boolean isStart = false;


    public FunGameHeader(Context context) {
        this(context, null);
    }

    public FunGameHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public FunGameHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FunGameHeader);
        headerType = typedArray.getInt(R.styleable.FunGameHeader_game_type, FunGameFactory.HITBLOCK);
        typedArray.recycle();

        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        funGameView = FunGameFactory.createFunGameView(mContext, attrs, headerType);
        funGameView.postStatus(FunGameView.STATUS_GAME_PREPAR);
        addView(funGameView);

        curtainReLayout = new RelativeLayout(mContext);
        maskReLayout = new RelativeLayout(mContext);
        maskReLayout.setBackgroundColor(Color.parseColor("#3A3A3A"));

        topMaskView = createMaskTextView("link start!", 20, Gravity.BOTTOM);
        bottomMaskView = createMaskTextView("来玩一玩吧", 18, Gravity.TOP);

        coverMaskView();

        funGameView.getViewTreeObserver().addOnGlobalLayoutListener(new MeasureListener());
    }

    private TextView createMaskTextView(String text, int textSize, int gravity) {
        TextView maskTextView = new TextView(mContext);
        maskTextView.setTextColor(Color.BLACK);
        maskTextView.setBackgroundColor(Color.WHITE);
        maskTextView.setGravity(gravity | Gravity.CENTER_HORIZONTAL);
        maskTextView.setTextSize(textSize);
        maskTextView.setText(text);
        return maskTextView;
    }

    private void coverMaskView() {
        LayoutParams maskLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        maskLp.topMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
        maskLp.bottomMargin = (int) FunGameView.DIVIDING_LINE_SIZE;

        addView(maskReLayout, maskLp);
        addView(curtainReLayout, maskLp);
    }

    public void moveRacket(float distance) {
        if (isStart)
        funGameView.moveController(distance);
    }

    private class MeasureListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            halfHitBlockHeight = (int) ((funGameView.getHeight() - 2 * FunGameView.DIVIDING_LINE_SIZE) * .5f);
            RelativeLayout.LayoutParams topRelayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, halfHitBlockHeight);
            RelativeLayout.LayoutParams bottomRelayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, halfHitBlockHeight);
            bottomRelayLayoutParams.topMargin = halfHitBlockHeight;

            curtainReLayout.removeAllViews();
            curtainReLayout.addView(topMaskView, 0, topRelayLayoutParams);
            curtainReLayout.addView(bottomMaskView, 1, bottomRelayLayoutParams);

            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }


    private void doStart(long delay) {
        ObjectAnimator topMaskAnimator = ObjectAnimator.ofFloat(topMaskView, "translationY", topMaskView.getTranslationY(), -halfHitBlockHeight);
        ObjectAnimator bottomMaskAnimator = ObjectAnimator.ofFloat(bottomMaskView, "translationY", bottomMaskView.getTranslationY(), halfHitBlockHeight);
        ObjectAnimator maskShadowAnimator = ObjectAnimator.ofFloat(maskReLayout, "alpha", maskReLayout.getAlpha(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(topMaskAnimator).with(bottomMaskAnimator).with(maskShadowAnimator);
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(delay);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                topMaskView.setVisibility(View.GONE);
                bottomMaskView.setVisibility(View.GONE);
                maskReLayout.setVisibility(View.GONE);

                funGameView.postStatus(FunGameView.STATUS_GAME_PLAY);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = 0, height = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            if (childView instanceof FunGameView) {
                width = childView.getMeasuredWidth();
                height = childView.getMeasuredHeight();
            }
        }

        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.EXACTLY) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void postStart() {
        if (!isStart) {
            doStart(200);
            isStart = true;
        }
    }

    public void postEnd() {
        isStart = false;
        funGameView.postStatus(FunGameView.STATUS_GAME_PREPAR);

        topMaskView.setTranslationY(topMaskView.getTranslationY() + halfHitBlockHeight);
        bottomMaskView.setTranslationY(bottomMaskView.getTranslationY() - halfHitBlockHeight);
        maskReLayout.setAlpha(1.f);

        topMaskView.setVisibility(View.VISIBLE);
        bottomMaskView.setVisibility(View.VISIBLE);
        maskReLayout.setVisibility(View.VISIBLE);
    }

    public void postComplete() {
        funGameView.postStatus(FunGameView.STATUS_GAME_FINISHED);
    }

}
