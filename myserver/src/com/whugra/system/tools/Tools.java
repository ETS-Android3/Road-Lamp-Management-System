package com.whugra.system.tools;

/**
 * Created by Hzy on 2020/4/15.
 */

import com.whugra.system.db.DBUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.sql.*;


public class Tools {
    public Tools() {}

    public static String joinArray(Object o) {
        //空值拦截
        if(o==null || o.equals(""))
        {
            return "";
        }
        //判断element到底是什么?是字符串还是字符串数组
        if(o instanceof java.lang.String[])
        {
            //将参数转换成字符串数组
            String arr[]=(String[])o;
            //计算数组长度
            int len=arr.length;
            //构造动态字符串对象,装载数组的每个元素
            StringBuilder tem=new StringBuilder(arr[0]);
            //循环读取数组的后继元素
            for(int i=1;i<len;i++)
            {
                tem.append(",").append(arr[i]);
            }
            return tem.toString();
        }
        else
        {
            return o.toString();
        }
    }


    //获取年码
    private static String getCurrentYear() {
//		//创建简单日期格式
//		SimpleDateFormat spf= new SimpleDateFormat("yyyy");
//		//生成日期
//		Date date = new Date();
//		//用简单日期格式生成日期
//		String currentYear = spf.format(date);
//		return currentYear;

        return new SimpleDateFormat("yyyy").format(new Date());
    }

    //获取尾码
    private static int getNumnberForYear(String firstName) throws Exception{
        PreparedStatement ps1=null;
        PreparedStatement ps2=null;
        ResultSet set=null;
        try {
            StringBuilder sql1 = new StringBuilder();
            sql1.append("select seqnum from sequence where date_format(seqdate,'%Y')"
                    + "=date_format(current_date(),'%Y') and seqname=?");
            ps1=DBUtils.prepareStatement(sql1.toString());
            ps1.setObject(1, firstName);
            set=ps1.executeQuery();

            int currentVal=0;
            StringBuilder sql2=new StringBuilder();
            if(set.next())
            {
                currentVal=set.getInt(1);
                sql2.append("update sequence ")
                        .append("   set seqnum=?")
                        .append(" where date_format(seqdate,'%Y')=date_format(current_date,'%Y')")
                        .append("   and seqname=?");
            }
            else
            {
                sql2.append("insert into sequence(seqnum,seqname,seqdate)")
                        .append("              values(?,?,current_date)")
                ;
            }
            ps2= DBUtils.prepareStatement(sql2.toString());
            ps2.setObject(1, ++currentVal);
            ps2.setObject(2, firstName);
            ps2.executeUpdate();
            return currentVal;
        }finally{
            DBUtils.close(set);
            DBUtils.close(ps1);
            DBUtils.close(ps2);
        }
    }

    //截取后几位
    public static String getFormatNumber(String firstName) throws Exception{
        //1.获取流水号
        int lastNumber=Tools.getNumnberForYear(firstName);
        //2.计算流水号的宽度
        int size=String.valueOf(lastNumber).length();

        return Tools.getCurrentYear()+new String("0000").substring(size)+lastNumber;
    }
}
