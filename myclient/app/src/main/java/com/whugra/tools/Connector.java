package com.whugra.tools;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Hzy on 2020/4/21.
 * Connector 用于发送request请求
 */
public class Connector
{
    private String url;
    private Map<String,String> paramMap;
    private String result;

    public Connector(String url, Map<String,String> paramMap)
    {
        this.url=url;
        this.paramMap=paramMap;
        this.result=null;
    }

    public String connect()
    {
        try
        {
            //创建OKHttpClient对象
            OkHttpClient client = new OkHttpClient();
            //创建builder对象，添加参数
            FormBody.Builder builder = new FormBody.Builder();
            //使用Map.Entry添加参数
            for(Entry<String,String> entry:this.paramMap.entrySet())
            {
                builder.add(entry.getKey(),entry.getValue());
            }
            //创建RequestBody对象，添加参数
            final RequestBody requestBody = builder.build();
            //根据url创建request对象
            Request request = new Request.Builder().post(requestBody).url(url).build();
            //使用enqueque方法异步发送请求
//            client.newCall(request).enqueue(new Callback()
//            {
//                @Override
//                public void onFailure(Call call, IOException e)
//                {
//                    System.out.println("连接失败");
//                }
//                @Override
//                public void onResponse(Call call, Response response) throws IOException
//                {
//                    //获取response中数据
//                    result = response.body().string();
//
//                }
//            });
            result = client.newCall(request).execute().body().string();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
}
