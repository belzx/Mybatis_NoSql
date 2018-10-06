package com.lizhi.bean;

import java.util.*;

/**
 * 通用CURD对象
 * @param <T>
 */
public class CURDParam<T extends CustomEntity> {

    /**包含的属性，用位运算判断*/
    private int type = NONE;

    private static int NONE = 0x0;

    private static int SORT = 0x1;

    private static int WHERE = 0x2;

    private static int GROUP = 0x4;

    private static int LIMIT = 0x8;

    private Map<String, Object> params;//where字段的参数

    private Map<String, String> sorts;//order by的参数

    private List<String> groups;//group by的参数

    private T updateObject;//单个更新

    private CustomParamsLimit limit;

    private int pageNumber;//页数，从0开始

    private int pageSize;//一页的数量

    public static CURDParam getInstans(){
        return new CURDParam();
    }

    public CURDParam<T> update(T updateObject) {
        this.updateObject = updateObject;
        return this;
    }

    public CURDParam where(String column, Object parma) {
        if (params == null) {
            type += WHERE;
            params = new HashMap<>();
        }
        params.put(column, parma);
        return this;
    }

    public CURDParam sort(String column, String desc) {
        if (sorts == null) {
            type += SORT;
            sorts = new LinkedHashMap<>();
        }
        sorts.put(column, desc);
        return this;
    }

    public CURDParam group(String column) {
        if (groups == null) {
            type += GROUP;
            groups = new LinkedList();
        }
        groups.add(column);
        return this;
    }

    public CURDParam limit(int skip, int len) {
        if (limit == null) {
            type += LIMIT;
        }
        limit = new CustomParamsLimit(skip, len);
        return this;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isLIMIT() {
        return !((type & LIMIT) == 0);
    }

    public boolean isWHERE() {
        return !((type & WHERE) == 0);
    }

    public boolean isSORT() {
        return !((type & SORT) == 0);
    }

    public boolean isGROUP() {
        return !((type & GROUP) == 0);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Map<String, String> getSorts() {
        return sorts;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getLimit() {
        return this.limit.skip + " , " + this.limit.len;
    }

    class CustomParamsLimit {
        private int skip;
        private int len;

        public CustomParamsLimit(int skip, int len) {
            this.skip = skip;
            this.len = len;
        }
    }

    public void setParams(Map<String, Object> params) {
        if (params == null) {
            type += WHERE;
        }
        this.params = params;
    }

    public void setSorts(Map<String, String> sorts) {
        if (this.sorts == null) {
            type += SORT;
        }
        this.sorts = sorts;
    }

    public void setGroups(List<String> groups) {
        if (this.groups == null) {
            type += GROUP;
        }
        this.groups = groups;
    }

    public void setPageNumber(int pageNumber) {
        if (limit == null) {
            type += LIMIT;
        }
        this.pageNumber = pageNumber;
    }

    public void setPageSize(int pageSize) {
        if (limit == null) {
            type += LIMIT;
        }
        this.pageSize = pageSize;
    }
}