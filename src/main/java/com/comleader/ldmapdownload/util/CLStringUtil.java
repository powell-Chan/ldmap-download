package com.comleader.ldmapdownload.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.MathUtil;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DecimalFormat;

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
     * @description: 根据传入的 z\x\y来创建本地图片路径
     * @return: java.lang.String
     * @author: zhanghang
     * @date: 2020/4/2
     **/
    public static File getFullFileNotExist(Integer z, Integer x, Integer y) {
        String fullFileName;
        if (MAP_TYPE == "ArcGIS"){
            fullFileName = getArcGISFullFile(z,x,y);
        }else {
            fullFileName = BASE_PATH + z + File.separator + x + File.separator + y + ".png";
        }
        File file = new File(fullFileName);
        if (file.exists()){
            return null;
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

    /**
     * @description: 下载文件的M级大小 
     * @param 
     * @return: java.lang.String
     * @author: zhanghang
     * @date: 2020/4/9
     **/
    public static String getDownTotalSizeUnitsM(){
        File file = FileUtil.file(BASE_PATH);
        if (file != null){
            long size = FileUtil.size(file);
            return new DecimalFormat("#.00").format(size/1024.0/1024.0) + " M";
        }
        return null;
    }

    /**
     * @description: 下载文件的字节大小 
     * @param
     * @return: java.lang.String
     * @author: zhanghang
     * @date: 2020/4/9
     **/
    public static String getDownTotalSizeUnitsB(){
        File file = FileUtil.file(BASE_PATH);
        if (file != null){
            long size = FileUtil.size(file);
            return new DecimalFormat("#,###").format(size) + " 字节";
        }
        return null;
    }

}
