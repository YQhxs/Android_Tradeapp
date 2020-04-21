package com.example.android;


import androidx.annotation.Nullable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;



import com.example.android.util.BaseActivity;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.example.android.util.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class LogIn extends BaseActivity implements View.OnClickListener {
    private EditText accountEdit;
    private EditText passwordEdit;
    private CheckBox rememberPassword;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String TAG = "LogIn";
    private HttpUtil httpUtil = new HttpUtil();

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
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("user", MODE_PRIVATE);
        boolean isRememberPassword = pref.getBoolean("is_remember", false);

        Button bt_login = findViewById(R.id.button_login);
        Button bt_logon = findViewById(R.id.button_logon);
        bt_login.setOnClickListener(this);
        bt_logon.setOnClickListener(this);
        accountEdit = findViewById(R.id.user_name);
        passwordEdit = findViewById(R.id.user_password);
        rememberPassword = findViewById(R.id.checkbox_hint);
        if (isRememberPassword) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPassword.setChecked(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                editor = getSharedPreferences("user", MODE_PRIVATE).edit();

                //向服务端验证用户,content_type需要改进
                Map map = new HashMap<>();
                map.put("user_id", account);
                map.put("password", password);
                Gson gson = new Gson();
                String param = gson.toJson(map);
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param);
                Request request = new Request.Builder()
                        .url("http://39.97.173.40:8999/login")
                        .post(requestBody)
                        .build();
                httpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String resposedata = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(resposedata);

                            if (jsonObject.has("error_code")) {
                                Toast.makeText(GetContext.getContext(), "账户或密码错误", Toast.LENGTH_SHORT).show();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        accountEdit.setText(null);
                                        passwordEdit.setText(null);
                                    }
                                });
                            } else {
                                if (rememberPassword.isChecked()) {
                                    editor.putBoolean("is_remember", true);
                                    editor.apply();
                                }
                                Gson gson = new Gson();
                                User user = gson.fromJson(resposedata, User.class);
                                Intent intent = new Intent(GetContext.getContext(), Trade.class);
                                intent.putExtra("user_nickName", user.getNick_NAME());
//                                后续有服务器图片时再加上
//                                if(user.getPhoto() != ""){
//                                    intent.putExtra("user_head", user.getPhoto());
//                                }

                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }
                });
                break;
            case R.id.button_logon:
                Intent intent = new Intent(GetContext.getContext(), LogOn.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String account = data.getStringExtra("return_account");
                    String password = data.getStringExtra("return_password");
                    LogUtil.d(TAG, "return_account:" + account);
                    LogUtil.d(TAG, "return_password:" + password);
                    accountEdit.setText(account);
                    passwordEdit.setText(password);
                }
                break;
        }

    }
}
