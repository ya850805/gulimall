package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author jason
 * @email ya850805@gmail.com
 * @date 2021-08-23 00:52:08
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
