// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.example.accessibilityt.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.*;
import androidx.appcompat.widget.AppCompatSeekBar;

public class VerticalSeekBar extends AppCompatSeekBar
{
    /* member class not found */
    class Callback {}


    public VerticalSeekBar(Context context)
    {
        super(context);
        isInScrollingContainer = false;
    }

    public VerticalSeekBar(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        isInScrollingContainer = false;
    }

    public VerticalSeekBar(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        isInScrollingContainer = false;
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private void attemptClaimDrag()
    {
        ViewParent viewparent = getParent();
        if(viewparent != null)
            viewparent.requestDisallowInterceptTouchEvent(true);
    }

    private void trackTouchEvent(MotionEvent motionevent)
    {
        int k = getHeight();
        int i = getPaddingTop();
        int j = getPaddingBottom();
        int l = k - i - j;
        int i1 = (int)motionevent.getY();
        float f1 = 0.0F;
        float f;
        if(i1 > k - j)
            f = 0.0F;
        else
        if(i1 < i)
        {
            f = 1.0F;
        } else
        {
            f = (float)((l - i1) + i) / (float)l;
            f1 = mTouchProgressOffset;
        }
        setProgress((int)(f1 + (float)getMax() * f));
    }

    public boolean isInScrollingContainer()
    {
        return isInScrollingContainer;
    }

    protected void onDraw(Canvas canvas)
    {
        this;
        JVM INSTR monitorenter ;
        canvas.rotate(-90F);
        canvas.translate(-getHeight(), 0.0F);
        super.onDraw(canvas);
        this;
        JVM INSTR monitorexit ;
        return;
        canvas;
        throw canvas;
    }

    protected void onMeasure(int i, int j)
    {
        this;
        JVM INSTR monitorenter ;
        super.onMeasure(j, i);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    protected void onSizeChanged(int i, int j, int k, int l)
    {
        super.onSizeChanged(j, i, l, k);
    }

    void onStartTrackingTouch()
    {
        mIsDragging = true;
    }

    void onStopTrackingTouch()
    {
        mIsDragging = false;
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        if(!isEnabled())
            return false;
        switch(motionevent.getAction())
        {
        default:
            break;

        case 2: // '\002'
            if(mIsDragging)
                trackTouchEvent(motionevent);
            else
            if(Math.abs(motionevent.getY() - mTouchDownY) > (float)mScaledTouchSlop)
            {
                setPressed(true);
                invalidate();
                onStartTrackingTouch();
                trackTouchEvent(motionevent);
                attemptClaimDrag();
            }
            onSizeChanged(getWidth(), getHeight(), 0, 0);
            break;

        case 1: // '\001'
            if(mIsDragging)
            {
                trackTouchEvent(motionevent);
                onStopTrackingTouch();
                setPressed(false);
            } else
            {
                onStartTrackingTouch();
                trackTouchEvent(motionevent);
                onStopTrackingTouch();
            }
            onSizeChanged(getWidth(), getHeight(), 0, 0);
            invalidate();
            break;

        case 0: // '\0'
            if(isInScrollingContainer())
            {
                mTouchDownY = motionevent.getY();
            } else
            {
                setPressed(true);
                invalidate();
                onStartTrackingTouch();
                trackTouchEvent(motionevent);
                attemptClaimDrag();
                onSizeChanged(getWidth(), getHeight(), 0, 0);
            }
            break;
        }
        return true;
    }

    protected void onVisibilityChanged(View view, int i)
    {
        super.onVisibilityChanged(view, i);
        if(8 == i)
        {
            view = mCallback;
            if(view != null)
                view.onSeekBarDetached();
        }
    }

    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    public void setInScrollingContainer(boolean flag)
    {
        isInScrollingContainer = flag;
    }

    public void setProgress(int i)
    {
        this;
        JVM INSTR monitorenter ;
        super.setProgress(i);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    private boolean isInScrollingContainer;
    private Callback mCallback;
    private boolean mIsDragging;
    private int mScaledTouchSlop;
    private float mTouchDownY;
    float mTouchProgressOffset;
}
