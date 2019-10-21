package com.sanriyue.test;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
/**
 * 
 * @author sanriyue
 * @deprecated 调远程接口（restTmplate,feign），发短信用的
 */
public class HttpClientTest {

	
	public static void main(String[] args) throws ClientProtocolException, IOException{
		//创建httpClient。注释相当于伪代码以后要经常写
		HttpClient httpClient = new DefaultHttpClient();
		//构造请求
		HttpGet httpGet = new HttpGet("http://www.baidu.com");
		//发出请求，收到响应
		HttpResponse response = httpClient.execute(httpGet);
		//获取响应的数据
		HttpEntity entity = response.getEntity();
		//得到响应的字符串数据
		String string = EntityUtils.toString(entity);
		
		System.out.println(string);
	}
}
