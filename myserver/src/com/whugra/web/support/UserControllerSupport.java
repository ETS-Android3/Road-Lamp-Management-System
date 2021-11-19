package com.whugra.web.support;

import com.whugra.services.impl.UserServicesImpl;

/**
 * Created by Hzy on 2020/4/16.
 */
public abstract class UserControllerSupport extends BaseControllerSupport
{
    public UserControllerSupport()
    {
        this.setServices(new UserServicesImpl());
    }
}
