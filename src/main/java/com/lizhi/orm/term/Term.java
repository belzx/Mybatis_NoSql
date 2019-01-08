package com.lizhi.orm.term;

import java.io.Serializable;

public class Term implements Serializable {
    private static final long serialVersionUID = -4341916502392785311L;
    private String column;
    private Object value;
    private Term.Type type;
    private Term.TermType termType;

    public Term() {
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TermType getTermType() {
        return termType;
    }

    public void setTermType(TermType termType) {
        this.termType = termType;
    }

    public Term(String column, Object value, Type type, TermType termType) {
        this();
        this.column = column;
        this.value = value;
        this.type = type;
        this.termType = termType;
    }


    public static enum TermType {
        // type    / termType   /    value
        in, //  and       in          (?,?)
        notin, //  and       in          (?,?)
        lt, //  and       <          values
        lte,//            <=
        gt, //            >
        gte,//            >=
        eq, //            =
        neq,//            <>
        like,//            like
        notlike,//         notlike
        isnull,//         is null
        notnull,//        is not null
        isvoid,//         =  ""
        notvoid;//        <> ""
    }

    public static enum Type {
        and,
        or;
    }
}
