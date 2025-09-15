package com.inso.framework.utils.seo;

public enum ChangeFreq {
	WEEKLY {
		@Override
		public String getName() {
			return "weekly";
		}
	},
	DAILY {
		public String getName() {
			return "daily";
		}
	};
	
	public abstract String getName();

}
