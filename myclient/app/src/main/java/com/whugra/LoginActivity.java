package com.whugra;

import android.content.Intent;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.whugra.tools.Connector;
import okhttp3.*;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录Activity
 */
public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //允许主线程等待网络请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Button login = findViewById(R.id.loginButton);
        //添加登录按钮监听器
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = "http://10.0.2.2:8080/myserver_war_exploded/userLogin.html";
                String username= ((EditText)findViewById(R.id.username)).getText().toString();//获取用户名
                String password = ((EditText)findViewById(R.id.password)).getText().toString();//获取密码
                if(username.equals("")||password.equals(""))
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"请输入用户名和密码",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("username",username);
                paramMap.put("password",password);
                Connector connector = new Connector(url,paramMap);
                String result= connector.connect();
                System.out.println("get"+result);
                try
                {
                    JSONObject jsonObject = new JSONObject().parseObject(result);
                    if(jsonObject.containsKey("msg"))
                    {
                        Toast.makeText(getApplicationContext(),"用户名或密码错误，请重新输入",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Map<String,Object> dataMap = (Map<String, Object>)jsonObject.get("dataMap");
                    System.out.println(dataMap);
                    String name = dataMap.get("name").toString();
                    String uid = dataMap.get("uid").toString();
                    String fids = dataMap.get("fids").toString();
                    String rids = dataMap.get("rids").toString();
                    //运行下一个activity，并放入登录获得的数据
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putCharSequence("uid",uid);
                    bundle.putCharSequence("name",name);
                    bundle.putCharSequence("rids",rids);
                    bundle.putCharSequence("fids", fids);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    //登录方法
    public void login(View view)
    {


    }
}
