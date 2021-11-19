package com.whugra.web.support;

import com.whugra.services.BaseServices;
import com.whugra.services.impl.LampServicesImpl;

/**
 * Created by Hzy on 2020/4/24.
 */
public abstract class LampControllerSupport extends BaseControllerSupport
{
    public LampControllerSupport()
    {
        this.setServices(new LampServicesImpl());
    }
}
