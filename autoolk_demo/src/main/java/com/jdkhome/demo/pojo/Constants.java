package com.jdkhome.demo.pojo;

/**
 * Created by Link on 2017/4/20.
 */
public class Constants {

    public static final String SearchAllCity="SELECT city.region_id, city.region_name FROM city";

    public static final String SearchCity="SELECT city.region_id AS `id`, city.region_name AS `regionName` FROM city WHERE city.region_id = ?";
    public static final String SearchNextCity="SELECT city.region_id AS `id`, city.region_name AS `regionName` FROM city WHERE city.parent_id = ?";

    public static final String UpdateCity="UPDATE city SET city.region_name = ? WHERE region_id = ?";

    public static final String DeleteCity="DELETE FROM city WHERE region_id = ?";

}