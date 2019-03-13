package com.lizhi.bean;

/**
 * 包含主键
 */
public interface EntityWithPrimary<PO> extends Entity<PO>{
    long serialVersionUID = -8371635050344958309L;

    PO getId();

    void setId(PO id);
}
