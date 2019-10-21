package com.sanriyue.scw.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	//以下这个方法就称之为接口，是可以被前端来调得
	@GetMapping("/hello")
	public String hello(String name) {
		return "YES"+name;
		
	}
}
