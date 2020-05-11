package com.cloudwalk.runner;

import com.cloudwalk.service.CaptureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动后，自动执行改代码
 */

@Component
@Order(value = 1)
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private CaptureService captureService;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        //读取es数据写入kafka中
        captureService.writeToKafka();
    }
}
