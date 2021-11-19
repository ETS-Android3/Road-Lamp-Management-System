package com.whugra;

import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.whugra.tools.Connector;
import com.alibaba.fastjson.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路灯管理界面
 */
public class ManageLampActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lamp);
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
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //生成列表布局
        TableLayout tlayout = findViewById(R.id.tablelayout);
        tlayout.removeAllViews();
        //查询路路灯信息
        String url = "http://10.0.2.2:8080/myserver_war_exploded/queryLamp.html";
        Connector connector = new Connector(url, new HashMap<String, String>());
        String result = connector.connect();
        //解析返回数据
        JSONObject jsonObject = JSONObject.parseObject(result);
        //获取数据列表
        List<Map<String,String>> dataList = (List<Map<String,String>>)jsonObject.get("dataList");
        for(final Map<String,String> map:dataList)
        {
            String lid = map.get("lid");
            String address = map.get("address");
            String lightstate = map.get("lightstate");
            String lightstatestr = "";
            TextView textView3 = new TextView(ManageLampActivity.this);
            switch (lightstate){
                case "1":
                    lightstatestr = "照明";
                    textView3.setTextColor(Color.GREEN);
                    break;
                case "2":
                    lightstatestr = "空闲";
                    textView3.setTextColor(Color.GRAY);
                    break;
                case "3":
                    lightstatestr = "故障";
                    textView3.setTextColor(Color.RED);
                    break;
            }
            //创建行
            TableRow tableRow= new TableRow(ManageLampActivity.this);
            //创建列
            TextView textView1 = new TextView(ManageLampActivity.this);
            textView1.setText("编号："+lid+"\t");
            textView1.setWidth(70);
            TextView textView2 = new TextView(ManageLampActivity.this);
            textView2.setText(address+"\t");
            textView2.setWidth(100);
            textView3.setText(lightstatestr);
            textView3.setWidth(60);
            //查看按钮
            Button button = new Button(ManageLampActivity.this);
            button.setText("查看");
            //添加单击事件 查看路灯详细信息
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //生成json数据
                    String data = JSONObject.toJSONString(map);
                    String isMt = getIntent().getExtras().getString("isMt");
                    Intent intent = new Intent(ManageLampActivity.this,SpecificLampActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("data",data);
                    bundle.putCharSequence("uid",getIntent().getExtras().getString("uid"));
                    bundle.putCharSequence("isMt",getIntent().getExtras().getString("isMt"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            //向行中添加列
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);
            tableRow.addView(button);
            //向布局中添加行
            tlayout.addView(tableRow);
        }
    }
}
