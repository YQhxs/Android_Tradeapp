package com.example.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.gson.TradeGoods;
import com.example.android.util.LogUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private Context mcontext;
    private List<TradeGoods> mgoodsList;
//    private OnItemClickLitener mOnItemClickLitener = null;
//    public interface OnItemClickLitener{
//        void onItemClick(View view, int position,int id);
//    }
//    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
//    {
//        this.mOnItemClickLitener = mOnItemClickLitener;
//    }

    public GoodsAdapter(List<TradeGoods> goodsList) {
        mgoodsList = goodsList;
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodsAdapter.ViewHolder holder, final int position) {
//        if(mOnItemClickLitener!=null){
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    int pos = position;
//                    mOnItemClickLitener.onItemClick(v,position,holder.goods_title.getId());
//                }
//            });
//        }
        TradeGoods good = mgoodsList.get(position);
//        holder.itemView.setId(good.getId());
        holder.itemView.setId(good.getId());
        LogUtil.e("TradeFragment的适配器", holder.itemView.getId() + "");
//        原来是设置在TextView上了，结果id并不好取，进入另一个活动太麻烦。
//        holder.goods_title.setId(good.getId());
        holder.goods_title.setText(good.getTitle());
        holder.solder_nickname.setText(good.getNick_NAME());
//        后期改动需要把TradeGoods里面的url改成string类型，把请求回来的数据放入goodlist；
//        Glide.with(mcontext).load(R.drawable.touxiang).into(holder.goods_img);
//        Glide.with(mcontext).load(R.drawable.touxiang).into(holder.solder_head);
        Glide.with(mcontext).load("http://39.97.173.40:8999/file/" + good.getPhotos().split("\\*\\*")[0]).into(holder.goods_img);
        if (!"未设置".equals(good.getPhoto())) {
            Glide.with(mcontext).load("http://39.97.173.40:8999/file/" + good.getPhoto()).into(holder.solder_head);
        }

    }

    @Override
    public GoodsAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        if (mcontext == null) {
            mcontext = parent.getContext();
        }
        View view = LayoutInflater.from(mcontext).inflate(R.layout.goods_item_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView goods_img;
        CircleImageView solder_head;
        TextView goods_title,solder_nickname;
        public ViewHolder(@NonNull View itemView) {
//            布局外壳，一个个view没有数据
            super(itemView);
            goods_img = itemView.findViewById(R.id.goods_image);
            goods_title  = itemView.findViewById(R.id.goods_name);
            solder_head = itemView.findViewById(R.id.solder_head);
            solder_nickname = itemView.findViewById(R.id.solder_name);
        }
    }

    @Override
    public int getItemCount() {
        return mgoodsList.size();
    }


}
