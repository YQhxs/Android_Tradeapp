package com.example.android.ui.history;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.gson.CarPool;
import com.example.android.gson.CarPoolInfo;

import com.example.android.ui.OnItemTouchListener;

import com.example.android.util.BaseActivity;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HistoryCarPool extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private HistoryCarPoolAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<CarPool> mlist = new ArrayList<>();
    private String userid;
    private ImageView imageView;
    private RadioButton rbSelectAll;
    private Button edit, delete;
    private RelativeLayout layoutEdit;
    private static final String TAG = "HistoryCarPool";
    private boolean ischanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_car_pool);

        //        来自Home.java的intent信息
        userid = getIntent().getStringExtra("user_id");
        LogUtil.e(TAG,"来自Home.java的intent信息"+userid);
        imageView = findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ischanged",ischanged);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
//        删除列表功能
        layoutEdit = findViewById(R.id.layout_edit);
        edit = findViewById(R.id.save);
        edit.setText("管理");
        rbSelectAll = findViewById(R.id.rb_select_all);
        delete = findViewById(R.id.edit_dele);
        rbSelectAll.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);

        initData();
        //        显示历史记录列表项
        recyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HistoryCarPoolAdapter(mlist);
        //全选后，取消一项，解除全选按钮选中状态,选中一项后，如果已经全选，改变全选按钮状态为选中
        adapter.setListener(new HistoryCarPoolAdapter.onSelectAllListener() {
            @Override
            public void cancleSelectAll() {
                rbSelectAll.setChecked(false);
            }

            @Override
            public void selectAll() {
                rbSelectAll.setChecked(true);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if ("取消".equals(edit.getText())) {
                    LogUtil.e("HistoryCarPool里面addOnItemTouchListener方法中viewholder值", vh.toString());
                    adapter.setSelectPosition(vh.getAdapterPosition());
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(HistoryCarPool.this, "长按操作，可以跟管理按钮结合起来代做" + vh.itemView.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTradeGoods();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("ischanged",ischanged);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save://操作的显示和隐藏
                edit();
                break;
            case R.id.rb_select_all://全选/取消全选
                clickSelectAll();
                break;
            case R.id.edit_dele://删除
                del();
                break;
        }
    }

    //    a.点击顶部右侧，来显示和隐藏列表中的操作按钮和底部的全选删除按钮edit().且管理状态不能刷新，反之可以；
//    同样每次进入管理状态，所有按钮都先置位
    private void edit() {
        String s = edit.getText().toString();
        if (s.equals("管理")) {
            edit.setText("取消");

            rbSelectAll.setChecked(false);
            adapter.cancleAll();

            adapter.setShowSelectBtn(true);
            layoutEdit.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setEnabled(false);
        } else {
            edit.setText("管理");
            adapter.setShowSelectBtn(false);
            layoutEdit.setVisibility(View.GONE);
            swipeRefreshLayout.setEnabled(true);
        }
    }

    //    b.全选按钮的触发效果
    private void clickSelectAll() {
        if (adapter.isSelectAllData()) {
            rbSelectAll.setChecked(false);
            adapter.cancleAll();
        } else {
            rbSelectAll.setChecked(true);
            adapter.selectAll();
        }
    }

    //    c.删除
    private void del() {
        String allSelectIds = adapter.getAllSelectIds();
        LogUtil.e("HistoryCarPool的del方法中要删除的商品id", allSelectIds);
        if (TextUtils.isEmpty(allSelectIds)) {
            Toast.makeText(GetContext.getContext(), "未选中任何", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.delSelect();
        adapter.notifyDataSetChanged();
        ischanged = true;
        delerecordfromserver(allSelectIds);
//TODO之后完善服务端的方法
    }

    private void initData() {
        /*当直接拼接字符串时，为user_ID:test1,出问题，缺少引号""*/
//        LogUtil.e(TAG,"HistoryGoods.java里面json字段为"+"{\n\t\"user_ID\":" + userid + "\n}");
        JSONObject json = new JSONObject();
        try {
            json.put("user_ID",userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
/*当直接拼接字符串时，为user_ID:test1,出问题，缺少引号""*/
        RequestBody requestBody = RequestBody.Companion.create(json.toString(), MediaType.Companion.parse("application/json"));
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "{\n\t\"id\":"+userid+"\n}");
//        String url ="http://"+getString(R.string.testip)+":8999/carpool/getinfobyuserid";
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/carpool/getinfobyuserid")
                .post(requestBody)
                .build();

        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e("HistoryCarpool出错了", "");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                LogUtil.e(TAG,"HistoryCarPool responsedata:"+responsedata);
                Gson gson = new Gson();
                CarPoolInfo carPoolInfo = gson.fromJson(responsedata, new TypeToken<CarPoolInfo>() {
                }.getType());
                mlist.clear();
                mlist.addAll(carPoolInfo.getCarPools());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void refreshTradeGoods() {
        initData();
//     因为是活动，所以刷新时活动未重新创建所以init方法，是往里面加数据
//        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void delerecordfromserver(String allid){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("multiId",allid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),jsonObject.toString());
        LogUtil.e(TAG,"initdata:"+"{\n\t\"multiId\":"+allid+"\n}");
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/carpool/deletemulticarpool")
                .post(requestBody)
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(TAG,e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
}
