package com.comleader.ldmapdownload.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;

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
    public void init() {
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
        if (MAP_TYPE == "ArcGIS") {
            fullFileName = getArcGISFullFile(z, x, y);
        } else {
            fullFileName = BASE_PATH + File.separator + z + File.separator + x + File.separator + y + ".png";
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
        if (MAP_TYPE == "ArcGIS") {
            fullFileName = getArcGISFullFile(z, x, y);
        } else {
            fullFileName = BASE_PATH + File.separator + z + File.separator + x + File.separator + y + ".png";
        }
        File file = new File(fullFileName);
        if (file.exists()) {
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
        String l = "L" + String.format("%02d", z);
        String r = "R" + makePath(y);
        String c = "C" + makePath(x);
        String fullFileName = BASE_PATH + File.separator + l + File.separator + r + File.separator + c + ".png";
        return fullFileName;
    }


    /**
     * @param z
     * @param x
     * @param y
     * @description: getImgUrl
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
        while (str.length() < 8) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * @param
     * @description: 下载文件的字节大小
     * @return: java.lang.String
     * @author: zhanghang
     * @date: 2020/4/9
     **/
    public static String getDownTotalSize() {
        double size = HttpUtil.totalSize / 1024.0;
        if (size < 1024) {
            return new DecimalFormat("#.##").format(size) + "K";
        } else if (size < 1024 * 1024) {
            size = size / 1024.0;
            return new DecimalFormat("#.##").format(size) + "M";
        } else if (size < 1024 * 1024 * 1024) {
            size = size / 1024.0 / 1024.0;
            return new DecimalFormat("#.##").format(size) + "G";
        } else {
            size = size / 1024.0 / 1024.0 / 1024.0;
            return new DecimalFormat("#.##").format(size) + "T";
        }
    }

    /**
     * @param body
     * @description: 计算出文件总个数
     * @return: int
     * @author: zhanghang
     * @date: 2020/4/10
     **/
    public static int countFileNum(Map<String, Object> body) {
        JSONObject jsonParam = JSONUtil.parseFromMap(body);
        JSONArray level = jsonParam.getJSONArray("level");
        Double minLng = jsonParam.getDouble("minLng");
        Double minLat = jsonParam.getDouble("minLat");
        Double maxLng = jsonParam.getDouble("maxLng");
        Double maxLat = jsonParam.getDouble("maxLat");
        int count = 0;
        for (int i = 0; i < level.size(); i++) {
            Integer z = Integer.valueOf(level.get(i).toString());
            //四个坐标划定了一个矩形区域
            int minY = getOSMTileYFromLatitude(maxLat, z);
            int maxY = getOSMTileYFromLatitude(minLat, z);
            int minX = getOSMTileXFromLongitude(minLng, z);
            int maxX = getOSMTileXFromLongitude(maxLng, z);
            count += (maxX - minX) * (maxY - minY);
        }
        return count;
    }

    /**
     * @param countFileNum
     * @description: 预估文件总大小
     * @return: int
     * @author: zhanghang
     * @date: 2020/4/10
     **/
    public static String countFileSize(int countFileNum) {
        // 平均每个图片20K
        double size = countFileNum * 20;
        if (size < 1024) {
            return new DecimalFormat("#.##").format(size) + "K";
        } else if (size < 1024 * 1024) {
            size = size / 1024.0;
            return new DecimalFormat("#.##").format(size) + "M";
        } else if (size < 1024 * 1024 * 1024) {
            size = size / 1024.0 / 1024.0;
            return new DecimalFormat("#.##").format(size) + "G";
        } else {
            size = size / 1024.0 / 1024.0 / 1024.0;
            return new DecimalFormat("#.##").format(size) + "T";
        }
    }


    /**
     * 根据经度获取切片规范下的行号
     *
     * @param lon
     * @param zoom
     * @return
     */
    public static int getOSMTileXFromLongitude(double lon, int zoom) {
        return (int) (Math.floor((lon + 180) / 360 * Math.pow(2, zoom)));
    }

    /**
     * 根据纬度获取切片规范下的列号
     *
     * @param lat
     * @param zoom
     * @return
     */
    public static int getOSMTileYFromLatitude(double lat, int zoom) {
        return (int) (Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1 / Math.cos(lat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, zoom)));
    }

}
