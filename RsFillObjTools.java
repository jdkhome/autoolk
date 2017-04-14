package com.jdkhome.autoolk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自动填装
 * @author Link
 *
 */
public class RsFillObjTools {

	
	// 把一个字符串的第一个字母大写、效率是最高的、  
    private static String getMethodName(String fildeName) throws Exception{  
        byte[] items = fildeName.getBytes();  
        items[0] = (byte) ((char) items[0] - 'a' + 'A');  
        return new String(items);  
    }  
	

    /**
     * 获取一个List的类型(比如List<Integer> 返回的是Integer.class)
     * @param field
     * @return 如果返回null 则说明获取失败
     */
    private static Class<?> getListType(Field field){
    	Class<?> fieldClass = field.getType();
		if(!fieldClass.isAssignableFrom(List.class)){
			//不是List类型，直接跳过
			return null;
		}
		
		Type fc = field.getGenericType();
		if(fc == null) return null;
		Class<?> genericClazz;
		if(fc instanceof ParameterizedType){   
			//如果是泛型参数的类型   
			ParameterizedType pt = (ParameterizedType) fc; 
			// 得到泛型里的class类型对象。
			genericClazz = (Class<?>)pt.getActualTypeArguments()[0];   
		}else{
			return null;
		}
		
    	return genericClazz;
    }
    
	private static Object[] getParameters(Object object,String parametersName) throws NoSuchMethodException, SecurityException, Exception{
		
		//若没有设置参数
		if(parametersName==null||parametersName.length()==0){
			return null;
		}
		
		//若设置对象为空
		if(object==null){
			return null;
		}
		
		// 拿到该类  
        Class<?> clz = object.getClass(); 
        // 获取实体类的所有属性，返回Field数组  
        Field[] fields = clz.getDeclaredFields();  

        for (Field field : fields) {
        	if(field.getName().compareTo(parametersName)==0){
        		
        		 //获取到参数对应字段
        		 Method m = (Method) object.getClass().getMethod(  
                         "get" + getMethodName(field.getName()));  

                 String val = (String) m.invoke(object).toString();// 调用getter方法获取属性值
                 
                 //拆分字符串,拿到参数列表
                 if(val==null||val.length()==0){
                	 return null;
                 }
                 
                 return val.split(",");
        		
        	}
        }
		
		return null;
	}
	
