package com.whugra.web.impl;

import com.whugra.web.support.UserControllerSupport;

/**
 * Created by Hzy on 2020/4/23.
 */
public class AddUserServlet extends UserControllerSupport
{
    @Override
    public void execute() throws Exception
    {
        this.update("addUser");
    }
}
