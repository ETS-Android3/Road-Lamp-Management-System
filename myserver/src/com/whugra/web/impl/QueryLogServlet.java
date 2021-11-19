package com.whugra.web.impl;

import com.whugra.web.support.LogControllerSupport;

/**
 * Created by Hzy on 2020/4/27.
 */
public class QueryLogServlet extends LogControllerSupport
{
    @Override
    public void execute() throws Exception
    {
        this.query();
    }
}
