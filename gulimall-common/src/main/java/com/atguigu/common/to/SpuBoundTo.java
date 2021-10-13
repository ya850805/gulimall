package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Jason
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
