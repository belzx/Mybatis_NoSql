package com.lizhi.service;

import com.lizhi.bean.CustomEntity;
import com.lizhi.bean.PagerResult;
import com.lizhi.dao.CustomMapper;
import com.lizhi.orm.param.DeleteParam;
import com.lizhi.orm.param.QueryParam;
import com.lizhi.orm.param.UpdateParam;
import com.lizhi.util.ClassExportValueUtil;
import com.lizhi.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;


public interface CustomService<PO extends CustomEntity, PK> {

    CustomMapper<PO, PK> getMapper();

    /**
     * 数据导出
     * @param params
     */
    default List<List<String>> export(QueryParam params){
        PagerResult<PO> poPagerResult = this.selectPager(params);
        List<PO> data = poPagerResult.getData();
        return ClassExportValueUtil.getReflectValues(data,ClassUtils.getSuperClassGenricType(this.getClass()));
    }

    /**
     * 分页查询
     * @param params
     */
    default PagerResult<PO> selectPager(QueryParam params){
        PagerResult<PO> result = new PagerResult<>();
        params.setPageNumber(params.getPageNumber() * params.getPageSize());

        int count = getMapper().count(params);
        if(count == 0){
            result.setData(new ArrayList<>());
            result.setTotal(0);
        }else {
            result.setTotal(count);
            result.setData(select(params));
        }
        return result;
    }

    /**
     * 不分页查询
     * @param params
     */
    default  List<PO> select(QueryParam params){
        return this.getMapper().query(params);
    }

    default  PO selectSingle(QueryParam params){
        PagerResult<PO> result = new PagerResult<>();
        params.setPageNumber(0);
        params.setPageSize(1);
        List<PO> select = this.query(params);
        if(select == null || select.isEmpty()){
            return  null;
        }else {
            return select.get(0);
        }
    }

    default int insert(PO entity) {
        return getMapper().insert((PO)entity);
    }

    default List<PO> batchInsert(List<PO> entityList) {
        return getMapper().batchInsert(entityList);
    }

    default int deleteByPK(Object id) {
        return getMapper().deleteByPK((PK)id);
    }

    default int delete(DeleteParam param) {
        return getMapper().delete(param);
    }

    default int update(PO entity) {
        UpdateParam updateParam =UpdateParam.build();
        updateParam.update(entity);
        if(entity.getId() == null){
            throw new IllegalArgumentException("主键参数id不能为空");
        }else{
            updateParam.where("id",entity.getId());
        }
        return getMapper().update(updateParam);
    }

    default int update(UpdateParam updateParam) {
        return getMapper().update(updateParam);
    }

    default List<PO> query(QueryParam param) {
        return getMapper().query(param);
    }

    default int count(QueryParam param) {
        return getMapper().count(param);
    }

    default PO selectByPK(PK id) {
        return  this.getMapper().selectByPK(id);
    }

    default void saveOrUpdate(PO entity) {
       if(selectByPK((PK)entity.getId()) == null){
          insert(entity);
       }else {
           update(entity);
       }
    }

}
