package com.konchalovmaxim.applicationms.filter;

import com.konchalovmaxim.applicationms.config.HttpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestResponseLoggingFilter implements Filter {

    private final HttpProperties httpProp;
    private final HttpMessageLogFormatter httpMessageLogFormatter;

    private Boolean shouldNotLog(String URI) {
        return httpProp.getExcludeUrls().stream().anyMatch(URI::contains);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest((HttpServletRequest) request);

        if (shouldNotLog(cachedBodyHttpServletRequest.getRequestURI())) {
            filterChain.doFilter(request, response);
        } else {
            httpMessageLogFormatter.doLogHttpMessage(new HttpMessage(cachedBodyHttpServletRequest));

            CachedBodyHttpServletResponse cachedBodyHttpServletResponse =
                    new CachedBodyHttpServletResponse((HttpServletResponse) response);

            filterChain.doFilter(cachedBodyHttpServletRequest, cachedBodyHttpServletResponse);


            httpMessageLogFormatter.doLogHttpMessage(new HttpMessage(cachedBodyHttpServletResponse));
        }
    }
}