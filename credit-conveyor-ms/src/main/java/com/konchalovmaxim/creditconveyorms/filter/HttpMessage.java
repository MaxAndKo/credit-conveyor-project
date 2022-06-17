package com.konchalovmaxim.creditconveyorms.filter;

import com.konchalovmaxim.creditconveyorms.enums.HttpMessageKind;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.client.support.HttpRequestWrapper;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Collections;

@NoArgsConstructor
@Getter
public final class HttpMessage {
    private final String type = "HTTP";
    private String headers;
    private String body;
    private String method;
    private String uri;
    private String status;
    private HttpMessageKind kind;

    public HttpMessage(HttpServletRequestWrapper httpServletRequestWrapper) throws IOException {

        this.headers = Collections.list(httpServletRequestWrapper.getHeaderNames()).
                stream().map((String s) -> s = String.format("%s=[%s]", s, httpServletRequestWrapper.getHeader(s))).
                toList().toString();
        this.body = new String(httpServletRequestWrapper.getInputStream().readAllBytes());
        this.uri = httpServletRequestWrapper.getRequestURL().toString();
        this.method = httpServletRequestWrapper.getMethod();
        this.kind = HttpMessageKind.REQUEST;
    }

    public HttpMessage(HttpServletResponseWrapper httpServletResponseWrapper) {

        this.headers = httpServletResponseWrapper.getHeaderNames().
                stream().map((String s) -> s = String.format("%s=[%s]", s, httpServletResponseWrapper.getHeader(s))).
                toList().toString();
        if (httpServletResponseWrapper instanceof CachedBodyHttpServletResponse)
            this.body = new String(((CachedBodyHttpServletResponse) httpServletResponseWrapper).getByteArray());
        this.status = String.valueOf(httpServletResponseWrapper.getStatus());
        this.kind = HttpMessageKind.RESPONSE;
    }

}
