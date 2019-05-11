package com.lizhi.orm;

import com.lizhi.orm.param.QueryParam;
import org.apache.ibatis.mapping.ResultMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lizhixiong
 * @time 2019 - 05 - 11 - 14:50
 */
public class SqlGeneratorMate {
    private static SqlGeneratorMate INSTANCE = new SqlGeneratorMate();

    private SqlGeneratorMate() {
    }

    protected static final Map<String, String> SELECT_FIELD = new HashMap<>();
    protected static final Map<String, String> UPDATE_FIELD = new HashMap<>();
    protected static final Map<String, String> INSERT_FIELD = new HashMap<>();
    protected static final Map<String, String> INSERT_BODY = new HashMap<>();

    public static SqlGeneratorMate instance() {
        return INSTANCE;
    }

    public void init(Map<String, List<ResultMapping>> map) {
        Set<Map.Entry<String, List<ResultMapping>>> entries = map.entrySet();
        for(Map.Entry<String, List<ResultMapping>> entry : entries){
            initSelectFiled(entry.getKey(),entry.getValue());
            initUpdateFiled(entry.getKey(),entry.getValue());
            initInsertFiled(entry.getKey(),entry.getValue());
            initInsertBody(entry.getKey(),entry.getValue());
        }
    }

    public void initSelectFiled(String resultMapId, List<ResultMapping> resultMapping) {
        SELECT_FIELD.put(resultMapId, SqlGenerator.instance().createSelectField(resultMapId, null, QueryParam.CONTAIN_NONE, null));
    }

    public void initUpdateFiled(String resultMapId, List<ResultMapping> resultMapping) {
        UPDATE_FIELD.put(resultMapId, SqlGenerator.instance().createUpdateaField(resultMapping));
    }

    public void initInsertFiled(String resultMapId, List<ResultMapping> resultMapping) {
        INSERT_FIELD.put(resultMapId, SqlGenerator.instance().createInsertField(resultMapping));
    }

    public void initInsertBody(String resultMapId, List<ResultMapping> resultMapping) {
        INSERT_BODY.put(resultMapId, SqlGenerator.instance().createInsertBody(resultMapping));
    }

    public String getSelectFiled(String resultMapId){
        return SELECT_FIELD.get(resultMapId);
    }
    public String getUpdateFiled(String resultMapId){
        return UPDATE_FIELD.get(resultMapId);
    }
    public String getInsertFiled(String resultMapId){
        return INSERT_FIELD.get(resultMapId);
    }
    public String getInsertBody(String resultMapId){
        return INSERT_BODY.get(resultMapId);
    }
}
