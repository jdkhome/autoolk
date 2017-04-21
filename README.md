# autoolk 一个简单的java-mysql框架
配置少,功能强大(给我一头牛我能吹到天上)，使用方便<br>


# 例子

```
	    //任意查询
        List<Map<String,Object>> result= Autoolk.select(Constants.SearchAllCity,new Object[]{});

        //查询实体
        List<City> cityList=Autoolk.select(Constants.SearchCity, new Object[] {142}, City.class);

        //插入
        Autoolk.insert(new City(2333,"测试城市"));

        //更新
        Autoolk.update(Constants.UpdateCity,new Object[]{"城市asd",2333});

        //删除
        Autoolk.delete(Constants.DeleteCity,new Object[]{2333});

        //Count
        Long count=Autoolk.count(Constants.CountCity,new Object[]{},"count");
	
```

sql语句 Constants.java
```
public class Constants {

    public static final String SearchAllCity="SELECT city.region_id as `id`, city.region_name as `name` FROM city";

    public static final String SearchCity="SELECT city.region_id AS `id`, city.region_name AS `regionName` FROM city WHERE city.region_id = ?";
    public static final String SearchNextCity="SELECT city.region_id AS `id`, city.region_name AS `regionName` FROM city WHERE city.parent_id = ?";

    public static final String UpdateCity="UPDATE city SET city.region_name = ? WHERE region_id = ?";

    public static final String DeleteCity="DELETE FROM city WHERE region_id = ?";

    public static final String CountCity="SELECT count(1) as `count` FROM city";

}
```

例子中的实体 City.java
```
@AutoLinkPojo
public class City {

    @AutoLinkInsert(value = "region_id",primarykey = true)
    @AutoLinkFill("id")
    Integer regionId;

    //如果没有指定@AutoLinkFill的value,则自动取变量名作为映射
    @AutoLinkInsert("region_name")
    @AutoLinkFill
    String regionName;

    /**
     * 查询下一级城市实体列表,parameters代表参数的变量名
     * 支持多个参数,只需将parameters的类型设置为String,并将多个参数用','隔开
     */
    @AutoLinkObjListFill(sql = Constants.SearchNextCity,parameters="regionId")
    List<City> nextCity;

    //构造
    public City(){}
    public City(Integer regionId,String regionName){
        this.regionId=regionId;
        this.regionName=regionName;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<City> getNextCity() {
        return nextCity;
    }

    public void setNextCity(List<City> nextCity) {
        this.nextCity = nextCity;
    }

}
```

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
