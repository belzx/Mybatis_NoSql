package com.lizhi.dao;

import com.lizhi.orm.param.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomMapper<PO> {
    int insert(@Param("t_parameters") List<PO> t_parameters);

    int delete(@Param("t_parameter") DeleteParam t_parameter);

    int update(@Param("t_parameter") IUpdateParam t_parameter);

    List<PO> select(@Param("t_parameter") QueryParam t_parameter);

    int count(@Param("t_parameter") QueryParam t_parameter);

    List<Map<String,Object>> selectMap(@Param("t_parameter") QueryParam t_parameter);
}
