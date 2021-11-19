package com.whugra;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SpecificLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_log);
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
        String uid = getIntent().getExtras().getString("uid");
        String lid = getIntent().getExtras().getString("lid");
        TextView textId = findViewById(R.id.textId);
        textId.setText("设备编号："+lid+"\t\t维护员编号："+uid);
        //时间
        TextView time = findViewById(R.id.time);
        time.setText(getIntent().getExtras().getString("mtime"));
        TextView content = findViewById(R.id.content);
        content.setText(getIntent().getExtras().getString("content"));
    }
}
