package com.inso.modules.admin.core.model;

public class Permission {
	
	private String key;
	private String name;
	private boolean checked;
	
	public static String getColumnPrefix(){
        return "permission";
    }
	
	public String getKey() {
		return key;
	}
	public String getName() {
		return name;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getMenuKey()
	{
		MyType type = MyType.getTypeByName(name);
		return key.substring(0, key.length() - type.getKey().length() - 1);
	}

	public static enum MyType {
		ADD("add", "添加"),
		DELETE("delete", "删除"),
		EDIT("edit", "编辑"),
		LIST("list", "查询"),
		
		;
		
		private String key;
		private String name;
		
		MyType(String key, String name)
		{
			this.key = key;
			this.name = name;
		}
		
		public String getKey()
		{
			return key;
		}
		
		public String getName()
		{
			return name;
		}
		
		public static MyType getType(String key)
		{
			MyType[] values = MyType.values();
			for(MyType type : values)
			{
				if(type.key.equalsIgnoreCase(key))
				{
					return type;
				}
			}
			
			return null;
		}

		public static MyType getTypeByName(String name)
		{
			MyType[] values = MyType.values();
			for(MyType type : values)
			{
				if(type.name.equalsIgnoreCase(name))
				{
					return type;
				}
			}

			return null;
		}
		
	}

}
