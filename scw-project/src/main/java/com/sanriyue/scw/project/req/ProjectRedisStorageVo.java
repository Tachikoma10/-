package com.sanriyue.scw.project.req;

import java.util.List;

import com.sanriyue.scw.project.bean.TReturn;

import lombok.Data;
import lombok.ToString;
/**
 * 项目后台提交全部数据的大Vo，有的数据需要用集合（分类和标签）来接受
 * @author wo
 *
 */
@Data
@ToString
public class ProjectRedisStorageVo extends BaseVo{
	//private String accessToken;
	private String projectToken;//项目发布时的临时令牌，就是标识
	private Integer memberid;//会员id 
	private List<Integer> typeids; //项目的分类id 
    private List<Integer> tagids; //项目的标签id 
    
    private String name;//项目名称 
    private String remark;//项目简介 
    private Integer money;//筹资金额 
    private Integer day;//筹资天数 
    private String headerImage;//项目头部图片 
    private List<String> detailsImage;//项目详情图片 
    
    private List<TReturn> projectReturns;//项目回报
    //发起人信息等
}
