package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jason
 */
@Data
public class MergeVo {
    private Long purchaseId; //整單id
    private List<Long> items; //合併項集合
}
