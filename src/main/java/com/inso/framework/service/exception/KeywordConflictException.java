package com.inso.framework.service.exception;

public class KeywordConflictException extends BaseException {

	private static final long serialVersionUID = 1L;

	private String keywordword;

	private Object value;

	public KeywordConflictException(String keyword, Object value, Throwable cause) {
		super(cause);
		this.keywordword = keyword;
		this.value = value;
	}

	public String getKeyword() {
		return keywordword;
	}

	public void setKeyword(String keyword) {
		this.keywordword = keyword;
	}

	public KeywordConflictException(String keyword, Object value) {
		super();
		this.keywordword = keyword;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
