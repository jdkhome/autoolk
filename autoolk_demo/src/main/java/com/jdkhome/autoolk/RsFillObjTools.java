package com.jdkhome.autoolk;

import com.jdkhome.autoolk.ann.*;
import com.jdkhome.autoolk.dao.SqlHelper;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 自动填装
 * @author Link
 *
 */
public class RsFillObjTools {

	private static Logger logger = Logger.getLogger(RsFillObjTools.class);
	
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

			    			//TODO 这里需要能够自动转换Long和Integer

			    			field.set(object, rs.getObject(sqlAsName));

						} catch (IllegalArgumentException e) {
							logger.error("Autoolk:查询语句中的["+sqlAsName+"]字段映射失败!请检查类["+clz+"]中,["+field.getName()+"]字段！");

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


	public static List<Map<String,Object>> select(String sql, Object[] parameters) throws SQLException {
		Map<?, ?> map=SqlHelper.executeQuery(null,sql, parameters);
		ResultSet rs=(ResultSet) map.get("rs");

		List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();

		//获取表头
		List<String> headerList=new ArrayList<String>();

		ResultSetMetaData liedata=rs.getMetaData();//获取 列信息
		int columns=liedata.getColumnCount();

		for(int i=1;i<=columns;i++){
			headerList.add(liedata.getColumnName(i));
		}

		while(rs.next()){
			Map<String, Object> obj = new HashMap<String, Object>();

			for(String header:headerList){
				obj.put(header,rs.getObject(header));
			}
			result.add(obj);
		}


		return result;
	}

	/**
	 * 插入(后面改成插入实体)
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public static Integer insert(String sql, Object[] parameters){
		return SqlHelper.insert(sql,parameters);
	}


	/**
	 * 插入对象
	 * @param obj
	 * @return
	 */
	public static Integer insert(Object obj,Class<?> clz) throws Exception {
		//根据对象和注解拼出一个sql语句
		StringBuffer sb=new StringBuffer();

		//获取clz类的注解
		AutoLinkPojo autoLinkPojo=(AutoLinkPojo) clz.getAnnotation(AutoLinkPojo.class);
		//获取表名
		String tableName=autoLinkPojo.value().compareTo("")==0?clz.getSimpleName():autoLinkPojo.value();
		sb.append("insert into ").append(tableName);


		Map<String,Object> tableFieldMap=new LinkedHashMap<String,Object>();

		//获取实体类的所有属性，返回Field数组
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields) {
			if(field.isAnnotationPresent(AutoLinkInsert.class)) {
				AutoLinkInsert autoLinkInsert = (AutoLinkInsert) field.getAnnotation(AutoLinkInsert.class);
				//获取表字段以及对应的值

				//获取到参数对应字段
				Object val=null;

				Method m = null;
				m = (Method) obj.getClass().getMethod("get" + getMethodName(field.getName()));
				val = m.invoke(obj);// 调用getter方法获取属性值
				if(val==null){
					continue;
				}
				tableFieldMap.put(autoLinkInsert.value().compareTo("")==0?field.getName():autoLinkInsert.value(),val);
			}
		}
		sb.append("(");
		for (Iterator it =  tableFieldMap.keySet().iterator();it.hasNext();){
			Object key = it.next();
			sb.append(key).append(",");
		}
		//删除最后拼上去的逗号
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		sb.append("value(");
		for(int i=0;i<tableFieldMap.size();i++){
			sb.append("?,");
		}
		//删除最后拼上去的逗号
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		//构造参数
		Object[] parameters=new Object[tableFieldMap.size()];
		int i=0;
		for (Iterator it =  tableFieldMap.keySet().iterator();it.hasNext();){
			Object key = it.next();
			parameters[i++]=tableFieldMap.get(key);

		}

		//插入
		return insert(sb.toString(),parameters);
	}

	/**
	 * 更新
	 * @param sql
	 * @param parameters
	 * @throws SQLException
	 */
	public static Integer update(String sql, Object[] parameters){
		return SqlHelper.update(sql, parameters);
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
	public static List sqlAutoFill(Connection m_conn,String sql, Object[] parameters,Class<?> clz) throws NoSuchMethodException, Exception{
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
			logger.debug("===========================");
			logger.debug("sql="+sql);
			logger.debug("parameters=");
			for(String s:(String[])parameters){
				logger.debug(s+" ");
			}
			logger.debug("");
			logger.debug("===========================");
			
			
		}
		return null;
		
		
	}
}
