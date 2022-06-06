package com.konchalovmaxim.creditconveyorms.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
    public class RequestResponseLoggingFilter implements Filter{

    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    //private static final int MAX_BODY_SIZE = 1024;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                             FilterChain chain) throws IOException, ServletException {

            ContentCachingRequestWrapper req = new ContentCachingRequestWrapper((HttpServletRequest) request);
            LOG.info("Logging Request  {} : {}", req.getMethod(), req.getRequestURI());
            req.getParameterMap();
            //TODO непонятно как тело читать
            String body = Arrays.toString(req.getContentAsByteArray()); // getContentAsByteArray() возвращает пустое тело, а запрос приходит в контроллер
            //String body2 = Arrays.toString(FileCopyUtils.copyToByteArray(req.getInputStream())); //запрос приходит в контроллер с пустым телом, но тело выводится в логи
            LOG.info("Request Body: " + body);

            HttpServletResponse res = (HttpServletResponse) response;
            chain.doFilter(request, response);
            LOG.info("Logging Response :{}", res.getContentType());

        }

    }

