package com.sanriyue.scw.user.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sanriyue.scw.utils.HttpUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Component
public class SmsTemplate {
	@Value("${sms.host}")
	private String host;
	@Value("${sms.path}")
	private String path;
	@Value("${sms.method}")
	private String method;
	@Value("${sms.appcode}")
	private String appcode;
	
	public String sendCode(Map<String, String> querys) {
		
		 Map<String, String> headers = new HashMap<String, String>();
		 headers.put("Authorization", "APPCODE " + appcode);
	    
	    Map<String, String> bodys = new HashMap<String, String>();

	    try {
	    	HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
	    	//String string = EntityUtils.toString(response.getEntity());
	    	//log.info("短信发送成功:${}",string);
	    	System.out.println(response.toString());
	    	return response.toString();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
}
