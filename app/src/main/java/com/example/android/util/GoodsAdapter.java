package com.example.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private Context mcontext;
    private List<TradeGoods> mgoodsList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView goods_img;
        CircleImageView solder_head;
        TextView goods_title,solder_nickname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            goods_img = itemView.findViewById(R.id.goods_image);
            goods_title  = itemView.findViewById(R.id.goods_name);
            solder_head = itemView.findViewById(R.id.solder_head);
            solder_nickname = itemView.findViewById(R.id.solder_name);
        }
    }
    public GoodsAdapter(List<TradeGoods> goodsList) {
        mgoodsList = goodsList;
    }


    @Override
    public GoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mcontext == null){
            mcontext = parent.getContext();
        }
        View view = LayoutInflater.from(mcontext).inflate(R.layout.goods_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsAdapter.ViewHolder holder, int position) {
        TradeGoods good = mgoodsList.get(position);
        holder.goods_title.setText(good.getTitle());
        holder.solder_nickname.setText(good.getSolder_nickname());
//        后期改动需要把TradeGoods里面的url改成string类型，把请求回来的数据放入goodlist；
        Glide.with(mcontext).load(R.drawable.touxiang).into(holder.goods_img);
        Glide.with(mcontext).load(R.drawable.touxiang).into(holder.solder_head);
//        Glide.with(mcontext).load(good.getImg_url()).into(holder.goods_img);
//        Glide.with(mcontext).load(good.getSolder_head()).into(holder.solder_head);
    }

    @Override
    public int getItemCount() {
        return mgoodsList.size();
    }


}
