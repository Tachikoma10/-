package com.sanriyue.scw.service.exp.handler;

import org.springframework.stereotype.Component;
import com.sanriyue.scw.service.MemberServiceFeign;
import com.sanriyue.scw.vo.resp.AppResponse;
import com.sanriyue.scw.vo.resp.UserRespVo;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class MemberServiceFeignHandler implements MemberServiceFeign {

	@Override
	public AppResponse<UserRespVo> login(String loginacct, String userpswd) {
		AppResponse<UserRespVo> fail = AppResponse.fail(null);
		fail.setMsg("远程调用【用户】微服务接口失败");
		log.debug("远程调用【用户】微服务接口失败");
		return fail;
	}

	
}
