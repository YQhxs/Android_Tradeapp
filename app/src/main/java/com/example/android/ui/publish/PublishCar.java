package com.example.android.ui.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.gson.PublishCarPool;
import com.example.android.util.BaseActivity;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishCar extends BaseActivity {
    private static final String TAG = "PublishGoods";
    private EditText editText;
    private ImageView imageView;
    private Button button;
    private String userid;
    private boolean ischanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_car);
//        Home.java 传来的userid
        userid = getIntent().getStringExtra("user_id");
        editText = findViewById(R.id.publish_carpool_content);
        imageView = findViewById(R.id.back);
        button = findViewById(R.id.save);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ischanged", ischanged);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
//        保存
//        if("".equals(editText.getText().toString())){
//            button.setEnabled(false);
//        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if ("".equals(content)) {
                    Toast.makeText(GetContext.getContext(), "内容为空，不发布", Toast.LENGTH_SHORT).show();
                    return;
                }
                button.setEnabled(true);
                PublishCarPool publishCarPool = new PublishCarPool(editText.getText().toString(), userid);
                Gson gson = new Gson();
                RequestBody requestBody = RequestBody.Companion.create(gson.toJson(publishCarPool), MediaType.Companion.parse("application/json"));
                Request request = new Request.Builder()
                        .url("http://39.97.173.40:8999/carpool/addcarpoolinfo")
                        .post(requestBody)
                        .build();
                HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        LogUtil.e(TAG, e + "");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    }
                });
//                要放在异步处理之后
                Toast.makeText(GetContext.getContext(), "发布成功", Toast.LENGTH_SHORT).show();
                ischanged = true;
                Intent intent = new Intent();
                intent.putExtra("ischanged", ischanged);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("ischanged", ischanged);
        setResult(RESULT_OK, intent);
        finish();
    }
}
