package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author jason
 * @email ya850805@gmail.com
 * @date 2021-08-23 00:38:08
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
