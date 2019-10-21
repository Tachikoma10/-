package com.sanriyue.scw.vo.resp;

import com.sanriyue.scw.enums.ResponseCodeEnume;

import lombok.Data;

/**
 * 统一的表示应用返回结果，而不是表示为一个具体的类型。数据封装类
 * 用来封装以下三个要返回的属性
 * @author Administrator
 * @param <T> 返回具体的结果数据类型
 */
@Data
public class AppResponse<T> {

	private Integer code;
	private String msg;
	private T data;
	

	/**
	 * 快速响应成功
	 * @param data
	 * @return
	 */
	public static<T> AppResponse<T> ok(T data){
		AppResponse<T> resp = new AppResponse<>();
		resp.setCode(ResponseCodeEnume.SUCCESS.getCode());
		resp.setMsg(ResponseCodeEnume.SUCCESS.getMsg());
		resp.setData(data);
		return resp;
	}
	
	/**
	 * 快速响应失败
	 */
	public static<T> AppResponse<T> fail(T data){
		AppResponse<T> resp = new AppResponse<>();
		resp.setCode(ResponseCodeEnume.FAIL.getCode());
		resp.setMsg(ResponseCodeEnume.FAIL.getMsg());
		resp.setData(data);
		return resp;
	}
}
