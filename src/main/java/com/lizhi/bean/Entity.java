package com.lizhi.bean;

import java.io.Serializable;

public interface Entity<PO> extends Serializable, Cloneable {
    long serialVersionUID = -8371635050344958309L;

    PO getId();

    void setId(PO id);
}
