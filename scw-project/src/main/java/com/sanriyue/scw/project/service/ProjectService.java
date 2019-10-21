package com.sanriyue.scw.project.service;

import com.sanriyue.scw.enums.ProjectStatusEnume;

public interface ProjectService {

	void saveProject(String projectToken, ProjectStatusEnume submitAuth);


}
