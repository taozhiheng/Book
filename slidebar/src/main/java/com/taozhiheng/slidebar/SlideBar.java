package com.taozhiheng.slidebar;

import android.widget.RelativeLayout;

/**
 * Created by taozhiheng on 15-11-11.
 */
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;


public class SlideBar extends RelativeLayout {

    private static final String TAG = "SlideBar";
    private static final boolean DEBUG = false;

    //布局内最外层view
    View mContentView;
    //滑动的起始位置
    private int mContentStartX = 0;
    //手指按下的横坐标
    private float mEventDownX;
    //子view距左边距离
    private float mContentIndicateLeft;
    //滑动结束监听
    private OnSlideListener mOnSlideListener;
    //速度侦测
    private VelocityTracker mVelocityTracker = null;
    //足以滑到两端的最小速度
    private int mMinVelocityXToSlide;

    //touch事件结束后的动画
    //左右滑动动画的时间
    private int mLeftAnimationDuration;
    private int mRightAnimationDuration;
    //左右滑动的动画
    private ObjectAnimator animLeftMoveAnimator;
    private ObjectAnimator animRightMoveAnimator;

    //触发有效滑动的最小距离
    private int mMinDistanceToSlide;
    //有效滑动的最大距离
    private int mMaxDistance;
    //滑动状态
    private SlideState mSlideState = SlideState.SLIDE_STOP;

    //是否正被拖动
    private boolean mIsBeingDragged = false;

    private enum SlideState
    {
        SLIDE_START, SLIDE_RUN, SLIDE_STOP
    }

    public enum SlideDirection
    {
        SLIDE_FROM_LEFT, SLIDE_FROM_RIGHT,
        SLIDE_TO_LEFT, SLIDE_TO_RIGHT, SLIDE_BACK
    }

    public interface OnSlideListener {

        void onStartSlide(SlideDirection from);

        void onSlideChanged(float distance);

        void onSlideRangeChanged(SlideDirection to);

        void onStopSlide(SlideDirection to);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SlideBar(Context context){
        super(context);
    }

    //初始化，从xml读取相应属性设置信息
    private void initView(Context context,AttributeSet attrs){

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideBar);
        mMinVelocityXToSlide = a.getInt(R.styleable.SlideBar_MinVelocityXToSlide,2000) ;
        mLeftAnimationDuration = a.getInt(R.styleable.SlideBar_LeftAnimationDuratioin,250) ;
        mRightAnimationDuration = a.getInt(R.styleable.SlideBar_RightAnimationDuratioin,250) ;
        a.recycle();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                mIsBeingDragged = true;
                break;
            case MotionEvent.ACTION_DOWN: {
                if (DEBUG) Log.v(TAG, "*** DOWN ***");
                handleDown(ev);
                mIsBeingDragged = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        boolean handled = false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                if (DEBUG) Log.v(TAG, "*** DOWN ***");
                handleDown(event);
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (DEBUG) Log.v(TAG, "*** MOVE ***");
                handleMove(event);
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (DEBUG) Log.v(TAG, "*** UP ***");
                handleUp(event);
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                if (DEBUG) Log.v(TAG, "*** CANCEL ***");
                mSlideState = SlideState.SLIDE_STOP;
                handled = true;
                break;

        }
        invalidate();
        return handled || super.onTouchEvent(event);
    }

    private void setupContent()
    {
        if(mContentView != null){
            return;
        }
        int count = getChildCount();
        if(count <= 0)
            return;
        int index = count > 1 ? 1 : 0;
        mContentView = getChildAt(index);
        mMaxDistance = mContentView.getWidth();
        mMinDistanceToSlide = mMaxDistance/2;
        Log.v(TAG, "first handleDown,mMaxDistance:" + mMaxDistance);
    }

    //处理down事件：记下手指按下的位置，子view为空时寻找view
    private void handleDown(MotionEvent event) {
        mEventDownX = event.getX();
        setupContent();
        mSlideState = SlideState.SLIDE_START;
    }

