package com.mprananda.jetty.servlets;

import com.mprananda.jetty.adapters.MessagingAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(name = "MessagingServlet", urlPatterns = {"/message"})
public class MessagingServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MessagingAdapter.class);
    }
}
