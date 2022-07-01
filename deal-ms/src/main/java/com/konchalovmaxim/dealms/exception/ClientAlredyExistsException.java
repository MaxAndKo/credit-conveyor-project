package com.konchalovmaxim.dealms.exception;

public class ClientAlredyExistsException extends RuntimeException{
    public ClientAlredyExistsException(String message) {
        super(message);
    }
}
