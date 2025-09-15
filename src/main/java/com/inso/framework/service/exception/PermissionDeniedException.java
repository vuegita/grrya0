package com.inso.framework.service.exception;

public class PermissionDeniedException extends BaseException {

	private static final long serialVersionUID = 1L;

	private String permission;

	private Object object;

	public PermissionDeniedException(String permission, Throwable cause) {
		super(cause);
		this.permission = permission;
	}

	public PermissionDeniedException(String permission, Object object,
			Throwable cause) {
		super(cause);
		this.permission = permission;
		this.object = object;
	}

	public PermissionDeniedException(String permission) {
		this.permission = permission;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}
