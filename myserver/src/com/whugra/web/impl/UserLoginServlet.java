package com.whugra.web.impl;

import com.whugra.web.support.UserControllerSupport;

/**
 * Created by Hzy on 2020/4/16.
 */
public class UserLoginServlet extends UserControllerSupport
{
    @Override
    public void execute() throws Exception
    {
        this.queryByID();
    }
}
