package com.example.android.ui;

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
import com.example.android.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TradeFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    @Nullable
    private List<TradeGoods> mlist = new ArrayList<>();
    private GoodsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade,container,false);
        initData();
        Log.e("执行init方法后，函数体外数据有没有，",mlist.toString());
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);

        adapter= new GoodsAdapter(mlist);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        scrollView = view.findViewById(R.id.scroll_View);
        return view ;
    }

    private void initData(){
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
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
                mlist.addAll(tradeinfo.getTradeInfo());
//                mlist = tradeinfo.getTradeInfo();赋值认为数据无变化
                Log.e("在碎片中请求数据回来，",mlist.toString());
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
}
