package com.lizhi.orm.param;

import com.lizhi.orm.term.Term;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author https://github.com/lizhixiong1994
 */
public class Param implements Cloneable, OrmParam {
    /*
     * 参数 .保存where的参数
     */
    private Map<String, Term> params = new HashMap<>();

    public static Param build() {
        return new Param();
    }

    public <T extends Param> T and(String column, Object value) {
        return this.where( Term.Type.and, column,Term.TermType.eq, value);
    }

    public <T extends Param> T or(String column, Object value) {
        return where( Term.Type.or,column, Term.TermType.eq, value);
    }

    public <T extends Param> T where(String column, Object value) {
        return this.and(column, value);
    }

    public <T extends Param> T where(Term.Type type,String column, Term.TermType termType, Object value) {
        addWhere(column, type, termType, value);
        return (T) this;
    }

    public <T extends Param, J> T andIn(String column, Collection<J> value) {
        return where(Term.Type.and, column, Term.TermType.in, value);
    }

    public <T extends Param, J> T orIn(String column, Collection<J> value) {
        return where(Term.Type.or, column, Term.TermType.in, value);
    }

    public void addWhere(Term term) {
        params.put(String.valueOf(params.size()), term);
    }

    public void addWhere(String column, Term.Type type, Term.TermType termType, Object value) {
        if (value != null && Term.TermType.in == termType || Term.TermType.notin == termType) {
            Map<String, Object> map = new HashMap<>();
            if (value instanceof Collection) {
                for(Object object : (Collection<String>) value){
                    map.put(String.valueOf(map.size()),object);
                }
                value = map;
            } else if (value instanceof String[]) {
                for(Object object : (String[]) value){
                    map.put(String.valueOf(map.size()),object);
                }
                value = map;
            }
        }
        addWhere(new Term(column, value, type, termType));
    }

    public Map<String, Term> getParams() {
        return params;
    }

    public QueryParam qert() {
        return (QueryParam)this;
    }

    public UpdateParam uert() {
        return (UpdateParam)this ;
    }

    public DeleteParam dert() {
        return (DeleteParam)this ;
    }

    public QueryParam qjert() {
        return (QueryJoinParam)this ;
    }
}
