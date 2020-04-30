package com.example.android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.android.R;

public class InfoItem extends LinearLayout {
    private ImageView right_image;
    private TextView left_text,center_text;
    public InfoItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LinearLayout info_change = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.info_item,this);
        right_image = info_change.findViewById(R.id.jt_right_iv);
        left_text = info_change.findViewById(R.id.title_tv);
        center_text = info_change.findViewById(R.id.content_edt);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.InfoItem);
        String text = a.getString(R.styleable.InfoItem_title);
//  考虑复用，需要左边可修改，中间显示，右边跳转
        left_text.setText(text);
        center_text.setHint(a.getString(R.styleable.InfoItem_edt_hint_content));
//        center_text.setEditableFactory();
        center_text.setText(a.getString(R.styleable.InfoItem_edt_content));
        right_image.setVisibility(a.getBoolean(R.styleable.InfoItem_jt_visible,true) ? View.VISIBLE : View.GONE);
        right_image.setClickable(true);
        a.recycle();
    }

}
