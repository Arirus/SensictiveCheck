package com.example.sensitivecheck;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import dalvik.system.PathClassLoader;

class CustomView extends View {
    public CustomView(Context context) {
        super(context);


        PathClassLoader pathClassLoader = new PathClassLoader("",context.getClassLoader());
        pathClassLoader.getParent();
        Log.i("dsds", "CustomView: "+pathClassLoader.equals("dsds"));
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }




}