    //处理up事件：判断滑动距离和滑动速度，距离或速度达到时。。。
    public void handleUp(MotionEvent event) {
        Log.v(TAG, "handleUp,mIndicateLeft:" + mContentIndicateLeft);
        //滑动距离足够，直接滑到最左或最右位置
        if(mContentIndicateLeft >= mMinDistanceToSlide){
            slideToRight();
            return;
        }
        else if(mContentIndicateLeft <= -mMinDistanceToSlide)
        {
            slideToLeft();
            return;
        }
        //用户滑动很快,判断处理是否需要滑到最左或最右的位置
        if(velocityTrySlide()){
            return;
        }
        //距离和速度都不够，滑回原处
        slideBack();

        mSlideState = SlideState.SLIDE_STOP;
    }

    //处理move事件：计算子view距左边距离，子view调用setX()方法
    private void handleMove(MotionEvent event) {
        if(mContentView == null)
            return;
        float lastIndicateLeft = mContentIndicateLeft;
        mContentIndicateLeft = event.getX() - mEventDownX + mContentStartX;
        mContentView.setX(mContentIndicateLeft);
        if(mSlideState == SlideState.SLIDE_START)
        {
            mSlideState = SlideState.SLIDE_RUN;
            if(mOnSlideListener != null)
                mOnSlideListener.onStartSlide(mContentIndicateLeft > 0 ?
                        SlideDirection.SLIDE_FROM_LEFT : SlideDirection.SLIDE_FROM_RIGHT);
        }
        else if(mSlideState == SlideState.SLIDE_RUN && mOnSlideListener != null)
        {
            mOnSlideListener.onSlideChanged(mContentIndicateLeft);
            if(mContentIndicateLeft > 0 && lastIndicateLeft <= 0)
                mOnSlideListener.onSlideRangeChanged(SlideDirection.SLIDE_TO_RIGHT);
            else if(mContentIndicateLeft < 0 && lastIndicateLeft >= 0)
                mOnSlideListener.onSlideRangeChanged(SlideDirection.SLIDE_TO_LEFT);
        }
    }

    /**
     * 另一种方式来滑动，如果用户滑动非常快
     */
    private boolean velocityTrySlide() {
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000);

        int velocityX = (int) velocityTracker.getXVelocity();

        Log.v(TAG, "velocityX:" + velocityX);

        //滑动速度足够，直接滑到最左或最右的位置
        if(velocityX > mMinVelocityXToSlide){
            slideToRight();
            return true;
        }
        else if(velocityX < -mMinVelocityXToSlide)
        {
            slideToLeft();
            return true;
        }


        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        return false;
    }

    //滑回最初的位置
    private void slideBack(){
        if(mContentView == null)
            return;
        Log.v(TAG, "slide back from:" + mContentIndicateLeft);
        animLeftMoveAnimator = ObjectAnimator.ofFloat(mContentView, "x", mContentView.getX(), mContentStartX)
                .setDuration(mLeftAnimationDuration);
        animLeftMoveAnimator.start();
        if(mOnSlideListener != null)
            mOnSlideListener.onStopSlide(SlideDirection.SLIDE_BACK);
    }

    //滑到最左的位置
    private void slideToLeft() {
        if(mContentView == null)
            return;
        Log.v(TAG, "slide to left from:" + mContentIndicateLeft);
        animRightMoveAnimator = ObjectAnimator.ofFloat(mContentView, "x", mContentView.getX(), -mMaxDistance)
                .setDuration(mRightAnimationDuration);
        animRightMoveAnimator.start();
        if(mOnSlideListener != null)
            mOnSlideListener.onStopSlide(SlideDirection.SLIDE_TO_LEFT);
    }

    //滑到最右的位置
    private void slideToRight() {
        if(mContentView == null)
            return;
        Log.v(TAG, "slide to right from:" + mContentIndicateLeft);
        animRightMoveAnimator = ObjectAnimator.ofFloat(mContentView, "x", mContentView.getX(), mMaxDistance)
                .setDuration(mRightAnimationDuration);
        animRightMoveAnimator.start();
        if(mOnSlideListener != null)
            mOnSlideListener.onStopSlide(SlideDirection.SLIDE_TO_RIGHT);
    }


    public void setOnSlideListener(OnSlideListener listener) {
        mOnSlideListener = listener;
    }

}