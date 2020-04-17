package com.example.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogIn extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            setContentView(R.layout.login_layout);
            Button bt_login = (Button)findViewById(R.id.button_login);
            Button bt_logon = (Button)findViewById(R.id.button_logon);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_login:
                break;
            case R.id.button_logon:
                break;
        }
    }
}
