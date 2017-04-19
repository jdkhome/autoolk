package com.jdkhome.autoolk.dao;

import com.mysql.jdbc.PreparedStatement;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by Link on 2017/4/18.
 * sql工具类
 */
public class SQLUtils {
    /**
     * 获得PreparedStatement向数据库提交的SQL语句
     *
     * @param sql
     * @param params
     * @return
     */
    public static String getPreparedSQL(String sql, Object[] params) {
        //1 如果没有参数，说明是不是动态SQL语句
        int paramNum = 0;
        if (null != params) paramNum = params.length;
        if (1 > paramNum) return sql;
        //2 如果有参数，则是动态SQL语句
        StringBuffer returnSQL = new StringBuffer();
        String[] subSQL = sql.split("\\?");
        for (int i = 0; i < paramNum; i++) {
            if (params[i] instanceof Date) {
                returnSQL.append(subSQL[i]).append(" '").append(javaDate2MysqlDate((java.util.Date) params[i])).append("' ");
            } else {
                returnSQL.append(subSQL[i]).append(" '").append(params[i]).append("' ");
            }
        }

        if (subSQL.length > params.length) {
            returnSQL.append(subSQL[subSQL.length - 1]);
        }
        return returnSQL.toString();
    }

    /**
     * 为PreparedStatement预编译的SQL语句设置参数
     *
     * @param pstmt
     * @param params
     */
    private static void setParams(PreparedStatement pstmt, Object[] params) {
        if (null != params) {
            for (int i = 0, paramNum = params.length; i < paramNum; i++) {
                try {
                    if (null != params[i] &&
                            params[i] instanceof java.util.Date) {
                        pstmt.setDate(i + 1, javaDate2MysqlDate((java.util.Date) params[i]));


                    } else {
                        pstmt.setObject(i + 1, params[i]);
                    }
                } catch (SQLException e) {
                }
            }
        }
    }

    //java 时间 转 sql 时间
    private static Date javaDate2MysqlDate(java.util.Date javaDate){
        return new java.sql.Date(javaDate.getTime());
    }
}
