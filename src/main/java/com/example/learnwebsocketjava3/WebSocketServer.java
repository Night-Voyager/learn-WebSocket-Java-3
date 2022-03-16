package com.example.learnwebsocketjava3;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Service
@ServerEndpoint("/api/websocket/{sid}")
public class WebSocketServer {
    // count the amount of online connections, should be thread-save
    private static int onlineCount = 0;

    // thread-save set, used to save the websocket objects related to each client
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    // the session connected to a client, used to send data
    private Session session;

    // receive sid
    private String sid = "";

    @OnOpen
    public void OnOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);
        this.sid = sid;
        addOnlineCount();
        try {
            sendMessage("conn_success");
            System.out.println("New connection: " + sid + ", currently " + getOnlineCount() + " connection(s) online.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void OnClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("Connection " + sid + " closed, currently " + getOnlineCount() + " connection(s) online.");
    }

    @OnMessage
    public void OnMessage(String message, Session session) {
        System.out.println("Message from " + sid + ": " + message);

        // send message to everyone
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void OnError(Session session, Throwable e) {
        System.out.println("Error occurs");
        e.printStackTrace();
    }

    // actively send message from server
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    // send customized message
    public static void sendInfo(String message, @PathParam("sid") String sid) {
        System.out.println("Message to " + sid + ": " + message);

        for (WebSocketServer item : webSocketSet) {
            try {
                if (sid == null)
                    item.sendMessage(message);
                else if (item.sid.equals(sid))
                    item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
}