	private static List<Object> AutoFill(Connection m_conn,Class<?> clz,ResultSet rs) throws NoSuchMethodException, Exception{
		
		//返回实例
		List<Object> objectList=new ArrayList<Object>();
		//获取实体类的所有属性，返回Field数组  
        Field[] fields = clz.getDeclaredFields();  
     
			while(rs.next()){
				Object object = clz.newInstance();
				
				
				//填装AutoLinkFill基础字段
				for (Field field : fields) {
			    	if(field.isAnnotationPresent(AutoLinkFill.class)){
			    		AutoLinkFill autoLinkFill=(AutoLinkFill) field.getAnnotation(AutoLinkFill.class);
			    		
			    		field.setAccessible(true);
			    		//获取sql映射名
			    		String sqlAsName=autoLinkFill.value().compareTo("")==0?field.getName():autoLinkFill.value();
			    		try {
			    			field.set(object, rs.getObject(sqlAsName));
						} catch (java.lang.IllegalArgumentException e) {
							System.out.println("Autoolk:查询语句中的["+sqlAsName+"]字段映射失败!请检查类["+clz+"]中,["+field.getName()+"]字段！");
							e.printStackTrace();
							//没有找到该列直接跳过
							continue;
						}
			    			
			    		
			    	}
			    }
				
				//填装AutoLinkBasicListFill字段
				for (Field field : fields) {
			    	if(field.isAnnotationPresent(AutoLinkBasicListFill.class)){
			    		AutoLinkBasicListFill autoLinkBasicListFill=(AutoLinkBasicListFill) field.getAnnotation(AutoLinkBasicListFill.class);
			    		
			    		field.setAccessible(true);
			    		//获取List的类型，如果获取失败，则跳过
			    		Class<?> genericClazz=getListType(field);
			    		if(genericClazz==null) continue;
			    		//获取sql映射名
			    		String sqlAsName=autoLinkBasicListFill.value().compareTo("")==0?field.getName():autoLinkBasicListFill.value();
						
			    		try {
			    			
			    			//这里要找到字段List的基础类型
			    			List<Object> thisBasicList=new ArrayList<Object>();
			    			
			    			//内容为空直接跳过
			    			if(rs.getObject(sqlAsName)==null){
			    				continue;
			    			}
			    			
			    			String thisBasicListStr[]=((String) rs.getObject(sqlAsName)).split(",");
			    			
			    			//转换类型并存到List
			    			for(String thisBasic:thisBasicListStr){
			    				if(genericClazz.equals(String.class)){
			    					thisBasicList.add(thisBasic);
			    				}else if(genericClazz.equals(Integer.class)){
			    					thisBasicList.add(Integer.parseInt(thisBasic));
			    				}else if(genericClazz.equals(Long.class)){
			    					thisBasicList.add(Long.parseLong(thisBasic));
			    				}else if(genericClazz.equals(Float.class)){
			    					thisBasicList.add(Float.parseFloat(thisBasic));
			    				}else if(genericClazz.equals(Double.class)){
			    					thisBasicList.add(Double.parseDouble(thisBasic));
			    				}else{
			    					//暂时只支持这些类型，够了。。
			    					continue;
			    				}
			    			}
			    			
			    			field.set(object, thisBasicList);
			    			
						} catch (Exception e) {
							e.printStackTrace();
							//没有找到该列直接跳过
							continue;
						}
			    			
			    		
			    	}
			    }
				
				//填装AutoLinkObjListFill字段
				for (Field field : fields) {
			    	if(field.isAnnotationPresent(AutoLinkObjListFill.class)){
			    		
			    		Class<?> genericClazz=getListType(field);
			    		if(genericClazz==null) continue;
			    		
			    		AutoLinkObjListFill autoLinkObjListFill=(AutoLinkObjListFill) field.getAnnotation(AutoLinkObjListFill.class);
			    		
			    		field.setAccessible(true);
			    		//获取查询语句
			    		String sql=autoLinkObjListFill.sql();
			    		//获取参数对象
			    		String parametersName=autoLinkObjListFill.parameters();
			    		try{
				    		//设置该List
							field.set(object, sqlAutoFill(m_conn,sql, getParameters(object, parametersName), genericClazz));
						}catch (Exception e) {
							
						}
			    	}
			    }
				
				
				objectList.add(object);
			}
		
		return objectList;
	}
	
	

	/**
	 * 
	 * 专门用于count的方法
	 * @param sql sql语句
	 * @param parameters 参数
	 * @param countAs count值的名称
	 * @return
	 * @throws SQLException 
	 */
	public static Long sql4Count(String sql, Object[] parameters,String countAs) throws SQLException{
		
		Map<?, ?> map=SqlHelper.executeQuery(null,sql, parameters);
		ResultSet rs=(ResultSet) map.get("rs");
		
		while(rs.next()){
			return (Long) rs.getObject(countAs);
		}
		
		return 0L;
		
		
	}
	
	
	/**
	 * 自动sql执行+填装
	 * @param sql sql语句
	 * @param parameters 参数
 	 * @param clz 返回类型(需对字段设置AutoLinkFill注解)
	 * @return
	 * @throws Exception 
	 * @throws NoSuchMethodException 
	 */
	public static List<Object> sqlAutoFill(Connection m_conn,String sql, Object[] parameters,Class<?> clz) throws NoSuchMethodException, Exception{
		try {
			//如果是一个新的查询，则传来的m_conn应该是null
			Connection thisConn=m_conn;
			
			Map<?, ?> map=SqlHelper.executeQuery(m_conn,sql, parameters);
			//保存 conn供子查询使用
			m_conn=(Connection) map.get("conn");
			List<Object> list=AutoFill(m_conn,clz,(ResultSet) map.get("rs"));
			
			//总查询完成，关闭链接
			if(thisConn==null){
				SqlHelper.closeDruild((Connection) map.get("conn"));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("===========================");
			System.out.println("sql="+sql);
			System.out.print("parameters=");
			for(String s:(String[])parameters){
				System.out.print(s+" ");
			}
			System.out.println("");
			System.out.println("===========================");
			
			
		}
		return null;
		
		
	}
}
