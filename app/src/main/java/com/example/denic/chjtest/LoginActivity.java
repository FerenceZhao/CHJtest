package com.example.denic.chjtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private String loginName;
    private String passWord;
   private  SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.login);
        EditText loginNameView= (EditText)findViewById(R.id.account);
        EditText passWordView= (EditText)findViewById(R.id.password);
         loginName=loginNameView.getText().toString();
         passWord=passWordView.getText().toString();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithHttpUrlConnection();

            }
        });
        pref=PreferenceManager.getDefaultSharedPreferences(this);
        boolean lt=pref.getBoolean("LoginType",false);
        if (lt){
            goToIndex();
        }

    }

    private void sendRequestWithHttpUrlConnection( ) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("loginName", loginName)
                            .add("passWord", passWord)
                            .build();
                    Request request = new Request.Builder()
                           // .url("http://10.0.210.104:8091/api/android/Login")
                            .url("http://192.168.8.104/CHJ_API/api/android/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    Log.d("test", responseDate);
                    parseJson(responseDate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJson(String JsonData) {
        try {
            Gson gson = new Gson();
            BackMsg_class backMsgs = gson.fromJson(JsonData, BackMsg_class.class);
            if (backMsgs.getState()) {
                SharedPreferences.Editor editor=pref.edit();
                editor.putBoolean("LoginType",true);
                editor.putString("userId",backMsgs.getMess());
                editor.apply();
                goToIndex();
            } else {
                AlertDialog.Builder builder =new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("登陆失败");
                builder.setMessage(backMsgs.getMess());
                builder.setCancelable(false);
            }
        } catch (Exception e) {

        }
    }
    private void goToIndex(){

        Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }
}
