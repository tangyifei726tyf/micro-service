package com.fukun.order.client;

import com.fukun.order.client.hystrix.StockHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 暴露的库存相关的restful风格的api
 *
 * @author tangyifei
 * @since 2019-5-24 09:31:13
 */
@FeignClient(name = "FUKUN-STOCK", path = "fukun-stock", fallback = StockHystrix.class)
public interface StockClient {

    /**
     * 扣减库存
     *
     * @return 影响的行数
     */
    @RequestMapping(value = "/stocks", method = RequestMethod.POST)
    void reduceStock();
}