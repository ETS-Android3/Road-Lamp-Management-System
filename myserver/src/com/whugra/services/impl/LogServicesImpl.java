package com.whugra.services.impl;

import com.whugra.services.JdbcServicesSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hzy on 2020/4/24.
 */
public class LogServicesImpl extends JdbcServicesSupport
{
    public boolean addLog() throws Exception
    {
        StringBuilder sql = new StringBuilder().append("insert into maintainlog (uid, lid, mtime, logcontent) " +
                "values (?,?,?,?)");
        List<Object> paramList = new ArrayList<>();
        paramList.add(Integer.parseInt(getFromDto("uid").toString()));
        paramList.add(Integer.parseInt(getFromDto("lid").toString()));
        paramList.add(getFromDto("mtime"));
        paramList.add(getFromDto("logcontent"));
        System.out.println(getFromDto("logcontent"));
        return this.executeUpdate(sql.toString(),paramList.toArray())>0;
    }

    public List<Map<String,String>> query() throws Exception
    {
        StringBuilder sql = new StringBuilder().append("select * from maintainlog");
        return this.queryForList(sql.toString());
    }
}

