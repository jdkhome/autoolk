package com.jdkhome.demo;

import com.jdkhome.autoolk.Autoolk;
import com.jdkhome.demo.pojo.City;
import com.jdkhome.demo.pojo.Constants;

import java.util.List;
import java.util.Map;

/**
 * Created by Link on 2017/4/20.
 */
public class Test {

    public static void main(String[] args) throws Exception {


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
        System.out.println("city count = " + count);

    }
}
