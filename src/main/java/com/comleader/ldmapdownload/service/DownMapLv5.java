package com.comleader.ldmapdownload.service;

import com.comleader.ldmapdownload.util.CLStringUtil;
import com.comleader.ldmapdownload.util.HttpUtil;

import java.io.File;

/**
 * @ClassName DownMapLv1
 * @Description: 下载第一层(全地图)
 * @Author zhanghang
 * @Date 2020/4/3
 * @Version V1.0
 **/
public class DownMapLv5 {

    private static int z = 5;

    /**
     * @param
     * @description: 下载第四级的Map
     * @return: void
     * @author: zhanghang
     * @date: 2020/4/3
     **/
    public static void downLoad() {
        new Thread(() -> {
            for (int x = 15; x <= 31; x++) { // Y轴
                for (int y = 10; y <= 23; y++) { // X轴
                    //高德地图(6：影像，7：矢量，8：影像路网)
                    String imgUrl = CLStringUtil.getImgUrl(z, x, y);
                    File file = CLStringUtil.getFullFileNotExist(z, x, y);

                    // 开始下载地图
                    if (file != null) {
                        try {
                            HttpUtil.downImageByGet(imgUrl, file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(imgUrl+" 基础地图加载");
                    }
                }
            }
        }).start();

    }

}
