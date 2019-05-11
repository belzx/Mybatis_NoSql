package com.lizhi.orm;

import com.lizhi.orm.constans.ConstansSqlType;
import com.lizhi.orm.param.QueryParam;
import com.lizhi.orm.param.WhereParam;
import com.lizhi.orm.term.SortTerm;
import com.lizhi.orm.term.Term;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * sql generator
 */
public class SqlGenerator {
    public static final String RESULTMAP_NAME = "resultMapId";
    /*INSTANCE*/
    private static final SqlGenerator SQL_GENERATOR = new SqlGenerator();
    /*resultMapId:(column:object)*/
    private static final Map<String, Map<String, ResultMapping>> COLUMN_RESULTMAPPING_MAPS = new HashMap<>();
    /*resultMapId:(property:object)*/
    private static final Map<String, Map<String, ResultMapping>> PROPERTY_RESULTMAPPING_MAPS = new HashMap<>();
    /*resultMapId:mapping*/
    private static final Map<String, List<ResultMapping>> RESULTMAPPINGS = new HashMap<>();
    /*resultMapId:pk name*/
    private static final Map<String, ResultMapping> RESULTID_PK = new HashMap<>();

    private SqlGenerator() {
    }

    public static SqlGenerator instance() {
        return SQL_GENERATOR;
    }

    public void init(SqlSessionFactory sqlSession) {
        initResultMappings(sqlSession.getConfiguration());
        initResultMapPks();
        initColumnResultMapping();
        initPropertyResultMapping();
        SqlGeneratorMate.instance().init(RESULTMAPPINGS);
    }


    public String createUpdateWhereByPk(String resultMapId) {
        ResultMapping m = getPkResultMappingByid(resultMapId);
        return new StringBuilder().append("\twhere\t")
                .append(m.getColumn())
                .append("\t=")
                .append("\t#{t_parameter.updateObject.")
                .append(m.getProperty())
                .append("}").toString();
    }

    public ResultMapping getPkResultMappingByid(String resultId) {
        ResultMapping resultMapping = RESULTID_PK.get(resultId);
        if (resultMapping == null) {
            throw new IllegalArgumentException(resultId + "not set id filed!!");
        } else {
            return resultMapping;
        }
    }

    public String createUpdateaField(String resultMapId, boolean isCustomEntity, Map<String, Term> terms) {
        Map<String, ResultMapping> stringResultMappingMap = COLUMN_RESULTMAPPING_MAPS.get(resultMapId);
        if (isCustomEntity) {
            return SqlGeneratorMate.instance().getUpdateFiled(resultMapId);
        } else {
            if (terms == null || terms.isEmpty())
                return "";
            boolean flag = false;
            String column;
            ResultMapping resultMapping;
            JdbcType jdbcType;
            StringBuilder updateField = new StringBuilder();
            for (Map.Entry<String, Term> entry : terms.entrySet()) {
                if (flag) {
                    updateField.append("\t,\t");
                } else {
                    flag = true;
                }
                column = entry.getValue().getColumn();
                resultMapping = stringResultMappingMap.get(column);
                updateField.append(column)
                        .append("\t=\t")
                        .append("#{t_parameter.updateObject.")
                        .append(column)
                        .append(".value");
                if (resultMapping != null) {
                    jdbcType = resultMapping.getJdbcType();
                    if (jdbcType != null) {
                        updateField.append(",jdbcType=").append(jdbcType);
                    }
                }
                updateField.append("}\t");
            }
            return updateField.toString();
        }
    }

