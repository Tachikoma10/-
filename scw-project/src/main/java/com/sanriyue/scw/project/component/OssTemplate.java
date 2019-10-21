package com.sanriyue.scw.project.component;


import java.io.FileInputStream;
import java.io.InputStream;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import lombok.Data;
@Data
public class OssTemplate {

	// Endpoint以杭州为例，其它Region请按实际情况填写。
	String endpoint;
	// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
	String accessKeyId;
	String accessKeySecret;
	String bucketName;
	String path;
	public String upload(InputStream inputStream,String fileName){
		try {
			// 创建OSSClient实例。
			OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
			// 上传文件流。
			inputStream = new FileInputStream("D:/photo/shan.jpg");
			ossClient.putObject(bucketName, "三笠/"+fileName, inputStream);
			// 关闭OSSClient。
			ossClient.shutdown();
			return "http://"+bucketName+"."+endpoint+path+"/"+fileName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
		
	}
}
