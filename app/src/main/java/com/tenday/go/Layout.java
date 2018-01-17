package com.tenday.go;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class Layout extends LinearLayout {
    public Layout(Context context) {
        super(context);
    }

    public Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Layout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getMeasuredWidth();
        int id = getId();
        if (R.id.desk9 == id)
            setMeasuredDimension(size, size+(9-(size-2*(int)(0.031*getMeasuredWidth()))%9)%9);
        else if (R.id.desk11 == id)
            setMeasuredDimension(size, size+(11-(size-2*(int)(0.031*getMeasuredWidth()))%11)%11);
        else if (R.id.desk13 == id)
            setMeasuredDimension(size, size+(13-(size-2*(int)(0.031*getMeasuredWidth()))%13)%13);
        size = (int)(0.031*getMeasuredWidth());
        setPadding(size,size,size,0);
    }
}