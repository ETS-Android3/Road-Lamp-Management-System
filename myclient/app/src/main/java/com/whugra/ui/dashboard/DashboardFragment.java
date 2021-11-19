package com.whugra.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import com.whugra.*;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private List<Integer> fidList = new ArrayList<>();

    private List<Button> buttonList = new ArrayList<>();

    private String isMt;

    private String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return root;
    }



    //重写onstart方法
    @Override
    public void onStart()
    {
        super.onStart();
        //获取用户角色列表
        isMt = "0";
        String[] ridString = getActivity().getIntent().getExtras().getString("rids").trim().split(" ");
        for(String s:ridString)
        {
            if(s.equals("3"))
            {
                isMt = "1";
            }
        }
        //获取用户id
        uid = getActivity().getIntent().getExtras().getString("uid");
        //获取用户的功能列表
        String[] fidString = getActivity().getIntent().getExtras().getString("fids").trim().split(" ");
        for (String s:fidString)
        {
            fidList.add(Integer.parseInt(s));
        }
        //生成按钮列表
        //管理员管理按钮
        Button button1 = new Button(getActivity());
        button1.setText("管理员管理");
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动管理员管理页面
                Intent intent = new Intent(getActivity(), ManageAdminActivity.class);
                startActivity(intent);
            }
        });
        //维护员管理按钮
        Button button2 = new Button((getActivity()));
        button2.setText("维护员管理");
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动维护员管理页面
                Intent intent = new Intent(getActivity(), ManageMtActivity.class);
                startActivity(intent);
            }
        });
        //路灯状态管理按钮
        Button button3 = new Button((getActivity()));
        button3.setText("路灯状态管理");
        button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动路灯管理页面
                Intent intent = new Intent(getActivity(), ManageLampActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("isMt",isMt);
                bundle.putCharSequence("uid",uid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //故障日志查看按钮
        Button button4 = new Button(getActivity());
        button4.setText("故障日志查看");
        button4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //启动查看日志页面
                Intent intent = new Intent(getActivity(), CheckLogActivity.class);
                startActivity(intent);
            }
        });
        //添加按钮到buttonList
        buttonList.add(button1);buttonList.add(button2);buttonList.add(button3);buttonList.add(button4);

        //获取布局管理器
        LinearLayout llayout = getActivity().findViewById(R.id.linearLayout);

        for(int i:fidList)
        {
            Button temp = buttonList.get(i-1);
            if(temp.getParent() != null) {
                ((ViewGroup)temp.getParent()).removeView(temp); // <- fix
            }
            llayout.addView(temp);
        }
    }
}
