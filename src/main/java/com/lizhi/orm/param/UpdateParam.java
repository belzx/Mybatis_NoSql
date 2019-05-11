package com.lizhi.orm.param;

/**
 * @author lizhixiong
 * @time 2019 - 05 - 09 - 23:20
 */
public class UpdateParam<T> implements IUpdateParam {

    private T updateObject;

    public UpdateParam save(T t){
        updateObject = t;
        return this;
    }

    public T get(){
        return updateObject;
    }

    public  static UpdateParam build(){
        return new UpdateParam();
    }
}
