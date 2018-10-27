package com.lizhi.bean;

import java.io.Serializable;

public class CustomEntity<PO> implements Serializable,Cloneable {
    private static final long serialVersionUID = -8371635050344958309L;
    public PO id;
    public PO getId() {
        return id;
    }

    public void setId(PO id) {
        this.id = id;
    }
}
