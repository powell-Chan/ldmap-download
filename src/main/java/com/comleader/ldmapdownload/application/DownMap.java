package com.comleader.ldmapdownload.application;

import com.comleader.ldmapdownload.util.CLStringUtil;
import com.comleader.ldmapdownload.util.HttpUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

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
@Order(16)
public class DownMap implements ApplicationRunner {

    @Value("${map.threadNum}")
    private int threadNum; // 最小层级

    // 执行任务的线程池
    private ExecutorService es;

    private CompletionService<String> completionService;

    //private volatile Map<String, String> errResults = new HashMap<>();
    private List<String> errResults = new CopyOnWriteArrayList<>();

    @Value("${map.minLv}")
    private int minLv; // 最小层级

    @Value("${map.maxLv}")
    private int maxLv; // 最大层级

    @Value("${map.Lv6.flag}")
    private boolean flag; // 记录前六级是否打开下载

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (flag){
            return;
        }

        es = Executors.newFixedThreadPool(threadNum);
        completionService = new ExecutorCompletionService<>(es);
        // 监听控制台输入下载区域
        Scanner sc = new Scanner(System.in);
        System.out.println("经纬度格式如: 110.391,36.527");
        System.out.println("请输入要下载地图的左下角经纬度:");
        String leftbottom = sc.next();
        System.out.println("请输入要下载地图的右上角经纬度:");
        String righttop = sc.next();

        System.out.println("开始下载经纬度: {" + leftbottom + "," + righttop + "}");
        // 左下角经度，纬度，右上角经度，纬度。
        String[] leftLngLat = leftbottom.contains(",") ? leftbottom.split(",") : leftbottom.split("，");
        String[] rightLngLat = righttop.contains(",") ? righttop.split(",") : righttop.split("，");

        //记录异步结果
        List<Future<String>> futures = new ArrayList<>();

        // 记录总用时
        long start = System.currentTimeMillis();

        for (int z = minLv; z <= maxLv; z++) { // 层级
            //计算行列号(使用瓦片的中心点经纬度计算)
            //四个坐标划定了一个矩形区域
            int minY = getOSMTileYFromLatitude(Double.valueOf(rightLngLat[0].trim()), z);
            int maxY = getOSMTileYFromLatitude(Double.valueOf(leftLngLat[1].trim()), z);
            int minX = getOSMTileXFromLongitude(Double.valueOf(leftLngLat[0].trim()), z);
            int maxX = getOSMTileXFromLongitude(Double.valueOf(rightLngLat[0].trim()), z);
            for (int y = minY; y <= maxY; y++) { // Y轴
                for (int x = minX; x <= maxX; x++) { // X轴
                    // 多线程异步执行下载
                    Future<String> resultFulture = completionService.submit(new DownMapCallable(z, x, y));
                    // 加入集合中
                    futures.add(resultFulture);
                }
            }
        }
        // 主线程阻塞等待执行完成
        for (Future<String> future : futures) {
            Future<String> take = completionService.take();
            String result = take.get();
            System.out.println(result);
        }
        // 打印下载失败的结果
        System.out.println("Falid download List:");
        for (String errResult : errResults) {
            System.out.println(errResult);
        }
        System.out.println("Falid count num: " + errResults.size());
        long end = System.currentTimeMillis();

        // 执行退出
        System.out.println("All Task Finished!! \nTotal Time:" + (end - start) / 1000 + " s");
        System.exit(0);
    }

    /**
     * 计算分辨率
     *
     * @param maxLevel 最大级别
     */
    //public static double[] getResolutions(int maxLevel) {
    //    double max = 360.0 / 256.0;
    //    double[] resolutions = new double[maxLevel + 1];
    //    for (int z = 0; z <= maxLevel; z++) resolutions[z] = max / Math.pow(2, z);
    //    return resolutions;
    //}

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


    class DownMapCallable implements Callable<String> {

        private int z;
        private int x;
        private int y;

        public DownMapCallable(int z, int x, int y) {
            this.z = z;
            this.x = x;
            this.y = y;
        }

        @Override
        public String call() throws Exception {
            String imgUrl = null;
            try {
                //高德地图(6：影像，7：矢量，8：影像路网)
                imgUrl = CLStringUtil.getImgUrl(z, x, y);
                File file = CLStringUtil.getFullFile(z, x, y);

                // 开始下载地图
                HttpUtil.downImageByGet(imgUrl, file);
                return imgUrl + " Success";
            } catch (Exception e) {
                //log.error(imgUrl + " Down Failed");
                errResults.add("Failed: " + imgUrl + " ErrorMsg >> " + e.getMessage());
                return imgUrl + " Down Failed";
            }
        }
    }

}
