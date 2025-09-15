package com.inso.framework.utils;

import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.common.collect.Maps;

public class ImageUtils 
{
	private static Map<String, Integer> maps = Maps.newHashMap();
	
	static {
		synchronized (maps) {
			maps.put("bmp", 1);
			maps.put("dib", 1);
			maps.put("gif", 1);
			maps.put("jfif", 1);
			maps.put("jpe", 1);
			maps.put("jpeg", 1);
			maps.put("jpg", 1);
			maps.put("png", 1);
			maps.put("tif", 1);
			maps.put("tiff", 1);
			maps.put("ico", 1);
		}
	}
	

	public static void saveImage(RenderedImage img, String path) 
    {  
    	try {
			OutputStream sos = new FileOutputStream(path);
			ImageIO.write(img, "png", sos);  
			sos.close();
		} catch (Exception e) {
		}
    }
	
//	public static boolean isBlack(String filename) {
//        org.eclipse.swt.graphics.ImageLoader loader = new org.eclipse.swt.graphics.ImageLoader();
//        loader.load(filename);
//        org.eclipse.swt.graphics.ImageData data = loader.data[0];
//        byte[] bytes = data.data;
//        for (byte b : bytes) {
//            if (b != 0) {
//                return false;
//            }
//        }
//        return true;
//    }
	
	public static boolean isPicture(String ext)
	{
		if(StringUtils.isEmpty(ext)) return false;
		return maps.containsKey(ext);
	}
	
}
