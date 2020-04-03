package com.comleader.ldmapdownload.util;

import cn.hutool.core.io.FileUtil;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @ClassName LDStringUtil
 * @Description: ComleaderString工具类
 * @Author zhanghang
 * @Date 2020/4/2
 * @Version V1.0
 **/
@Component
@PropertySource(value = {"classpath:config/download-map.properties"}, encoding = "UTF-8")
public class CLStringUtil {

    @Value("${file.basepath}")
    private String BASE_PATH_CONFIG;

    public static String BASE_PATH;

    @Value("${map.baseurl}")
    private String BASE_URL_CONFIG;

    public static String BASE_URL;

    @Value("${map.type}")
    private String MAP_TYPE_CONFIG;

    public static String MAP_TYPE;


    //当容器实例化当前受管Bean时@PostConstruct注解的方法会被自动触发，借此来实现静态变量初始化
    @PostConstruct
    public void init(){
        this.BASE_PATH = BASE_PATH_CONFIG;
        this.BASE_URL = BASE_URL_CONFIG;
        this.MAP_TYPE = MAP_TYPE_CONFIG;
    }


    /**
     * @param z
     * @param x
     * @param y
     * @description: 根据传入的 z\x\y来创建本地图片路径
     * @return: java.lang.String
     * @author: zhanghang
     * @date: 2020/4/2
     **/
    public static File getFullFile(Integer z, Integer x, Integer y) {
        String fullFileName;
        if (MAP_TYPE == "ArcGIS"){
            fullFileName = getArcGISFullFile(z,x,y);
        }else {
            fullFileName = BASE_PATH + z + File.separator + x + File.separator + y + ".png";
        }
        return FileUtil.file(fullFileName);
    }


    /**
     * @param z
     * @param x
     * @param y
     * @description: 获取ArcGIS的图片存储路径格式
     * @return: java.lang.String
     * @author: zhanghang
     * @date: 2020/4/2
     **/
    private static String getArcGISFullFile(Integer z, Integer x, Integer y) {
        String l = "L" + String.format("%02d",z);
        String r = "R" + makePath(y);
        String c = "C" + makePath(x);
        String fullFileName = BASE_PATH + l + File.separator + r + File.separator + c + ".png";
        return fullFileName;
    }


   /**
    * @description: getImgUrl 
    * @param z
    * @param x
    * @param y
    * @return: java.lang.String
    * @author: zhanghang
    * @date: 2020/4/2
    **/
    public static String getImgUrl(Integer z, Integer x, Integer y) {
        String url = BASE_URL.replaceAll("\\{x\\}", String.valueOf(x)).replaceAll("\\{y\\}", String.valueOf(y))
                .replaceAll("\\{z\\}", String.valueOf(z));
        return url;
    }


    private static String makePath(int num) {
        String str = Integer.toHexString(num);
        //ArcGIS行列都是8位长度
        while(str.length() < 8) {
            str = "0" + str;
        }
        return str;
    }

}
