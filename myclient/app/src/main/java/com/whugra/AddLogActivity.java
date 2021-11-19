package com.whugra;

import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.whugra.tools.Connector;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddLogActivity extends AppCompatActivity {

    private String uid;

    private String lid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log);
        //允许主线程等待网络请求
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //为返回按钮添加单击事件管理器
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        //显示设备和用户编号
        uid = getIntent().getExtras().getString("uid");
        lid = getIntent().getExtras().getString("lid");
        TextView textId = findViewById(R.id.textId);
        textId.setText("设备编号："+lid+"\t\t维护员编号："+uid);
        //为提交按钮添加单击事件监听器
        Button addlog = findViewById(R.id.addlog);
        addlog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //获取数据
                EditText content = findViewById(R.id.content);
                String logcontent = content.getText().toString();
                Date date=new Date();
                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String mtime = simpleDateFormat.format(date);//格式化后的时间
                //发送请求
                String url = "http://10.0.2.2:8080/myserver_war_exploded/addLog.html";
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("uid",uid);
                paramMap.put("lid",lid);
                paramMap.put("mtime",mtime);
                paramMap.put("logcontent",logcontent);
                Connector connector = new Connector(url,paramMap);
                //解析结果
                String result = connector.connect();
                JSONObject jsonObject = JSONObject.parseObject(result);
                Toast.makeText(getApplicationContext(),
                        jsonObject.getString("msg").equals("success")?"提交成功":"提交失败",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
