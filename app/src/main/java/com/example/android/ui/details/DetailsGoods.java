package com.example.android.ui.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.R;
import com.example.android.gson.TradeGoods;
import com.example.android.gson.Tradeinfo;
import com.example.android.util.BaseActivity;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailsGoods extends BaseActivity {
    private ImageView back;
    private int id;
    private CircleImageView circleImageView;
    private TextView username,price,title;
    private RecyclerView recyclerView;
    private DetailsGoodsAdapter adapter;
    private TradeGoods tradeGoods;
    private List<String> photos = new ArrayList<>();
    private Handler handler;

    private static final String TAG = "DetailsGoods";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_goods);
//        来自fragment的商品信息id
        id = getIntent().getIntExtra("id",-1);
        LogUtil.e(TAG,id+"----来自碎片trade里传来的商品信息id");

        initData();

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        circleImageView = findViewById(R.id.goods_useravatar);
        username = findViewById(R.id.goods_username);
        price = findViewById(R.id.goods_price);
        title = findViewById(R.id.goods_title);
        recyclerView = findViewById(R.id.goods_photos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailsGoodsAdapter(photos);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                LogUtil.e(TAG,"异步外数据"+tradeGoods.toString());
                Glide.with(GetContext.getContext()).load("http://39.97.173.40:8999/file/"+tradeGoods.getPhoto()).into(circleImageView);
                username.setText(tradeGoods.getUser_ID());
                price.setText(tradeGoods.getPrice());
                title.setText(tradeGoods.getTitle());
                String[] tempphotos = tradeGoods.getPhotos().split("\\*\\*");
                Collections.addAll(photos,tempphotos);
                LogUtil.e(TAG,"分割后的转换成list"+photos.toString());
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void initData(){

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "{\n\t\"id\":"+id+"\n}");
        LogUtil.e(TAG,"initdata:"+"{\n\t\"id\":"+id+"\n}");
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/transaction/gettransinfobyinfoid")
                .post(requestBody)
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(TAG,e+"");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                    Gson gson = new Gson();
                 tradeGoods = gson.fromJson(responsedata,TradeGoods.class);

//                mlist = tradeinfo.getTradeInfo();赋值认为数据无变化
                LogUtil.e("在详情页中请求数据回来，",tradeGoods.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                Message msg = new Message();
                msg.obj = tradeGoods;
                handler.sendMessage(msg);

            }
        });
    };

}
