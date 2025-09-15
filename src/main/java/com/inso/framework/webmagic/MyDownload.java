package com.inso.framework.webmagic;

import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.File;
import java.io.IOException;

public class MyDownload implements Downloader {

	public static int SUCCESS_CODE = 200;

	private HttpSesstionManager mHttpManager = HttpSesstionManager.getInstance();

	public boolean fetchFromLocal = false;

	private interface MyDownloadInternal {
		public MyDownload mgr = new MyDownload();
	}

	public static MyDownload getInstanced()
	{
		return MyDownloadInternal.mgr;
	}

	private MyDownload()
	{
	}


	public void setProxy()
	{
		String proxyIP = "127.0.0.1";
		int proxyPort = 51080;
		this.mHttpManager = new HttpSesstionManager(10, 10, 10, proxyIP, proxyPort);
	}

	@Override
	public Page download(Request req, Task task) {
		Page page = new Page();
		page.setRequest(req);
		try {
			if(fetchFromLocal)
			{
				page.setRawText(getFileText());
				page.setStatusCode(SUCCESS_CODE);
			}
			else
			{
				page.setUrl(new PlainText(req.getUrl()));
				byte[] byteArray = mHttpManager.syncGet(req.getUrl());
				if(byteArray != null)
				{
					page.setRawText(new String(byteArray));
					page.setStatusCode(SUCCESS_CODE);
				}

				page.setRequest(req);
				page.setDownloadSuccess(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	@Override
	public void setThread(int arg0) {
//		Spider
	}

	private String getFileText()
	{
		try {
			String filepath = "C:\\Users\\Administrator\\Desktop\\aaaaa.txt";

			return FileUtils.readFileToString(new File(filepath), StringUtils.UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) throws IOException {

		MyDownload download = MyDownload.getInstanced();
		download.setProxy();

		String url = "https://play.google.com/store/apps?hl=en&gl=US";
		byte[] rs = download.mHttpManager.syncGet(url);

		System.out.println(new String(rs));

	}

}
