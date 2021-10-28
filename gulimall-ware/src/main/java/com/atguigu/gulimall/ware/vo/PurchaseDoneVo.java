package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Jason
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id; // 採購單id

    private List<PurchaseItemDoneVo> items;
}
