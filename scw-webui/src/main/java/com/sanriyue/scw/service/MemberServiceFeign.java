package com.sanriyue.scw.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sanriyue.scw.service.exp.handler.MemberServiceFeignHandler;
import com.sanriyue.scw.vo.resp.AppResponse;
import com.sanriyue.scw.vo.resp.UserRespVo;

@FeignClient(value="SCW-USER",fallback=MemberServiceFeignHandler.class)
public interface MemberServiceFeign {
	@PostMapping("/member/login")
	public AppResponse<UserRespVo> login(
			@RequestParam(value="loginacct",required=true)String loginacct,
			@RequestParam(value="userpswd",required=true)String userpswd);
}
