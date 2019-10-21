package com.sanriyue.scw.project.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.sanriyue.scw.enums.ProjectStatusEnume;
import com.sanriyue.scw.project.bean.TReturn;
import com.sanriyue.scw.project.component.OssTemplate;
import com.sanriyue.scw.project.consts.ProjectConstant;
import com.sanriyue.scw.project.req.BaseVo;
import com.sanriyue.scw.project.req.ProjectBaseInfoVo;
import com.sanriyue.scw.project.req.ProjectRedisStorageVo;
import com.sanriyue.scw.project.req.ProjectReturnVo;
import com.sanriyue.scw.project.service.ProjectService;
import com.sanriyue.scw.vo.resp.AppResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "项目发起模块")
@RequestMapping("/project/create")
@RestController
public class ProjectCreateController {

	@Autowired
	OssTemplate ossTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	ProjectService projectService;
	@ApiOperation(value = "项目发布第一步：同意协议")
	@PostMapping("/init")
	public AppResponse<Object> init(BaseVo vo) {
		String accessToken = vo.getAccessToken();
		String memberId  = stringRedisTemplate.opsForValue().get(accessToken);
		
		if (StringUtils.isEmpty(memberId)) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("请登录后在执行此操作");
			return resp;
		}
		
		//2.初始化大Vo，封装memberId数据
		ProjectRedisStorageVo bigVo = new ProjectRedisStorageVo();
		
		bigVo.setAccessToken(accessToken);
		bigVo.setMemberid(Integer.parseInt(memberId));
		String projectToken = UUID.randomUUID().toString().replaceAll("-", "");
		bigVo.setProjectToken(projectToken);
		
		//3.将大vo序列化为json串，放入redis中
		String bigStr = JSON.toJSONString(bigVo);
		stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+projectToken, bigStr);
		
		return AppResponse.ok(bigVo);
	}
	
	@ApiOperation(value = "项目发布第二步：提交基本项目信息和发起人信息")
	@PostMapping("/projectInfo")
	public AppResponse<Object> projectInfo(ProjectBaseInfoVo vo) {
		String accessToken = vo.getAccessToken();
		String memberId  = stringRedisTemplate.opsForValue().get(accessToken);
		
		if (StringUtils.isEmpty(memberId)) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("请登录后在执行此操作");
			return resp;
		}
		//从redis中获取大Vo的json串，然后再将其转化为大Vo对象
		String bigStr = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX+vo.getProjectToken());
		ProjectRedisStorageVo bigVo = JSON.parseObject(bigStr, ProjectRedisStorageVo.class);
		//将ProjectBaseInfoVo的信息对拷到大Vo中
		BeanUtils.copyProperties(vo, bigVo);
		//重新序列化为json串,并保存到redis中
		String bigStr2 = JSON.toJSONString(bigVo);
		stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+vo.getProjectToken(), bigStr2);
		return AppResponse.ok(bigVo);
	}
	
	@ApiOperation(value = "上传图片")
	@PostMapping("/upload")
	public AppResponse<Object> upload(@RequestParam("imagFile") MultipartFile[] imagFile) {
		List<String> fileList = new ArrayList<String>();
		
		try {
			for (MultipartFile multipartFile : imagFile) {
				InputStream inputStream = multipartFile.getInputStream();
				String originalFilename = multipartFile.getOriginalFilename();
				String fileName = UUID.randomUUID().toString().replaceAll("-", "")+"_"+originalFilename;
				String imageUrl = ossTemplate.upload(inputStream, fileName);
				
				fileList.add(imageUrl);
			}
			return AppResponse.ok(fileList);
		} catch (IOException e) {
			e.printStackTrace();
			return AppResponse.fail(null);
		}
	}
	
	@ApiOperation(value = "项目发布第三步：添加项目回报信息")
	@DeleteMapping("/return")
	public AppResponse<Object> ReturnDetail(@RequestBody List<ProjectReturnVo> voList) {
		String accessToken = voList.get(0).getAccessToken();
		String memberId  = stringRedisTemplate.opsForValue().get(accessToken);
		
		if (StringUtils.isEmpty(memberId)) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("请登录后在执行此操作");
			return resp;
		}
		
		//从redis中获取大Vo的json串，然后再将其转化为大Vo对象
		String bigStr = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX+voList.get(0).getProjectToken());
		ProjectRedisStorageVo bigVo = JSON.parseObject(bigStr, ProjectRedisStorageVo.class);
		
		//将List<ProjectReturnVo> voList的信息对拷到大Vo中
		List<TReturn> returnList = new ArrayList<>();
		for (ProjectReturnVo projectReturnVo : voList) {
			TReturn tReturn = new TReturn();
			BeanUtils.copyProperties(projectReturnVo, tReturn);
			returnList.add(tReturn);
		}
		bigVo.setProjectReturns(returnList);
		//重新序列化为json串,并保存到redis中
		String bigStr3 = JSON.toJSONString(bigVo);
		stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+voList.get(0).getProjectToken(), bigStr3);
		return AppResponse.ok(bigVo);
	}
	
	@ApiOperation(value = "项目发布第四步：项目提交审核申请")
	@PostMapping("/submit")
	//提交项目和保存草稿放在一起，提交时要注意提交项目token，用户的token
	public AppResponse<Object> submit(String accessToken,String projectToken,String ops) {
		String memberId  = stringRedisTemplate.opsForValue().get(accessToken);
		
		if (StringUtils.isEmpty(memberId)) {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("请登录后在执行此操作");
			return resp;
		}
		//提交或保存
		if ("1".equals(ops)) {
			projectService.saveProject(projectToken,ProjectStatusEnume.SUBMIT_AUTH);
			return AppResponse.ok("ok");
		}else if ("0".equals(ops)) {
			projectService.saveProject(projectToken,ProjectStatusEnume.DRAFT);
			return AppResponse.ok("ok");
		}else {
			AppResponse<Object> resp = AppResponse.fail(null);
			resp.setMsg("不支持此操作");
			return resp;
		}
		
	}
	
}
