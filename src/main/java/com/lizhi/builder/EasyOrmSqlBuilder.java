package com.lizhi.builder;


import com.lizhi.bean.CURDParam;
import com.lizhi.bean.CustomParam;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EasyOrmSqlBuilder {

    volatile static SqlSessionFactory sqlSession;

    private static final EasyOrmSqlBuilder instance = new EasyOrmSqlBuilder();

    public static final String UPDATE_SQL = "%s = #{t_parameter.updateObject.%s}";

//    public static final String INSERT_SQL = "insert into %s(%s) values (%s)";

//    public static final String BATCH_INSERT_SQL = "insert into %s(%s) values ( <foreach collection=\"list\" item=\"item\" open=\"(\" close=\")\" separator=\",\" index=\"index\"> %s  </foreach>)";
    /**
     * key:(property:object)
     */
    protected static final Map<String, Map<String, ResultMapping>> mpas_property_resultMapping = new HashMap<>();

    protected static final Map<String, String> maps_selectfiled = new HashMap<>();

    protected static final Map<String, String> maps_updatefiled = new HashMap<>();

    protected static final Map<String, String> maps_insert_field = new HashMap<>();

    protected static final Map<String, String> maps_insert_content = new HashMap<>();

    private EasyOrmSqlBuilder() {
    }

    public static void setSqlSession(SqlSessionFactory sqlSession) {
        EasyOrmSqlBuilder.sqlSession = sqlSession;
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
    public void initResultMap(String resultMapId) {

        /**获取resultMapId的list ResultMapping*/
        List<ResultMapping> resultMapping = getSqlSession().getConfiguration().getResultMap(resultMapId).getResultMappings();

        /**初始化key（属性名-属性）*/
        mpas_property_resultMapping.put(resultMapId, resultMapping.stream().collect(Collectors.toMap(ResultMapping::getProperty, d -> d)));

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
     * @param tableName
     * @param customParams
     * @return
     */

    public static final String BASESQLWHERE = "%s = #{t_parameter.params.%s.value,jdbcType=%s}";
    public String buildWhere(String resultMapId, CURDParam CURDParam) {
        if (CURDParam.getParams() == null || CURDParam.getParams().isEmpty()) return "";

        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId);

        final StringBuilder where = new StringBuilder();
//        ArrayList<CustomParam> values = new ArrayList(CURDParam.getParams().values());
//        ArrayList<CustomParam> values = new ArrayList(CURDParam.getParams().values());
        Set<Map.Entry<Integer,CustomParam>> entrys = CURDParam.getParams().entrySet();
        for(Map.Entry<Integer,CustomParam> entry : entrys){
            CustomParam param = entry.getValue();

            if (where.length() != 0) where.append(param.getLink());

            if(param.isOriginalSql()){
                where.append(param.getColumn() + param.getSymbol()+"'" + param.getValue()+"'");
                continue;
            }

            ResultMapping resultMapping = resultMappingMaps.get(param.getColumn());
            if(resultMapping == null)
                throw new IllegalArgumentException("使用了非法的参数：" + param.getColumn());
            where.append(String.format(BASESQLWHERE, resultMapping.getColumn(),entry.getKey(), resultMapping.getJdbcType()));
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
        StringBuilder sb = new StringBuilder();
        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId);
        if (CURDParam.getGroups()!= null && !CURDParam.getGroups().isEmpty()) {
            StringBuilder s1 = new StringBuilder("GROUP BY ");
            ArrayList<CustomParam> groups = new ArrayList(CURDParam.getGroups().values());
            for(CustomParam param : groups){
                if (!"GROUP BY ".equals(s1.toString())) {
                    s1.append(",");
                }

                if(param.isOriginalSql()){
                    s1.append(param.getValue());
                    continue;
                }

                ResultMapping resultMapping = resultMappingMaps.get(param.getColumn());
                if(resultMapping == null){
                    throw new IllegalArgumentException("参数"+param.getColumn()+",不存在于resultMapId:{"+resultMapId+"}的配置文件中" );
                }

                s1.append(resultMapping.getColumn());
            }
            sb.append(s1.toString()).append("\n");
        }
        return sb.toString();
    }

    public String buildSortField(String resultMapId, CURDParam CURDParam) {
        StringBuilder sb = new StringBuilder();
        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId);
        if (CURDParam.getSorts()!= null &&!CURDParam.getSorts().isEmpty()) {
            StringBuilder s1 = new StringBuilder("ORDER BY ");
            ArrayList<CustomParam> sorts = new ArrayList(CURDParam.getSorts().values());
            for(CustomParam param : sorts){
                if (!"ORDER BY ".equals(s1.toString())) {
                    s1.append(",");
                }

                if(param.isOriginalSql()){
                    s1.append(param.getValue());
                    continue;
                }

                ResultMapping resultMapping = resultMappingMaps.get(param.getColumn());
                if(resultMapping == null)
                    throw new IllegalArgumentException("使用了非法的参数：" + param.getColumn());

                s1.append(resultMapping.getColumn()).append(" ").append(param.getValue());
            }
            sb.append(s1.toString()).append("\n");
        }
        return sb.toString();
    }

    public String buildLimitField(String resultMapId, CURDParam CURDParam) {
        StringBuilder sb = new StringBuilder();
        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId);
        if (!(CURDParam.getPageNumber() == 0 && CURDParam.getPageSize() == 0)) {
            sb.append("LIMIT " + CURDParam.getPageNumber()).append(",").append(CURDParam.getPageSize());
        }
        return sb.toString();
    }
//    /**
//     * group by name1 ,name2
//     * order by name1 desc ,name2 adc
//     * limit 0 ,10
//     *
//     * @param resultMapId
//     * @param CURDParam
//     * @return
//     */
//    public String buildConditionField(String resultMapId, CURDParam CURDParam) {
//        StringBuilder sb = new StringBuilder();
//        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId);
//
//
//
//        if (!(CURDParam.getPageNumber() == 0 && CURDParam.getPageSize() == 0)) {
//            sb.append("LIMIT " + CURDParam.getPageNumber()).append(",").append(CURDParam.getPageSize());
//        }
//
//        return sb.toString();
//    }

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

    public Map<String, ResultMapping> getResultMapping(String resultMapId) {
        Map<String, ResultMapping> maps = mpas_property_resultMapping.get(resultMapId);
        if (maps == null) {
            initResultMap(resultMapId);
            maps = mpas_property_resultMapping.get(resultMapId);
        }
        return maps;
    }

}