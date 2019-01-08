package com.lizhi.dao;

import com.lizhi.bean.CustomEntity;
import com.lizhi.orm.param.DeleteParam;
import com.lizhi.orm.param.QueryParam;
import com.lizhi.orm.param.UpdateParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomMapper<PO extends CustomEntity, PK> {
    //param名称必须与参数名一致，若不一致，ognl调用方法时会抛错

    int insert(@Param("t_parameter") PO t_parameter);

    List<PO> batchInsert(@Param("t_parameters") List<PO> t_parameters);

    int deleteByPK(@Param("id") PK id);

    PO selectByPK(@Param("id") PK id);

    int delete(@Param("t_parameter") DeleteParam t_parameter);

    int update(@Param("t_parameter") UpdateParam t_parameter);

    List<PO> query(@Param("t_parameter") QueryParam t_parameter);

    int count(@Param("t_parameter") QueryParam t_parameter);
}
