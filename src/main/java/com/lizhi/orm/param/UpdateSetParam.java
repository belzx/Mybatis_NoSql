package com.lizhi.orm.param;


import com.lizhi.orm.term.Term;

import java.util.HashMap;
import java.util.Map;

public class UpdateSetParam extends WhereParam<UpdateSetParam> implements IUpdateParam {

    private Map<String, Term> updateObject = new HashMap();

    public static UpdateSetParam build() {
        return new UpdateSetParam();
    }

    public UpdateSetParam set(String column, Object value) {
        updateObject.put(column, new Term(column, value, null, null));
        return this;
    }

    public Map<String, Term> get() {
        return updateObject;
    }
}
