package com.aryangzhu.connector;

import com.aryangzhu.engine.HttpServletRequestImpl;
import com.aryangzhu.engine.HttpServletResponseImpl;
import com.aryangzhu.engine.ServletContextImpl;
import com.aryangzhu.engine.filter.LogFilter;
import com.aryangzhu.engine.servlet.IndexServlet;
import com.aryangzhu.engine.servlet.LoginServlet;
import com.aryangzhu.engine.servlet.LogoutServlet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

public class HttpConnector implements AutoCloseable, HttpHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final ServletContextImpl servletContext;
    final HttpServer httpServer;
    final Duration stopDelay = Duration.ofSeconds(5);

    public HttpConnector() throws IOException {
        this.servletContext = new ServletContextImpl();
        this.servletContext.initServlets(List.of(IndexServlet.class, LoginServlet.class, LogoutServlet.class));
        this.servletContext.initFilters(List.of(LogFilter.class));
        // start http server:
        String host = "0.0.0.0";
        int port = 8080;
        this.httpServer = HttpServer.create(new InetSocketAddress(host, port), 0, "/", this);
        this.httpServer.start();
        logger.info("jerrymouse http server started at {}:{}...", host, port);
    }

    @Override
    public void close() {
//        this.httpServer.stop(3);
        this.httpServer.stop((int) this.stopDelay.toSeconds());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var adapter = new HttpExchangeAdapter(exchange);
        var response = new HttpServletResponseImpl(adapter);
        var request = new HttpServletRequestImpl(this.servletContext,adapter,response);
        // process:
        try {
            this.servletContext.process(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
