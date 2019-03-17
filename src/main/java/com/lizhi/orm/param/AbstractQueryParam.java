package com.lizhi.orm.param;

import com.lizhi.orm.term.SortTerm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractQueryParam<T extends AbstractQueryParam> extends OParam<T> {

    private static final long serialVersionUID = 8097500947924037523L;

    public static int CONTAIN_NONE = 0;

    public static int CONTAIN_INCLUDES = 1;

    public static int CONTAIN_EXCLUDES = 2;

    public static int CONTAIN_GROUP = 4;

    /**
     * limit pageIndex ，pageSize
     */
    private int pageNumber = 0;//页数，从0开始

    private int pageSize = 0;//一页的数量

    /**
     * CONTAIN_NONE 使用默认值
     * CONTAIN_INCLUDES 包含
     * CONTAIN_EXCLUDES 剔除
     * 只包含的字段
     */
    private Set<String> cludes;

    private List<SortTerm> sorts;

    private Set<String> groups;

    /**
     * 是否包含字段筛选。下面两个是互斥关系
     */
    private int containFeild = CONTAIN_NONE;

    public T excludes(String... columns) {
        for (String column : columns) {
            this.cludes(column, CONTAIN_EXCLUDES);
        }
        return (T) this;
    }

    public T includes(String... columns) {
        for (String column : columns) {
            this.cludes(column, CONTAIN_INCLUDES);
        }
        return (T) this;
    }

    public T sortDesc(String column) {
        addSortTerm(new SortTerm(column, SortTerm.DESC));
        return (T) this;
    }

    public T sortAsc(String column) {
        addSortTerm(new SortTerm(column, SortTerm.ASC));
        return (T) this;
    }

    public T group(String column) {
        addGroup(column);
        return (T) this;
    }

    public T limit(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return (T) this;
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
