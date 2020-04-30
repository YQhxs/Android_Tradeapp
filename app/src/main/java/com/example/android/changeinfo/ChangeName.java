package com.example.android.changeinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.gson.User;
import com.example.android.util.HttpUtil;
import com.example.android.util.LogUtil;
import com.example.android.widget.InfoTitle;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class ChangeName extends AppCompatActivity implements View.OnClickListener {

    private Button save_button;
    private ImageView back_image;
    private EditText name_content;
    private Intent intent;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        InfoTitle  infoTitle = findViewById(R.id.change_name);
        save_button = infoTitle.findViewById(R.id.save);
        back_image = findViewById(R.id.back);
        save_button.setOnClickListener(this);
        back_image.setOnClickListener(this);

        name_content  = findViewById(R.id.change_name_content);
        name_content.setText(user.getNick_NAME());
        final String oldContent = name_content.getText().toString();
//        name_content.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                save_button.setEnabled(true);
////                String newContent = s.toString();
////                user.setNick_NAME(newContent);
////                name_content.setText(newContent);
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                save_button.setEnabled(true);
//                String newContent = s.toString();
//                user.setNick_NAME(newContent);
//                name_content.setText(newContent);
//            }
//        });
        name_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LogUtil.e("----键盘回车取值是什么",""+event);
                String newContent = name_content.getText().toString();
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(!oldContent.equals(newContent)){
                        user.setNick_NAME(newContent);
//                        name_content.setText(newContent);
                        save_button.setEnabled(true);
                    }else {
                        save_button.setEnabled(false);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.back:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.save:
                updatechange();
                intent.putExtra("name",user.getNick_NAME());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
    private void updatechange(){
        Gson gson = new Gson();
        String param = gson.toJson(user);
        LogUtil.d("ChangeName改变名字后，向服务器更新昵称前的user对象", "param-----" + param);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param);
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/user/update")
                .post(requestBody)
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e("服务响应错误","---"+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });

    }
}
