package com.inso.framework.spring.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ValidateCodeUtil;

/**
 * 图片验证码工具类
 * @author Administrator
 *
 */
public class WebImageValidCode {
	
	private static Log LOG = LogFactory.getLog(WebImageValidCode.class);
	
	public static void generateAndSendImgCode(String sessionKey)
	{
		 try {
			 	String code =  ValidateCodeUtil.getRandomString();
				HttpServletRequest request = WebRequest.getHttpServletRequest();
				HttpServletResponse response = WebRequest.getHttpServletResponse();
				request.getSession().setAttribute(sessionKey, code);
				response.setContentType("image/jpeg");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				OutputStream outputStream = response.getOutputStream();
				ValidateCodeUtil.stringToImage(code, outputStream);
				outputStream.flush();
				outputStream.close();
	        } catch (IOException e) {
	        	LOG.error("generate image valid code error:", e);
	        }
	}
	
	public static boolean checkImgCode(String code, String sessionKey)
	{
		if(StringUtils.isEmpty(code)) return false;
		HttpServletRequest request = WebRequest.getHttpServletRequest();
		String sessionImageCode = (String) request.getSession().getAttribute(sessionKey);
		return code.equalsIgnoreCase(sessionImageCode);
	}
	
	public static void removeImgCode(String sessionKey)
	{
		WebRequest.getSession().setAttribute(sessionKey, StringUtils.getEmpty());
	}
	
	public static enum ImageValidCodeType {
		NUM,
		NUM_AND_DIGIT
	}

}
