package com.comleader.ldmapdownload.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.sql.SQLOutput;

/**
 * @ClassName HttpUtil
 * @Description: Http请求类
 * @Author zhanghang
 * @Date 2020/4/2
 * @Version V1.0
 **/
@Component
public class HttpUtil {

    @Resource
    private CloseableHttpClient httpClient;

    // 自定义一个静态载体,用来承载Spring管理的类
    private static CloseableHttpClient staticHttpClient;

    @Resource
    private RequestConfig requestConfig;

    // 自定义一个静态载体,用来承载Spring管理的类
    private static RequestConfig staticRequestConfig;


    //当容器实例化当前受管Bean时@PostConstruct注解的方法会被自动触发，借此来实现静态变量初始化
    @PostConstruct
    public void init() {
        this.staticHttpClient = httpClient;
        this.staticRequestConfig = requestConfig;
    }

    /**
     * 发送httpGet请求
     *
     * @param url
     * @return
     */
    public static void downImageByGet(String url, File file) throws Exception {
        CloseableHttpResponse response = null;
        InputStream ins = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(staticRequestConfig);
            response = staticHttpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200){
                ins =  response.getEntity().getContent();
                // 写入图片
                FileUtil.writeFromStream(ins, file);
            }else {
                response.getEntity();
            }
        } finally {
            // 释放资源
            try {
                if (ins != null) ins.close();
                if (response != null) response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
