package com.whugra.web;

/**
 * Created by Hzy on 2020/4/15.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.whugra.web.support.BaseController;


@WebServlet("*.html")
public class BaseServlet extends HttpServlet{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Map<String, Object> attribute=null;
        try
        {
            //解析访问路径,获取目标类的名称
            //拦截请求的访问路径
            String uri=request.getRequestURI();
            //获取请求资源的主文件名
            String baseName=uri.substring(uri.lastIndexOf("/")+1).replace(".html", "");
            //获取控制器的前缀名
            String servletName=baseName.substring(0, 1).toUpperCase()+baseName.substring(1);
            //定义变量,描述所有业务控制器的基础包名称
            String packagePath="com.whugra.web.impl.";
            String name=packagePath+servletName+"Servlet";
            System.out.println(name);
            //实例化业务控制器
            BaseController controller=(BaseController)Class.forName(name).newInstance();
            //为业务控制器织入DTO切片
            controller.setDto(this.createDto(request));
            //调用流程控制方法
            controller.execute();
            //解析属性，处理控制器向页面输出的数据
            attribute=controller.getAttribute();
        }
        catch(Exception ex)
        {
            request.setAttribute("msg", "fail");
            //toPath="Error";
            ex.printStackTrace();
        }
        //request.getRequestDispatcher("/"+toPath+".jsp").forward(request, response);
        response.setContentType("application/json; charset=utf-8");
        //织入属性处理切片
        PrintWriter writer = response.getWriter();
        //使用fastjson 转换为json格式发送
        writer.write(JSONObject.toJSON(attribute).toString());
        writer.flush();
        writer.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doGet(request, response);
    }

//    private void parseRequestAttribute(HttpServletRequest request,Map<String,Object> requestAttribute)
//    {
//        //1.还原所有的键值对,形成集合
//        Set<Map.Entry<String, Object>> entrySet=rueqestAttribute.entrySet();
//        //2.循环集合
//        for(Map.Entry<String, Object> entry:entrySet)
//        {
//            //3.将map的每个键值对,转换成request的属性
//            request.setAttribute(entry.getKey(), entry.getValue());
//        }
//        //清除所有的request级属性数据
//        rueqestAttribute.clear();
//    }


    //使用getParameterMap()获取request中数据
    private Map<String, Object> createDto(HttpServletRequest request)
    {
        //1.获取数据map
        Map<String, String[]> map=request.getParameterMap();
        //2.从map中获取键值对entry，构成集合entrySet
        Set<Entry<String, String[]>> entrySet=map.entrySet();
        //3.创建结果集map
        Map<String, Object> result=new HashMap<>();
        //4.若string数组长度为一，置为string放入result中
        for(Entry<String, String[]> entry:entrySet)
        {
            if(entry.getValue().length==1)
            {
                result.put(entry.getKey(), entry.getValue()[0]);
            }else
            {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

}

