package com.inso.framework.utils.seo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;

public class SitemapsManager {
	
	private static  Log LOG = LogFactory.getLog(SitemapsManager.class);
	
	private static String DEFAULT_XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static String DEFAULT_SITEMAP_HEADER = "\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";
	private static String DEFAULT_SITEMAP_FOOTER = "</urlset>";
	
	/*** 最大10M, 这里只给3M ***/
	private static final long DEFAUL_MAX_FILE_SIZE = 1024 * 1024 * 1024 * 3L;// 3M
	/*** 最大5w条记录， 这里只给1w条 ***/
	private static final int DEFAULT_MAX_URL_COUNT_OF_FILE = 10000;
	
	private StringBuffer mStringBuffer = new StringBuffer();
	private String mSaveDir;
	
	/*** 每个文件最大URL个数 ***/
	private long maxUrlCountOfFile = DEFAULT_MAX_URL_COUNT_OF_FILE;
	private long currentUrlCountOfFile = 0;
	
	/*** 文件最大是多少M ***/
	private long currentFileSize = 0;
	
	
	private int currentFileCount = 0;
	
	private String mFilename;
	
	public SitemapsManager(String saveDir, String filename)
	{
		if(StringUtils.isEmpty(filename))
		{
			throw new RuntimeException("filename is null error");
		}
		this.mSaveDir = saveDir;
		this.mFilename = filename;
		
		mStringBuffer.append(DEFAULT_XML_HEADER);
		mStringBuffer.append(DEFAULT_SITEMAP_HEADER);
	}
	
	public void setMaxUrlCountOfFile(long maxUrlCountOfFile)
	{
		if(maxUrlCountOfFile> 45000)
		{
			maxUrlCountOfFile = 45000;
		}
		this.maxUrlCountOfFile = maxUrlCountOfFile;
	}
	
//	public void addSitemap(Sitemaps sitemaps)
//	{
//		String line = sitemaps.toString();
//		mStringBuffer.append(line);
//		
//		int len = line.length();
//		this.currentCount += 1;
//		this.currentSize += len;
//		
//		if(this.currentCount >= DEFAULT_MAX_COUNT)
//		{
//			saveToFile();
//		}
//		else if(this.currentSize >= DEFAUL_MAX_SIZE)
//		{
//			saveToFile();
//		}
//	}
	
	public void addSitemaps(String loc, Date lastmod, ChangeFreq changeFreq)
	{
		StringBuilder sb = new StringBuilder();
        sb.append("<url>\n");
        sb.append("	<loc>" + loc + "</loc>\n");
        sb.append("	<lastmod>" + DateUtils.convertString(lastmod, DateUtils.TYPE_YYYY_MM_DD) + "</lastmod>\n");
        sb.append("	<changefreq>" + changeFreq.getName() + "</changefreq>\n");
        sb.append("</url>\n");
        
        String line = sb.toString();
        mStringBuffer.append(line);
		
		int len = line.length();
		this.currentUrlCountOfFile += 1;
		this.currentFileSize += len;
		
		if(this.currentUrlCountOfFile >= maxUrlCountOfFile)
		{
			saveToFile();
		}
		else if(this.currentFileSize >= DEFAUL_MAX_FILE_SIZE)
		{
			saveToFile();
		}
	}
	
	public void finish()
	{
		saveToFile();
	}
	
	private void saveToFile()
	{
		try {
			mStringBuffer.append(DEFAULT_SITEMAP_FOOTER);
			// save to file
			File file = new File(mSaveDir + StringUtils.URL_SPLIT + mFilename + (currentFileCount ++) + ".xml");
			FileUtils.writeStringToFile(file, mStringBuffer.toString(), StringUtils.UTF8, false);
			
			// clear
			this.currentUrlCountOfFile = 0;
			this.currentFileSize = 0;
			int len = this.mStringBuffer.length();
			if(len > 0)
			{
				this.mStringBuffer.delete(0, len);
				mStringBuffer.append(DEFAULT_XML_HEADER);
				mStringBuffer.append(DEFAULT_SITEMAP_HEADER);
			}
		} catch (IOException e) {
			LOG.error("save error:", e);
		}
	}
	
	public static void main(String[] args)
	{
		Date date = new Date();
		SitemapsManager mgr = new SitemapsManager("/srv/sitemaps/tech", "doc");
		
		mgr.addSitemaps("https://www.pangugle.com/tech/article/java/intro.html", date, ChangeFreq.DAILY);
		
		mgr.finish();
	}

}
