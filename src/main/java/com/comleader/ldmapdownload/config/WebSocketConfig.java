package com.comleader.ldmapdownload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @ClassName WebSocketConfig
 * @Description: WebSocket配置类
 * @Author zhanghang
 * @Date 2020/3/31 
 * @Version V1.0
 **/
@Component
public class WebSocketConfig {

    /**
     * @descrip: WebSoeckt配置类
     * @param: []
     * @return: org.springframework.web.com.comleader.ldmapdownload.socket.server.standard.ServerEndpointExporter
     * @author: zhanghang
     * @date: 2020/3/31
     **/
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
