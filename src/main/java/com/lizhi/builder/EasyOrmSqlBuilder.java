package com.lizhi.builder;


import com.lizhi.bean.CURDParam;
import com.lizhi.bean.CustomParam;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EasyOrmSqlBuilder {

    volatile static SqlSessionFactory sqlSession;

    private static final EasyOrmSqlBuilder instance = new EasyOrmSqlBuilder();

    public static final String UPDATE_SQL = "%s = #{t_parameter.updateObject.%s}";

//    public static final String BASESQL_WHERE = "%s %s #{t_parameter.params.%s.value,jdbcType=%s}";

    public static final String BASESQL_WHERE = "%s %s #{t_parameter.params.%s.value %s}";

//    public static final String INSERT_SQL = "insert into %s(%s) values (%s)";

//    public static final String BATCH_INSERT_SQL = "insert into %s(%s) values ( <foreach collection=\"list\" item=\"item\" open=\"(\" close=\")\" separator=\",\" index=\"index\"> %s  </foreach>)";
    /**
     * key:(property:object)
     */
    protected static final Map<String, Map<String, ResultMapping>> mpas_column_resultMapping = new HashMap<>();

    protected static final Map<String, String> maps_selectfiled = new HashMap<>();

    protected static final Map<String, String> maps_updatefiled = new HashMap<>();

    protected static final Map<String, String> maps_insert_field = new HashMap<>();

    protected static final Map<String, String> maps_insert_content = new HashMap<>();

    private EasyOrmSqlBuilder() {
    }

    public static void setSqlSession(SqlSessionFactory sqlSession) {
        EasyOrmSqlBuilder.sqlSession = sqlSession;
        for(String resultId : sqlSession.getConfiguration().getResultMapNames()){
            initResultMap(resultId);
        }
    }

    public static EasyOrmSqlBuilder getInstance() {
        return instance;
    }

    public static SqlSessionFactory getSqlSession() {
        if (sqlSession == null) {
            throw new UnsupportedOperationException("sqlSession is null");
        }
        return sqlSession;
    }

    /**
     * 初始化语句，一些固定的值
     *
     * @param resultMapId
     */
    public static void initResultMap(String resultMapId) {

        /**获取resultMapId的list ResultMapping*/
        List<ResultMapping> resultMapping = getSqlSession().getConfiguration().getResultMap(resultMapId).getResultMappings().stream().filter(d ->{ return StringUtils.isEmpty(d.getNestedQueryId());}).collect(Collectors.toList());

        /**初始化key（属性名-属性）*/
        mpas_column_resultMapping.put(resultMapId, resultMapping.stream().collect(Collectors.toMap(ResultMapping::getColumn, d -> d)));

        /**初始化select固定的字符串*/
        StringBuilder selectField = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (!StringUtils.isEmpty(selectField.toString())) {
                selectField.append(",");
            }
            selectField.append(d.getColumn()).append(" AS ").append(d.getProperty());
        });
        maps_selectfiled.put(resultMapId, selectField.toString());

        /**初始化insert固定的字段*/
        /**初始化batchinsert固定的内容*/
        StringBuilder insertField = new StringBuilder();
        StringBuilder insertContent = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (insertField.length() != 0) {
                insertField.append(",");
                insertContent.append(",");
            }
            insertField.append(d.getColumn());
            insertContent.append("#{t_parameter." + d.getProperty() + "}");
        });
        maps_insert_field.put(resultMapId, insertField.toString());
        maps_insert_content.put(resultMapId, insertContent.toString());


        /**初始化update固定的字符串*/
        final StringBuilder updateField = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (updateField.length() != 0) {
                updateField.append(",");
            }
            updateField.append(String.format(UPDATE_SQL, d.getColumn(), d.getProperty()));
        });
        maps_updatefiled.put(resultMapId, updateField.toString());
    }


    /**
     * 构造where查询的条件语句
     * 需要构造成这个user = #{params.param.userCode,jdbcType=VARCHAR}
     *
     * @param resultMapId
     * @return
     */
    public String buildWhere(String resultMapId , CURDParam CURDParam) {
        if (CURDParam.getParams() == null || CURDParam.getParams().isEmpty()) return "";

        final StringBuilder where = new StringBuilder();

        Set<Map.Entry<Integer,CustomParam>> entrys = CURDParam.getParams().entrySet();
        Map<String, ResultMapping> stringResultMappingMap = mpas_column_resultMapping.get(resultMapId);

        for(Map.Entry<Integer,CustomParam> entry : entrys){
            CustomParam param = entry.getValue();

            if (where.length() != 0){
                where.append(" "+param.getParamLink()+" ");
            }

            ResultMapping resultMapping = stringResultMappingMap.get(param.getColumn());
            if(resultMapping != null){
                if(resultMapping.getJdbcType() == null || "".equals(resultMapping.getJdbcType().toString())){
                    where.append(String.format(BASESQL_WHERE,resultMapping.getColumn(),param.getSymbol(),entry.getKey(), ",jdbcType=VARCHAR"));
                }else {
                    where.append(String.format(BASESQL_WHERE,resultMapping.getColumn(),param.getSymbol(),entry.getKey(), ",jdbcType="+resultMapping.getJdbcType().toString()));
                }
             }else {
                where.append(String.format(BASESQL_WHERE,param.getColumn(),param.getSymbol(),entry.getKey(),""));
            }
          }
        return where.toString();
    }

    /**
     * 更新语句
     * P_NAME=#{t_parameter.updateObject.name},P_PASSWORD=#{password}
     *
     * @param resultMapId
     * @param CURDParam
     * @return
     */
    public String buildUpdateFields(String resultMapId, CURDParam CURDParam) {
        String updatePro = maps_updatefiled.get(resultMapId);
        if (StringUtils.isEmpty(updatePro)) {
            initResultMap(resultMapId);
            updatePro = maps_updatefiled.get(resultMapId);
        }
        return updatePro;
    }

    /**
     * 从缓存中取，如果缓存中不存在，则新建
     * id as id，inickName as inickName 。。。
     *
     * @param resultMapId
     * @param CURDParam
     * @return
     */
    public String buildSelectFields(String resultMapId, CURDParam CURDParam) {
        String selectPro = maps_selectfiled.get(resultMapId);
        if (StringUtils.isEmpty(selectPro)) {
            initResultMap(resultMapId);
            selectPro = maps_selectfiled.get(resultMapId);
        }
        return selectPro;
    }

    public String buildGroupField(String resultMapId, CURDParam CURDParam) {
        StringBuilder sql = new StringBuilder();

        if (CURDParam.getGroups()!= null && !CURDParam.getGroups().isEmpty()) {
            StringBuilder s1 = new StringBuilder("GROUP BY ");
            ArrayList<CustomParam> groups = new ArrayList(CURDParam.getGroups().values());
            for(CustomParam param : groups){
                if (!"GROUP BY ".equals(s1.toString())) {
                    s1.append(",");
                }
                s1.append(param.getColumn());
            }
            sql.append(s1.toString()).append("\n");
        }

        return sql.toString();
    }

    public String buildSortField(String resultMapId, CURDParam CURDParam) {
        StringBuilder sql = new StringBuilder();

        if (CURDParam.getSorts()!= null &&!CURDParam.getSorts().isEmpty()) {
            StringBuilder s1 = new StringBuilder("ORDER BY ");
            ArrayList<CustomParam> sorts = new ArrayList(CURDParam.getSorts().values());
            for(CustomParam param : sorts){
                if (!"ORDER BY ".equals(s1.toString())) {
                    s1.append(",");
                }

                s1.append(param.getColumn()).append(" ").append(param.getValue());
            }
            sql.append(s1.toString()).append("\n");
        }

        return sql.toString();
    }

    public String buildLimitField(String resultMapId, CURDParam CURDParam) {
        StringBuilder sb = new StringBuilder();
        if (!(CURDParam.getPageNumber() == 0 && CURDParam.getPageSize() == 0)) {
            sb.append("LIMIT " + CURDParam.getPageNumber()).append(",").append(CURDParam.getPageSize());
        }
        return sb.toString();
    }

    public String buildInsertField(String resultMapId) {
        String field = maps_insert_field.get(resultMapId);
        if (StringUtils.isEmpty(field)) {
            initResultMap(resultMapId);
            field = maps_insert_field.get(resultMapId);
        }
        return field;
    }

    public String buildInsertContent(String resultMapId) {
        String content = maps_insert_content.get(resultMapId);
        if (StringUtils.isEmpty(content)) {
            initResultMap(resultMapId);
            content = maps_insert_content.get(resultMapId);
        }
        return content;
    }
}