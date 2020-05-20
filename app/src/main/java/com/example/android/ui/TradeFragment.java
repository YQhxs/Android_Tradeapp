package com.example.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.R;
import com.example.android.gson.TradeGoods;
import com.example.android.gson.Tradeinfo;
import com.example.android.ui.details.DetailsGoods;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TradeFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    @Nullable
    private List<TradeGoods> mlist = new ArrayList<>();
    private GoodsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade,container,false);
        initData();
        Log.e("执行init方法后，函数体外数据有没有，",mlist.toString());
        recyclerView = view.findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        adapter= new GoodsAdapter(mlist);
//        适用控件较少情况
//        adapter.setOnItemClickLitener(new GoodsAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(View view, int position, int id) {
//                Intent intent = new Intent(getActivity(), DetailsGoods.class);
//                intent.putExtra("id",id);
//                LogUtil.e("点击的id",id+"");
//                startActivity(intent);
//            }
//
//        });
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        scrollView = view.findViewById(R.id.scroll_View);
        return view ;
    }

    private void initData(){
        RequestBody body = RequestBody.Companion.create("", MediaType.Companion.parse("text/plain"));
        Request request = new Request.Builder()
                .post(body)
                .url("http://39.97.173.40:8999/transaction/getall")
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                Gson gson = new Gson();
                Tradeinfo tradeinfo = gson.fromJson(responsedata,new TypeToken<Tradeinfo>(){}.getType());
                mlist.clear();
                mlist.addAll(tradeinfo.getTradeInfo());
//                mlist = tradeinfo.getTradeInfo();赋值认为数据无变化
                Log.e("在碎片中请求数据回来，再来测试刷新", mlist.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    @Override
    public void onRefresh() {
        refreshTradeGoods();
    }

    private void refreshTradeGoods() {
        initData();
//     因为是活动，所以刷新时活动未重新创建所以init方法，是往里面加数据
//        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    //    留给活动去调用
    public void refresh(){
        scrollView.fling(0);
        scrollView.scrollTo(0,0);
        swipeRefreshLayout.setRefreshing(true);
        refreshTradeGoods();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Intent intent = new Intent(getActivity(), DetailsGoods.class);
                LogUtil.e("TradeFragment里面", vh.toString());
                LogUtil.e("TradeFragment里面", vh.itemView.getId() + "");
                intent.putExtra("id", vh.itemView.getId());
//                return;
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
//               Toast.makeText(getContext(),"长按删除，还需做个清空", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
