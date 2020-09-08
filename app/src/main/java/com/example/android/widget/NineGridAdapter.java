package com.example.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.util.GetContext;

import java.io.File;
import java.util.List;

/**
 * @Description: 九宫格选择图片发布
 * @Author: YQHxs
 * @Date: Created in 2020/5/4 11:44
 * @Modified By:
 */
public class NineGridAdapter extends RecyclerView.Adapter<NineGridAdapter.MyViewHolder> {

    private List<String> mData;
    private final int mCountLimit = 9;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onTakePhotoClick();
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public NineGridAdapter(List<String> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dip2px(parent.getContext(), 95), dip2px(parent.getContext(), 95));
        params.setMargins(10, 10, 10, 10);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        return new MyViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (position == getItemCount() - 1 && mData.size() < mCountLimit) {
            holder.imageView.setImageResource(R.mipmap.add_image);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onTakePhotoClick();
                }
            });
        } else {
            // 本地图片
            Glide.with(GetContext.getContext()).load(new File(mData.get(position))).into(holder.imageView);
//            Glide.with(GetContext.getContext()).load(R.drawable.touxiang).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击操作，后续可添加点击后的响应
                }
            });
            // 长按监听
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 满 9张图就不让其添加新图
        if (mData != null && mData.size() >= mCountLimit) {
            return mCountLimit;
        } else {
            return mData == null ? 1 : mData.size() + 1;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
