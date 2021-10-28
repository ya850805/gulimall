package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author Jason
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
