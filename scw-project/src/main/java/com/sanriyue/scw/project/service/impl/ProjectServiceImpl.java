package com.sanriyue.scw.project.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.sanriyue.scw.enums.ProjectStatusEnume;
import com.sanriyue.scw.project.bean.TProject;
import com.sanriyue.scw.project.bean.TProjectImages;
import com.sanriyue.scw.project.bean.TProjectTag;
import com.sanriyue.scw.project.bean.TProjectType;
import com.sanriyue.scw.project.bean.TReturn;
import com.sanriyue.scw.project.consts.ProjectConstant;
import com.sanriyue.scw.project.enums.ProjectImageTypeEnume;
import com.sanriyue.scw.project.mapper.TProjectImagesMapper;
import com.sanriyue.scw.project.mapper.TProjectMapper;
import com.sanriyue.scw.project.mapper.TProjectTagMapper;
import com.sanriyue.scw.project.mapper.TProjectTypeMapper;
import com.sanriyue.scw.project.mapper.TReturnMapper;
import com.sanriyue.scw.project.req.ProjectRedisStorageVo;
import com.sanriyue.scw.project.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	TProjectMapper tProjectMapper;
	@Autowired
	TReturnMapper tReturnMapper;
	@Autowired
	TProjectImagesMapper imagesMapper;
	@Autowired
	TProjectTypeMapper projectTypeMapper;
	@Autowired
	TProjectTagMapper projectTagMapper;
	
	@Override
	@Transactional
	public void saveProject(String projectToken, ProjectStatusEnume submitAuth) {
		//根据projectToken获取redis中的大Vo字符串转换为对象
		String bigString = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX+projectToken);
		
		ProjectRedisStorageVo bigVo = JSON.parseObject(bigString, ProjectRedisStorageVo.class);
		//将大vo中的数据对拷到多个实体类中，然后利用dao保存
		//1.保存项目数据
		TProject tProject = new TProject();
		tProject.setName(bigVo.getName());
		tProject.setRemark(bigVo.getRemark());
		tProject.setMoney(bigVo.getMoney().longValue());
		tProject.setDay(bigVo.getDay());
		
		tProject.setStatus(submitAuth.getCode()+"");
		tProject.setMemberid(bigVo.getMemberid());
		tProject.setCreatedate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		//要得到主键值，就要进行两个配置。
		//useGeneratedKeys="true" keyProperty="id"
		tProjectMapper.insertSelective(tProject);
		//下面的保存中要用到这个主键作为外键
		Integer projectId = tProject.getId();
		//2.保存项目图片数据
		String headerImage = bigVo.getHeaderImage();
		TProjectImages images = new TProjectImages();
		images.setProjectid(projectId);
		images.setImgurl(headerImage);
		images.setImgtype(ProjectImageTypeEnume.HEADER.getCode());
		imagesMapper.insertSelective(images);
		
		List<String> detailsImage = bigVo.getDetailsImage();
		for (String string : detailsImage) {
			TProjectImages image = new TProjectImages();
			image.setImgtype(ProjectImageTypeEnume.DETAILS.getCode());
			image.setProjectid(projectId);
			image.setImgurl(string);
			imagesMapper.insertSelective(image);
		}
		//3.项目回报数据
		List<TReturn> tReturnList = bigVo.getProjectReturns();
		for (TReturn tReturn : tReturnList) {
			tReturn.setProjectid(projectId);
			tReturnMapper.insertSelective(tReturn);
		}
		
		//4.项目和分类的关系数据
		//private List<Integer> typeids; 项目的分类id 
	    //private List<Integer> tagids; 项目的标签id 
		List<Integer> typeids = bigVo.getTypeids();
		for (Integer tid : typeids) {
			TProjectType tProjectType = new TProjectType();
			tProjectType.setProjectid(projectId);
			tProjectType.setTypeid(tid);
			projectTypeMapper.insertSelective(tProjectType);
		}
		//5.项目和标签的关系数据
		List<Integer> tagids = bigVo.getTagids();
		for (Integer tagid : tagids) {
			TProjectTag tProjectTag = new TProjectTag();
			tProjectTag.setProjectid(projectId);
			tProjectTag.setTagid(tagid);
			projectTagMapper.insertSelective(tProjectTag);
		}
		
		//6.清理redis中的大vo
		stringRedisTemplate.delete(ProjectConstant.TEMP_PROJECT_PREFIX+projectToken);
	}
		

}
