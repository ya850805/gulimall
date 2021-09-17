package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author jason
 * @email ya850805@gmail.com
 * @date 2021-08-19 01:41:14
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名稱必須提交")
    private String name;
    /**
     * 品牌logo地址
     */
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotEmpty
    @Pattern(regexp = "/^[a-zA-Z]$/", message = "檢索首字母必須是一個字母")
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull
    @Min(value = 0, message = "排序必須大於等於0")
    private Integer sort;

}
