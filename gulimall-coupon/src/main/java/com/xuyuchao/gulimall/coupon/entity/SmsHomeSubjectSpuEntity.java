package com.xuyuchao.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 专题商品
 * 
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-08-04 17:19:28
 */
@Data
@TableName("sms_home_subject_spu")
public class SmsHomeSubjectSpuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 专题名字
	 */
	private String name;
	/**
	 * 专题id
	 */
	private Long subjectId;
	/**
	 * spu_id
	 */
	private Long spuId;
	/**
	 * 排序
	 */
	private Integer sort;

}
