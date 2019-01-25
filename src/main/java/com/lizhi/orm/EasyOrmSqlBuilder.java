package com.lizhi.orm;

import com.lizhi.bean.CustomEntity;
import com.lizhi.orm.param.Param;
import com.lizhi.orm.param.QueryJoinParam;
import com.lizhi.orm.param.QueryParam;
import com.lizhi.orm.param.UpdateParam;
import com.lizhi.orm.term.Term;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;

/**
 * sql builder
 */
public class EasyOrmSqlBuilder {

    volatile static SqlSessionFactory sqlSession;

    private static final EasyOrmSqlBuilder INSTANCE = new EasyOrmSqlBuilder();

    private static OrmSqlGenerator ormSqlGenerator = OrmSqlGenerator.instance();

    private EasyOrmSqlBuilder() {

    }

    public static void setSqlSessionFactory(SqlSessionFactory sqlSession) {
        EasyOrmSqlBuilder.sqlSession = sqlSession;
        ormSqlGenerator.processSqlSession(sqlSession);
    }

    public static EasyOrmSqlBuilder getInstance() {
        return INSTANCE;
    }

    public static SqlSessionFactory getSqlSession() {
        if (sqlSession == null) {
            throw new UnsupportedOperationException("sqlSession is null");
        }
        return sqlSession;
    }

    /**
     * @param resultMapId
     * @param
     * @return  id as id，inickName as inickName 。。。
     */
    public String buildSelectFields(String resultMapId, QueryParam param) {
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
            resultselectfield =  ormSqlGenerator.createSelectField(resultMapId,tableAlias);
        }else {
            resultselectfield =  ormSqlGenerator.createSelectField(resultMapId,cludes, type,tableAlias);
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
    public String buildWhere(String resultMapId, Param param) {
        if(param == null){
            return "";
        }
        return ormSqlGenerator.createWhereField(resultMapId,param);
    }


    /**
     * 更新语句
     * P_NAME=#{t_parameter.updateObject.name},P_PASSWORD=#{password}
     *
     * @return
     */
    public String buildUpdateFields(String resultMapId, UpdateParam param) {
        Object updateObject = param.getUpdateObject();
        if(updateObject instanceof CustomEntity){
            return ormSqlGenerator.createUpdateaField(resultMapId,true,null);
        }else if(updateObject instanceof Map){
            return ormSqlGenerator.createUpdateaField(resultMapId,false,(Map<String,Term>)updateObject);
        }else {
            throw new RuntimeException("更新对象的类型有误");
        }
    }

    public String buildInsertField(String resultMapId) {
        return ormSqlGenerator.createInsertField(resultMapId);
    }

    public String buildInsertContent(String resultMapId) {
        return ormSqlGenerator.createInsertBody(resultMapId);
    }
}