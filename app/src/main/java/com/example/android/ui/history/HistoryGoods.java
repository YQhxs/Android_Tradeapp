package com.example.android.ui.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.gson.TradeGoods;
import com.example.android.gson.Tradeinfo;
import com.example.android.ui.OnItemTouchListener;
import com.example.android.ui.details.DetailsGoods;
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
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HistoryGoods extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private HistoryGoodsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<TradeGoods> mlist = new ArrayList<>();
    private Integer id;
    private ImageView imageView;

    private RadioButton rbSelectAll;
    private Button edit, delete;
    private RelativeLayout layoutEdit;
    private static final String TAG = "HistoryGoods";
    private boolean ischanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_goods);
//        来自Home.java的intent信息
        id = getIntent().getIntExtra("id",-1);
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

        adapter = new HistoryGoodsAdapter(mlist);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new HistoryGoodsAdapter.onSelectAllListener() {
            @Override
            public void cancleSelectAll() {
                rbSelectAll.setChecked(false);
            }

            @Override
            public void selectAll() {
                rbSelectAll.setChecked(true);
            }
        });
//        管理状态时删除，普通状态可跳转
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if ("取消".equals(edit.getText())) {
                    LogUtil.e("HistoryGoods里面addOnItemTouchListener方法中viewholder值", vh.toString());
                    adapter.setSelectPosition(vh.getAdapterPosition());
                }else {
                    Intent intent = new Intent(HistoryGoods.this, DetailsGoods.class);
                    LogUtil.e("TradeFragment里面",vh.toString());
                    LogUtil.e("TradeFragment里面",vh.itemView.getId()+"");
                    intent.putExtra("id",vh.itemView.getId());
//                return;
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(GetContext.getContext(),"长按删除，还需做个清空,且删除所需id为"+vh.itemView.getId(),Toast.LENGTH_SHORT).show();
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
        LogUtil.e("HistoryGoods的del方法中要删除的商品id", allSelectIds);
        if (TextUtils.isEmpty(allSelectIds)) {
            Toast.makeText(GetContext.getContext(), "未选中任何", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.delSelect();
        ischanged = true;
        adapter.notifyDataSetChanged();
        delerecordfromserver(allSelectIds);
//TODO之后完善服务端的方法
    }
    private void initData(){
//        JSONObject jsonObject = new JSONObject();
//        try {
//            if(id == -1){
//                LogUtil.e("History里面用户id值","+id");
//                return;
//            }
//            jsonObject.put("id",id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        String param = jsonObject.toString();
//        LogUtil.e("HistoryGoods.java里面", "param-----" + param);
        /*当id如果去字符串时，不能使用简单拼接，还是使用JSONObject方式*/
        LogUtil.e(TAG,"HistoryGoods.java里面json字段为"+"{\n\t\"id\":"+id+"\n}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "{\n\t\"id\":"+id+"\n}");
//        String url ="http://"+getString(R.string.testip)+":8999/transaction/getbyid";
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/transaction/getbyid")
                .post(requestBody)
                .build();

        HttpUtil.sendOkHttpRequest(request,new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e("History出错了","");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                LogUtil.e(TAG,"History里面返回历史记录"+responsedata);
                Gson gson = new Gson();
                Tradeinfo tradeinfo = gson.fromJson(responsedata,new TypeToken<Tradeinfo>(){}.getType());
//                前期数据少
                mlist.clear();
                mlist.addAll(tradeinfo.getTradeInfo());
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
                .url("http://39.97.173.40:8999/transaction/deletemultitransinfowithseller")
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
