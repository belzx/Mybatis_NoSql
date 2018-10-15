package com.lizhi.dao;

import com.lizhi.bean.CURDParam;
import com.lizhi.bean.CustomEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomMapper<Po extends CustomEntity, Pk> {
    //param名称必须与参数名一致，若不一致，ognl调用方法时会抛错

    int insert(@Param("t_parameter") Po t_parameter);

    List<Po> batchInsert(@Param("t_parameters") List<Po> t_parameters);

    int deleteByPk(@Param("id") Pk id);

    Po selectByPk(@Param("id") Pk id);

    int delete(@Param("t_parameter") CURDParam t_parameter);

    int update(@Param("t_parameter") CURDParam t_parameter);

    List<Po> query(@Param("t_parameter") CURDParam t_parameter);

    int count(@Param("t_parameter") CURDParam t_parameter);

}
