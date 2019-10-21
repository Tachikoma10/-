package com.sanriyue.scw.controller;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sanriyue.scw.service.MemberServiceFeign;
import com.sanriyue.scw.vo.resp.AppResponse;
import com.sanriyue.scw.vo.resp.UserRespVo;

//前端项目不能用restController了
@Controller
public class DispatcherController {

	@Autowired
	MemberServiceFeign memberServiceFeign;
	
	@RequestMapping("/index")
	public String index(Model model) {
		
		return "index";
	}
	
	@RequestMapping("/login")
	public String login() {
		
		return "login";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		if (session!=null) {
			session.invalidate();
		}
		return "redirect:/index";
	}
	
	@RequestMapping("/doLogin")
	public String doLogin(String loginacct,String userpswd,Model model,HttpSession session) {
		AppResponse<UserRespVo> login = memberServiceFeign.login(loginacct, userpswd);
		//下面这个vo中不包含id和密码，只是有个令牌
		UserRespVo data = login.getData();
		if (data==null) {
			String msg = login.getMsg();
			model.addAttribute("msg",msg);
			return "login";
		}else {
			//登录成功，将账号和密码保存进session域中,其实就是放进redis中,解决session不一致
			session.setAttribute("userRespVo", data);
			return "redirect:/index";
		}
	}
}
