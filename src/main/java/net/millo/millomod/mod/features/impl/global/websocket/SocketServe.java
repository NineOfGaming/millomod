package net.millo.millomod.mod.features.impl.global.websocket;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.impl.global.websocket.client.MilloWebSocketServer;

import java.net.InetSocketAddress;

public class SocketServe extends Feature {

    private MilloWebSocketServer webSocketServer;

    public SocketServe() {
        webSocketServer = new MilloWebSocketServer(new InetSocketAddress("localhost", 31321));
        try {
            new Thread(webSocketServer, "Millo-Websocket-Thread").start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getKey() {
        return "socket_serve";
    }

}
