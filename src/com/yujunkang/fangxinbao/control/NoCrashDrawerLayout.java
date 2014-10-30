package com.yujunkang.fangxinbao.control;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;



/**
 * 
 * @date 2014-8-25
 * @author xieb
 * 
 */
public class NoCrashDrawerLayout extends DrawerLayout {


    public NoCrashDrawerLayout(Context context) {
        super(context);
    }

    public NoCrashDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoCrashDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

}


