package com.lizhi.orm.param;

import com.lizhi.orm.term.SortTerm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryParam extends WhereParam<QueryParam> {
    private static final long serialVersionUID = 8097500947924037523L;
    /*
     * CONTAIN_NONE 使用默认值
     * CONTAIN_INCLUDES 包含
     * CONTAIN_EXCLUDES 剔除
     * 只包含的字段
     */
    public static int CONTAIN_NONE = 0;
    public static int CONTAIN_INCLUDES = 1;
    public static int CONTAIN_EXCLUDES = 2;
    public static int CONTAIN_GROUP = 4;

    public static QueryParam build() {
        return new QueryParam();
    }

    /*limit pageIndex ，pageSize*/
    private int pageNumber = 0;//页数，从0开始
    private int pageSize = 0;//一页的数量

    private Set<String> cludes;
    private List<SortTerm> sorts;
    private Set<String> groups;

    /**
     * 是否包含字段筛选。下面两个是互斥关系
     */
    private int containFeild = CONTAIN_NONE;

    public QueryParam excludes(String... columns) {
        for (String column : columns) {
            this.cludes(column, CONTAIN_EXCLUDES);
        }
        return this;
    }

    public QueryParam includes(String... columns) {
        for (String column : columns) {
            this.cludes(column, CONTAIN_INCLUDES);
        }
        return  this;
    }

    public QueryParam sortDesc(String column) {
        addSortTerm(new SortTerm(column, SortTerm.DESC));
        return  this;
    }

    public QueryParam sortAsc(String column) {
        addSortTerm(new SortTerm(column, SortTerm.ASC));
        return  this;
    }

    public QueryParam group(String column) {
        addGroup(column);
        return this;
    }

    public QueryParam limit(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return  this;
    }


    private void cludes(String column, int type) {
        if (cludes == null) {
            cludes = new HashSet<>();
        }

        if (containFeild != type) {
            cludes.clear();
            containFeild = type;
        }

        cludes.add(column);
    }

    private void addSortTerm(SortTerm term) {
        if (sorts == null) {
            sorts = new ArrayList<SortTerm>();
        }
        sorts.add(term);
    }

    private void addGroup(String group) {
        if (groups == null) {
            groups = new HashSet<>();
        }
        groups.add(group);
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

    public Set<String> getCludes() {
        return cludes;
    }

    public void setCludes(Set<String> cludes) {
        this.cludes = cludes;
    }

    public List<SortTerm> getSorts() {
        return sorts;
    }

    public void setSorts(List<SortTerm> sorts) {
        this.sorts = sorts;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public void setContainFeild(int containFeild) {
        this.containFeild = containFeild;
    }

    public int getContainFeild() {
        return containFeild;
    }
}
