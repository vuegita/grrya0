package com.inso.framework.utils;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.shell.Java2ShellExcutor;
import com.inso.framework.shell.Java2ShellExcutor.CommandResult;

public class MediaConvertUtils {
	
	private static final Log LOG = LogFactory.getLog(MediaConvertUtils.class);
	
	private static final int DEFAULT_BITRATE = 768;
	
	private static final String BIN_DIR = MyEnvironment.getHome() + "/bin/";
	private static Java2ShellExcutor mShellExcutor = new Java2ShellExcutor(10);
	
	/**
	 * 视频转码，默认 bitrate=768
	 * @param inputFile 源路径
	 * @param outputFile 目标路径
	 */
	public static boolean convertVideo(String inputFile, String outputFile)
	{
		return convertVideo(inputFile, outputFile, DEFAULT_BITRATE);
	}
	
	private static boolean convertVideo(String inputFile, String outputFile, int bitrate)
	{
		try {
			// 源文件不存在
			if(! new File(inputFile).exists()) {
				LOG.error("src file not exist error for " + inputFile);
				return false;
			}
			// 目标文件已存在，不需要转换
			File targetFile = new File(outputFile);
			if(targetFile.exists()) {
				LOG.debug("target file exist , the file is " + outputFile);
				 return true;
			}
			LOG.info("start convert file for " + inputFile);
			
			String datetime = DateUtils.convertString(DateUtils.TYPE_YYYYMMDDHHMMSS, new Date());
			String basename = FilenameUtils.getBaseName(outputFile);
			String extension = FilenameUtils.getExtension(outputFile);
			File tempfile = new File("/tmp/" + basename + datetime + "." + extension);
			
			LOG.debug("temfile = " + tempfile.getAbsolutePath());
			
			// 比特率=>768k, 格式=>libx264, 帧数=>20, 分辨率=>320*240
			String args = " video_convert " + inputFile + " " + tempfile.getAbsolutePath() + " " + bitrate;
			CommandResult result = mShellExcutor.execScript("job-media-convert.sh", args, null, BIN_DIR);
			
			// rename
			if(tempfile.length() > 0) 
			{
				FileUtils.moveFile(tempfile, targetFile);
				return targetFile.exists();
			}
			
			LOG.info("convert video result for " + inputFile + ", " + result.toString());
		} catch (Exception e) {
			LOG.error("convert error:", e);
		}
		return false;
	}

}
