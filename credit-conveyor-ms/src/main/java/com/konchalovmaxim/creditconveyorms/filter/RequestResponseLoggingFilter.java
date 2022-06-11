package com.konchalovmaxim.creditconveyorms.filter;

import com.konchalovmaxim.creditconveyorms.config.HttpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class RequestResponseLoggingFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    private final HttpProperties notLogHttp;

    public RequestResponseLoggingFilter(HttpProperties notLogHttp) {
        this.notLogHttp = notLogHttp;
    }

    public Boolean shouldLog(String URI){
        if (URI.contains(notLogHttp.getApiDocs()) ||
                URI.contains(notLogHttp.getSwagger())){
            return false;
        }
        return true;
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

        LOG.info("Request info:");
        LOG.info("Request Method: {}", cachedBodyHttpServletRequest.getMethod());
        LOG.info("Request URL: {}", cachedBodyHttpServletRequest.getRequestURL());
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
