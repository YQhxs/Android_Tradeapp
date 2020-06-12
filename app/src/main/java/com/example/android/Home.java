package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.changeinfo.Info;
import com.example.android.gson.User;
import com.example.android.ui.CarPoolFragment;
import com.example.android.ui.TradeFragment;
import com.example.android.ui.history.HistoryCarPool;
import com.example.android.ui.history.HistoryGoods;
import com.example.android.ui.publish.PublishCar;
import com.example.android.ui.publish.PublishGoods;
import com.example.android.util.ActivityCollector;
import com.example.android.util.BaseActivity;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Home extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private static final String TAG = "Trade";

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    Handler nHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    //左侧导航栏的点击事件
    private NavigationView.OnNavigationItemSelectedListener tOnNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Intent intent1;
            //填写页面跳转逻辑
            switch (menuItem.getItemId()) {
                case R.id.nav_goods:
                    intent1 = new Intent(GetContext.getContext(), HistoryGoods.class);
                    intent1.putExtra("id", user.getId());
                    startActivityForResult(intent1, 3);
                    break;
                case R.id.nav_carpool:
                    intent1 = new Intent(GetContext.getContext(), HistoryCarPool.class);
                    intent1.putExtra("user_id", user.getUser_ID());
                    startActivityForResult(intent1, 2);
                    break;
                case R.id.nav_info:
                    intent1 = new Intent(GetContext.getContext(), Info.class);
                    intent1.putExtra("user_info", user);
                    startActivityForResult(intent1, 1);
                    break;
            }
            return true;
        }
    };
    private User user;
    private TextView headername,toolbar_trade,toolbar_carpool;
    private CircleImageView headerimage;
    private View headerLayout;
//两个布尔值，分别代表点击标题后，是否可以刷新
private Boolean tradeCanFresh, carCanFresh;
    private Fragment tradeFragment, carpoolFragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //从上一活动中接收来的用户信息
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user_info");
//        LogUtil.e("-----Trade接收Login传来的user对象", user.toString());
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        headerLayout = navigationView.getHeaderView(0);
        headerimage = headerLayout.findViewById(R.id.header_image);
        headerimage.setOnClickListener(this);
//        显示头像及昵称
        showInfo(user);
        //设置toolbar及导航图标
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         toolbar_trade = findViewById(R.id.toolbar_trade);
        toolbar_carpool = findViewById(R.id.toolbar_carpool);
        toolbar_trade.setOnClickListener(this);
        toolbar_carpool.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.nav);
        }
//        默认碎片是交易页面；
        fragmentManager = getSupportFragmentManager();
        showFragment(1);
        toolbar_trade.setTextColor(getResources().getColor(R.color.colorAccent));
//        左侧导航栏
        navigationView.setNavigationItemSelectedListener(tOnNavigationItemSelectedListener);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_trade:
                toolbar_trade.setTextColor(getResources().getColor(R.color.colorAccent));
                toolbar_carpool.setTextColor(getResources().getColor(R.color.black0));
                if (tradeCanFresh) {
                    TradeFragment tradeFragment = (TradeFragment) getSupportFragmentManager().findFragmentByTag("fragment1");
                    tradeFragment.refresh();
                }else {
                    showFragment(1);
                }
                break;
            case R.id.toolbar_carpool:
                toolbar_trade.setTextColor(getResources().getColor(R.color.black0));
                toolbar_carpool.setTextColor(getResources().getColor(R.color.colorAccent));
                if (carCanFresh) {
                    CarPoolFragment carPoolFragment = (CarPoolFragment) getSupportFragmentManager().findFragmentByTag("fragment2");
                    carPoolFragment.refresh();
                }else {
                    showFragment(2);
                }
                break;
            case R.id.header_image:
                Intent intent = new Intent(GetContext.getContext(), Info.class);
                intent.putExtra("user_info", user);
                startActivityForResult(intent, 1);
                break;
        }
    }
    private void showFragment(int index){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (index){
            case 1:

                if(tradeFragment == null){
                    tradeFragment = new TradeFragment();
                    transaction.add(R.id.replace,tradeFragment,"fragment1");
                }else {
                    transaction.show(tradeFragment);
                }
                tradeCanFresh = true;
                carCanFresh = false;
                break;
            case 2:
//                hideFragment(transaction);
                if(carpoolFragment == null){
                    carpoolFragment = new CarPoolFragment();
                    transaction.add(R.id.replace,carpoolFragment,"fragment2");
                }else {

                    transaction.show(carpoolFragment);
                }
                carCanFresh = true;
                tradeCanFresh = false;
                break;
        }
        transaction.commit();
    }

    //    如果已经被实例化就隐藏
    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(tradeFragment!=null){
            fragmentTransaction.hide(tradeFragment);
        }
        if(carpoolFragment!=null){
            fragmentTransaction.hide(carpoolFragment);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_item, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.publish_goods:
                intent = new Intent(GetContext.getContext(), PublishGoods.class);
                intent.putExtra("user_id", user.getUser_ID());
                startActivityForResult(intent, 3);
                //跳转到发布商品页面；
                break;
            case R.id.publish_carpool:
//                调转到发布拼车信息页面；
                intent = new Intent(GetContext.getContext(), PublishCar.class);
                intent.putExtra("user_id", user.getUser_ID());
                startActivityForResult(intent, 2);
                break;
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(GetContext.getContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            ActivityCollector.finishAll();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        2,3表示拼车历史与发布，交易历史预发布
//        LogUtil.e("---", "Home接收Info传回来的状态码" + requestCode + "--" + resultCode + data.getBooleanExtra("ischanged", false));
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("user_id", user.getUser_ID());
                        jsonObject.put("password", user.getPassword());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String param = jsonObject.toString();

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param);
                    Request request = new Request.Builder()
                            .url("http://39.97.173.40:8999/login")
                            .post(requestBody)
                            .build();
                    HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            final String resposedata = response.body().string();
                            LogUtil.e("-----Trade在Info信息改变后重新请求user对象", resposedata);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Gson gson = new Gson();
                                    user = gson.fromJson(resposedata, User.class);
                                    showInfo(user);
                                }
                            });
                        }
                    });
                }
                break;
            case 2:
                if (resultCode == RESULT_OK && data.getBooleanExtra("ischanged", false)) {
                    CarPoolFragment carPoolFragment = (CarPoolFragment) getSupportFragmentManager().findFragmentByTag("fragment2");
                    if (carPoolFragment != null) {
                        LogUtil.e("为为什么未刷新", "2");
                        carPoolFragment.refresh();
                    }
                }
                break;
            case 3:
                if (resultCode == RESULT_OK && data.getBooleanExtra("ischanged", false)) {
                    TradeFragment tradeFragment = (TradeFragment) getSupportFragmentManager().findFragmentByTag("fragment1");
                    LogUtil.e("为为什么未刷新", "3");
                    tradeFragment.refresh();

                }
                break;
        }
    }

    private void showInfo(User user) {
//        更换头像
        String header_image = user.getPhoto();
        if (!header_image.equals("未设置")) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.touxiang);
            Glide.with(GetContext.getContext()).load("http://39.97.173.40:8999/file/" + header_image).apply(options).into(headerimage);
        }
        String header_Name = user.getNick_NAME();
        headername = headerLayout.findViewById(R.id.header_name);
        headername.setText(header_Name);
    }
}
