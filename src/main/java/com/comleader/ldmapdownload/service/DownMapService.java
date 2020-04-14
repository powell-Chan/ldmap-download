package com.comleader.ldmapdownload.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.comleader.ldmapdownload.util.CLStringUtil;
import com.comleader.ldmapdownload.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
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
@SuppressWarnings("all")
public class DownMapService {

    @Value("${map.threadNum}")
    private int threadNum; // 最小层级

    // 执行任务的线程池
    private ExecutorService es;

    private CompletionService<String> completionService;

    //private volatile Map<String, String> errResults = new HashMap<>();
    private List<String> errResults = new CopyOnWriteArrayList<>();

    @Value("${map.Lv6.flag}")
    private Boolean lvFlag; // 记录前六级是否打开下载

    @Value("${map.download.append}")
    private boolean appendFlag; // 是否是继续下载

    public static volatile int speed; // 下载的速度(根据每秒下载的文件记录下来)

    public static volatile int schedule; // 下载进度

    public static volatile int countSuccessFile; // 下载完成的文件个数

    public static int readyCountFile; // 预计需要下载的文件

    public static boolean finished = false; // 是否执行完成

    public static volatile boolean stoped = false; // 是否停止下载了

    public static volatile boolean isBusy = false; // 正在下载

    /**
     * @param body
     * @param session
     * @description: 下载地图文件保存到本地
     * @return: java.util.Map<java.lang.String   ,   java.lang.Object>
     * @author: zhanghang
     * @date: 2020/4/13
     **/
    public Map<String, Object> downLoad(Map<String, Object> body, Session session) throws Exception {
        this.stoped = false;

        es = Executors.newFixedThreadPool(threadNum);
        completionService = new ExecutorCompletionService<>(es);
        //记录返回结果
        Map<String, Object> resBody = new HashMap<>();
        //记录异步结果
        List<Future<String>> futures = new ArrayList<>();
        // 记录总用时
        long start = System.currentTimeMillis();

        JSONObject jsonParam = JSONUtil.parseFromMap(body);
        // 是否覆盖下载
        this.appendFlag = jsonParam.getBool("appendFlag");
        // 获取要下载的层级与经纬度区域
        JSONArray level = jsonParam.getJSONArray("level");
        Double minLng = jsonParam.getDouble("minLng");
        Double minLat = jsonParam.getDouble("minLat");
        Double maxLng = jsonParam.getDouble("maxLng");
        Double maxLat = jsonParam.getDouble("maxLat");

        // 设置下载目录
        String filePath = jsonParam.getStr("filePath");
        CLStringUtil.BASE_PATH = filePath + File.separator;

        // 开始下载s
        for (int i = 0; i < level.size(); i++) { // 层级
            int z = level.getInt(i);
            //计算行列号(使用瓦片的中心点经纬度计算)
            int minY = CLStringUtil.getOSMTileYFromLatitude(maxLat, z);
            int maxY = CLStringUtil.getOSMTileYFromLatitude(minLat, z);
            int minX = CLStringUtil.getOSMTileXFromLongitude(minLng, z);
            int maxX = CLStringUtil.getOSMTileXFromLongitude(maxLng, z);
            for (int x = minX; x <= maxX; x++) { // Y轴
                for (int y = minY; y <= maxY; y++) { // X轴
                    // 多线程异步执行下载
                    Future<String> resultFulture = completionService.submit(new DownMapCallable(z, x, y));
                    // 加入集合中
                    futures.add(resultFulture);
                }
            }
        }
        // 启动一个定时器,每秒刷新下载速度
        Timer speedTimer = new Timer();
        speedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (DownMapService.stoped || DownMapService.finished) {
                    speedTimer.cancel();
                    return;
                }
                DownMapService.speed = 0;
            }
        }, 1000, 1000);
        // 刷新进度
        Timer scheduleTimer = new Timer();
        scheduleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (DownMapService.stoped || DownMapService.finished) {
                    scheduleTimer.cancel();
                    return;
                }
                DownMapService.schedule = countSuccessFile * 100 / readyCountFile; // 下载的进度
            }
        }, 200, 200);

        // 主线程阻塞等待执行完成
        for (Future<String> future : futures) {
            if (DownMapService.stoped || !session.isOpen()) {
                DownMapService.finished = true;
                stopDownLoad(futures);
                break;
            }
            completionService.take();
            Future<String> take = completionService.take();
            String result = take.get();
            speed++; // 累计到下载进度上
            countSuccessFile++;
            System.out.println(result);
        }
        // 如果发布了取消任务，则取消任务
        // 结束定时器
        speedTimer.cancel();
        long end = System.currentTimeMillis();
        // 封装执行结果
        // 失败的个数
        resBody.put("falidNum", errResults.size());
        // 总用时
        resBody.put("totalTime", (end - start) / 1000 + " s");
        // 总文件大小
        resBody.put("totalSize", CLStringUtil.getDownTotalSize());
        // 完成标志
        this.finished = true;
        return resBody;
    }

    private void stopDownLoad(List<Future<String>> futures) {
        for (Future<String> future : futures) {
            if (!future.isDone()) {
                future.cancel(false);
            }
        }
    }


    /**
     * @param body
     * @description: 下载信息确认
     * @return: java.util.Map<java.lang.String   ,   java.lang.Object>
     * @author: zhanghang
     * @date: 2020/4/13
     **/
    public Map<String, Object> readyDownLoad(Map<String, Object> body) {
        int countFileNum = CLStringUtil.countFileNum(body);
        String countFileSize = CLStringUtil.countFileSize(countFileNum);
        Map<String, Object> resBody = new HashMap<>();
        resBody.put("countFileNum", countFileNum);
        resBody.put("countFileSize", countFileSize);
        this.readyCountFile = countFileNum;
        return resBody;
    }

    // 校验格式是否正确
    private String verify(String leftbottom, String righttop) {
        String[] leftLngLat = leftbottom.contains(",") ? leftbottom.split(",") : leftbottom.split("，");
        String[] rightLngLat = righttop.contains(",") ? righttop.split(",") : righttop.split("，");
        // 左下角经纬度
        Double leftLng = Double.valueOf(leftLngLat[0].trim());
        Double leftLat = Double.valueOf(leftLngLat[1].trim());
        // 右上角经纬度
        Double rightLng = Double.valueOf(rightLngLat[0].trim());
        Double rightLat = Double.valueOf(rightLngLat[1].trim());

        if (leftLng > rightLng || leftLat > rightLat) {
            return "坐标错误,右上角的精度和纬度必须大于左下角的精度和纬度!";
        }

        return null;
    }

    /**
     * @description: 下载地图的Callable, 该类用于异步多线程下载地图
     * @author: zhanghang
     * @date: 2020/4/7
     **/
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
            File file = null;
            try {
                if (DownMapService.stoped) { // 停止下载的命令
                    DownMapService.finished = true;
                    return null;
                }
                //高德地图(6：影像，7：矢量，8：影像路网)
                imgUrl = CLStringUtil.getImgUrl(z, x, y);
                file = (!appendFlag) ? CLStringUtil.getFullFileNotExist(z, x, y) : CLStringUtil.getFullFile(z, x, y);

                // 开始下载地图
                if (file != null) {
                    HttpUtil.downImageByGet(imgUrl, file);
                    return imgUrl + " Success";
                }
                return imgUrl + " Loaded";
            } catch (Exception e) {
                e.printStackTrace();
                if (file != null && file.exists()) {
                    file.delete();
                }
                errResults.add("Failed: " + imgUrl + " ErrorMsg >> " + e.getMessage());
                return imgUrl + " Down Failed";
            }
        }
    }

}
