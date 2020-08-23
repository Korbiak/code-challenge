package com.korbiak.challenge.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(name = "fixer", url = "${fixer-url}")
public interface FixerClient {

    @GetMapping
    FixerResponse getRecentExchangeRateData();
}
