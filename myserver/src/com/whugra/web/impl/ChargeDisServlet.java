package com.whugra.web.impl;

import com.whugra.web.support.LampControllerSupport;

/**
 * Created by Hzy on 2020/4/25.
 */
public class ChargeDisServlet extends LampControllerSupport
{
    @Override
    public void execute() throws Exception
    {
        this.update("charge_dis");
    }
}
