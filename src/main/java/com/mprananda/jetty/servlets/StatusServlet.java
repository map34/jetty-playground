package com.mprananda.jetty.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("serial")
@WebServlet(name="StatusServlet", urlPatterns = {"/status"}, asyncSupported = true)
public class StatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsyncContext async = request.startAsync();
        ServletOutputStream out = response.getOutputStream();
        ByteBuffer content = ByteBuffer.wrap(
                buildResponse().getBytes(StandardCharsets.UTF_8));

        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while(out.isReady()) {
                    if (!content.hasRemaining()) {
                        response.setContentType("application/json");
                        response.setStatus(200);
                        async.complete();
                        return;
                    }
                    out.write(content.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async Error", t);
                async.complete();
            }
        });
    }

    private String buildResponse() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();
        ((ObjectNode) rootNode).put("status", "OK");
        return mapper.writeValueAsString(rootNode);
    }
}
