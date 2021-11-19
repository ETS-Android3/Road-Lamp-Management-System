package com.whugra.web.support;

import com.whugra.services.impl.LogServicesImpl;

/**
 * Created by Hzy on 2020/4/24.
 */
public abstract class LogControllerSupport extends BaseControllerSupport
{
    public LogControllerSupport()
    {
        this.setServices(new LogServicesImpl());
    }
}
