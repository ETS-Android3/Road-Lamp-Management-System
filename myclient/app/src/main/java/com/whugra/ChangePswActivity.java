package com.whugra;

import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.whugra.tools.Connector;
import okhttp3.*;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 修改密码页面
 */
public class ChangePswActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        //允许主线程等待网络请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //为修改密码按钮添加单击事件监听器
        Button buttonChangePsw = findViewById(R.id.buttonComfirm);
        buttonChangePsw.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //获取新密码
                String newPsw1 = ((EditText)findViewById(R.id.newPsw1)).getText().toString();
                String newPsw2 = ((EditText)findViewById(R.id.newPsw1)).getText().toString();

                //确认不为空
                if (newPsw1.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"请输入新密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPsw2.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"请确认密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                //确认两次输入密码一致
                if(!newPsw1.equals(newPsw2))
                {
                    Toast.makeText(getApplicationContext(),"两次输入的密码不相同！",Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取用户id
                String uid = getIntent().getExtras().getString("uid");
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("uid",uid);
                paramMap.put("password",newPsw1);
                String url = "http://10.0.2.2:8080/myserver_war_exploded/changePsw.html";
                Connector connector = new Connector(url,paramMap);
                String result = connector.connect();
                try
                {
                    //JSONObject解析数据
                    JSONObject jsonObject = new JSONObject().parseObject(result);
                    //生成结果
                    Toast.makeText(getApplicationContext(),
                            jsonObject.get("msg").equals("fail")?"修改失败":"修改成功",Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });
        //为返回按钮添加单击事件监听器
        Button buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                finish();
            }
        });


    }

}
