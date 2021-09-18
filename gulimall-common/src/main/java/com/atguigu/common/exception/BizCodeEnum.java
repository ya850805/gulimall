package com.atguigu.common.exception;

/**
 * @author Jason
 * <p>
 * 錯誤碼和錯誤信息定義類
 * 1. 錯誤碼定義規則為5位數字
 * 2. 前兩位表示業務場景，後三位代表錯誤碼。例如：10001。10：通用，001：系統未知異常
 * <p>
 * 錯誤碼列表：
 * 10：通用
 * 001：參數格式校驗
 * 11：商品
 * 12：訂單
 * 13：購物車
 * 14：物流
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系統未知異常"),
    VALID_EXCEPTION(10001, "參數格式校驗失敗");

    private Integer code;
    private String message;

    BizCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
