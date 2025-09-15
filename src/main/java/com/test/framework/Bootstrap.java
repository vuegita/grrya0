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

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.inso.framework.spring.SpringBootManager;
import com.inso.framework.spring.beans.SimpleNameGenerator;

/**
 * 仅供测试部署专用,生产环境请勿使用
 * @author Administrator
 *
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "com.inso" }, nameGenerator = SimpleNameGenerator.class)
@EnableTransactionManagement
public class Bootstrap {

	public static void main(String[] args) throws Exception {
		// run spring
		SpringBootManager.run(Bootstrap.class, "bootstrap.global.web.server.port", "global_web", args);
	}

}
