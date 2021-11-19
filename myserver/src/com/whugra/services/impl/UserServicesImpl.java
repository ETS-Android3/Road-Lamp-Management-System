package com.whugra.services.impl;

import com.whugra.services.JdbcServicesSupport;
import com.whugra.system.tools.Tools;

import javax.jws.Oneway;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hzy on 2020/4/16.
 */
public class UserServicesImpl extends JdbcServicesSupport
{
    //登录方法
    @Override
    public Map<String,String> queryByID() throws Exception
    {
        //编写sql语句
        StringBuilder sql = new StringBuilder()
                .append("select u.uid, u.name, f.fid, r.rid from user u, ur_relation ur, role r, rf_relation rf, function f ")
                .append("where u.uid = ur.uid and ur.rid = r.rid ")
                .append("and r.rid = rf.rid and rf.fid = f.fid ")
                .append("and u.uname = ? and u.upassword = ?")
                ;
        //创建参数列表
        List<Object> paramList = new ArrayList<>();
        paramList.add(this.getFromDto("username"));
        paramList.add(this.getFromDto("password"));
        //执行sql语句
        List<Map<String,String>> resultList = this.queryForList(sql.toString(), paramList.toArray());
        if (resultList.size()==0)
        {
            return null;
        }
        //获取fid
        StringBuilder fids = new StringBuilder();
        for(Map<String,String> map:resultList)
        {
            fids.append(map.get("fid")).append(" ");
        }
        //获取rid
        StringBuilder rids = new StringBuilder();
        for(Map<String,String> map:resultList)
        {
            rids.append(map.get("rid")).append(" ");
        }
        Map<String,String> result = new HashMap<>();
        result.put("uid",resultList.get(0).get("uid"));
        result.put("name",resultList.get(0).get("name"));
        result.put("fids",fids.toString().trim());
        result.put("rids",rids.toString().trim());
        return result;
    }

    //查询方法
    @Override
    public List<Map<String,String>> query() throws Exception
    {
        //编写sql语句
        StringBuilder sql = new StringBuilder()
                .append("select u.uid, u.uname, u.name from user u, ur_relation ur where u.uid = ur.uid and ur.rid = ?");
        List<Object> paramList = new ArrayList<>();
        paramList.add(getFromDto("rid"));
        System.out.println(getFromDto("rid"));
        return this.queryForList(sql.toString(), paramList.toArray());

    }

    //修改用户名方法
    public boolean changeName() throws Exception
    {
        //sql语句：
        StringBuilder sql = new StringBuilder().append("update user u set name = ? where uid = ?");
        //创建参数列表
        List<Object> paramList = new ArrayList<>();
        paramList.add(this.getFromDto("name"));
        paramList.add(this.getFromDto("uid"));
        //执行更新
        return this.executeUpdate(sql.toString(),paramList.toArray())>0;
    }

    //修改密码方法
    public boolean changePsw() throws Exception
    {
        //sql语句：
        StringBuilder sql = new StringBuilder().append("update user u set upassword = ? where uid = ?");
        //创建参数列表
        List<Object> paramList = new ArrayList<>();
        paramList.add(this.getFromDto("password"));
        paramList.add(this.getFromDto("uid"));
        //执行更新
        return this.executeUpdate(sql.toString(),paramList.toArray())>0;
    }

    //添加用户方法
    public boolean addUser() throws Exception
    {
        //添加user表
        //决定初始用户名
        String uname = Tools.getFormatNumber("U");
        String name = getFromDto("rid").equals("2")?"admin":"maintainer";
        StringBuilder sql1 = new StringBuilder().append("insert into user(uname, upassword, name) values ('")
                .append(uname).append("', ").append("'123456', '").append(name).append("')");
        boolean result1 = this.executeUpdate(sql1.toString())>0;
        //获取uid
        StringBuilder sql2 = new StringBuilder().append("select uid from user where uname = "+uname);
        String uid = this.queryForMap(sql2.toString()).get("uid");
        //添加ur_relation表
        StringBuilder sql3 = new StringBuilder().append("insert into ur_relation(uid, rid) values (?,?)");
        List<Object> paramList2 = new ArrayList<>();
        paramList2.add(uid);
        paramList2.add(getFromDto("rid"));
        boolean result2 = this.executeUpdate(sql3.toString(),paramList2.toArray())>0;
        //返回结果
        return (result1&&result2);
    }

    //重置密码
    public boolean resetPsw() throws Exception
    {
        //sql
        StringBuilder sql = new StringBuilder().append("update user u set upassword = '123456' where uid = ?");
        List<Object> paramList = new ArrayList<>();
        paramList.add(getFromDto("uid"));
        return this.executeUpdate(sql.toString(),paramList.toArray())>0;
    }

}
