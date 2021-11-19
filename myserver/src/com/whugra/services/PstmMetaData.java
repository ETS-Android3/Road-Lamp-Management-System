package com.whugra.services;

/**
 * Created by Hzy on 2020/4/15.
 */
/**
 * 描述每条SQL语句的执行方式
 */

import java.sql.*;

import com.whugra.system.db.DBUtils;

class PstmMetaData
{
    private PreparedStatement pstm=null;
    private boolean           isBatch=false;
    public PstmMetaData(final PreparedStatement pstm,final boolean isBatch)
    {
        this.pstm=pstm;
        this.isBatch=isBatch;
    }

    public void executePreparedStatement()throws Exception
    {
        if(this.isBatch)
        {
            this.pstm.executeBatch();
        }
        else
        {
            this.pstm.executeUpdate();
        }
    }

    public void close()
    {
        DBUtils.close(this.pstm);
    }
}
