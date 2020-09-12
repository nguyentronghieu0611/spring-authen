package com.bnv.model;

public class Response {

    private String message;
    private int err_code;

    public Response(String message, int err_code) {
        this.message = message;
        this.err_code = err_code;
    }

    public Response(int err_code) {
        this.err_code = err_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

}