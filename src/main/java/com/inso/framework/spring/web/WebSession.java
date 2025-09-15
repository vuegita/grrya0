package com.inso.framework.spring.web;

import javax.servlet.http.HttpSession;

public class WebSession {
	
   public static HttpSession getHttpSession(){  
       return WebRequest.getHttpServletRequest().getSession();  
   } 

}
