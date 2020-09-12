package com.bnv.model;

public class MsgResponse extends Response {

    private String transaction_id;

    public MsgResponse(String message, int err_code, String transaction_id) {
        super(message, err_code);
        this.transaction_id = transaction_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

}