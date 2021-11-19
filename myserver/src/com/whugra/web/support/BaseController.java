package com.whugra.web.support;

import java.util.Map;

public interface BaseController {
	//dto封装
	void setDto(Map<String,Object> dto);
	
    void execute()throws Exception;
    
    Map<String,Object> getAttribute();
}
