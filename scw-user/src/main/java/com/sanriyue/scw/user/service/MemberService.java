package com.sanriyue.scw.user.service;

import com.sanriyue.scw.user.bean.TMember;

public interface MemberService {

	int saveMember(TMember member);

	TMember getMemberByLogin(String loginacct, String userpswd);
	
}
