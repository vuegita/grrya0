package com.inso.framework.db.jdbc;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import com.inso.framework.reflect.JavaBeanField;
import com.inso.framework.reflect.MyFieldFactory;

/**
 * Resultset 自动转成model, model必须实现getTablePrefix静态方法
 * 原则是约定优于配置
 *
 * @author Administrator
 */
@Component
public class ResultSetUtils {
	
	public static String BOOLEAN = "boolean";
    public static String LONG = "Long";

    public static <T> T convertJavaBean(Class<T> cls, ResultSet rs) {
        try {
            // 实例化对象
            //T obj = cls.newInstance();
            T obj = null;

            JavaBeanField beanField = MyFieldFactory.getField(cls);
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            if (rs.next()) {
                obj = cls.newInstance();
                for (int i = 1; i <= count; i++) {
                    String column = meta.getColumnLabel(i);
//                    Object value = rs.getObject(column);
                    Field field = beanField.getField(column);
					if(field != null) {
						safeSetFieldValue(obj, field, rs, column);
//						String fieldType = field.getGenericType().toString();
//						 if(BOOLEAN.equalsIgnoreCase(fieldType))
//						 {
//	                    	 field.set(obj, rs.getBoolean(column));
//	                    	// System.out.println(column +  " = " + rs.getBoolean(column));
//						 }
//						 else
//						 {
//							 field.set(obj, value);
//						 }
					}
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T resultSetToJavaBean(Class<T> cls, ResultSet rs) {
        try {
            T obj = null;

            JavaBeanField beanField = MyFieldFactory.getField(cls);
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            obj = cls.newInstance();
            for (int i = 1; i <= count; i++) {
                String column = meta.getColumnLabel(i);
                Object value = rs.getObject(column);
                Field field = beanField.getField(column);
                if (value == null){
                    Class<?> fieldType = field.getType();
                    if (fieldType != null){
                        if (fieldType == BigDecimal.class){
                            value = BigDecimal.ZERO;
                        }
                    }
                }


                if (field != null){
                	safeSetFieldValue(obj, field, rs, column);
                    //beanField.getField(column).set(obj, value);
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T> void safeSetFieldValue(T model, Field field, ResultSet rs, String column) throws Exception
    {
        try {
            String fieldType = field.getGenericType().toString();
            if(ResultSetUtils.BOOLEAN.equalsIgnoreCase(fieldType))
            {
                field.set(model, rs.getBoolean(column));
            }
            if(ResultSetUtils.LONG.equalsIgnoreCase(fieldType))
            {
                field.set(model, rs.getLong(column));
            }
            else
            {
                field.set(model, rs.getObject(column));
            }
        } catch (Exception e) {
        }
    }

    public static <T> void safeSetFieldNull(T model, Field field) throws Exception
    {
        try {
//            String fieldType = field.getGenericType().toString();
            Class<?> fieldType = field.getType();
            String typeName = fieldType.getTypeName();

            if(typeName.equalsIgnoreCase(int.class.getName()))
            {
                field.set(model, 0);
            }
            else if(typeName.equalsIgnoreCase(Boolean.class.getName()))
            {
                field.set(model, false);
            }
            else if(typeName.equalsIgnoreCase(Float.class.getName()))
            {
                field.set(model, 0);
            }
            else if(typeName.equalsIgnoreCase(Double.class.getName()))
            {
                field.set(model, 0);
            }
            else if(typeName.equalsIgnoreCase(Long.class.getName()))
            {
                field.set(model, 0);
            }
            else
            {
                field.set(model, null);
            }

//            if(ResultSetUtils.BOOLEAN.equalsIgnoreCase(fieldType))
//            {
//                field.set(model, rs.getBoolean(column));
//            }
//            if(ResultSetUtils.LONG.equalsIgnoreCase(fieldType))
//            {
//                field.set(model, rs.getLong(column));
//            }
//            else
//            {
//                field.set(model, rs.getObject(column));
//            }
        } catch (Exception e) {
        }
    }
}
