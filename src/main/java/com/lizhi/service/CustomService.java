package com.lizhi.service;

import com.lizhi.bean.CustomEntity;
import com.lizhi.bean.PagerResult;
import com.lizhi.dao.CustomMapper;
import com.lizhi.bean.CURDParam;
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
    default List<List<String>> export(CURDParam params){
        PagerResult<PO> poPagerResult = this.selectPager(params);
        List<PO> data = poPagerResult.getData();
        return ClassExportValueUtil.getReflectValues(data,ClassUtils.getSuperClassGenricType(this.getClass()));
    }

    /**
     * 分页查询
     * @param params
     */
    default PagerResult<PO> selectPager(CURDParam params){
        PagerResult<PO> result = new PagerResult<>();
        params.limit(params.getPageNumber() * params.getPageSize(),params.getPageSize());

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
    default  PagerResult<PO> select(CURDParam params){
        PagerResult<PO> result = new PagerResult<>();
        result.setTotal(getMapper().count(params));
        result.setData(getMapper().query(params));
        return result;
    }

    default  PO selectSingle(CURDParam params){
        PagerResult<PO> result = new PagerResult<>();
        List<PO> select = this.query(params.limit(0, 1));
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

    default int delete(CURDParam param) {
        return getMapper().delete(param);
    }

    default int update(PO entity) {
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

    default List<PO> query(CURDParam param) {
        return getMapper().query(param);
    }

    default int count(CURDParam param) {
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
