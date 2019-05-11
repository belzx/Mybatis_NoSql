package com.lizhi.orm.param;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CreateParam<T> implements Param, Serializable {

    private static final long serialVersionUID = 8097500947924037523L;

    private List<T> inserts = new ArrayList<>();

    public static CreateParam build(){
        return new CreateParam();
    }

    public CreateParam save(T t) {
        inserts.add(t);
        return this;
    }

    public CreateParam save(Collection<T> t) {
        inserts.addAll(t);
        return this;
    }
}
