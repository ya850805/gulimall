package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Jason
 */
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}
