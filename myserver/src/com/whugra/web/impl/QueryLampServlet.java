package com.whugra.web.impl;

import com.whugra.web.support.LampControllerSupport;

/**
 * Created by Hzy on 2020/4/24.
 */
public class QueryLampServlet extends LampControllerSupport
{
    @Override
    public void execute() throws Exception {
        this.query();
    }
}
