package com.bnv.model;

public class Response {

	private String message;
	private int err_code;
	private String value;

	// Constructor, getters and setters omitted

	public Response(String message, int err_code, String value) {
		this.message=message;
		this.err_code=err_code;
		this.value=value;
	}

    public Response(String message, int err_code) {
        this.message=message;
        this.err_code=err_code;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}