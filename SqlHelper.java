
package com.jdkhome.autoolk;

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
	 * 处理多个update/delete/insert
	 * TODO 暂时留着
	 * @param sql
	 * @param parameters
	 */
	public static void executeUpdateMultiParams(String[] sql, String[][] parameters) {
		try {
			// 获得连接
			_Connection = getConnectionFromDruid();
			// 可能传多条sql语句
			_Connection.setAutoCommit(false);
			for (int i = 0; i < sql.length; i++) {
				if (parameters[i] != null) {
					ps = _Connection.prepareStatement(sql[i]);
					for (int j = 0; j < parameters[i].length; j++)
						ps.setString(j + 1, parameters[i][j]);
				}
				ps.executeUpdate();
			}
			_Connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			try {
				_Connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				log.debug(e1.getMessage());
			}
			throw new RuntimeException(e.getMessage());
		} finally {
			// 关闭资源
			close(rs, ps, _Connection);
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
		}
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
