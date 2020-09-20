package com.thoughtworks.rslist.exception;

public class CommonsException extends RuntimeException {
    private String error;

    public CommonsException(String error) {
        super(error);
        this.error = error;
    }

    @Override
    public String toString() {
        return error;
    }
}
