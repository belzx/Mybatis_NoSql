package com.lizhi.orm.term;

public class SortTerm extends Term {
    private static final long serialVersionUID = -165240611714598720L;

    public static final String DESC = "desc";

    public static final String ASC = "asc";


    public SortTerm(String column) {
        super(column,DESC,null,null);
    }

    public  SortTerm(String column ,String description) {
        super(column,description,null,null);
    }
}
