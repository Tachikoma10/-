package com.sanriyue.scw.user.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanriyue.scw.user.bean.TMember;
import com.sanriyue.scw.user.component.SmsTemplate;
import com.sanriyue.scw.user.consts.Const;
import com.sanriyue.scw.user.exp.LoginException;
import com.sanriyue.scw.user.service.MemberService;
import com.sanriyue.scw.user.vo.req.UserRegistVo;
import com.sanriyue.scw.user.vo.resp.UserRespVo;
import com.sanriyue.scw.vo.resp.AppResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags="用户微服务:处理登录-注册")
@Slf4j
@RestController//返回结果统一AppResponse<T>，服务端是不跳转页面的
public class MemberController {
	
	@Autowired
	SmsTemplate smsTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	MemberService memberService;
	
	@ApiOperation("用户登录")
	@ApiImplicitParams({
	@ApiImplicitParam(name="loginacct",value="用户名",required=true),
	@ApiImplicitParam(name="userpswd",value="密码",required=true)
	})//@ApiImplicitParams：描述所有参数；@ApiImplicitParam描述某个参数
	@PostMapping("/member/login")
	public AppResponse<UserRespVo> login(String loginacct,String userpswd){
		try {
			//1.首先进行登录校验
			TMember member = memberService.getMemberByLogin(loginacct,userpswd);
			//2.对拷
			UserRespVo respVo = new UserRespVo();
			BeanUtils.copyProperties(member, respVo);
			//3.生成令牌，封装到vo中
			String token = UUID.randomUUID().toString().replaceAll("-", ""); 
			respVo.setAccessToken(token);
			//4.保存临时token
			stringRedisTemplate.opsForValue().set(token, member.getId().toString());
			return AppResponse.ok(respVo);
		} catch (LoginException e) {
			e.printStackTrace();
			AppResponse fail = AppResponse.fail(null);
			fail.setMsg(e.getMessage());
			return fail;
		} catch (Exception e) {
			e.printStackTrace();
			return AppResponse.fail(null);
		}
	}
	
	
	
	
	@ApiOperation(value="注册服务")
	@PostMapping(value="/member/regist")
	public AppResponse<Object> regist(UserRegistVo vo){
		log.debug("UserRegistVo={}",vo);
		//1.从redis中取出验证码
		String code = stringRedisTemplate.opsForValue().get(vo.getLoginacct());
		log.debug("redis中取出验证码={}",code);
		//2.验证码是否失效或一致
		if (StringUtils.isEmpty(code)) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("验证码"+Const.TIME_OUT_CODE+"分钟有效，已经失效，请重新发送");
			log.debug("验证码{}分钟有效，已经失效，请重新发送",Const.TIME_OUT_CODE);
			return resp;
		}
		if (!code.equals(vo.getCode())) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("验证码不一致，请重新输入");
			log.debug("验证码不一致，请重新输入");
			return resp;
		}
		//3.注册，并保存用户信息
		//需要用到dao和service,而方法接受的参数是vo所以要用到对拷
		TMember member = new TMember();
		BeanUtils.copyProperties(vo, member);
		
		int i = memberService.saveMember(member);
		if (i==1) {
			stringRedisTemplate.delete(vo.getLoginacct());
			AppResponse<Object> resp = AppResponse.ok(null);
			resp.setMsg("注册成功");
			return resp;
		}else {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("注册失败");
			return resp;
		}

	}
	@ApiOperation(value="发送验证码服务")
	@ApiImplicitParams(value= {
			@ApiImplicitParam(value="手机号",name="loginacct",required=true)
	})
	@PostMapping("/member/sendSms")
	public AppResponse<Object> sendSms(String loginacct){
		//生成验证码,用stringBuilder节省内存空间
		StringBuilder code = new StringBuilder();
		for (int i = 1; i <=4; i++) {
			code.append(new Random().nextInt(10));
		}
		//发送验证码
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", loginacct);
		querys.put("param", "code:"+code);
		querys.put("tpl_id", "TP1711063");//短信模板
		//判断发送是否成功。将其保存到缓存redis中。用于注册校验，需设置有效时间
		String result = smsTemplate.sendCode(querys);
		if (result==null) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("调用短信服务失败");
			return resp;
		}else {
			stringRedisTemplate.opsForValue().set(loginacct, code.toString(), 3, TimeUnit.MINUTES);
			AppResponse<Object> resp = AppResponse.ok(null);
			resp.setMsg("调用短信服务成功");
			return resp;
		}
	}
}
