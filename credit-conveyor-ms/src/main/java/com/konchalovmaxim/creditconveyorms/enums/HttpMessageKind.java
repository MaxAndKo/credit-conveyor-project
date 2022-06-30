package com.konchalovmaxim.creditconveyorms.enums;

public enum HttpMessageKind {

    RESPONSE("<<"),
    REQUEST(">>");

    public final String prefix;

    HttpMessageKind(String kind) {
        this.prefix = kind;
    }
}
