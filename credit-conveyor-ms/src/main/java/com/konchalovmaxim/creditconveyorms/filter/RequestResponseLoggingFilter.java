package com.konchalovmaxim.creditconveyorms.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Profile("http_logs")
@Component
public class RequestResponseLoggingFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {


        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest((HttpServletRequest) request);

        if (cachedBodyHttpServletRequest.getRequestURI().contains("api-docs") ||
                cachedBodyHttpServletRequest.getRequestURI().contains("swagger")){
            filterChain.doFilter(request, response);
            return;
        }

        LOG.info("Request info:");
        LOG.info("Request Method: {}", cachedBodyHttpServletRequest.getMethod());
        LOG.info("Request URI: {}", cachedBodyHttpServletRequest.getRequestURI());
        LOG.info("Request body: {}", new String(cachedBodyHttpServletRequest.getInputStream().readAllBytes()));

        CachedBodyHttpServletResponse cachedBodyHttpServletResponse =
                new CachedBodyHttpServletResponse((HttpServletResponse) response);

        filterChain.doFilter(cachedBodyHttpServletRequest, cachedBodyHttpServletResponse);

        Collection<String> headers = cachedBodyHttpServletResponse.getHeaderNames();
        headers = headers.stream().map((String s) -> s = String.format("%s:%s", s,
                cachedBodyHttpServletResponse.getHeader(s))).collect(Collectors.toList());

        LOG.info("Response info:");
        LOG.info("Response Status: {}", cachedBodyHttpServletResponse.getStatus());
        LOG.info("Response Content-Type: {}", cachedBodyHttpServletResponse.getContentType());
        LOG.info("Response headers: {}", headers);
        LOG.info("Response body: {}", new String(cachedBodyHttpServletResponse.getByteArray()));
    }
}
