package com.sanriyue.scw.project.resp;

import java.io.Serializable;
import java.util.List;

import com.sanriyue.scw.project.bean.TReturn;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProjectVo implements Serializable{

	private Integer projectId;
	private String name;// 项目名称
	private String remark;// 项目简介

	private String headerImage;// 项目头部图片
	
}
