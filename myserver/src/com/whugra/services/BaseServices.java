package com.whugra.services;

/**
 * Created by Hzy on 2020/4/15.
 */
import java.util.List;
import java.util.Map;

public interface BaseServices {

    void setDto(Map<String,Object> dto);

    default Map<String,String> findByID()
    {
        return null;
    }

    default List<Map<String,String>> query() throws Exception
    {
        return null;
    }

    default Map<String,String> queryByID() throws Exception
    {
        return null;
    }
}
