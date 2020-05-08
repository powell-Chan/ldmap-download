package com.comleader.ldmapdownload.socket;

import cn.hutool.json.JSONUtil;
import com.comleader.ldmapdownload.bean.SocketResultData;
import com.comleader.ldmapdownload.service.DownMapService;
import com.comleader.ldmapdownload.util.CLStringUtil;
import com.comleader.ldmapdownload.util.HttpUtil;
import com.comleader.ldmapdownload.util.OperationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName WebSocket
 * @Description: WebSocket服务类, 统一功能: 0: 代表请求下载文件校验信息,1代表
 * @Author zhanghang
 * @Date 2020/3/31
 * @Version V1.0
 **/

/**
 * @ServerEndpoint 这个注解有什么作用？
 * <p>
 * 这个注解用于标识作用在类上，它的主要功能是把当前类标识成一个WebSocket的服务端
 * 注解的值用户客户端连接访问的URL地址
 */
@Slf4j
@Component
@ServerEndpoint("/downLoadMapSocket/{name}")
public class DownLoadMapWebSocket {

    private Session session;// 与某个客户端连接对话,需要通过他来向客户端发送消息

    private String name; // 当前连接的客户端的用户名

    private Timer scheduleTimer;

    private Timer speedTimer;

    /*
     * @description:  websocket无法通过@Autowried注入Service,需要通过如下方式进行解决
     **/
    //此处是解决无法注入的关键
    private static ApplicationContext applicationContext;
    //要注入的service
    private DownMapService downMapService;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        DownLoadMapWebSocket.applicationContext = applicationContext;
    }


    /**
     * @param session
     * @param name
     * @description: OnOpen连接打开时触发的方法
     * @return: void
     * @author: zhanghang
     * @date: 2020/4/1
     **/
    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name) {
        this.session = session;
        this.name = name;
        initDownLoad();
        DownMapService.isBusy = false;
        // 通过applicationContext注入bean
        this.downMapService = applicationContext.getBean(DownMapService.class);
        // name是用来表示唯一客户端，如果需要指定发送，需要指定发送通过name来区分
        SocketResultData socketResultData = new SocketResultData(OperationTypeEnum.SYS_SUCCESS, "WebSocket连接成功", null);
        AppointSending(JSONUtil.toJsonStr(socketResultData));
        // 发送基本信息
        SocketResultData resultData = new SocketResultData(OperationTypeEnum.DOWNLOAD_BASEPATH, CLStringUtil.BASE_PATH, null);
        AppointSending(JSONUtil.toJsonStr(resultData));
        log.info("[WebSocket] 连接成功，当前连接用户：={}");
    }

    /**
     * @param
     * @description: 断开连接触发的方法
     * @return: void
     * @author: zhanghang
     * @date: 2020/4/1
     **/
    @OnClose
    public void OnClose() {
        DownMapService.stoped = true;
        DownMapService.finished = true;
        DownMapService.isBusy = false;
        log.info("[WebSocket] 退出成功");
    }

    /**
     * @param message: 格式 json: { type : 0 , body: {}}
     * @description: 接收消息
     * @return: void
     * @author: zhanghang
     * @date: 2020/4/1
     **/
    @OnMessage
    public void OnMessage(String message) {
        try {
            log.info("[WebSocket] 收到消息：{}", message);
            // 执行下载的专用线程
            Thread thread = null;
            SocketResultData socketData = JSONUtil.toBean(message, SocketResultData.class);
            // 校验下载信息(操作类型为0)
            if (socketData.getType() == OperationTypeEnum.DOWNLOAD_READY.getType()) {
                if (DownMapService.isBusy) {
                    // 线程未执行完成
                    return;
                }
                AppointSending(JSONUtil.toJsonStr(new SocketResultData(OperationTypeEnum.SYS_SUCCESS, "准备下载", null)));
                //initDownLoad();
                DownMapService.readyCountFile = 0;
                Map<String, Object> body = downMapService.readyDownLoad(socketData.getBody());
                // 校验信息结果
                SocketResultData resultData = new SocketResultData(OperationTypeEnum.DOWNLOAD_READY, body);
                // 返回结果
                AppointSending(JSONUtil.toJsonStr(resultData));
            }

            //  停止下载(5)
            if (socketData.getType() == OperationTypeEnum.DOWNLOAD_STOP.getType()) {
                if (!DownMapService.isBusy) {
                    // 线程未执行完成
                    AppointSending(JSONUtil.toJsonStr(new SocketResultData(OperationTypeEnum.SYS_SUCCESS, "暂无下载任务", null)));
                    return;
                }
                AppointSending(JSONUtil.toJsonStr(new SocketResultData(OperationTypeEnum.SYS_SUCCESS, "正在停止...", null)));
                DownMapService.stoped = true;
                DownMapService.finished = true;
                SocketResultData resultData = new SocketResultData(OperationTypeEnum.SYS_SUCCESS, "停止下载...", null);
                // 返回结果
                AppointSending(JSONUtil.toJsonStr(resultData));
            }

            // 操作类型为1,开始下载
            if (socketData.getType() == OperationTypeEnum.DOWNLOAD_MAP.getType()) {
                if (DownMapService.isBusy) {
                    // 线程未执行完成
                    return;
                }
                initDownLoad(); // 初始化下载信息
                // 下载和发送地图(由于是单机，只有一个session，所以需要开启一个线程执行，否则执行期间服务器接收不到其他命令)
                thread = new Thread(() -> {
                    try {
                        downLoadMap(socketData, session);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppointSending(JSONUtil.toJsonStr(new SocketResultData(OperationTypeEnum.SYS_ERROR, "系统异常!!", null)));
                    }
                });
                thread.start();
            }
        } catch (Exception e) {
            AppointSending(JSONUtil.toJsonStr(new SocketResultData(OperationTypeEnum.SYS_ERROR, "系统异常!!", null)));
            e.printStackTrace();
        }
    }

    private void downLoadMap(SocketResultData socketData, Session session) throws Exception {
        // 创建定时任务向前端发送速度\进度
        // 创建定时任务向前端发送进度
        scheduleTimer = new Timer();
        // 每隔0.2秒汇报一次
        scheduleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 发送了停止命令或者已经完成
                if (DownMapService.stoped || DownMapService.finished || !session.isOpen()) {
                    DownMapService.isBusy = false;
                    SocketResultData schedule = new SocketResultData(OperationTypeEnum.DOWNLOAD_SCHEDULE, 100 + "", null);
                    AppointSending(JSONUtil.toJsonStr(schedule));
                    scheduleTimer.cancel();
                } else {
                    // 发送进度
                    SocketResultData schedule = new SocketResultData(OperationTypeEnum.DOWNLOAD_SCHEDULE, DownMapService.schedule + "", null);
                    AppointSending(JSONUtil.toJsonStr(schedule));
                }
            }
        }, 1000, 1000);
        speedTimer = new Timer();
        // 每隔三秒汇报一次
        speedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 发送了停止命令或者已经完成
                if (DownMapService.stoped || DownMapService.finished || !session.isOpen()) {
                    speedTimer.cancel();
                }else {
                    // 发送速度
                    SocketResultData speed = new SocketResultData(OperationTypeEnum.SYS_SUCCESS, "下载速度" + DownMapService.speed + "张/s", null);
                    AppointSending(JSONUtil.toJsonStr(speed));
                }
            }
        }, 2000, 2000);

        // 发送执行结果(这里是异步阻塞的,会等待downLoad执行完成)
        Map<String, Object> resBody = downMapService.downLoad(socketData.getBody(), session);

        String msg = "下载完成，总用时" + resBody.get("totalTime") + "，总大小" + resBody.get("totalSize") +
                "，总用时" + resBody.get("totalTime") + "，失败个数" + resBody.get("falidNum") + "(可选择非覆盖重新下载!)";
        SocketResultData resultData = new SocketResultData(OperationTypeEnum.SYS_SUCCESS, msg, null);
        AppointSending(JSONUtil.toJsonStr(resultData));
        DownMapService.isBusy = false;
        // 通知下载完成
        AppointSending(JSONUtil.toJsonStr(new SocketResultData(OperationTypeEnum.DOWNLOAD_FINISHED)));
    }

    private static void initDownLoad() {
        HttpUtil.totalSize = 0;
        DownMapService.finished = false;
        DownMapService.stoped = false;
        DownMapService.speed = 0;
        DownMapService.schedule = 0;
        DownMapService.countSuccessFile = 0;
        DownMapService.isBusy = true;
    }


    /**
     * @param message
     * @description: 指定发送
     * @return: void
     * @author: zhanghang
     * @date: 2020/3/31
     **/
    private void AppointSending(String message) {
        try {
            synchronized (session){
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }
}
