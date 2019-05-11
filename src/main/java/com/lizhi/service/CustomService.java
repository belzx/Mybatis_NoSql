package com.lizhi.service;

import com.lizhi.bean.PagerResult;
import com.lizhi.dao.CustomMapper;
import com.lizhi.orm.param.*;
import com.lizhi.util.CommonsUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface CustomService<PO> {

    CustomMapper<PO> getMapper();

    /**
     * 分页查询
     */
    default PagerResult selectPager(QueryParam params) {
        PagerResult result = new PagerResult<>();
        int count = this.count(params);
        if (count == 0) {
            result.setData(new ArrayList<>());
            result.setTotal(0);
        } else {
            result.setTotal(count);
            params.setPageNumber(CommonsUtils.parseSkip(params.getPageNumber(), params.getPageSize(), count));
            result.setData(select(params));
        }
        return result;
    }

    /**
     * 不分页查询
     */
    default List select(QueryParam params) {
        return this.getMapper().select(params);
    }

    default List<Map<String, Object>> selectMap(QueryParam params) {
        return this.getMapper().selectMap(params);
    }

    default int count(QueryParam param) {
        return getMapper().count(param);
    }

    default PO selectSingle(QueryParam params) {
        List<PO> select = this.select(params);
        if (select == null || select.isEmpty()) {
            return null;
        } else {
            return select.get(0);
        }
    }

    default PO selectByPK(Object id) {
        List<PO> select = this.getMapper().select(QueryParam.build().whereByPk(id));
        if (!select.isEmpty()) {
            return select.get(0);
        }
        return null;
    }

    default List<PO> selectByPK(List ids) {
        return this.getMapper().select(QueryParam.build().whereByPk(ids));
    }

    @Transactional
    default int insert(PO entity) {
        return getMapper().insert(Arrays.asList(entity));
    }

    @Transactional
    default int insert(List<PO> entityList) {
        return getMapper().insert(entityList);
    }

    @Transactional
    default int delete(DeleteParam param) {
        return getMapper().delete(param);
    }

    @Transactional
    default  int updateByPK(PO entity) {
        return getMapper().update(UpdateParam.build().save(entity));
    }

    @Transactional
    default int deleteByPK(Object id) {
        return getMapper().delete(DeleteParam.build().whereByPk(id));
    }

    @Transactional
    default int update(UpdateSetParam updateParam) {
        return getMapper().update(updateParam);
    }
}
