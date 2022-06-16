package com.konchalovmaxim.creditconveyorms.filter;

import com.konchalovmaxim.creditconveyorms.config.HttpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RequestResponseLoggingFilter implements Filter {

    private final HttpProperties httpProp;
    private final HttpMessageLogFormatter httpMessageLogFormatter;

    public RequestResponseLoggingFilter(HttpProperties httpProp, HttpMessageLogFormatter httpMessageLogFormatter) {
        this.httpProp = httpProp;
        this.httpMessageLogFormatter = httpMessageLogFormatter;
    }

    public Boolean shouldLog(String URI){
        return httpProp.getExcludeUrls().stream().noneMatch(URI::contains);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest((HttpServletRequest) request);

        if (!shouldLog(cachedBodyHttpServletRequest.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String type = cachedBodyHttpServletRequest.getProtocol();
        String headers = Collections.list(cachedBodyHttpServletRequest.getHeaderNames()).
                stream().map((String s) -> s = String.format("%s=[%s]", s, cachedBodyHttpServletRequest.getHeader(s))).
                toList().toString();
        String body = new String(cachedBodyHttpServletRequest.getInputStream().readAllBytes());
        String uri = cachedBodyHttpServletRequest.getRequestURL().toString();
        String method = cachedBodyHttpServletRequest.getMethod();

        httpMessageLogFormatter.doLogHttpMessage(new HttpMessage(type, headers, body, method, uri));

        CachedBodyHttpServletResponse cachedBodyHttpServletResponse =
                new CachedBodyHttpServletResponse((HttpServletResponse) response);

        filterChain.doFilter(cachedBodyHttpServletRequest, cachedBodyHttpServletResponse);

        type = "HTTP";
        headers = cachedBodyHttpServletResponse.getHeaderNames().
                stream().map((String s) -> s = String.format("%s=[%s]", s, cachedBodyHttpServletResponse.getHeader(s))).
                toList().toString();
        body = new String(cachedBodyHttpServletResponse.getByteArray());
        String status = String.valueOf(cachedBodyHttpServletResponse.getStatus());

        httpMessageLogFormatter.doLogHttpMessage(new HttpMessage(type, headers, body, status));

    }
}
