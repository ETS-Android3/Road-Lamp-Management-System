package com.whugra.services.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hzy on 2020/4/23.
 */
public class Test
{
    public static void main(String[] args)
    {
        UserServicesImpl usi = new UserServicesImpl();
        Map<String, Object> map = new HashMap<>();
        map.put("rid","2");
        usi.setDto(map);
        try
        {
            System.out.println(usi.addUser());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
