package com.example.android.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.R;
import com.example.android.gson.CarPool;
import com.example.android.gson.CarPoolInfo;
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

public class CarPoolFragment extends Fragment {

    private static final String TAG = "CarPoolFragment";
    private List<CarPool> mlist = new ArrayList<>();
    private CarPoolAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_pool, container, false);

        initData();
        Log.e(TAG, "执行init方法后，函数体外数据有没有" + mlist.toString());
        RecyclerView recyclerView = view.findViewById(R.id.carpool_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CarPoolAdapter(mlist);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.carpool_refresh);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCarPool();
            }
        });

        scrollView = view.findViewById(R.id.carpool_scroll_View);
        return view;

    }

    private void initData() {
        RequestBody body = RequestBody.Companion.create("", MediaType.Companion.parse("text/plain"));
        String url = "http://" + getResources().getString(R.string.testip) + ":8999/carpool/getall";
        String url1 = "http://" + getString(R.string.testip) + ":8999/carpool/getall";
//        三个urlhttp://192.168.249.101:8999/carpool/getall--http://192.168.249.101:8999/carpool/getall--http://192.168.249.101:8999/carpool/getall
        String url3 = "http://" + getContext().getResources().getString(R.string.testip) + ":8999/carpool/getall";
        LogUtil.e(TAG, "三个url" + url + "--" + url1 + "--" + url3);
        Request request = new Request
                .Builder()
                .post(body)
                .url("http://39.97.173.40:8999/carpool/getall")
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(TAG, "失败了在CarpoolFragment");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                Gson gson = new Gson();
                CarPoolInfo carPoolInfo = gson.fromJson(responsedata, new TypeToken<CarPoolInfo>() {
                }.getType());
                mlist.addAll(carPoolInfo.getCarPools());

                Log.e(TAG, "在CarPool碎片中请求数据回来，" + mlist.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void refreshCarPool() {
        initData();
//     因为是活动，所以刷新时活动未重新创建所以init方法，是往里面加数据
//        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //    留给活动去调用,即点击拼车标题回到顶部并刷新
    public void refresh() {
        scrollView.fling(0);
        scrollView.scrollTo(0, 0);
        swipeRefreshLayout.setRefreshing(true);
        refreshCarPool();
    }

}
