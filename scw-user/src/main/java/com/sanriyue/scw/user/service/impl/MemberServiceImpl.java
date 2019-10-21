package com.sanriyue.scw.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sanriyue.scw.user.bean.TMember;
import com.sanriyue.scw.user.bean.TMemberExample;
import com.sanriyue.scw.user.bean.TMemberExample.Criteria;
import com.sanriyue.scw.user.consts.Const;
import com.sanriyue.scw.user.exp.LoginException;
import com.sanriyue.scw.user.mapper.TMemberMapper;
import com.sanriyue.scw.user.service.MemberService;
@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	TMemberMapper tMemberMapper;

	@Transactional(isolation=Isolation.DEFAULT,
			propagation=Propagation.REQUIRED,
			timeout=3,rollbackFor=Exception.class)
	@Override
	public int saveMember(TMember member) {
		//1.检验用户名唯一，也就是电话号
		
		
		//2.检验电子邮箱唯一
		
		
		//3.加密处理
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		member.setUserpswd(encoder.encode(member.getUserpswd()));
		//4.保存
		return tMemberMapper.insert(member);
	}

	@Transactional(readOnly=true)
	@Override
	public TMember getMemberByLogin(String loginacct, String userpswd) {
		TMemberExample example = new TMemberExample();
		Criteria criteria = example.createCriteria();
		criteria.andLoginacctEqualTo(loginacct);
		List<TMember> list = tMemberMapper.selectByExample(example);
		
		if (list.size()!=1) {
			//异常信息也可以用枚举类来表示,能获得更多异常信息。如状态码等
			throw new LoginException(Const.LOGINACCT_ERROR);
		}
		TMember tMember = list.get(0);
		//判断用户名密码
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(userpswd, tMember.getUserpswd())) {
			throw new LoginException(Const.USERPSWD_ERROR);
		}
		return tMember;
	}
}
