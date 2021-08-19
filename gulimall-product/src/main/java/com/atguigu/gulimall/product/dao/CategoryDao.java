package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author jason
 * @email ya850805@gmail.com
 * @date 2021-08-19 01:41:14
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
