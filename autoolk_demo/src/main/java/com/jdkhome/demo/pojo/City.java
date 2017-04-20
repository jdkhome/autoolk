package com.jdkhome.demo.pojo;

import com.jdkhome.autoolk.ann.AutoLinkFill;
import com.jdkhome.autoolk.ann.AutoLinkInsert;
import com.jdkhome.autoolk.ann.AutoLinkObjListFill;
import com.jdkhome.autoolk.ann.AutoLinkPojo;

import java.util.List;

/**
 * Created by Link on 2017/4/20.
 */
@AutoLinkPojo
public class City {

    @AutoLinkInsert("region_id")
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