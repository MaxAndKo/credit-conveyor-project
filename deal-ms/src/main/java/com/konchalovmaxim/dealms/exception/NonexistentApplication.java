package com.konchalovmaxim.dealms.exception;

public class NonexistentApplication extends RuntimeException {
    public NonexistentApplication(String message) {
        super(message);
    }
}
