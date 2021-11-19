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
import com.alibaba.fastjson.JSONObject;
import com.whugra.tools.Connector;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 修改用户名界面
 */
public class ChangeUnameActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_uname);
        //允许主线程等待网络请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //为返回按钮添加监听器
        Button buttonReturn = findViewById(R.id.buttonReturn);
         buttonReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //返回mainActivity
                finish();
            }
        });
        //为修改用户名按钮添加监听器
        Button buttonComfirm = findViewById(R.id.buttonComfirm);
        buttonComfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //获取新用户名
                String newUname = ((EditText)findViewById(R.id.newUname)).getText().toString();
                if(newUname.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"请输入新用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取用户id
                Intent intent = getIntent();
                String uid = intent.getExtras().getString("uid");
                //与服务器通信修改密码
                try
                {
                    String url = "http://10.0.2.2:8080/myserver_war_exploded/ChangeName.html";
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("uid",uid);
                    paramMap.put("name",newUname);
                    Connector connector = new Connector(url,paramMap);
                    String result = connector.connect();
                    JSONObject jsonObject = new JSONObject().parseObject(result);
                    //生成结果
                    boolean b = !jsonObject.get("msg").equals("fail");
                    Toast.makeText(getApplicationContext(), b?"修改成功":"修改失败",Toast.LENGTH_SHORT).show();
                    //返回新name
                    if(b)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("newName", newUname);
                        intent.putExtras(bundle);
                        //放入返回值和intent对象
                        setResult(0x11,intent);
                    }else
                    {
                        setResult(0x01);
                    }
                    finish();
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
