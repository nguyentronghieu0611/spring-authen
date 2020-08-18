package com.bnv.model;

public class DataResponse {
    private Object data;
    private int err_code;

    public DataResponse(Object data, int err_code) {
        this.data = data;
        this.err_code = err_code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }
}
