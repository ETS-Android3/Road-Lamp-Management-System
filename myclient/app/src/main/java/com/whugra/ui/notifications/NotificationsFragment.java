package com.whugra.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.whugra.*;

public class NotificationsFragment extends Fragment
{
    String name = "";
    //创建view视图方法
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        return root;
    }

    //onstart方法
    @Override
    public void onStart()
    {
        super.onStart();
        TextView nameView = getActivity().findViewById(R.id.textName);
        //加载用户名
        if(this.name.equals(""))
        {
            name = getActivity().getIntent().getExtras().getString("name");
        }
        nameView.setText(name);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //添加退出按钮监听器
        Button quit = getActivity().findViewById(R.id.buttonQuit);
        quit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        //添加修改密码按钮监听器
        Button changePsw = getActivity().findViewById(R.id.buttonChangePsw);
        changePsw.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ChangePswActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("uid",getActivity().getIntent().getExtras().getString("uid"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //添加修改用户名按钮监听器
        Button changeUname = getActivity().findViewById(R.id.buttonChangeUname);
        changeUname.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //修改成功时，更新用户名
                Intent intent = new Intent(getActivity(), ChangeUnameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("uid",getActivity().getIntent().getExtras().getString("uid"));
                intent.putExtras(bundle);
                startActivityForResult(intent, 0x11);

            }
        });
    }

    //重写onActivityResult方法 获取返回书写
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0x11 && resultCode==0x11)
        {
            this.name = data.getExtras().getString("newName");
        }
    }
}
