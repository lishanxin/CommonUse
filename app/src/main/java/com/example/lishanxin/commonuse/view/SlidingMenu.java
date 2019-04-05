package com.example.lishanxin.commonuse.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.lishanxin.commonuse.utils.ScreenUtils;

public class SlidingMenu extends HorizontalScrollView {
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * dp
     *
     */
    private int mMenuRightPadding = 50;

    /**
     * 菜单的宽度
     *
     */
    private int mMenuWidth;
    private int mHalfMenuWidth;

    private boolean once;


    public SlidingMenu(Context context) {
        super(context);
        init(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }




    private void init(Context context, AttributeSet attrs) {
        mScreenWidth = ScreenUtils.getScreenWidth(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once){
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            ViewGroup menu = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup content = (ViewGroup) wrapper.getChildAt(1);
            mMenuRightPadding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, mMenuRightPadding, content
                            .getResources().getDisplayMetrics());
            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mHalfMenuWidth = mMenuWidth/2;

            menu.getLayoutParams().width = mMenuWidth;
            content.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            //将菜单隐藏
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX > mHalfMenuWidth){
                    this.smoothScrollTo(mMenuWidth, 0);
                }else {
                    this.smoothScrollTo(0,0);
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }
}
