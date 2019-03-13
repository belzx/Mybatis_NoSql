package com.lizhi.orm.constans;

import com.lizhi.orm.term.Term;

public enum ConstansSqlType {
    eq(Term.TermType.eq, " = "),
    neq(Term.TermType.neq, " <> "),
    lt(Term.TermType.lt, " < "),
    lte(Term.TermType.lte, " <= "),
    gt(Term.TermType.gt, " > "),
    gte(Term.TermType.gte, " >= "),
    isnull(Term.TermType.isnull, " is null "),
    isvoid(Term.TermType.isvoid, " = \"\" "),
    notnull(Term.TermType.notnull, " is not null "),
    notvoid(Term.TermType.notvoid, " <> \"\" "),
    in(Term.TermType.in, " in "),
    notin(Term.TermType.notin, " not in "),
    like(Term.TermType.like, " like "),
    notlike(Term.TermType.notlike, "not like ");

    private final Term.TermType type;

    private final String desc;

    public static final String DEFAULT_TYPE = " = ";

    private ConstansSqlType(final Term.TermType status, final String desc) {
        this.type = status;
        this.desc = desc;
    }

    public static String getDesc(Term.TermType type) {
        ConstansSqlType[] constansSqlTypes = values();
        for (ConstansSqlType constansSqlType : constansSqlTypes) {
            if (constansSqlType.type().equals(type)) {
                return constansSqlType.desc();
            }
        }
        return DEFAULT_TYPE;
    }

    public Term.TermType type() {
        return this.type;
    }

    public String desc() {
        return this.desc;
    }

}