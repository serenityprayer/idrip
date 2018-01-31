package com.github.serenity.drip.config;

import com.github.serenity.drip.base.DripWorker;
import com.github.serenity.drip.service.DripServiceImpl;
import com.github.serenity.drip.service.IDripService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IDripServiceConfig {

    @Bean(name = "dripService")
    public IDripService dripService() {
        DripServiceImpl dripService = new DripServiceImpl();
        DripWorker dripWorker = new DripWorker(1, 0);
        dripService.setDripWorker(dripWorker);
        return dripService;
    }

    //@Bean("dripService2")
    public IDripService dripService2() {
        DripServiceImpl dripService = new DripServiceImpl();
        DripWorker dripWorker = new DripWorker(1, 1);
        dripService.setDripWorker(dripWorker);
        return dripService;
    }

}
