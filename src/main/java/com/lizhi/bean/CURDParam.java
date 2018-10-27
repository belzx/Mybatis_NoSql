package com.lizhi.bean;

import java.util.*;

/**
 * 通用CURD对象
 * @param <T>
 */
public class CURDParam<T extends CustomEntity> {

    //key只能为string
    private Map<String, CustomParam> params;//where字段的参数

    private Map<String, CustomParam> sorts;//order by的参数

    private Map<String, CustomParam> groups;//order by的参数

    private T updateObject;//单个更新

    private int pageNumber = 0;//页数，从0开始

    private int pageSize = 0;//一页的数量

    private boolean isCount;//是否为统计数据

    public static CURDParam getInstans(){
        return new CURDParam();
    }

    public boolean isCount() {
        return isCount;
    }

    public void setCount(boolean count) {
        isCount = count;
    }

    public CURDParam update(T entity) {
        this.updateObject = entity;
        return this;
    }

    public CURDParam where(CustomParam customParam){
        if(params == null) params = new HashMap<>();
        this.params.put(params.size()+"",customParam);
        return this;
    }

    public CURDParam where(String column,Object value){
        if(params == null) params = new HashMap<>();
        this.params.put(params.size()+"",new CustomParam(column,value));
        return this;
    }

    public CURDParam group(String column){
        if(groups == null) groups = new HashMap<>();
        this.groups.put(params.size()+"",new CustomParam(column,null));
        return this;
    }

    public CURDParam group(CustomParam customParam){
        if(groups == null) groups = new HashMap<>();
        this.groups.put(params.size()+"",customParam);
        return this;
    }

    public CURDParam sort(CustomParam customParam){
        if(sorts == null) sorts = new HashMap<>();
        this.sorts.put(params.size()+"",customParam);
        return this;
    }

    public CURDParam sort(String column,Object value){
        if(sorts == null) sorts = new HashMap<>();
        this.sorts.put(params.size()+"",new CustomParam(column,value));
        return this;
    }

    public CURDParam limit(int pageNumber,int pageSize){
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return this;
    }

    public Map<String, CustomParam> getParams() {
        return params;
    }

    public void setParams(Map<String, CustomParam> params) {
        this.params = params;
    }

    public Map<String, CustomParam> getSorts() {
        return sorts;
    }

    public void setSorts(Map<String, CustomParam> sorts) {
        this.sorts = sorts;
    }

    public Map<String, CustomParam> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, CustomParam> groups) {
        this.groups = groups;
    }

    public T getUpdateObject() {
        return updateObject;
    }

    public void setUpdateObject(T updateObject) {
        this.updateObject = updateObject;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}