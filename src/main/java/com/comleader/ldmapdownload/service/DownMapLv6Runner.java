package com.comleader.ldmapdownload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @ClassName DownMap
 * @Description: 下载地图的启动类
 * @Author zhanghang
 * @Date 2020/4/2
 * @Version V1.0
 **/
@Component
@PropertySource(value = {"classpath:config/download-map.properties"}, encoding = "UTF-8")
@Slf4j
@Order(6)
public class DownMapLv6Runner implements ApplicationRunner {

    @Value("${map.Lv6.flag}")
    private boolean flag; // 记录前六级是否打开下载


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 是否下载前6级
        if (!flag){
            return;
        }

        DownMapLv3.downLoad();
        DownMapLv4.downLoad();
        DownMapLv5.downLoad();
        DownMapLv6.downLoad();

    }
}
