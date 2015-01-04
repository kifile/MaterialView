package com.kifile.materialwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * The detector handle the touch event, and display a circle on your canvas.
 * Created by kifile on 15-1-4.
 */
public class MaterialBackgroundDetector {
    private static final String TAG = "MaterialBackgroundDetector";
    private static final boolean DBG = true;

    private static final int DEFAULT_DURATION = 1200;
    private static final int DEFAULT_FAST_DURATION = 300;
    public static final int DEFAULT_COLOR = Color.BLACK;

    private Context mContext;
    private View mView;
    private int mColor;
    private Paint mCirclePaint;
    private int mFocusColor;
    private int mCircleColor;

    private float mX;
    private float mY;
    private float mCenterX;
    private float mCenterY;
    private float mViewRadius;
    private float mRadius;
    private int mWidth;
    private int mHeight;
    private int mMinPadding;

    private ObjectAnimator mAnimator;
    private int mDuration = DEFAULT_DURATION;
    /*package*/ boolean mIsAnimation;
    private boolean mIsFocused;

    public MaterialBackgroundDetector(Context context, View view, int color) {
        mContext = context;
        mView = view;
        setColor(color);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinPadding = configuration.getScaledEdgeSlop();
    }

    private void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            mFocusColor = computeFocusColor(mColor);
            mCircleColor = computeCircleColor(mColor);
            resetPaint();
            mView.invalidate();
        }
    }

    private void resetPaint() {
        if (mCirclePaint == null) {
            mCirclePaint = new Paint();
        }
        mCirclePaint.setColor(mCircleColor);
    }

    private int computeCircleColor(int color) {
        return ColorUtils.getColorAtAlpha(color, 33);
    }

    private int computeFocusColor(int color) {
        return ColorUtils.getColorAtAlpha(color, 33);
    }

    /**
     * Called when view size changed.
     *
     * @param width
     * @param height
     */
    public void onSizeChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
        mViewRadius = (float) Math.sqrt(mWidth * mWidth / 4 + mHeight * mHeight / 4);
    }

    public boolean onTouchEvent(MotionEvent event, boolean result) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsFocused = true;
                if (!mIsAnimation) {
                    mX = event.getX();
                    mY = event.getY();
                    mAnimator = ObjectAnimator.ofFloat(this, "radius", mMinPadding, mViewRadius);
                    mAnimator.setDuration(mDuration);
                    mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    mAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mIsAnimation = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (DBG) {
                                Log.d(TAG, "AnimationEnd");
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            if (DBG) {
                                Log.d(TAG, "AnimationCancel");
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mAnimator.start();
                    if (DBG) {
                        Log.i(TAG, "Down,from:" + 0 + ",to:" + mViewRadius);
                    }
                }
                // Ensure the following motion event can be received.
                if (!result) {
                    result = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsFocused = false;
                cancelAnimator();
                mX = mCenterX;
                mY = mCenterY;
                mRadius = Math.max(mRadius, mViewRadius * 0.1f);
                int duration = (int) (DEFAULT_FAST_DURATION * (mViewRadius - mRadius) / mViewRadius);
                mAnimator = ObjectAnimator.ofFloat(this, "radius", mRadius, mViewRadius);
                mAnimator.setDuration(duration);
                mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIsAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIsAnimation = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                mAnimator.start();
                if (DBG) {
                    Log.i(TAG, "UP,from:" + mRadius + ",to:" + mViewRadius);
                }
                break;
        }
        return result;
    }

    public void cancelAnimator() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    public void draw(Canvas canvas) {
        if (mIsFocused || mIsAnimation) {
            if (DBG) {
                Log.d(TAG, "DrawFocusColor");
            }
            canvas.drawColor(mFocusColor);
        }
        if (mIsAnimation) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mCirclePaint);
        }
    }

    public void setRadius(float radius) {
        float percent = 0;
        if (mAnimator != null) {
            percent = mAnimator.getAnimatedFraction();
        }
        mRadius = radius;
        mCenterX = mX + percent * (mWidth / 2 - mX);
        mCenterY = mY + percent * (mHeight / 2 - mY);
        float distance = (float) Math.sqrt((mCenterX - mX) * (mCenterX - mX) + (mCenterY - mY) * (mCenterY - mY)) + mMinPadding;
        if (distance > radius) {
            mCenterX = mX + (mCenterX - mX) * radius / distance;
            mCenterY = mY + (mCenterY - mY) * radius / distance;
        }
        mView.invalidate();
    }
}
