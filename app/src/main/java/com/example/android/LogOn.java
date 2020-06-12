package com.example.android;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.util.ActivityCollector;
import com.example.android.util.BaseActivity;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LogOn extends BaseActivity implements View.OnClickListener {
    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText nextPasswordEdit;
    private HttpUtil httpUtil = new HttpUtil();
    private MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
    private static final String TAG = "LogOn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_log_on);
        accountEdit = findViewById(R.id.register_name);
        passwordEdit = findViewById(R.id.register_password);
        nextPasswordEdit = findViewById(R.id.register_next_password);
        Button register = findViewById(R.id.button_register);
        register.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        final String account = accountEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        String nextPassword = nextPasswordEdit.getText().toString();
        if (password.equals(nextPassword)) {
            String json = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", account);
                jsonObject.put("password", password);
                json = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Request request = new Request.Builder()
                    .url("http://39.97.173.40:8999/user/add")
                    .post(RequestBody.create(JSON, json))
                    .build();
            httpUtil.sendOkHttpRequest(request, new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String response_data = response.body().string();
                    LogUtil.d(TAG, "-------" + response_data);
                    try {
                        JSONObject jsonObject = new JSONObject(response_data);

                        //状态码设计有问题
                        if (jsonObject.has("success_code")) {
//                            //因为注册之后还要带数据跳转到登录页面，因此不需要添加到缓存
//                            editor = getSharedPreferences("user", MODE_PRIVATE).edit();
//                            editor.putString("account", account);
//                            editor.putString("password", password);
//                            editor.apply();

                            Intent intent = new Intent();
                            intent.putExtra("return_account", account);
                            intent.putExtra("return_password", password);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Looper.prepare();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    accountEdit.setText(null);
                                    passwordEdit.setText(null);
                                    nextPasswordEdit.setText(null);
                                }
                            });
                            Toast.makeText(GetContext.getContext(), jsonObject.getString("error_description"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            });
            //上传数据到服务端，并存储到数据库

        } else {

            Toast.makeText(GetContext.getContext(), "密码不一致", Toast.LENGTH_SHORT).show();
            nextPasswordEdit.setText(null);

        }
    }
}
