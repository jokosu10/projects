package com.smsgt.mobile.application.supertsuper_v3.custom_ui;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SlidingDrawer;

@SuppressWarnings("deprecation")
public class CustomClickableSlider extends SlidingDrawer {

	private ViewGroup mHandleLayout;
    private final Rect mHitRect = new Rect();

    public CustomClickableSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomClickableSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View handle = getHandle();

        if (handle instanceof ViewGroup) {
            mHandleLayout = (ViewGroup) handle;
        }
    }

    //@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mHandleLayout != null) {
        	
            if (Build.VERSION.SDK_INT < 11) {
                
            	int childCount = mHandleLayout.getChildCount();
            	
            		ViewHelper.setTranslationX(mHandleLayout, 0f);
            		ViewHelper.setTranslationY(mHandleLayout, 0f);

                    int handleClickX = (int)(event.getX() - ViewHelper.getX(mHandleLayout));
                    int handleClickY = (int)(event.getY() - ViewHelper.getY(mHandleLayout));
                    
                	Rect hitRect = mHitRect;
                    for (int i=0;i < childCount;i++) {
                        View childView = mHandleLayout.getChildAt(i);
                        childView.getHitRect(hitRect);

                        if (hitRect.contains(handleClickX, handleClickY)) {
                            return false;
                        }
                    }
            	
            } else {
                int childCount = mHandleLayout.getChildCount();
                
                int handleClickX = (int)(event.getX() - mHandleLayout.getX());
                int handleClickY = (int)(event.getY() - mHandleLayout.getY());
                
                Rect hitRect = mHitRect;

                for (int i=0;i < childCount;i++) {
                    View childView = mHandleLayout.getChildAt(i);
                    childView.getHitRect(hitRect);

                    if (hitRect.contains(handleClickX, handleClickY)) {
                        return false;
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(event);
    }
}
