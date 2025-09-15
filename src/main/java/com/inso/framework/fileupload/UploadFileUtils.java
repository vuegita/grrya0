package com.inso.framework.fileupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.MD5;

/**
 * @Description 文件上传工具包
 * @Author LXF
 * @Create 2018-11-08 11:35
 */
public class UploadFileUtils {

	private static Log LOG = LogFactory.getLog(UploadFileUtils.class);

	private static MyConfiguration conf = MyConfiguration.getInstance();
	private static final String tempDIR = conf.getString("root.upload_tmp_path");

	public static boolean uploadFile(MultipartFile file, File targetFile) {
		try {
			if (targetFile == null || targetFile.exists())
				return false;
			String tmpPath = tempDIR + "/" + MD5.encode(targetFile.getAbsolutePath() + file.getOriginalFilename()) + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			File tempFile = new File(tmpPath);
			FileUtils.forceMkdirParent(tempFile);
			byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
			OutputStream outStream = new FileOutputStream(tempFile);
			IOUtils.write(byteArray, outStream);
			outStream.close();
			FileUtils.moveFile(tempFile, targetFile);
			return true;
		} catch (Exception e) {
			LOG.error("upload file error:", e);
			return false;
		}
	}


	public static boolean deleteFile(String filepath) {
		//System.out.println("删除图片开始，图片路径为"+filepath);
		File file = new File(filepath);
		//判断文件是否存在
		if (file.exists() == true){
			System.out.println("图片存在，可执行删除操作");
			Boolean flag = false;
			flag = file.delete();
			if (flag){
				System.out.println("成功删除图片"+file.getName());
				return true;
			}else {
				System.out.println("删除失败");
				return false;
			}
		}else {
			System.out.println("图片不存在，终止操作");
			return false;
		}



	}

	public static void main(String[] args) {
		 String filename = "sdfasdfasd.png";
		 System.out.println(FilenameUtils.getExtension(filename));
	}

}
