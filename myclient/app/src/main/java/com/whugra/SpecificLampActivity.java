package com.whugra;

import android.content.Intent;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.whugra.tools.Connector;

import java.util.HashMap;
import java.util.Map;

/**
 * 查看单一路灯数据页面
 */
public class SpecificLampActivity extends AppCompatActivity {

    String uid;
    String lid;
    boolean isopen;
    boolean ischarging;
    Button open_close;
    Button charge_dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_lamp);
        //允许主线程等待网络请求
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //为返回按钮添加单击事件监听器
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //解析json数据
        String data = getIntent().getExtras().getString("data");
        System.out.println(data);
        JSONObject jsonObject = JSON.parseObject(data);
        //lid
        TextView lampid = findViewById(R.id.lampid);
        lid = jsonObject.getString("lid");
        lampid.setText(lid);
        //type
        TextView type = findViewById(R.id.type);
        type.setText(jsonObject.getString("type"));
        //lightmod
        TextView lightmod = findViewById(R.id.lightmod);
        lightmod.setText(jsonObject.getString("lightmod"));
        //temperature
        TextView temperature = findViewById(R.id.temperature);
        temperature.setText(jsonObject.getString("temperature")+"℃");
        //batteryvol
        TextView batteryvol = findViewById(R.id.batteryvol);
        batteryvol.setText(jsonObject.getString("batteryvol")+"V");
        //solarpanelvol
        TextView solarpanelvol = findViewById(R.id.solarpanelvol);
        solarpanelvol.setText(jsonObject.getString("solarpanelvol")+"V");
        //dischargeele
        TextView dischargeele = findViewById(R.id.dischargeele);
        dischargeele.setText(jsonObject.getString("dischargeele")+"ma");
        //inchargeele
        TextView inchargeele = findViewById(R.id.inchargeele);
        inchargeele.setText(jsonObject.getString("inchargeele")+"ma");
        //totalincharge
        TextView totalincharge = findViewById(R.id.totalincharge);
        totalincharge.setText(jsonObject.getString("totalincharge")+"AH");
        //totaldischarge
        TextView totaldischarge = findViewById(R.id.totaldischarge);
        totaldischarge.setText(jsonObject.getString("totaldischarge")+"AH");
        //hwversion
        TextView hwversion = findViewById(R.id.hwversion);
        hwversion.setText(jsonObject.getString("hwversion"));
        //swversion
        TextView swversion = findViewById(R.id.swversion);
        swversion.setText(jsonObject.getString("swversion"));
        //updatetime
        TextView updatetime = findViewById(R.id.updatetime);
        updatetime.setText(jsonObject.getString("updatetime"));
        //address
        TextView address = findViewById(R.id.address);
        address.setText(jsonObject.getString("address"));

        //向buttonRow中添加按钮
        TableRow buttonRow = findViewById(R.id.buttonRow);
        //判断是否故障
        //获取uid
        uid = getIntent().getExtras().getString("uid");
        String lightstate = jsonObject.getString("lightstate");
        if(lightstate.equals("3"))
        {
            if (getIntent().getExtras().getString("isMt").equals("1"))
            {
                Button maintain = new Button(SpecificLampActivity.this);
                maintain.setText("维护");
                //为维护按钮添加单击事件监听器
                maintain.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(SpecificLampActivity.this,AddLogActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("uid",uid);
                        bundle.putCharSequence("lid",lid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                buttonRow.addView(maintain);
            }
        }
        else
        {
            open_close = new Button(SpecificLampActivity.this);
            isopen = lightstate.equals("1");
            open_close.setText(isopen?"关闭":"打开");
            //为开关按钮添加单击事件监听器
            open_close.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String url = "http://10.0.2.2:8080/myserver_war_exploded/openClose.html";
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("lid",lid);
                    paramMap.put("isopen",isopen?"1":"2");
                    Connector connector = new Connector(url,paramMap);
                    String result = connector.connect();
                    JSONObject jsonObject1 = JSONObject.parseObject(result);
                    boolean msg = jsonObject1.getString("msg").equals("success");
                    Toast.makeText(getApplicationContext(),
                            msg?(isopen?"关闭":"打开")+"成功":"网络错误",Toast.LENGTH_SHORT).show();
                    if(msg)
                    {
                        isopen = !isopen;
                        open_close.setText(isopen?"关闭":"打开");
                    }
                }
            });

            charge_dis = new Button(SpecificLampActivity.this);
            ischarging =jsonObject.getString("chargestate").equals("1");
            charge_dis.setText(ischarging?"停止充电":"充电");
            //为充电按钮添加单击事件监听器
            charge_dis.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String url = "http://10.0.2.2:8080/myserver_war_exploded/chargeDis.html";
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("lid",lid);
                    paramMap.put("ischarging",ischarging?"0":"1");
                    Connector connector = new Connector(url,paramMap);
                    String result = connector.connect();
                    JSONObject jsonObject1 = JSONObject.parseObject(result);
                    boolean msg = jsonObject1.getString("msg").equals("success");
                    Toast.makeText(getApplicationContext(),
                            msg?(ischarging?"停止充电":"充电")+"成功":"网络错误",Toast.LENGTH_SHORT).show();
                    if(msg)
                    {
                        ischarging = !ischarging;
                        charge_dis.setText(ischarging?"停止充电":"充电");
                    }
                }
            });
            buttonRow.addView(open_close);buttonRow.addView(charge_dis);
        }
    }
}
