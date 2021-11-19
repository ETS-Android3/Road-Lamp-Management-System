package com.whugra.web.impl;
import com.whugra.web.support.UserControllerSupport;
/**
 * Created by Hzy on 2020/4/21.
 */
public class ChangeNameServlet extends UserControllerSupport
{
    @Override
    public void execute() throws Exception
    {
        this.update("changeName");
    }
}
