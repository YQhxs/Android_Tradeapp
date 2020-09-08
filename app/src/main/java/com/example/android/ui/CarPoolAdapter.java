package com.example.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.R;
import com.example.android.gson.CarPool;
import com.example.android.util.GetContext;
import com.example.android.util.LogUtil;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Description:拼车页面的recycleview适配器
 * @Author: YQHxs
 * @Date: Created in 2020/5/3 18:01
 * @Modified By:
 */
public class CarPoolAdapter extends RecyclerView.Adapter<CarPoolAdapter.ViewHoder> {
    private List<CarPool> mlist;
    private Context mcontext;
    public CarPoolAdapter(List<CarPool> list) {
        mlist = list;
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mcontext == null){
            mcontext = parent.getContext();
        }
        View view = LayoutInflater.from(mcontext).inflate(R.layout.carpool_item_layout,parent,false);


        return new ViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        CarPool carPool = mlist.get(position);
        holder.nickName.setText(carPool.getNickName());
        holder.content.setText(carPool.getContent());
        String avatar = carPool.getAvatar();
//        Glide.with(mcontext).load(R.drawable.touxiang).into(holder.avatar);
        if (!"未设置".equals(avatar)) {
            LogUtil.e("CarPoolAdapter里面测试equals",""+"未设置".equals(avatar));
            RequestOptions options = new RequestOptions().placeholder(R.drawable.touxiang);
            Glide.with(mcontext).load("http://39.97.173.40:8999/file/" + avatar).apply(options).into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    /**
     * 是CarPoolAdapter的内部类，里面进行子项布局id的初始化
     */
    class ViewHoder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView nickName,content;
        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.carpooler_avatar);
            nickName = itemView.findViewById(R.id.carpooler_name);
            content = itemView.findViewById(R.id.carpool_content);
        }
    }
}
