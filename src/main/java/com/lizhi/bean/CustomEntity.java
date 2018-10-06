package com.lizhi.bean;

import java.io.Serializable;

public class CustomEntity<Po> implements Serializable,Cloneable {
    private static final long serialVersionUID = -8371635050344958309L;
    public Po id;
    public Po getId() {
        return id;
    }

    public void setId(Po id) {
        this.id = id;
    }
}
