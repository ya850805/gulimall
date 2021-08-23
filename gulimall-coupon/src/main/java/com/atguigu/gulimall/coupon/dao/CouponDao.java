package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author jason
 * @email ya850805@gmail.com
 * @date 2021-08-22 23:01:18
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
