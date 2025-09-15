package com.inso.framework.bean;

import java.io.Serializable;

import com.inso.framework.utils.FastJsonHelper;

public class Model implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() {
		String rs;
		try {
			rs = FastJsonHelper.jsonEncode(this);
		} catch (Exception e) {
			rs = super.toString();
		}
		return rs;
	}
	
}
