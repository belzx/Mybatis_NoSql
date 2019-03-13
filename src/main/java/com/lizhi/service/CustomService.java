package com.lizhi.service;

import com.lizhi.bean.Entity;
import com.lizhi.bean.EntityWithPrimary;
import com.lizhi.bean.PagerResult;
import com.lizhi.dao.CustomMapper;
import com.lizhi.orm.param.AbstractQueryParam;
import com.lizhi.orm.param.DeleteParam;
import com.lizhi.orm.param.UpdateParam;
import com.lizhi.util.CommonsUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(rollbackFor = Throwable.class)
public interface CustomService<PO extends Entity, PK> {

    CustomMapper<PO, PK> getMapper();

    /**
     * 分页查询
     */
    default PagerResult<PO> selectPager(AbstractQueryParam params) {
        PagerResult<PO> result = new PagerResult<>();
        int count = getMapper().count(params);
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
    @Transactional(readOnly = true)
    default List<PO> select(AbstractQueryParam params) {
        return this.getMapper().query(params);
    }

    @Transactional(readOnly = true)
    default List<Map<String, Object>> selectByJoin(AbstractQueryParam params) {
        return this.getMapper().queryByJoin(params);
    }

    @Transactional(readOnly = true)
    default int count(AbstractQueryParam param) {
        return getMapper().count(param);
    }

    @Transactional(readOnly = true)
    default PO selectSingle(AbstractQueryParam params) {
        List<PO> select = this.query(params);
        if (select == null || select.isEmpty()) {
            return null;
        } else {
            return select.get(0);
        }
    }

    @Transactional(readOnly = true)
    default PO selectByPK(PK id) {
        return this.getMapper().selectByPK(id);
    }

    @Transactional(readOnly = true)
    default List<PO> selectByPKS(List<PK> ids) {
        return this.getMapper().selectByPKS(ids);
    }

    @Transactional(readOnly = true)
    default List<PO> query(AbstractQueryParam param) {
        return getMapper().query(param);
    }

    default int insert(PO entity) {
        return getMapper().insert(entity);
    }

    default int insert(List<PO> entityList) {
        return getMapper().batchInsert(entityList);
    }

    default int delete(DeleteParam param) {
        return getMapper().delete(param);
    }

    default <PO extends EntityWithPrimary> int updateByPK(PO entity) {
        UpdateParam updateParam = UpdateParam.build();
        updateParam.set(entity);
        if (entity.getId() == null) {
            throw new IllegalArgumentException("主键参数id不能为空");
        } else {
            updateParam.where("id", entity.getId());
        }
        return getMapper().update(updateParam);
    }

    default int deleteByPK(Object id) {
        return getMapper().deleteByPK((PK) id);
    }

    default int update(UpdateParam updateParam) {
        return getMapper().update(updateParam);
    }

    default <PG extends EntityWithPrimary> int saveOrUpdate(PG entity) {
        if (selectByPK((PK) entity.getId()) == null) {
            return insert((PO) entity);
        } else {
            return updateByPK(entity);
        }
    }
}
