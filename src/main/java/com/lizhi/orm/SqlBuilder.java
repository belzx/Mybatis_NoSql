package com.lizhi.orm;

import com.lizhi.orm.param.*;

import java.util.Set;

/**
 * sql builder
 */
public class SqlBuilder {

    private static final SqlBuilder INSTANCE = new SqlBuilder();
    private static final SqlGenerator SQL_GENERATOR ;

    static {
        SQL_GENERATOR = SqlGenerator.instance();
    }
    private SqlBuilder() {
    }

    public static SqlBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * @param resultMapId
     * @param
     * @return  id as id，inickName as inickName 。。。
     */
    public String buildSelectFields(String resultMapId, QueryParam param) {

        Set<String> cludes = param.getCludes();
        int type = param.getContainFeild();
        if (type == QueryParam.CONTAIN_NONE
                || cludes == null
                || cludes.isEmpty()
                || (type != QueryParam.CONTAIN_EXCLUDES
                && type != QueryParam.CONTAIN_INCLUDES
                && type != QueryParam.CONTAIN_GROUP)
                ) {
            type = QueryParam.CONTAIN_NONE;
        }

        String resultselectfield = "";
        if(type == QueryParam.CONTAIN_NONE){
            resultselectfield = "\t*\t";
//            resultselectfield =  SqlGenerator.createSelectField(resultMapId,tableAlias);
        }else {
            String tableAlias = null;
//            if(param instanceof QueryJoinParam){
//                tableAlias = ((QueryJoinParam) param).getMasterTableAlias();
//            }
            resultselectfield =  SQL_GENERATOR.createSelectField(resultMapId,cludes, type,tableAlias);
        }
        return resultselectfield;
    }


    /**
     * 创建where 后面的部分
     * where
     * 1 = 1
     * and  id = #{t_parameter.params.1.value,jdbcType=%s}"
     * or  id1 in ( #{t_parameter.params.1.value,jdbcType=VARCHAR})
     * and id1 > 1 and ...
     *
     * order by
     * sort by
     */
    public String buildWhere(String resultMapId, WhereParam param) {
        if(param == null){
            return "";
        }
        return SqlGenerator.instance().createWhereField(resultMapId,param);
    }

    /**
     * 更新语句
     * P_NAME=#{t_parameter.updateObject.name},P_PASSWORD=#{password}
     * @return
     */
    public String buildUpdateFields(String resultMapId, IUpdateParam param) {
        StringBuilder result = new StringBuilder();
        if(param instanceof UpdateParam){
            result.append(SQL_GENERATOR.createUpdateaField(resultMapId,true,null));
            result.append(SQL_GENERATOR.createUpdateWhereByPk(resultMapId));
        }else if(param instanceof UpdateSetParam){
            result.append(SQL_GENERATOR.createUpdateaField(resultMapId,false,((UpdateSetParam)param).get()));
            result.append(this.buildWhere(resultMapId,(WhereParam) param));
        }else {
            throw new IllegalArgumentException("error!!");
        }
        return result.toString();
    }

    public String buildInsertField(String resultMapId) {
        return SqlGeneratorMate.instance().getInsertFiled(resultMapId);
    }

    public String buildInsertContent(String resultMapId) {
        return SqlGeneratorMate.instance().getInsertBody(resultMapId);
    }
}