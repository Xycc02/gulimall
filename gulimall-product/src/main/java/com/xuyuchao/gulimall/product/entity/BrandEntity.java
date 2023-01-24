package com.xuyuchao.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.xuyuchao.common.validator.annotation.ListValue;
import com.xuyuchao.common.validator.group.AddGroup;
import com.xuyuchao.common.validator.group.UpdateGroup;
import com.xuyuchao.common.validator.group.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-18 23:37:13
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改品牌必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增品牌不能指定品牌id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo图片地址非法",groups = {AddGroup.class,UpdateGroup.class})
	@NotBlank(message = "logo图片地址不能为空",groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "品牌介绍不能为空",groups = {AddGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	//Integer类型不能使用Pattern注解,需要自定义注解
	// @Pattern(regexp = "^[01]$",message = "显示状态只能为0或1",groups = {AddGroup.class,UpdateGroup.class})
	@ListValue(values={0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	@NotNull(message = "品牌显示状态不能为空",groups = {AddGroup.class,UpdateStatusGroup.class,UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母在a-z或A-Z之间",groups = {AddGroup.class,UpdateGroup.class})
	@NotNull(message = "品牌检索首字母不能为空",groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于等于0",groups={AddGroup.class,UpdateGroup.class})
	@NotNull(message = "品牌排序字段不能为空",groups = {AddGroup.class})
	private Integer sort;

}
