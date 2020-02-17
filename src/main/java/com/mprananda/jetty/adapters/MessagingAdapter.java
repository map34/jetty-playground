package com.mprananda.jetty.adapters;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class MessagingAdapter extends WebSocketAdapter {
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        String data = "Got your message, mate, I will keep sending you stuff!";
        RemoteEndpoint remote = getSession().getRemote();
        try {
            int time = 2000;
            remote.sendString(data);
            for (int i = 0; i < 10; i++) {
                Thread.sleep(time);
                remote.sendString( "" + (i + 1) * time);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

}
