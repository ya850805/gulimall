package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jason
 */
@Data
public class SpuSaveVo {
    private String spuName;
    private String spuDescription;
    private long catalogId;
    private long brandId;
    private double weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}
