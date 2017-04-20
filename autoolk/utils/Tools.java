package com.jdkhome.autoolk.utils;

import com.jdkhome.autoolk.Autoolk;
import com.jdkhome.autoolk.ann.AutoLinkBasicListFill;
import com.jdkhome.autoolk.ann.AutoLinkFill;
import com.jdkhome.autoolk.ann.AutoLinkObjListFill;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Link on 2017/4/20.
 */
public class Tools {

    private static Logger logger = Logger.getLogger(Tools.class);

    // 把一个字符串的第一个字母大写、效率是最高的、
    public static String getMethodName(String fildeName) throws Exception{
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    /**
     * 获取一个List的类型(比如List<Integer> 返回的是Integer.class)
     * @param field
     * @return 如果返回null 则说明获取失败
     */
    public static Class<?> getListType(Field field){
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

    //获取参数集合
    public static Object[] getParameters(Object object,String parametersName) throws NoSuchMethodException, SecurityException, Exception{

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
                        "get" + Tools.getMethodName(field.getName()));

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



}
