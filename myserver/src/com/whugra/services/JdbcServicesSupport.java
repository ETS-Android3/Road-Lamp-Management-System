package com.whugra.services;

/**
 * Created by Hzy on 2020/4/15.
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whugra.system.db.DBUtils;

/**
 *抽象类:可以包含抽象方法的类
 *
 */
public abstract class JdbcServicesSupport implements BaseServices
{
    /**************************************************************
     * 	                       以下为属性定义
     **************************************************************/
    //语句列表
    private final List<PstmMetaData> pstmList=new ArrayList<>();

    private Map<String, Object> dto =null;

    /**
     * 为Services传递DTO
     * @param dto
     */
    @Override
    public void setDto(Map<String, Object> dto)
    {
        this.dto=dto;
    }



    /**************************************************************
     * 	                      辅助方法
     **************************************************************/

    protected final Object getFromDto(String key)
    {
        return this.dto.get(key);
    }

    protected final void putIntoDto(String key,Object o)
    {
        this.dto.put(key,o);
    }

    protected final String[] getIdList(String key)
    {
        Object temp = this.dto.get(key);

        if(temp instanceof String[])
        {
            return (String[])temp;
        }
        else {
            return new String[] {temp.toString()};
        }
    }


    /**************************************************************
     * 	                       以下为多表级联与混合事务的实现方式
     **************************************************************/

    protected final boolean executeTransaction()throws Exception
    {
        //定义事务返回值
        boolean tag=false;
        //开启事务
        DBUtils.beginTransaction();
        try
        {
            //循环语句列表
            for(PstmMetaData pmd:this.pstmList)
            {
                //逐一执行SQL语句
                pmd.executePreparedStatement();
            }
            //提交事务
            DBUtils.commit();

            //修改返回值
            tag=true;
        }
        catch(Exception ex)
        {
            //事务回滚
            DBUtils.rollback();
            ex.printStackTrace();
        }
        finally
        {
            //结束事务
            DBUtils.endTransaction();
            //销毁所有的 语句对象
            for(PstmMetaData pmd:this.pstmList)
            {
                pmd.close();
            }
            //删除该事务执行期间注册的所有语句
            this.pstmList.clear();
        }
        return tag;
    }

    /**
     * 数据批量删除方法
     * <
     *    delete  from  table where id=?
     * >
     * @param sql      ---  sql语句
     * @param idlist   ---  主键列表
     * @return
     * @throws Exception
     */

    protected final void appendBatch(final String sql,
                                     final Object...idlist)throws Exception
    {
        PreparedStatement pstm=DBUtils.prepareStatement(sql);
        for(Object id:idlist)
        {
            pstm.setObject(1, id);
            pstm.addBatch();
        }

        this.regPstmObject(pstm);
    }

    /**
     * 单一状态批处理
     * <
     *    适合如下SQL语句
     *    update table
     *       set col=?
     *     where id=?
     * >
     * @param sql      ---  sql语句
     * @param newState ---  单一列的目标状态
     * @param idlist   ---  主键数组
     * @return
     */
    protected final void appendBatch(final String sql,
                                     final Object newState,
                                     final Object...idlist)throws Exception
    {
        PreparedStatement pstm=DBUtils.prepareStatement(sql);
        pstm.setObject(1, newState);
        for(Object id:idlist)
        {
            pstm.setObject(2, id);
            pstm.addBatch();
        }
        this.regPstmObject(pstm);
    }

    /**
     * 多状态修改
     * <
     *   update table
     *     set col1=?,col2=?,col3=?...coln=?
     *   where id=?
     *   例如:给一批员工上调职级并同时加薪
     *   update ac01
     *       set aac111=aac111+?,aac112=?
     *     where aac101=?
     * >
     * @param sql
     * @param stateList
     * @param idlist
     * @return
     * @throws Exception
     */
    protected final void appendBatch(final String sql, final Object stateList[],Object...idlist)throws Exception
    {
        PreparedStatement pstm=DBUtils.prepareStatement(sql);
        int index=1;
        for(Object state:stateList)
        {
            pstm.setObject(index++, state);
        }
        for(Object id:idlist)
        {
            pstm.setObject(index, id);
            pstm.addBatch();
        }
        this.regPstmObject(pstm);
    }


    /**
     * 向语句列表,注册SQL语句
     * @param pstm
     */
    private void regPstmObject(PreparedStatement pstm)
    {
        //构造Pstm描述对象
        PstmMetaData pmd=new PstmMetaData(pstm, true);
        //添加到语句列表
        this.pstmList.add(pmd);
    }


