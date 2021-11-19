package com.whugra.services.impl;

import com.whugra.services.JdbcServicesSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by Hzy on 2020/4/24.
 */
public class LampServicesImpl extends JdbcServicesSupport
{
    //查询路灯
    @Override
    public List<Map<String, String>> query() throws Exception
    {
        StringBuilder sql = new StringBuilder().append("select * from roadlamp");
        return this.queryForList(sql.toString());
    }

    //开关路灯
    public boolean open_close() throws Exception
    {
        //获取当前照明状态
        String lightmod = this.getFromDto("isopen").toString();
        int lightstate = lightmod.equals("1")?2:1;
        //sql
        String sql = "update roadlamp set lightstate = "+lightstate+" where lid = "+this.getFromDto("lid");
        return this.executeUpdate(sql)>0;
    }

    //充放电
    public boolean charge_dis() throws Exception
    {

        //获取当前充放电状态
        String chargemod = this.getFromDto("ischarging").toString();
        int chargestate = chargemod.equals("0")?1:0;
        //sql
        String sql = "update roadlamp set chargestate = "+chargestate+" where lid = "+this.getFromDto("lid");
        System.out.println(sql);
        return this.executeUpdate(sql)>0;
    }
}
