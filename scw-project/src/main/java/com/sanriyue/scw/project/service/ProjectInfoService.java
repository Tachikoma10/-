package com.sanriyue.scw.project.service;

import java.util.List;

import com.sanriyue.scw.project.bean.TProject;
import com.sanriyue.scw.project.bean.TProjectImages;
import com.sanriyue.scw.project.bean.TReturn;
import com.sanriyue.scw.project.bean.TTag;
import com.sanriyue.scw.project.bean.TType;


public interface ProjectInfoService {

	List<TType> getProjectTypes();

	List<TTag> getAllProjectTags(); 

	TProject getProjectInfo(Integer projectId);

	List<TReturn> getProjectReturns(Integer projectId);

	List<TProject> getAllProjects();

	List<TProjectImages> getProjectImages(Integer id);

	TReturn getProjectReturnById(Integer retId);

}
