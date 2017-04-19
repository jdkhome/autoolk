
package com.jdkhome.autoolk.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SqlHelper {

	// 定义一个全局的记录器，通过LoggerFactory获取
	static Log log = LogFactory.getLog(SqlHelper.class);
	
	/**
	 * 获取连接
	 * @return
	 */
	public static Connection getConnectionFromDruid(){
		DbPoolConnection dbp = DbPoolConnection.getInstance();
		Connection conn=null;
		try{
			conn=dbp.getConnection();
		}catch(Exception e){
			System.out.println("连接获取失败");
			e.printStackTrace();
		}
		return conn;
	}
	 
	/**
	 * 释放连接
	 * @param conn
	 */
	public static void closeDruild(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	    
	// 定义要使用的变量
	private static Connection _Connection = null;
	private static PreparedStatement ps = null;
	
	private static ResultSet rs = null;
	private static CallableStatement cs = null;

	public static ResultSet getRs() {
		return rs;
	}

	public static CallableStatement getCs() {
		return cs;
	}


	/**
	 * 插入一条
	 * @param sql
	 * @param parameters
	 * @return 正常返回主键id 失败返回null
	 */
	public static Integer insert(String sql, Object[] parameters){
		Connection conn=null;
		PreparedStatement thisPs=null;
		try {
			conn =getConnectionFromDruid();
			thisPs=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);//获取一个新的PreparedStatement
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					thisPs.setObject(i + 1, parameters[i]);
				}
			}
			thisPs.executeUpdate();
           /* todo 不知道为啥取不到返回值
           ResultSet resultSet=thisPs.getGeneratedKeys();
            if(resultSet.next()){
                return thisPs.getGeneratedKeys().getInt(1);
            }*/
            return null;


		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
            //debug模式下输出sql语句
            log.info(SQLUtils.getPreparedSQL(sql,parameters));

            //关闭连接
            closeDruild(conn);
		}
	}

	/**
	 * 更新
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public static Integer update(String sql, Object[] parameters){
		Connection conn=null;
		PreparedStatement thisPs=null;
		try {
			conn =getConnectionFromDruid();
			thisPs=conn.prepareStatement(sql);//获取一个新的PreparedStatement
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					thisPs.setObject(i + 1, parameters[i]);
				}
			}
			return thisPs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} finally {

		    //debug模式下输出sql语句
            log.debug(SQLUtils.getPreparedSQL(sql,parameters));

            //关闭连接
            closeDruild(conn);
		}
	}

	/**
	 * 查询
	 * 可使用现有连接进行查询，若现有连接为null，则从新从连接池申请资源
	 * @param m_conn 现有连接
	 * @param sql sql语句
	 * @param parameters 参数
	 * @return 返回ResultSet和Connection 外部根据需求自行关闭连接
	 */
	public static Map< String, Object> executeQuery(Connection m_conn,String sql, Object[] parameters) {
		ResultSet rs = null;
		Connection  conn=null;
		try {
			conn =(m_conn==null)?getConnectionFromDruid():m_conn;
			PreparedStatement thisPs=conn.prepareStatement(sql);//获取一个新的PreparedStatement
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					thisPs.setObject(i + 1, parameters[i]);
				}
			}
			rs = thisPs.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
            //debug模式下输出sql语句
            log.debug(SQLUtils.getPreparedSQL(sql,parameters));
		}

		//将查询结果和连接都返回出去，外部可以继续使用链接
		Map< String, Object> mapCR=new HashMap< String, Object>();
		mapCR.put("rs", rs);
		mapCR.put("conn", conn);
		return mapCR;
	}

	/**
	 * 关闭连接 暂留
	 * 
	 * @param rs
	 * @param ps
	 * @param conn
	 */
	public static void close(ResultSet rs, Statement ps, Connection conn) {
		if (rs != null)
			try {
				rs.close();
				log.debug(new Date() + "关闭ResultSet");
			} catch (SQLException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}
		rs = null;
		if (ps != null)
			try {
				ps.close();
				log.debug(new Date() + "关闭Statement");
			} catch (SQLException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}
		ps = null;
		if (conn != null)
			try {
				conn.close();
				System.out.println(new Date() + "关闭Connection");
				log.debug(new Date() + "关闭Connection");
			} catch (SQLException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}
		conn = null;
		_Connection = null;
	}

	public static void close() {
		close(rs, ps, _Connection);
	}
	
	public static void closeSeltConnection(Connection connect) {
		if (connect != null){
			try {
				connect.close();
				System.out.println(new Date() + "关闭Connection");
				log.debug(new Date() + "关闭Connection");
			} catch (SQLException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}
		}
	}
	
}
