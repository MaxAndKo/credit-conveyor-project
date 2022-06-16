package com.konchalovmaxim.creditconveyorms.filter;

import com.konchalovmaxim.creditconveyorms.enums.HttpMessageKind;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HttpMessage {
    String type;
    String headers;
    String body;
    String method;
    String uri;
    String status;
    HttpMessageKind kind;

    public HttpMessage(String type, String headers, String body, String method, String uri) {
        this.type = type;
        this.body = body;
        this.headers = headers;
        this.method = method;
        this.uri = uri;
        this.kind = HttpMessageKind.REQUEST;
    }

    public HttpMessage(String type, String headers, String body, String status) {
        this.type = type;
        this.body = body;
        this.headers = headers;
        this.status = status;
        this.kind = HttpMessageKind.RESPONSE;
    }

}
