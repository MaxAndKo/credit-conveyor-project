package com.konchalovmaxim.applicationms.filter;

public enum HttpMessageKind {

    RESPONSE("<<"),
    REQUEST(">>");

    public final String prefix;

    HttpMessageKind(String kind) {
        this.prefix = kind;
    }
}
