package com.mprananda.jetty.adapters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class MessagingAdapter extends WebSocketAdapter {
    private static Logger logger = LogManager.getLogger(WebSocketAdapter.class);

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        logger.info("Socket Connected from: {}", sess.getRemoteAddress().toString());
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        logger.info("Received WS message: {}", message);
        RemoteEndpoint remote = getSession().getRemote();
        try {
            String data = "";
            switch(message) {
                case "time":
                    data = (new Date()).toInstant().toString();
                    break;
                case "hostname":
                    data = InetAddress.getLocalHost().getHostName();
                    break;
                case "mydata":
                    data = this.getSession().toString();
                    break;
                default:
                    data = "Your request is not known";
                    break;
            }
            remote.sendString(data);

        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        logger.info("Socket Closed: [ {} ] reason: {}", statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        logger.error("Error on websocket connection.", cause);
    }

}
