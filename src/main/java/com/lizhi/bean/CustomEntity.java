package com.lizhi.bean;


public class CustomEntity<PO> implements EntityWithPrimary<PO> {
    public static final long serialVersionUID = -8371635050344958309L;
    public PO id;

    @Override
    public PO getId() {
        return id;
    }


    @Override
    public void setId(PO id) {
        this.id = id;
    }
}
