package com.konchalovmaxim.applicationms.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public final class HttpMessage {
    @Getter
    private static final String type = "HTTP";
    private String headers;
    private String body;
    private String method;
    private String uri;
    private String status;
    private HttpMessageKind kind;

    public HttpMessage(HttpServletRequestWrapper httpServletRequestWrapper) throws IOException {

        this.headers = Collections.list(httpServletRequestWrapper.getHeaderNames())
                .stream()
                .map(s -> s = String.format("%s=[%s]", s, httpServletRequestWrapper.getHeader(s)))
                .collect(Collectors.joining());
        this.body = new String(httpServletRequestWrapper.getInputStream().readAllBytes());
        this.uri = getFullURL(httpServletRequestWrapper);
        this.method = httpServletRequestWrapper.getMethod();
        this.kind = HttpMessageKind.REQUEST;
    }

    public HttpMessage(HttpServletResponseWrapper httpServletResponseWrapper) {

        this.headers = httpServletResponseWrapper.getHeaderNames()
                .stream()
                .map(s -> s = String.format("%s=[%s]", s, httpServletResponseWrapper.getHeader(s)))
                .collect(Collectors.joining());
        if (httpServletResponseWrapper instanceof CachedBodyHttpServletResponse)
            this.body = new String(((CachedBodyHttpServletResponse) httpServletResponseWrapper).getByteArray());
        this.status = String.valueOf(httpServletResponseWrapper.getStatus());
        this.kind = HttpMessageKind.RESPONSE;
    }

    private static String getFullURL(HttpServletRequestWrapper request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

}
