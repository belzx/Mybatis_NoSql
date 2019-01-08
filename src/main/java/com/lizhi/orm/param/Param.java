package com.lizhi.orm.param;

import com.lizhi.orm.term.Term;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Param  implements Cloneable,OrmParam {
     /*
      * 参数 .保存where的参数
      */
     private Map<String, Term> params = new HashMap<>();

     public static Param build(){
          return new Param();
     }

     public  <T extends Param> T and(String column, Object value) {
          return this.where(column, Term.Type.and, Term.TermType.eq, value);
     }

     public <T extends Param> T or(String column, Object value) {
          return where(column, Term.Type.or, Term.TermType.eq, value);
     }

     public  <T extends Param> T where(String column, Object value) {
          return this.and(column,value);
     }

     public <T extends Param> T where(String column, Term.Type type, Term.TermType termType, Object value) {
          addWhere(column, type, termType, value);
          return  (T) this;
     }

     public <T extends Param> T andIn(String column, Collection value) {
         return where(column, Term.Type.and, Term.TermType.in, value);
     }

     public <T extends Param> T  orIn(String column, Collection value) {
          return   where(column, Term.Type.or, Term.TermType.in, value);
     }


//     public <T extends Param> T   orIn(String column, Term.Type type, Term.TermType termType, Collection value) {
//          where(column, type, termType, value);
//          return (T) this;
//     }

//     public T in(String column, Term.Type type, Term.TermType termType, String... value) {
//          where(column, type, termType, value);
//          return (T) this;
//     }

     private void addWhere(Term term) {
          params.put(String.valueOf(params.size()), term);
     }

     private void addWhere(String column, Term.Type type, Term.TermType termType, Object value) {
          if (value != null && Term.TermType.in == termType || Term.TermType.notin == termType) {
               if (value instanceof Collection) {
                    value = String.join(",", (Collection) value);
               } else if (value instanceof String[]) {
                    value = String.join(",", (String[]) value);
               }
          }
          addWhere(new Term(column, value, type, termType));
     }

     public Map<String, Term> getParams() {
          return params;
     }

     public void setParams(Map<String, Term> params) {
          this.params = params;
     }

     public QueryParam convert(){
          return this instanceof QueryParam ? (QueryParam)this:null;
     }

     public UpdateParam convertUpdateParam(){
          return this instanceof UpdateParam ? (UpdateParam)this:null;
     }

     public DeleteParam convertDeleteParam(){
          return this instanceof DeleteParam ? (DeleteParam)this:null;
     }
}
