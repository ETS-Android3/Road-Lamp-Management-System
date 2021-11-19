package com.whugra;

import android.os.StrictMode;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.alibaba.fastjson.JSONObject;
import com.whugra.tools.Connector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维护员管理界面
 */
public class ManageMtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin);
        //允许主线程等待网络请求
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //为返回按钮添加单击事件管理器
        Button returnButton = findViewById(R.id.buttonReturn);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        //为添加按钮添加单击事件
        Button addButton = findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = "http://10.0.2.2:8080/myserver_war_exploded/addUser.html";
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("rid","3");
                Connector connector = new Connector(url, paramMap);
                String result = connector.connect();
                //获取结果
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.get("msg").equals("fail"))
                {
                    Toast.makeText(getApplicationContext(),"添加失败",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //重新查询
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        //获取所有管理员信息
        String url = "http://10.0.2.2:8080/myserver_war_exploded/queryUser.html";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("rid","3");
        Connector connector = new Connector(url,paramMap);
        String result = connector.connect();
        //分析json
        JSONObject jsonObject = JSONObject.parseObject(result);
        List<Map<String,String>> dataList = (List<Map<String,String>>)jsonObject.get("dataList");
        //生成列表布局
        TableLayout tlayout = findViewById(R.id.tablelayout);
        try
        {
            for(Map<String,String> map:dataList)
            {
                //用户登录名
                String uname = map.get("uname");
                //用户名
                String name = map.get("name");
                //用户id
                final String uid = map.get("uid");
                //创建行
                TableRow tableRow = new TableRow(ManageMtActivity.this);
                //创建列
                TextView unameView = new TextView(ManageMtActivity.this);
                unameView.setText(uname);
                unameView.setWidth(120);
                TextView nameView = new TextView(ManageMtActivity.this);
                nameView.setWidth(100);
                nameView.setText(name);
                Button resetPsw = new Button(ManageMtActivity.this);
                resetPsw.setWidth(100);
                resetPsw.setText("重置密码");
                //设置重置密码按钮
                resetPsw.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String url = "http://10.0.2.2:8080/myserver_war_exploded/resetPsw.html";
                        Map<String,String> paramMap = new HashMap<>();
                        paramMap.put("uid",uid);
                        Connector connector = new Connector(url, paramMap);
                        String result = connector.connect();
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        Toast.makeText(getApplicationContext(),
                                jsonObject.get("msg").equals("fail")?"重置失败":"重置成功",Toast.LENGTH_SHORT).show();
                    }
                });
                //将列添加到行中
                tableRow.addView(unameView);
                tableRow.addView(nameView);
                tableRow.addView(resetPsw);
                //将行添加到layout中
                tlayout.addView(tableRow);
            }
        }catch (NullPointerException e)
        {

        }
    }
}
