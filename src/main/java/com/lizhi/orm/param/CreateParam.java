package com.lizhi.orm.param;


import com.lizhi.bean.CustomEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CreateParam<E extends CustomEntity> implements OrmParam, Serializable {

    private static final long serialVersionUID = 8097500947924037523L;

    private List<E> inserts;

    public static CreateParam build(){
        return new CreateParam();
    }

    public List<E> getInserts() {
        return inserts;
    }

    public void setInserts(List<E> inserts) {
        this.inserts = inserts;
    }

    public void insert(E t) {
        if (inserts == null) {
            inserts = new ArrayList<>();
        }
        inserts.add(t);
    }

    public void insert(Collection<E> t) {
        inserts.addAll(t);
    }
}
