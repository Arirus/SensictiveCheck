package com.example.sensitivecheck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

class CustomView extends androidx.appcompat.widget.AppCompatTextView {
    public CustomView(Context context) {
        super(context);


//        PathClassLoader pathClassLoader = new PathClassLoader("",context.getClassLoader());
//        pathClassLoader.getParent();
//        Log.i("dsds", "CustomView: "+pathClassLoader.equals("dsds"));
    }

    Paint paint = new Paint();

    {
        paint.setColor(getResources().getColor(R.color.colorAccent));
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.AT_MOST)
            widthSize = 100;

        if (widthMode == MeasureSpec.UNSPECIFIED)
            widthSize = 100*2;

        if (heightMode == MeasureSpec.AT_MOST)
            heightSize = 100;


        if (heightMode == MeasureSpec.UNSPECIFIED)
            heightSize = 100*2;



        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(0f, 0f, getWidth(), getHeight(), paint);

    }
}
