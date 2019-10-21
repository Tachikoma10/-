package com.sanriyue.scw.user.exp;

import org.springframework.stereotype.Component;

public class LoginException extends RuntimeException {

	public LoginException() {
	}

	public LoginException(String message) {
		super(message);
	}
	
}