    public final String createWhereField(String resultMapId, WhereParam param) {
        final Map<String, Term> whereTerms = param.getParams();
        final StringBuilder where = new StringBuilder();
        //append where
        if (!whereTerms.isEmpty())
            where.append("\twhere\t");

        boolean isFrist = true;
        boolean flag = false;
        boolean isWhereByPk = param.isWhereByPk();
        Term term = null;
        ResultMapping resultMapping = null;
        JdbcType jdbcType = null;
        final Map<String, ResultMapping> stringResultMappingMap = COLUMN_RESULTMAPPING_MAPS.get(resultMapId);
        for (Map.Entry<String, Term> entry : whereTerms.entrySet()) {
            term = entry.getValue();
            if (isFrist) {
                isFrist = false;
                if (isWhereByPk) {
                    where.append("\t")
                            .append(getPkResultMappingByid(resultMapId).getColumn())
                            .append(ConstansSqlType.getDesc(term.getTermType()));
                } else {
                    where.append("\t")
                            .append(term.getColumn())
                            .append(ConstansSqlType.getDesc(term.getTermType()));
                }
            } else {
                where.append(term.getType().name())
                        .append("\t")
                        .append(term.getColumn())
                        .append(ConstansSqlType.getDesc(term.getTermType()));
            }
            resultMapping = stringResultMappingMap.get(term.getColumn());
            if (resultMapping != null)
                jdbcType = resultMapping.getJdbcType();
            //in or not in
            if (term.getTermType() == Term.TermType.in || term.getTermType() == Term.TermType.notin) {
                // where id in (#{t_parameter.params.0.value.1,jdbcType = VARCHAR"),#{t_parameter.params.0.value.2,jdbcType = VARCHAR"))
                int inSize = ((Map) entry.getValue().getValue()).size();
                flag = false;
                where.append("\t(");
                for (int i = 0; i < inSize; i++) {
                    if (flag) {
                        where.append("\t,\t");
                    } else {
                        flag = true;
                    }
                    where.append(" #{t_parameter.params.")
                            .append(entry.getKey())
                            .append(".value.")
                            .append(i);
                    if (jdbcType != null)
                        where.append(",jdbcType=").append(jdbcType);
                    where.append("}\t");
                }
                where.append(")\t");
            } else {
                // where id = #{t_parameter.params.0.value,jdbcType = VARCHAR")
                where.append(" #{t_parameter.params.")
                        .append(entry.getKey())
                        .append(".value");
                if (jdbcType != null)
                    where.append(",jdbcType=").append(jdbcType);
                where.append("}\t");
            }
        }

        //order by
        if (param instanceof QueryParam) {
            QueryParam queryParam = (QueryParam) param;
            //创建sort
            if (queryParam.getSorts() != null && !queryParam.getSorts().isEmpty()) {
                where.append("\n").append("order by\t");
                flag = false;
                for (Object sortTerm : queryParam.getSorts()) {
                    if (flag) {
                        where.append("\t,\t");
                    } else {
                        flag = true;
                    }
                    where.append(((SortTerm) sortTerm).getColumn())
                            .append("\t")
                            .append(((SortTerm) sortTerm).getValue());
                }
            }

            //group by
            if (queryParam.getGroups() != null && !queryParam.getGroups().isEmpty()) {
                where.append("\n").append("group by\t");
                flag = false;
                for (Object group : queryParam.getGroups()) {
                    if (flag) {
                        where.append("\t,\t");
                    } else {
                        flag = true;
                    }
                    where.append(group);
                }
            }

            //limit
            if (queryParam.getPageNumber() != 0 || queryParam.getPageSize() != 0) {
                where.append("\n")
                        .append("limit\t")
                        .append(queryParam.getPageNumber())
                        .append(",")
                        .append(queryParam.getPageNumber() + queryParam.getPageSize());
            }
        }
        return where.toString();
    }


