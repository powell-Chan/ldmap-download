package com.comleader.ldmapdownload;

import com.comleader.ldmapdownload.socket.DownLoadMapWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LdmapDownloadApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LdmapDownloadApplication.class, args);
        // 解决WebSocket不能注入的问题
        DownLoadMapWebSocket.setApplicationContext(run);


    }

}
