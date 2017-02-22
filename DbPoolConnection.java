package com.jdkhome.autoolk;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

public class DbPoolConnection {
	private static DbPoolConnection databasePool = null;
	private static DruidDataSource dds = null;
	 static{
	        Properties properties=new Properties();
	        InputStream in = Object.class.getResourceAsStream("/db.properties");
	        try {
	        	properties.load(in);
	        	dds = (DruidDataSource) DruidDataSourceFactory
						.createDataSource(properties);
	    
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

	    }
	
	private DbPoolConnection() {}
	public static  DbPoolConnection getInstance() {
		if (null == databasePool) {
			databasePool = new DbPoolConnection();
		}
		return databasePool;
	}
	public static DruidPooledConnection getConnection() throws SQLException {
		return dds.getConnection();
	}


}
