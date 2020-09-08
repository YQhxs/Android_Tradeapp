package com.example.android.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.gson.TradeGoods;
import com.example.android.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 历史记录中拼车信息
 * @Author: YQHxs
 * @Date: Created in 2020/5/14 11:01
 * @Modified By:
 */
public class HistoryGoodsAdapter extends RecyclerView.Adapter<HistoryGoodsAdapter.ViewHolder> {
    private Context mcontext;
    private List<TradeGoods> mlist;
    private ArrayList<Integer> selectArrays = new ArrayList<>();//选中数据项在列表中的位置集合
    private boolean isShowSelectBtn;//是否显示选中按钮,默认不显示
    private boolean isSelectAll;//外部【全选】按钮是否为选中状态
    public onSelectAllListener listener;//选中和取消选中监听器
    public void setListener(onSelectAllListener listener) {
        this.listener = listener;
    }

    public interface onSelectAllListener {
        void cancleSelectAll();
        void selectAll();
    }
    public HistoryGoodsAdapter(List<TradeGoods> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mcontext==null){
            mcontext = parent.getContext();
        }
        View view = LayoutInflater.from(mcontext).inflate(R.layout.history_goods_item, parent,false);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mcontext,"要跳转到交易物品详情页,且商品信息id为"+holder.title.getId(),Toast.LENGTH_SHORT).show();
//            }
//        });
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(mcontext,"长按删除，还需做个清空",Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TradeGoods good = mlist.get(position);
        Glide.with(mcontext).load("http://39.97.173.40:8999/file/"+good.getPhotos().split("\\*\\*")[0]).into(holder.imageView);
//        后期处理显示成商品图片，选择一张
        holder.itemView.setId(good.getId());
        holder.title.setText(good.getTitle());
        holder.price.setText(good.getPrice());
        if(isShowSelectBtn){
            holder.rbSelect.setVisibility(View.VISIBLE);
            boolean contains = selectArrays.contains(position);//是否选中
            holder.rbSelect.setChecked(contains);
        }else {
            holder.rbSelect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rbSelect;
        ImageView imageView;
        TextView title,price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.goods_image);
            title = itemView.findViewById(R.id.goods_title);
            price = itemView.findViewById(R.id.goods_price);
            rbSelect = itemView.findViewById(R.id.rb_select);
        }
    }



    /**
     * 选中/取消
     *
     * @param selectPosition
     */
    public void setSelectPosition(Integer selectPosition) {
        boolean contains = selectArrays.contains(selectPosition);
        if (contains) {
            selectArrays.remove(selectPosition);
            //取消一项后，如果全选按钮的状态为：选中，应该取消全选按钮选中状态,但不触发取消列表所有已经选中项的事件
            if (isSelectAll) {
                isSelectAll = false;
                if (listener != null) listener.cancleSelectAll();
            }
        } else {
            //如果添加一项后，（外部[全选按钮]为：未选中&&列表数据全部选中），那么设置全选按钮为选中状态，不触发选中事件
            selectArrays.add(selectPosition);
            if (!isSelectAll && isSelectAllData()) {
                isSelectAll = true;
                if (listener != null) listener.selectAll();
            }
        }
        Collections.sort(selectArrays);
        notifyItemChanged(selectPosition);
    }

    /**
     * 全选
     */
    public void selectAll() {
        for (int i = 0; i < mlist.size(); i++) {
            boolean contains = selectArrays.contains(i);
            if (!contains) selectArrays.add(i);
        }
        Collections.sort(selectArrays);//进行排序，避免因为选择前后顺序不一致，导致更新位置越界
        LogUtil.e("HistoryGoodsAdapter里面的selectall方法selectArrays==","" + selectArrays.toString());
        notifyDataSetChanged();
        isSelectAll = true;
    }

    /**
     * 取消全选
     */
    public void cancleAll() {
        selectArrays.clear();
        notifyDataSetChanged();
        isSelectAll = false;
    }

    /**
     * 是否选中了所有的数据
     * 用来设定RadioButton状态
     */
    public boolean isSelectAllData() {
        return mlist.size() == selectArrays.size();
    }

    /**
     * 删除选中项
     * position 为选中数据项在列表中的位置，如0,1,2，3
     * i 为当前集合遍历位置，所以remove()移除时要使用position-i
     * 例如选中 position  1和3，那么遍历两次i为 0,1
     * 由于第一次移除位置position=1的时候，列表数据少了一项，所以原来位置为3的变成了位置2，所以3-1=2
     * 也就是position-i才会拿到正确的列表数据位置
     */
    public void delSelect() {
        for (int i = 0; i < selectArrays.size(); i++) {
            Integer position = selectArrays.get(i);
            mlist.remove(position - i);
        }
//        notifyDataSetChanged();
        LogUtil.e("HistoryGoods里面",mlist.toString());
        selectArrays.clear();
    }

    /**
     * 获得所有选中的记录,并组装为id集合，以“*”分割的字符串
     * 方便接口删除收藏项
     *
     * @return
     */
    public String getAllSelectIds() {
        if (selectArrays.size() == 0) return "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < selectArrays.size(); i++) {
            String goodsId = String.valueOf(mlist.get(selectArrays.get(i)).getId());
            buffer.append(goodsId);
            if (i < selectArrays.size() - 1) buffer.append("*");
        }
        return buffer.toString();
    }

    /**
     * 显示和隐藏操作按钮
     *
     * @param showSelectBtn
     */
    public void setShowSelectBtn(boolean showSelectBtn) {
        isShowSelectBtn = showSelectBtn;
        notifyDataSetChanged();
    }

}
