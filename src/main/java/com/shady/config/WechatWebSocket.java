package com.shady.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 前后端交互的类实现消息的接收推送(自己发送给自己)
 *
 */
@Slf4j
@ServerEndpoint(value = "/websocket/{userid}")   //前端通过此URI和后端交互，建立连接
@Component
public class WechatWebSocket {

    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    private static Map<String, WechatWebSocket> clients = new ConcurrentHashMap<String, WechatWebSocket>();
    private String userid;
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("userid") String userid, Session session) {
        onlineCount.incrementAndGet(); // 在线数加1
        this.userid = userid;
        this.session = session;
        clients.put(userid, this);
        log.info("有新连接加入, sessionID：{}, userID: {},当前在线人数为：{}", session.getId(), userid, onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        clients.remove(userid);
        log.info("有一连接关闭, sessionID：{}, userID: {}, 当前在线人数为：{}", session.getId(), userid, onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param data 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String data, Session session) {
        JsonObject jsonTo = JsonParser.parseString(data).getAsJsonObject();
        String message = jsonTo.get("message").getAsString();
        String source = jsonTo.get("From").getAsString();
        String target = jsonTo.get("To").getAsString();
        log.info("服务端收到客户端[{}]，即userid{}的消息:{}", session.getId(), source, message);
        this.sendMessageTo("Hello, " + source, source);

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
//    private void sendMessage(String message, Session toSession) {
//        try {
//            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
//            toSession.getBasicRemote().sendText(message);
//        } catch (Exception e) {
//            log.error("服务端发送消息给客户端失败：{}", e.getMessage());
//        }
//    }

    public void sendMessageTo(String message, String To) {
        for (WechatWebSocket item : clients.values()) {
            if (item.userid.equals(To) )
                item.session.getAsyncRemote().sendText(message);
        }
    }
//
//    public void sendMessageAll(String message) {
//        for (WechatWebSocket item : clients.values()) {
//            item.session.getAsyncRemote().sendText(message);
//        }
//    }

    public static Map<String, WechatWebSocket> getClients() {
        return clients;
    }

    public static void setClients(Map<String, WechatWebSocket> clients) {
        WechatWebSocket.clients = clients;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
