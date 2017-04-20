package com.jdkhome.demo;

import com.jdkhome.autoolk.RsFillObjTools;
import com.jdkhome.demo.pojo.City;
import com.jdkhome.demo.pojo.Constants;

import java.util.List;
import java.util.Map;

/**
 * Created by Link on 2017/4/20.
 */
public class Test {

    public static void main(String[] args) throws Exception {

        //任意查询 适用于 只查询少量字段的情况
        List<Map<String,Object>> result= RsFillObjTools.select(Constants.SearchAllCity,new Object[]{});
        System.out.println(result.size());

        //查询实体
        List<City> cityList=RsFillObjTools.sqlAutoFill(null, Constants.SearchCity, new Object[] {142}, City.class);
        System.out.println(cityList.size());

        //插入
        City city=new City();
        city.setRegionId(2333);
        city.setRegionName("测试城市");
        RsFillObjTools.insert(city,City.class);

        //更新和删除
        RsFillObjTools.update(Constants.UpdateCity,new Object[]{"城市asd",2333});

        RsFillObjTools.update(Constants.DeleteCity,new Object[]{2333});

    }
}