    /**
     * 为事务添加非批处理SQL语句
     * <
     *   delete from table where id=?
     *   update table set col1=? where id=?
     *   update table set col1=?,col2=?,col3=?.... where id=?
     * >
     * @param sql      ----  执行的SQL语句
     * @param args     ----  参数列表
     * @throws Exception
     */
    protected final void apppendSql(final String sql,final Object...args)throws Exception
    {
        //1.定义JDBC接口变量
        PreparedStatement pstm=DBUtils.prepareStatement(sql);
        int index=1;
        for(Object param:args)
        {
            pstm.setObject(index++, param);
        }
        //构造pstmBean,描述语句的执行方式
        PstmMetaData pmd=new PstmMetaData(pstm, false);
        //将语句对象放入当前类的语句列表中
        this.pstmList.add(pmd);
    }



    /**************************************************************
     * 	                       以下为单一表事务更新方法
     **************************************************************/

    /**
     * 多状态修改
     * <
     *   update table
     *     set col1=?,col2=?,col3=?...coln=?
     *   where id=?
     *   例如:给一批员工上调职级并同时加薪
     *   update ac01
     *       set aac111=aac111+?,aac112=?
     *     where aac101=?
     * >
     * @param sql
     * @param stateList
     * @param idlist
     * @return
     * @throws Exception
     */
    protected final boolean batchUpdate(final String sql, final Object stateList[],Object...idlist)throws Exception
    {
        //1.定义JDBC接口变量
        PreparedStatement pstm=null;
        try
        {
            //编译SQL
            pstm=DBUtils.prepareStatement(sql);
            int index=1;
            for(Object state:stateList)  //为set列表的字段赋值
            {
                pstm.setObject(index++, state);
            }
            /**
             * 当set列表赋值结束时候,
             * index当前值就是where语句中主键列的索引
             */
            for(Object id:idlist)
            {
                pstm.setObject(index, id);
                pstm.addBatch();
            }
            return this.executeBatchTransaction(pstm);
        }
        finally
        {
            DBUtils.close(pstm);
        }
    }

    /**
     * 数据批量删除方法
     * <
     *    delete  from  table where id=?
     * >
     * @param sql      ---  sql语句
     * @param idlist   ---  主键列表
     * @return
     * @throws Exception
     */
    protected final boolean batchUpdate(final String sql,
                                        final Object...idlist)throws Exception
    {
        PreparedStatement pstm=null;
        try
        {
            pstm=DBUtils.prepareStatement(sql);
            for(Object id:idlist)
            {
                //参数赋值
                pstm.setObject(1, id);
                pstm.addBatch();
            }

            return this.executeBatchTransaction(pstm);
        }
        finally
        {
            DBUtils.close(pstm);
        }
    }


    /**
     * 单一状态批处理
     * <
     *    适合如下SQL语句
     *    update table
     *       set col=?
     *     where id=?
     * >
     * @param sql      ---  sql语句
     * @param newState ---  单一列的目标状态
     * @param idlist   ---  主键数组
     * @return
     */
    protected final boolean batchUpdate(final String sql,
                                        final Object newState,
                                        final Object...idlist)throws Exception
    {
        //1.定义JDBC接口变量
        PreparedStatement pstm=null;
        try
        {
            //编译SQL语句
            pstm=DBUtils.prepareStatement(sql);
            //参数赋值---set列表数据项赋值
            pstm.setObject(1, newState);
            //循环主键数组
            for(Object id:idlist)
            {
                //主键列赋值
                pstm.setObject(2, id);
                //将准备好的SQL语句放入缓冲区
                pstm.addBatch();
            }
            return this.executeBatchTransaction(pstm);
        }
        finally
        {
            DBUtils.close(pstm);
        }
    }

    /**
     * 单一表批处理事务执行方法
     * @param pstm
     * @return
     * @throws Exception
     */
    private boolean executeBatchTransaction(PreparedStatement pstm)throws Exception
    {
        //1.定义事务返回值
        boolean tag=false;
        //2.开启事务
        DBUtils.beginTransaction();
        try
        {
            //3.执行批处理
            pstm.executeBatch();
            //4.提交事务
            DBUtils.commit();
            //5.修改返回值
            tag=true;
        }
        catch(Exception ex)
        {
            //回滚事务
            DBUtils.rollback();
            ex.printStackTrace();
        }
        finally
        {
            //结束事务
            DBUtils.endTransaction();
        }
        return tag;
    }


