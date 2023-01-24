package com.xuyuchao.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuyuchao.common.utils.PageUtils;
import com.xuyuchao.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author xuyuchao
 * @email 2672424338@qq.com
 * @date 2022-07-19 10:39:21
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

