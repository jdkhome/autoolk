package com.jdkhome.autoolk;

import com.jdkhome.autoolk.ann.*;
import com.jdkhome.autoolk.dao.SqlHelper;
import com.jdkhome.autoolk.utils.Tools;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Link on 2017/4/20.
 */
public class Autoolk {


    private static Logger logger = Logger.getLogger(Autoolk.class);


    /**
     * 查询 - 返回实体
     * @param sql sql语句
     * @param parameters 参数
     * @param clz 实体类型 eg: com.jdkhome.demo.pojo.City.class
     * @return
     */
    public static List select(String sql, Object[] parameters, Class<?> clz) throws SQLException, IllegalAccessException, InstantiationException {

        //获取List<Map<String,Object>>
        List<Map<String,Object>> selectResult=select(sql,parameters);


        List<Object> result=new ArrayList<Object>(selectResult.size());

        for(Map<String,Object> map:selectResult) {

            Object object = clz.newInstance();

            //获取实体类的所有属性，返回Field数组
            Field[] fields = clz.getDeclaredFields();

            //填装AutoLinkFill基础字段
            for (Field field : fields) {
                if (field.isAnnotationPresent(AutoLinkFill.class)) {
                    AutoLinkFill autoLinkFill =  field.getAnnotation(AutoLinkFill.class);

                    field.setAccessible(true);
                    //获取sql映射名
                    String sqlAsName = autoLinkFill.value().compareTo("") == 0 ? field.getName() : autoLinkFill.value();
                    try {

                        field.set(object, map.get(sqlAsName));

                    } catch (IllegalArgumentException e) {
                        logger.error("Autoolk:查询语句中的[" + sqlAsName + "]字段映射失败!请检查类[" + clz + "]中,[" + field.getName() + "]字段！");

                        e.printStackTrace();
                        //没有找到该列直接跳过
                        continue;
                    }


                }
            }

            //填装AutoLinkBasicListFill字段
            for (Field field : fields) {
                if (field.isAnnotationPresent(AutoLinkBasicListFill.class)) {
                    AutoLinkBasicListFill autoLinkBasicListFill = (AutoLinkBasicListFill) field.getAnnotation(AutoLinkBasicListFill.class);

                    field.setAccessible(true);
                    //获取List的类型，如果获取失败，则跳过
                    Class<?> genericClazz = Tools.getListType(field);
                    if (genericClazz == null) continue;
                    //获取sql映射名
                    String sqlAsName = autoLinkBasicListFill.value().compareTo("") == 0 ? field.getName() : autoLinkBasicListFill.value();

                    try {

                        //这里要找到字段List的基础类型
                        List<Object> thisBasicList = new ArrayList<Object>();

                        //内容为空直接跳过
                        if (!map.containsKey(sqlAsName) || map.get(sqlAsName) == null || map.get(sqlAsName).toString().length() == 0) {
                            continue;
                        }

                        String thisBasicListStr[] = ((String) map.get(sqlAsName)).split(",");

                        //转换类型并存到List
                        for (String thisBasic : thisBasicListStr) {
                            if (genericClazz.equals(String.class)) {
                                thisBasicList.add(thisBasic);
                            } else if (genericClazz.equals(Integer.class)) {
                                thisBasicList.add(Integer.parseInt(thisBasic));
                            } else if (genericClazz.equals(Long.class)) {
                                thisBasicList.add(Long.parseLong(thisBasic));
                            } else if (genericClazz.equals(Float.class)) {
                                thisBasicList.add(Float.parseFloat(thisBasic));
                            } else if (genericClazz.equals(Double.class)) {
                                thisBasicList.add(Double.parseDouble(thisBasic));
                            } else {
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
                if (field.isAnnotationPresent(AutoLinkObjListFill.class)) {

                    Class<?> genericClazz = Tools.getListType(field);
                    if (genericClazz == null) continue;

                    AutoLinkObjListFill autoLinkObjListFill = (AutoLinkObjListFill) field.getAnnotation(AutoLinkObjListFill.class);

                    field.setAccessible(true);
                    //获取查询语句
                    String nextsql = autoLinkObjListFill.sql();
                    //获取参数对象
                    String parametersName = autoLinkObjListFill.parameters();
                    try {
                        //设置该List
                        field.set(object, Autoolk.select(nextsql, Tools.getParameters(object, parametersName), genericClazz));
                    } catch (Exception e) {
                    }
                }
            }

            result.add(object);
        }

        //关闭链接？？？

        return result;
    }


    /**
     * 查询 - 返回 Map 列表
     * @param sql sql语句
     * @param parameters 参数
     * @return Map形式的结果集
     * @throws SQLException
     */
    public static List<Map<String,Object>> select(String sql, Object[] parameters) throws SQLException {
        Map<?, ?> map=SqlHelper.executeQuery(sql, parameters);
        ResultSet rs=(ResultSet) map.get("rs");

        List<Map<String,Object>> result=new ArrayList<Map<String, Object>>();

        //获取表头
        List<String> headerList=new ArrayList<String>();

        ResultSetMetaData liedata=rs.getMetaData();//获取 列信息



        int columns=liedata.getColumnCount();

        for(int i=1;i<=columns;i++){
            headerList.add(liedata.getColumnLabel(i));
        }

        while(rs.next()){
            Map<String, Object> obj = new TreeMap<String, Object>();

            for(String header:headerList){
                obj.put(header,rs.getObject(header));
            }
            result.add(obj);
        }

        //
        SqlHelper.closeDruild((Connection) map.get("conn"));

        return result;
    }

    /**
     * 插入
     * @param sql sql语句
     * @param parameters 参数
     * @return
     */
    public static Integer insert(String sql, Object[] parameters){
        return SqlHelper.insert(sql,parameters);
    }


    /**
     * 插入对象
     * @param obj 对象实体(需要加上@AutoLinkPojo 注解)
     * @return
     */
    public static Integer insert(Object obj) throws Exception {

        //获取对象类型
        Class<?> clz=obj.getClass();
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
                m = (Method) obj.getClass().getMethod("get" + Tools.getMethodName(field.getName()));
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
     * @param sql sql 语句
     * @param parameters 参数
     * @throws SQLException
     */
    public static Integer update(String sql, Object[] parameters){
        return SqlHelper.update(sql, parameters);
    }

    /**
     * 删除
     * @param sql sql 语句
     * @param parameters 参数
     * @return
     */
    public static Integer delete(String sql, Object[] parameters){
        return update(sql, parameters);
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
    public static Long count(String sql, Object[] parameters,String countAs) throws SQLException{

        Map<?, ?> map=SqlHelper.executeQuery(sql, parameters);
        ResultSet rs=(ResultSet) map.get("rs");

        while(rs.next()){
            return (Long) rs.getObject(countAs);
        }

        return 0L;
    }

}
