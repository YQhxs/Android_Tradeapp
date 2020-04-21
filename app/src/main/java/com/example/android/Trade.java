package com.example.android;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.android.util.ActivityCollector;
import com.example.android.util.BaseActivity;

import com.example.android.util.GetContext;
import com.example.android.util.GoodsAdapter;
import com.example.android.util.LogUtil;
import com.example.android.util.TradeGoods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Trade extends BaseActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private static final String TAG = "Trade";
    private TradeGoods[] tradeGoods = {
            new TradeGoods("笔记本1",R.drawable.touxiang,"nickname",R.drawable.touxiang),
            new TradeGoods("笔记本2",R.drawable.touxiang,"nickname",R.drawable.touxiang),
            new TradeGoods("笔记本3",R.drawable.touxiang,"nickname",R.drawable.touxiang),
            new TradeGoods("笔记本4",R.drawable.touxiang,"nickname",R.drawable.touxiang),
            new TradeGoods("笔记本5",R.drawable.touxiang,"nickname",R.drawable.touxiang)
    };
    private List<TradeGoods> goodsList = new ArrayList<>();
    private GoodsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    private NavigationView.OnNavigationItemSelectedListener tOnNavigationItemSelectedListener =new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Intent intent1;
            //填写页面跳转逻辑
            switch (menuItem.getItemId()){
                case R.id.nav_goods:
                    intent1 = new Intent(GetContext.getContext(),Trade.class);
                    startActivity(intent1);
                    break;
                case R.id.nav_carpool:
                    intent1 = new Intent(GetContext.getContext(),CarPool.class);
                    startActivity(intent1);
                    break;
                case R.id.nav_info:
                    intent1 = new Intent(GetContext.getContext(),Info.class);
                    startActivity(intent1);
                    break;
            }
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        //从上一活动中接收来的用户信息
        Intent intent = getIntent();
        String header_Name = intent.getStringExtra("user_nickName");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView headerimage = headerLayout.findViewById(R.id.header_image);
        TextView headername = headerLayout.findViewById(R.id.header_name);
        LogUtil.d(TAG,"--------headername"+headername);

        if(intent.hasExtra("user_head")){
            String header_head = intent.getStringExtra("user_head");
            Glide.with(GetContext.getContext()).load(header_head).into(headerimage);
        }
        headername.setText(header_Name);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //为recyclerview添加适配器加载布局
        initTradeGoods();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GoodsAdapter(goodsList);
        recyclerView.setAdapter(adapter);

//监听下拉刷新
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTradeGoods();
            }
        });
//设置导航图标
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("交易");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.nav);

        }

//顶部导航
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(tOnNavigationItemSelectedListener);

    }


    //加载主界面交易信息的
    private void refreshTradeGoods(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                //向服务端请求最新数据，并显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initTradeGoods();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();

    }
    private void initTradeGoods(){
        goodsList.clear();
        for (int i = 0; i < 20; i++) {
            Random random = new Random();
            int index = random.nextInt(tradeGoods.length);
            goodsList.add(tradeGoods[index]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.publish_item,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent ;
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.publish_goods:
                intent = new Intent(GetContext.getContext(),PublishGoods.class);
                startActivity(intent);
                //跳转到发布商品页面；
                break;
            case R.id.publish_carpool:
//                调转到发布拼车信息页面；
                intent = new Intent(GetContext.getContext(),PublishCar.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}