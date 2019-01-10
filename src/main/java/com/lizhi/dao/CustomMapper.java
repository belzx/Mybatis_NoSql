package com.lizhi.dao;

import com.lizhi.bean.CustomEntity;
import com.lizhi.orm.param.DeleteParam;
import com.lizhi.orm.param.QueryParam;
import com.lizhi.orm.param.UpdateParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomMapper<PO extends CustomEntity, PK> {
    int insert(@Param("t_parameter") PO t_parameter);

    int batchInsert(@Param("t_parameters") List<PO> t_parameters);

    int deleteByPK(@Param("id") PK id);

    PO selectByPK(@Param("id") PK id);

    int delete(@Param("t_parameter") DeleteParam t_parameter);

    int update(@Param("t_parameter") UpdateParam t_parameter);

    List<PO> query(@Param("t_parameter") QueryParam t_parameter);

    List<Map<String,Object>> queryByJoin(@Param("t_parameter") QueryParam t_parameter);

    int count(@Param("t_parameter") QueryParam t_parameter);

    List<PO> selectByPKS(@Param("t_list")List<PK> ids);
}
