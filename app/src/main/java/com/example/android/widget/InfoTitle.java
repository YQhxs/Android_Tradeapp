package com.example.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.android.R;

public class InfoTitle extends LinearLayout {
    private ImageView back;
    private TextView title;
    private Button save;

    public InfoTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LinearLayout bar_title = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.bar_title, this);
        back = bar_title.findViewById(R.id.back);
        title = bar_title.findViewById(R.id.bar_title);
        save = bar_title.findViewById(R.id.save);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BarTitle);
//        back.setClickable(a.getBoolean(R.styleable.BarTitle_isClickable,true));
        title.setText(a.getString(R.styleable.BarTitle_bartitle));
        save.setVisibility(a.getBoolean(R.styleable.BarTitle_isVisible, true) ? View.VISIBLE : View.GONE);
        save.setEnabled(a.getBoolean(R.styleable.BarTitle_isClickable, true));
        a.recycle();
    }

}
