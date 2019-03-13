package com.lizhi.orm.param;

import java.io.Serializable;


public class QueryParam extends AbstractQueryParam<QueryParam> implements Serializable {

    private static final long serialVersionUID = 8097500947924037523L;

    public static QueryParam build(){
        return new QueryParam();
    }
}
