package com.lizhi.bean;


import java.util.Collections;
import java.util.List;

public class PagerResult<E> {
    private static final long serialVersionUID = -6171751136953308027L;

    public static <E> PagerResult<E> empty(){
        return new PagerResult<>(0, Collections.emptyList());
    }

    public static <E> PagerResult<E> of(int total,List<E> list){
        return new PagerResult<>(total,list);
    }
    private int total;

    private List<E> data;

    public PagerResult() {
    }

    public PagerResult(int total, List<E> data) {
        this.total = total;
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public PagerResult<E> setTotal(int total) {
        this.total = total;
        return this;
    }

    public List<E> getData() {
        return data;
    }

    public PagerResult<E> setData(List<E> data) {
        this.data = data;
        return this;
    }

}
