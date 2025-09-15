package com.inso.framework.bean;

public interface ErrorResult {
	// special-1,doc-2,news-3,wiki-4,passport-5,
	public String getError();
	public int getCode();

	public String getSPError();
	public String getYDError();
}
