package com.inso.framework.service.exception;

public class ObjectNotExistException extends BaseException {

	private static final long serialVersionUID = 1L;

	private String name;

	private Object value;

	public ObjectNotExistException(String name, Object value, Throwable cause) {
		super(cause);
		this.name = name;
		this.value = value;
	}

	public ObjectNotExistException(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
