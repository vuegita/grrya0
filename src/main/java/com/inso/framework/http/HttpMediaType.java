package com.inso.framework.http;

public enum HttpMediaType {
	JSON{
		public String getValue() {
			return "application/json; charset=utf-8";
		}
	},	
	FORM{
		public String getValue() {
			return "application/x-www-form-urlencoded;charset=UTF-8";
		}
	},
	TEXT{
		public String getValue() {
			return "application/text;charset=UTF-8";
		}
	};
	
	public abstract String getValue();
}
