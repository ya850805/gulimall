package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author jason
 * @email ya850805@gmail.com
 * @date 2021-08-23 00:56:54
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}