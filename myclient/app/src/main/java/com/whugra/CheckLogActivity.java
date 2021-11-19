package com.whugra;

import android.content.Intent;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.alibaba.fastjson.JSONObject;
import com.whugra.tools.Connector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看故障日志界面
 */
public class CheckLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_log);
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
        //获取所有日志信息
        String url = "http://10.0.2.2:8080/myserver_war_exploded/queryLog.html";
        Connector connector = new Connector(url, new HashMap<String, String>());
        String result = connector.connect();
        //分析json
        JSONObject jsonObject = JSONObject.parseObject(result);
        List<Map<String,String>> dataList = (List<Map<String,String>>)jsonObject.get("dataList");
        System.out.println(dataList.size());
        TableLayout tlayout = findViewById(R.id.tablelayout);
        for(Map<String,String> map:dataList)
        {
            final String uid = map.get("uid");
            final String lid = map.get("lid");
            final String mtime = map.get("mtime");
            final String content = map.get("logcontent");
            //创建行
            TableRow tableRow = new TableRow(CheckLogActivity.this);
            //创建列
            TextView uidView = new TextView(CheckLogActivity.this);
            uidView.setText("维护员："+uid);
            uidView.setWidth(80);
            TextView lidView = new TextView(CheckLogActivity.this);
            lidView.setText("设备："+lid);
            lidView.setWidth(80);
            TextView timeView = new TextView(CheckLogActivity.this);
            timeView.setWidth(80);
            timeView.setText(mtime);
            //查看日志按钮
            Button checkLog = new Button(CheckLogActivity.this);
            checkLog.setText("查看");
            //单击事件监听器
            checkLog.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(CheckLogActivity.this,SpecificLogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("uid",uid);
                    bundle.putCharSequence("lid",lid);
                    bundle.putCharSequence("mtime",mtime);
                    bundle.putCharSequence("content",content);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            //将列添加到行
            tableRow.addView(uidView);
            tableRow.addView(lidView);
            tableRow.addView(timeView);
            tableRow.addView(checkLog);
            //行添加到表
            tlayout.addView(tableRow);
        }
    }
}
