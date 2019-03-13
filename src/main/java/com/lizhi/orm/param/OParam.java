package com.lizhi.orm.param;

import com.lizhi.orm.term.Term;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author https://github.com/lizhixiong1994
 */
public class OParam<T> implements Cloneable, Param {

    /*
     * 参数 .保存where的参数
     */
    private Map<String, Term> params = new HashMap<>();

    public T and(String column, Object value) {
        if(value instanceof Collection){
            return this.where(Term.Type.and, column, Term.TermType.in, value);
        }
        return this.where(Term.Type.and, column, Term.TermType.eq, value);
    }

    public T where(String column, Object value) {
        if(value instanceof Collection){
            return this.where(Term.Type.and, column, Term.TermType.in, value);
        }
        return this.where(Term.Type.and, column, Term.TermType.eq, value);
    }

    public T or(String column, Object value) {
        if(value instanceof Collection){
            return where(Term.Type.or, column, Term.TermType.in, value);
        }
        return where(Term.Type.or, column, Term.TermType.eq, value);
    }

    public T where(Term.Type type, String column, Term.TermType termType, Object value) {
        addWhere(column, type, termType, value);
        return (T) this;
    }

    public void addWhere(Term term) {
        params.put(String.valueOf(params.size()), term);
    }

    public void addWhere(String column, Term.Type type, Term.TermType termType, Object value) {
        if (value != null && Term.TermType.in == termType || Term.TermType.notin == termType) {
            Map<String, Object> map = new HashMap<>();
            if (value instanceof Collection) {
                for (Object object : (Collection<String>) value) {
                    map.put(String.valueOf(map.size()), object);
                }
                value = map;
            }
        }
        addWhere(new Term(column, value, type, termType));
    }

    public Map<String, Term> getParams() {
        return params;
    }
}