    /**************************************************************
     * 	                       以下为查询方法
     **************************************************************/
    protected final List<Map<String,String>> queryForList(final String sql, Object[] objects)throws Exception
    {
        PreparedStatement pstm = null;
        ResultSet set = null;
        try {
            //编译SQL
            pstm=DBUtils.prepareStatement(sql.toString());
            //执行SQL
            int index=1;
            for(Object o:objects) {
                pstm.setObject(index++, o);
            }
            set=pstm.executeQuery();
            //获取结果集描述对象
            ResultSetMetaData rsmd=set.getMetaData();
            //查询的列数
            int count=rsmd.getColumnCount();
            //计算安全的初始容量
            int initSize=(int)(count/0.75)+1;
            //定义装载当前行数据的Map容器变量
            Map<String,String> temp = null;
            //定义List容器,装载整个查询结果
            List<Map<String,String>> list=new ArrayList<>(initSize);
            //循环结果集
            while (set.next()) {
                //实例化当前行数据的承载容器
                temp=new HashMap<>();
                for(int i=1;i<=count;i++) {
                    //完成列级映射
                    temp.put(rsmd.getColumnLabel(i).toLowerCase(), set.getString(i));
                }
                //向list中放入当前行数据
                list.add(temp);
            }
            return list;
        }
        finally {
            DBUtils.close(pstm);
            DBUtils.close(set);
        }
    }


    protected final List<Map<String,String>> queryForList(final String sql)throws Exception
    {
        //定义JDBC接口变量
        PreparedStatement pstm=null;
        ResultSet rs=null;
        try
        {
            //编译SQL
            pstm=DBUtils.prepareStatement(sql.toString());
            //执行SQL
            rs=pstm.executeQuery();
            //获取结果集描述对象
            ResultSetMetaData rsmd=rs.getMetaData();
            //计算查询的列数
            int count=rsmd.getColumnCount();
            //计算安全的初始容量
            int initSize=((int)(count/0.75))+1;

            //定义装载查询结果的List容器对象
            List<Map<String,String>> rows=new ArrayList<>();
            //定义装载当前行数据的Map容器变量
            Map<String,String> ins=null;

            //循环控制rs指针的滚动
            while(rs.next())
            {
                //实例化当前行数据容器
                ins=new HashMap<>(initSize);
                //循环当前行所有列
                for(int i=1;i<=count;i++)
                {
                    //列级映射
                    ins.put(rsmd.getColumnLabel(i).toLowerCase(), rs.getString(i));
                }
                //将当前行数据放入List
                rows.add(ins);
            }
            return rows;
        }
        finally
        {
            DBUtils.close(rs);
            DBUtils.close(pstm);
        }
    }


    protected final Map<String,String> queryForMap(final String sql,final Object...args)throws Exception
    {
        //定义JDBC接口变量
        PreparedStatement pstm=null;
        ResultSet rs=null;
        try
        {
            //编译SQL
            pstm=DBUtils.prepareStatement(sql);
            //参数赋值
            int index=1;
            for(Object param:args)
            {
                pstm.setObject(index++, param);
            }

            //执行SQL--通过语句对象执行SQL语句,然后由结果集对象接受查询结果
            rs=pstm.executeQuery();
            //定义装载数据的容器变量
            Map<String,String> ins=null;
            //判断是否存在查询结果
            if(rs.next())
            {
                //获取结果集描述对象
                ResultSetMetaData rsmd=rs.getMetaData();
                //计算查询的列数
                int count=rsmd.getColumnCount();
                //计算安全的初始容量
                int initSize=((int)(count/0.75))+1;
                //实例化容器
                ins=new HashMap<>(initSize);
                //循环所有列
                for(int i=1;i<=count;i++)
                {
                    //完成列级映射
                    ins.put(rsmd.getColumnLabel(i).toLowerCase(), rs.getString(i));
                }
            }
            //返回数据
            return ins;
        }
        finally
        {
            DBUtils.close(rs);
            DBUtils.close(pstm);
        }
    }


    /**************************************************************
     * 	                       以下为单一表非事务更新方法
     **************************************************************/

    /**
     * 单一表数据更新通用方法
     * @param sql   --  需要执行的SQL语句
     * @param args  --  SQL语句的参数数组
     * @return
     * @throws Exception
     */
    protected final int executeUpdate(final String sql,final Object...args)throws Exception
    {
        //1.定义JDBC接口变量
        PreparedStatement pstm=null;
        try
        {
            //3.编译SQL
            pstm=DBUtils.prepareStatement(sql);
            //4.参数赋值
            int index=1;
            for(Object param:args)
            {
                pstm.setObject(index++, param);
            }
            //5.执行SQL语句
            return pstm.executeUpdate();
        }
        finally
        {
            DBUtils.close(pstm);
        }
    }

    protected boolean isNotNull(Object o) {
        return (o!=null)&&(!o.equals(""));
    }
}
