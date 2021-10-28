package com.atguigu.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Jason
 */
@EnableTransactionManagement
@MapperScan("com.atguigu.gulimall.ware.dao")
public class WareMyBatisConfig {

    //引入分頁插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //設置請求的頁面大於最大頁後操作，true調回到首頁，false繼續請求，default為false
        paginationInterceptor.setOverflow(true);
        //最大單頁限制數量，default為500，-1表示不受限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
