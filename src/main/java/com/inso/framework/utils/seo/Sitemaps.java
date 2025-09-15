package com.inso.framework.utils.seo;

import java.util.Date;

import com.inso.framework.utils.DateUtils;

public class Sitemaps {
	
	/*** 网址  ***/
	private String loc;
	/*** 更新时间 yyyy-MM-dd ***/
	private Date lastmod;
	
	public Sitemaps(String loc, Date date)
	{
		this.loc = loc;
		this.lastmod = date;
	}
	
	
	public String getLoc() {
		return loc;
	}
	public Date getLastmod() {
		return lastmod;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public void setLastmod(Date lastmod) {
		this.lastmod = lastmod;
	}
	
	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<url>");
        sb.append("<loc>" + loc + "</loc>");
        sb.append("<lastmod>" + DateUtils.convertString(lastmod, DateUtils.TYPE_YYYY_MM_DD) + "</lastmod>");
        sb.append("<mobile:mobile type=\"autoadapt\"/>");
        sb.append("<changefreq>daily</changefreq>");
        sb.append("</url>");
        return sb.toString();
    }

}