    /**
     * update tablename set
     * id = #{t_parameter.updateObject.id}
     *
     * @return
     */
    public String createUpdateaField(List<ResultMapping> resultMapping) {
        /**初始化update固定的字符串*/
        final StringBuilder updateField = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (SqlGeneratorUtil.isIdFiled(d)) {
                return;
            } else {
                if (updateField.length() != 0) {
                    updateField.append(",");
                }
                updateField.append(d.getColumn()).append("\t=\t")
                        .append("#{t_parameter.updateObject.")
                        .append(d.getProperty())
                        .append(",jdbcType=")
                        .append(d.getJdbcType())
                        .append("}");
            }
        });
        return updateField.toString();
    }


    /**
     * 创建查询selelct 的字段
     * selelct  id as id....
     *
     * @param cludes
     * @param type   0:正常 1：排除 2：包含
     * @return
     */
    public String createSelectField(String resultMapId, Set<String> cludes, int type, String tableAlias) {
        if (tableAlias != null) {
            tableAlias = tableAlias + ".";
        } else {
            tableAlias = "";
        }
        StringBuilder selectField = new StringBuilder();
        boolean flag = false;
        if (type == QueryParam.CONTAIN_INCLUDES) {
            for (String str : cludes) {
                if (flag) {
                    selectField.append("\t,\t");
                } else {
                    flag = true;
                }
                selectField
                        .append(tableAlias)
                        .append(str);
            }
        } else if (type == QueryParam.CONTAIN_EXCLUDES) {
            List<ResultMapping> resultMapping = SqlGenerator.RESULTMAPPINGS.get(resultMapId);
            for (ResultMapping resultMap : resultMapping) {
                if (!cludes.contains(resultMap.getColumn())) {
                    if (flag) {
                        selectField.append("\t,\t");
                    } else {
                        flag = true;
                    }
                    selectField
                            .append(tableAlias)
                            .append(resultMap.getColumn());
                }
            }
        }
        return selectField.toString();
    }

    /**
     * 创建insert字段组成部分
     * id,labelName,parentId,articleId"
     *
     * @param resultMapping
     * @return
     */
    public String createInsertField(List<ResultMapping> resultMapping) {
        StringBuilder insertField = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (insertField.length() != 0) {
                insertField.append(",");
            }
            insertField.append(d.getColumn());
        });
        return insertField.toString();
    }

    /**
     * 创建insert的body部分
     * insert into
     * "#{t_parameter.id},#{t_parameter.labelName},#{t_parameter.parentId},#{t_parameter.articleId}"
     *
     * @param resultMapping
     * @return
     */
    public String createInsertBody(List<ResultMapping> resultMapping) {
        StringBuilder insertContent = new StringBuilder();
        resultMapping.stream().forEach(d -> {
            if (insertContent.length() != 0) {
                insertContent.append("\t,\t");
            }
            insertContent.append("#{t_parameter.")
                    .append(d.getProperty())
                    .append("}");
        });
        return insertContent.toString();
    }

    private Set<String> getAllResultMapids(Configuration configuration) {
        Map<String, XNode> sqlFragments = configuration.getSqlFragments();
        Set<String> resultMapList = new HashSet<>();
        sqlFragments.entrySet().stream().forEach(entry -> {
            if (entry.getKey().endsWith(".config")) {
                XNode value = entry.getValue();
                List<XNode> children = value.getChildren();
                for (XNode xNode : children) {
                    String resultMapName = xNode.getStringAttribute("name");
                    if (resultMapName != null && RESULTMAP_NAME.endsWith(resultMapName)) {
                        resultMapList.add(SqlGeneratorUtil.checkAndConvertResultMapName(xNode.getStringAttribute("value")));
                    }
                }
            }
        });
        return resultMapList;
    }


    private void initResultMappings(Configuration configuration) {
        //get all resultId
        Set<String> allResultMapids = getAllResultMapids(configuration);

        //INIT RESULTMAPPINGS
        for (String resultMapId : allResultMapids) {
            /*获取resultMapId的list ResultMapping*/
            List<ResultMapping> resultMapping = configuration.getResultMap(resultMapId).getResultMappings().stream().filter(d -> {
                return StringUtils.isEmpty(d.getNestedQueryId());
            }).collect(Collectors.toList());
            RESULTMAPPINGS.put(resultMapId, resultMapping);
        }
    }

    private void initResultMapPks() {
        RESULTMAPPINGS.entrySet().forEach(k -> {
            k.getValue().stream().forEach(d -> {
                if (SqlGeneratorUtil.isIdFiled(d)) {
                    RESULTID_PK.put(k.getKey(), d);
                }
            });
        });
    }

    private void initColumnResultMapping() {
        RESULTMAPPINGS.entrySet().forEach(k -> {
            k.getValue().stream().forEach(d -> {
                /*初始化key（字段名-属性）*/
                COLUMN_RESULTMAPPING_MAPS.put(k.getKey(), k.getValue().stream().collect(Collectors.toMap(ResultMapping::getColumn, f -> f)));
            });
        });
    }

    private void initPropertyResultMapping() {
        RESULTMAPPINGS.entrySet().forEach(k -> {
            k.getValue().stream().forEach(d -> {
                /*初始化key（属性名-属性）*/
                PROPERTY_RESULTMAPPING_MAPS.put(k.getKey(), k.getValue().stream().collect(Collectors.toMap(ResultMapping::getProperty, f -> f)));
            });
        });
    }
}
