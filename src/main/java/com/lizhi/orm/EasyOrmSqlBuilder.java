package com.lizhi.orm;

import com.lizhi.bean.CustomEntity;
import com.lizhi.orm.param.*;
import com.lizhi.orm.term.Term;

import java.util.List;
import java.util.Map;

/**
 * sql builder
 */
public class EasyOrmSqlBuilder {

    private static final EasyOrmSqlBuilder INSTANCE = new EasyOrmSqlBuilder();

    private EasyOrmSqlBuilder() {
    }

    public static EasyOrmSqlBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * @param resultMapId
     * @param
     * @return  id as id，inickName as inickName 。。。
     */
    public String buildSelectFields(String resultMapId, AbstractQueryParam param) {
        List<String> cludes = param.getCludes();
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
        String tableAlias = null;
        if(param instanceof QueryJoinParam){
            tableAlias = ((QueryJoinParam) param).getMasterTableAlias();
        }

        if(type == QueryParam.CONTAIN_NONE){
            resultselectfield =  OrmSqlGenerator.createSelectField(resultMapId,tableAlias);
        }else {
            resultselectfield =  OrmSqlGenerator.createSelectField(resultMapId,cludes, type,tableAlias);
        }

        if(param instanceof QueryJoinParam){
            if(((QueryJoinParam) param).getJoinCludes() != null){
                if(resultselectfield.length() != 0){
                    resultselectfield +=",";
                }

                resultselectfield += String.join(" , ",((QueryJoinParam) param).getJoinCludes());
            }
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
    public String buildWhere(String resultMapId, OParam param) {
        if(param == null){
            return "";
        }
        return OrmSqlGenerator.createWhereField(resultMapId,param);
    }

    /**
     * 更新语句
     * P_NAME=#{t_parameter.updateObject.name},P_PASSWORD=#{password}
     * @return
     */
    public String buildUpdateFields(String resultMapId, UpdateParam param) {
        Object updateObject = param.getUpdateObject();
        if(updateObject instanceof CustomEntity){
            return OrmSqlGenerator.createUpdateaField(resultMapId,true,null);
        }else if(updateObject instanceof Map){
            return OrmSqlGenerator.createUpdateaField(resultMapId,false,(Map<String,Term>)updateObject);
        }else {
            throw new RuntimeException("更新对象的类型有误");
        }
    }

    public String buildInsertField(String resultMapId) {
        return OrmSqlGenerator.createInsertField(resultMapId);
    }

    public String buildInsertContent(String resultMapId) {
        return OrmSqlGenerator.createInsertBody(resultMapId);
    }
}