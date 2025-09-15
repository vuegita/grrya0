package com.inso.modules.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.NetUtils;
import com.inso.modules.web.logical.SystemStatusManager;

@RestController
@RequestMapping("/systemApi")
public class SystemStatusController {

	
	@RequestMapping("/stop")
	public String stop()
	{
		String remoteip = WebRequest.getRemoteIP();
		if(!NetUtils.isLocalHost(remoteip))
		{
			throw new RuntimeException("Error");
		}
		SystemStatusManager.getInstance().stop();
		return "ok";
	}
	
}
