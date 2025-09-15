/*
 * Copyright (C) 2019  即时通讯网(www.pangugle.com) & Jack Pangugle.
 * The pangugle project. All rights reserved.
 * 
 * 【本产品为著作权产品，合法授权后请放心使用，禁止外传！】
 * 【本次授权给：<xxx网络科技有限公司>，授权编号：<授权编号-xxx>】
 * 
 * 本系列产品在国家版权局的著作权登记信息如下：
 * 1）国家版权局登记名（简称）和证书号：Project_name（软著登字第xxxxx号）
 * 著作权所有人：厦门盘古网络科技有限公司
 * 
 * 违法或违规使用投诉和举报方式：
 * 联系邮件：2624342267@qq.com
 * 联系微信：pangugle
 * 联系QQ：2624342267
 * 官方社区：http:/www.pangugle.com
 */
package com.test.framework;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class SoftAppInfo {
	
	private static String info = "";
	
	public static void printlnFile(File file) throws IOException
	{
		String name = file.getName();
		if(file.isDirectory())
		{
			for(File child : file.listFiles())
			{
				printlnFile(child);
			}
			return;
		}
		if(!name.endsWith("java") || !file.isFile()) return;
		
		StringBuffer buffer = new StringBuffer();
		List<String> lineList = FileUtils.readLines(file, "utf-8");
		boolean read = false;
		for(String line : lineList)
		{
			if(line.startsWith("package com.inso."))
			{
				buffer.append(info);
				read = true;
			}
			if(read)
			{
				buffer.append(line).append("\n");
			}
		}
		if(buffer.length() > 0)
		{
//			System.out.println(buffer.toString());
			FileUtils.writeStringToFile(file, buffer.toString(), "utf-8", false);
		}
		
	}
	
	public static void main(String[] args) throws IOException
	{
		String path = System.getProperty("user.dir")+"\\src\\main\\java\\";
		
		File rootFile = new File(path);
		
		printlnFile(rootFile);
	}
	
	

}
