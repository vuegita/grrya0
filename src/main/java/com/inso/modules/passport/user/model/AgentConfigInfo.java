package com.inso.modules.passport.user.model;


import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.ui.Model;

import java.util.Date;

public class AgentConfigInfo {

	/**
	 config_id 						int(11) NOT NULL AUTO_INCREMENT,
	 config_agentid 				    int(11) NOT NULL comment '',
	 config_agentname                  varchar(50) NOT NULL comment '',
	 config_type    	  		        varchar(255) NOT NULL,
	 config_key    	  		        varchar(255) NOT NULL,
	 config_value    	  		        varchar(255) NOT NULL,
	 config_status    	  		        varchar(255) NOT NULL,
	 config_createtime  				datetime DEFAULT NULL ,
	 */
	private long id;
	private long agentid;
	private String agentname;
	private String type;
	private String value;

	private String status;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;

	public static String getColumnPrefix(){
		return "config";
	}


	public long getAgentid() {
		return agentid;
	}

	public void setAgentid(long agentid) {
		this.agentid = agentid;
	}

	public String getAgentname() {
		return agentname;
	}

	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public enum AgentConfigType {

		COIN_DEFI_STAKING("coin_defi_staking", "DeFi质押"),
		COIN_DEFI_VOUCHER("coin_defi_voucher", "DeFi代金"),
		COIN_DEFI_SETLLE_WITHDRAW("coin_defi_settle_withdraw", "提现结算"),

		;

		private String key;
		private String name;

		AgentConfigType(String key, String name)
		{
			this.key = key;
			this.name = name;
		}

		public String getKey()
		{
			return key;
		}

		public String getName() {
			return name;
		}

		public static AgentConfigType getType(String key)
		{
			AgentConfigType[] values = AgentConfigType.values();
			for(AgentConfigType type : values)
			{
				if(type.getKey().equalsIgnoreCase(key))
				{
					return type;
				}
			}
			return null;
		}

		public static void addModel(Model model)
		{
			AgentConfigType[] exchangeRateTypeArr = AgentConfigType.values();
			model.addAttribute("agentConfigArr", exchangeRateTypeArr);
		}
	}
}
