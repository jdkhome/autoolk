# autoolk 一个简单的数据库查询框架
主要功能是：<br>
将sql语句查询的到结果，自动转换成对应的实体对象List，支持1对多的关系(即1个实体中有另一个实体的List)<br>
特点是：<br>
使用注解，减少了很多配置<br>

pom.xml
```
    <!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.0.12</version>
		</dependency>
		
		<!-- https://mvnrepository.com/artifact/commons-logging/commons-logging -->
		<dependency>
			<groupId>commons-logging</groupId>
			<artifactId>commons-logging</artifactId>
			<version>1.1</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/commons-pool/commons-pool -->
		<dependency>
			<groupId>commons-pool</groupId>
			<artifactId>commons-pool</artifactId>
			<version>1.6</version>
		</dependency>
		
		<!-- https://mvnrepository.com/artifact/log4j/log4j -->
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.13</version>
		</dependency>


		<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.6</version>
		</dependency>
```

db.properties
```
driverClassName=com.mysql.jdbc.Driver
url=jdbc\:mysql\://localhost\:3306/testdb?useUnicode\=true&characterEncoding\=UTF-8
username=root
password=root
filters=stat
MinIdle=50
initialSize=1
maxActive=300
maxWait=60000
timeBetweenEvictionRunsMillis=60000
minEvictableIdleTimeMillis=300000
validationQuery=SELECT 1
testWhileIdle=true
testOnBorrow=false
testOnReturn=false
poolPreparedStatements=false
maxPoolPreparedStatementPerConnectionSize=200
```

