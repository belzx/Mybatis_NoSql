package com.lizhi.orm.param;


import com.lizhi.bean.CustomEntity;
import com.lizhi.orm.term.Term;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UpdateParam<E extends CustomEntity> extends Param  implements Serializable {

    private static final long serialVersionUID = 8097500947924037523L;

    private Object updateObject;

    public static UpdateParam build(){
        return new UpdateParam();
    }

    public <T extends UpdateParam> T set(E t) {
        this.updateObject = t;
        return (T) this;
    }

    public <T extends UpdateParam> T set(String column, Object value) {
        if (updateObject == null) {
            updateObject = new HashMap<String, Term>();
        }
        ((Map<String, Term>) updateObject).put(column, new Term(column, value, null, null));
        return (T) this;
    }


    public Object getUpdateObject() {
        return updateObject;
    }

    public void setUpdateObject(Object updateObject) {
        this.updateObject = updateObject;
    }
}
