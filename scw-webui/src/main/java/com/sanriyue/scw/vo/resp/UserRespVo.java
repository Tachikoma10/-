package com.sanriyue.scw.vo.resp;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserRespVo implements Serializable{
	@ApiModelProperty("访问令牌，请妥善保管，以后每次请求都要带上")
	private String accessToken;// 访问令牌
	private String loginacct; // 存储手机号
	private String username;
	private String email;
	private String authstatus;
	private String usertype;
	private String realname;
	private String cardnum;
	private String accttype;
}
