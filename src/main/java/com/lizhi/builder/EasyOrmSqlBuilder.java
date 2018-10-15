package com.lizhi.builder;


import com.lizhi.bean.CURDParam;
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
     * @param tableName
     */
    public void initResultMap(String resultMapId, String tableName) {
        String key = tableName.concat("-").concat(resultMapId);
        /**获取resultMapId的list ResultMapping*/
        List<ResultMapping> resultMapping = getSqlSession().getConfiguration().getResultMap(resultMapId).getResultMappings();

        /**初始化key（属性名-属性）*/
        mpas_property_resultMapping.put(key, resultMapping.stream().collect(Collectors.toMap(ResultMapping::getProperty, d -> d)));

        /**初始化select固定的字符串*/
        StringBuilder selectField = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (!StringUtils.isEmpty(selectField.toString())) {
                selectField.append(",");
            }
            selectField.append(d.getColumn()).append(" AS ").append(d.getProperty());
        });
        maps_selectfiled.put(key, selectField.toString());

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
        maps_insert_field.put(key, insertField.toString());
        maps_insert_content.put(key, insertContent.toString());


        /**初始化update固定的字符串*/
        final StringBuilder updateField = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (updateField.length() != 0) {
                updateField.append(",");
            }
            updateField.append(String.format(UPDATE_SQL, d.getColumn(), d.getProperty()));
        });
        maps_updatefiled.put(key, updateField.toString());
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
    public static final String BASESQLWHERE = "%s = #{t_parameter.params.%s,jdbcType=%s}";

    public String buildWhere(String resultMapId, String tableName, CURDParam CURDParam) {
        if (!CURDParam.isWHERE()) return "";

        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId, tableName);
        final StringBuilder where = new StringBuilder();
        ((Map<String, Object>) CURDParam.getParams()).entrySet().forEach(d -> {
            if (where.length() != 0) {
                where.append(" AND ");
            }
            ResultMapping resultMapping = resultMappingMaps.get(d.getKey());
            if (resultMapping == null) {
                if (CURDParam.isORIGINALSQL()) {
                    where.append(d.getKey() + " = '" + d.getValue().toString()+"'");
                } else {
                    throw new IllegalArgumentException("非法参数,没有表明为原生参数也不存在mapping中：" + d);
                }
            } else {
                where.append(String.format(BASESQLWHERE, resultMapping.getColumn(), d.getKey(), resultMapping.getJdbcType()));
            }
        });
        return where.toString();
    }

    /**
     * 更新语句
     * P_NAME=#{t_parameter.updateObject.name},P_PASSWORD=#{password}
     *
     * @param resultMapId
     * @param tableName
     * @param CURDParam
     * @return
     */
    public String buildUpdateFields(String resultMapId, String tableName, CURDParam CURDParam) {
        String key = tableName.concat("-").concat(resultMapId);
        String updatePro = maps_updatefiled.get(key);
        if (StringUtils.isEmpty(updatePro)) {
            initResultMap(resultMapId, tableName);
            updatePro = maps_updatefiled.get(key);
        }
        return updatePro;
    }

    /**
     * 从缓存中取，如果缓存中不存在，则新建
     * id as id，inickName as inickName 。。。
     *
     * @param resultMapId
     * @param tableName
     * @param CURDParam
     * @return
     */
    public String buildSelectFields(String resultMapId, String tableName, CURDParam CURDParam) {
        String key = tableName.concat("-").concat(resultMapId);
        String selectPro = maps_selectfiled.get(key);
        if (StringUtils.isEmpty(selectPro)) {
            initResultMap(resultMapId, tableName);
            selectPro = maps_selectfiled.get(key);
        }
        return selectPro;
    }

    /**
     * group by name1 ,name2
     * order by name1 desc ,name2 adc
     * limit 0 ,10
     *
     * @param resultMapId
     * @param tableName
     * @param CURDParam
     * @return
     */
    public String buildConditionField(String resultMapId, String tableName, CURDParam CURDParam) {
        StringBuilder sb = new StringBuilder();
        final Map<String, ResultMapping> resultMappingMaps = getResultMapping(resultMapId, tableName);
        if (CURDParam.isGROUP()) {
            StringBuilder s1 = new StringBuilder("GROUP BY ");
            ((List<String>) CURDParam.getGroups()).forEach(d -> {
                if (s1.length() != 9) {
                    s1.append(",");
                }
                if (resultMappingMaps.get(d) == null) {
                    if (CURDParam.isORIGINALSQL()) {
                        s1.append(" ").append(d);
                    } else {
                        throw new IllegalArgumentException("非法参数,没有表明为原生参数也不存在mapping中：" + d);
                    }
                } else {
                    s1.append(" ").append(resultMappingMaps.get(d).getColumn());
                }
            });
            sb.append(s1.toString()).append(" ");
        }
        if (CURDParam.isSORT()) {
            StringBuilder s1 = new StringBuilder("ORDER BY ");
            ((Map<String, String>) CURDParam.getSorts()).entrySet().forEach(d -> {
                if (s1.length() != 9) {
                    s1.append(",");
                }
                if (resultMappingMaps.get(d.getKey()) == null) {
                    if (CURDParam.isORIGINALSQL()) {
                        s1.append(" ").append(d.getKey()).append(" ").append(d.getValue());

                    } else {
                        throw new IllegalArgumentException("非法参数,没有表明为原生参数也不存在mapping中：" + d);
                    }
                } else {
                    s1.append(" ").append(resultMappingMaps.get(d.getKey()).getColumn()).append(" ").append(d.getValue());
                }
            });
            sb.append(s1.toString()).append(" ");
        }
        if (CURDParam.isLIMIT()) {
            sb.append("LIMIT " + CURDParam.getLimit()).append(" ");
        }
        return sb.toString();
    }

    public String buildInsertField(String resultMapId, String tableName) {
        String key = tableName.concat("-").concat(resultMapId);
        String field = maps_insert_field.get(key);
        if (StringUtils.isEmpty(field)) {
            initResultMap(resultMapId, tableName);
            field = maps_insert_field.get(key);
        }
        return field;
    }

    public String buildInsertContent(String resultMapId, String tableName) {
        String key = tableName.concat("-").concat(resultMapId);
        String content = maps_insert_content.get(key);
        if (StringUtils.isEmpty(content)) {
            initResultMap(resultMapId, tableName);
            content = maps_insert_content.get(key);
        }
        return content;
    }

    public Map<String, ResultMapping> getResultMapping(String resultMapId, String tableName) {
        String key = tableName.concat("-").concat(resultMapId);
        Map<String, ResultMapping> maps = mpas_property_resultMapping.get(key);
        if (maps == null) {
            initResultMap(resultMapId, tableName);
            maps = mpas_property_resultMapping.get(key);
        }
        return maps;
    }

}