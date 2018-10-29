package com.lizhi.bean;

public class CustomParam {

    /**连接符*/
    private String paramLink = "AND";

    private String column;

    private Object value;

    /**运算符*/
    private String symbol = "=";

//    /**是否为原始sql*/
//    boolean originalSql = false;

    public CustomParam() {
    }

    public CustomParam(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public CustomParam( String column, Object value , String symbol , String link ) {
        this.symbol = symbol;
        this.column = column;
        this.value = value;
        this.paramLink = link;
//        this.originalSql = isOriginalSql;
    }

    public String getParamLink() {
        return paramLink;
    }

    public void setParamLink(String paramLink) {
        this.paramLink = paramLink;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

//    public boolean isOriginalSql() {
//        return originalSql;
//    }
//
//    public void setOriginalSql(boolean originalSql) {
//        this.originalSql = originalSql;
//    }
}
