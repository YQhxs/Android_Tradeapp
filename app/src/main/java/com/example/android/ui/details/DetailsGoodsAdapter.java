package com.example.android.ui.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.util.LogUtil;

import java.util.List;

/**
 * @Description:
 * @Author: YQHxs
 * @Date: Created in 2020/5/19 13:03
 * @Modified By:
 */
public class DetailsGoodsAdapter extends RecyclerView.Adapter<DetailsGoodsAdapter.ViewHodler> {
    private Context context;
    private List<String> mlist;

    public DetailsGoodsAdapter(List<String> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public DetailsGoodsAdapter.ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.details_photos_item, parent, false);
        ViewHodler viewHodler = new ViewHodler(view);
        viewHodler.photoitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"之后实现点击查看",Toast.LENGTH_SHORT).show();
            }
        });
        return viewHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsGoodsAdapter.ViewHodler holder, int position) {
        LogUtil.e("E/DetailsGoodsAdapter里面的bindview",mlist.get(position));
        Glide.with(context).load("http://39.97.173.40:8999/file/" + mlist.get(position)).into(holder.photoitem);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        ImageView photoitem;
        public ViewHodler(@NonNull View itemView) {
            super(itemView);
            photoitem = itemView.findViewById(R.id.photo_item);
        }
    }
}
