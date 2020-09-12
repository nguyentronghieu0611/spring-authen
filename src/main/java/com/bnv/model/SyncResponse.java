package com.bnv.model;

public class SyncResponse extends Response {

    private String value;


    public SyncResponse(String message, int err_code, String value) {
        super(message, err_code);
        this.value = value;
    }

    public SyncResponse(String message, int err_code) {
        super(message, err_code);
    }

    public SyncResponse(int err_code, String value) {
        super(err_code);
        this.value = value;
    }

    public SyncResponse(int err_code)
    {
        super(err_code);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}