package com.lizhi.service;

import com.lizhi.bean.CustomEntity;
import com.lizhi.bean.PagerResult;
import com.lizhi.dao.CustomMapper;
import com.lizhi.bean.CURDParam;

import java.util.ArrayList;
import java.util.List;


public interface CustomService<Po extends CustomEntity, Pk> {

    CustomMapper<Po, Pk> getMapper();

    /**
     * 分页查询
     * @param params
     */
    default PagerResult<Po> selectPager(CURDParam params){
        final int pageNumber = params.getPageNumber();
        final int pageSize = params.getPageSize();
        PagerResult<Po> result = new PagerResult<>();
        params.limit(pageNumber*pageSize,pageSize);

        int count = getMapper().count(params);
        if(count == 0){
            result.setData(new ArrayList<>());
            result.setTotal(0);
        }else {
            result.setTotal(count);
            result.setData(getMapper().query(params));
        }
        return result;
    }

    /**
     * 不分页查询
     * @param params
     */
    default  PagerResult<Po> select(CURDParam params){
        PagerResult<Po> result = new PagerResult<>();
        result.setTotal(getMapper().count(params));
        result.setData(getMapper().query(params));
        return result;
    }

    default Po insert(Po entity) {
        return getMapper().insert((Po)entity);
    }

    default List<Po> batchInsert(List<Po> entityList) {
        return getMapper().batchInsert(entityList);
    }

    default int deleteByPk(Object id) {
        return getMapper().deleteByPk((Pk)id);
    }

    default int delete(CURDParam param) {
        return getMapper().delete(param);
    }

    default int update(Po entity) {
        CURDParam<CustomEntity> customEntityCURDParam = new CURDParam<>();
        customEntityCURDParam.update(entity);
        if(entity.getId() == null){
            throw new IllegalArgumentException("主键参数id不能为空");
        }else{
            customEntityCURDParam.where("id",entity.getId());
        }
        return getMapper().update(customEntityCURDParam);
    }

    default int update(CURDParam CURDParam) {
        return getMapper().update(CURDParam);
    }

    default List<Po> query(CURDParam param) {
        return getMapper().query(param);
    }

    default int count(CURDParam param) {
        return getMapper().count(param);
    }

    default Po selectByPk(Pk id) {
        return  this.getMapper().selectByPk(id);
    }

    default void saveOrUpdate(Po entity) {
       if(selectByPk((Pk)entity.getId()) == null){
          insert(entity);
       }else {
           update(entity);
       }
    }

}
