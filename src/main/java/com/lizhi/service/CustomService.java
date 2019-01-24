package com.lizhi.service;

import com.lizhi.bean.CustomEntity;
import com.lizhi.bean.PagerResult;
import com.lizhi.dao.CustomMapper;
import com.lizhi.orm.param.DeleteParam;
import com.lizhi.orm.param.QueryParam;
import com.lizhi.orm.param.UpdateParam;
import com.lizhi.util.CommonsUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(rollbackFor = Throwable.class)
public interface CustomService<PO extends CustomEntity, PK> {

    CustomMapper<PO, PK> getMapper();

    /**
     * 分页查询
     *
     * @param params
     */
    default PagerResult<PO> selectPager(QueryParam params) {
        PagerResult<PO> result = new PagerResult<>();
        int count = getMapper().count((QueryParam) params);
        if (count == 0) {
            result.setData(new ArrayList<>());
            result.setTotal(0);
        } else {
            result.setTotal(count);
            params.setPageNumber(CommonsUtils.parseSkip(params.getPageNumber(),params.getPageSize(),count));
            result.setData(select(params));
        }
        return result;
    }

    /**
     * 不分页查询
     * @param params
     */
    @Transactional(readOnly = true)
    default List<PO> select(QueryParam params) {
        return this.getMapper().query((QueryParam) params);
    }

    @Transactional(readOnly = true)
    default List<Map<String, Object>> selectByJoin(QueryParam params) {
        return this.getMapper().queryByJoin((QueryParam) params);
    }

    @Transactional(readOnly = true)
    default int count(QueryParam param) {
        return getMapper().count((QueryParam) param);
    }

    @Transactional(readOnly = true)
    default PO selectSingle(QueryParam params) {
        PagerResult<PO> result = new PagerResult<>();
        params.setPageNumber(0);
        params.setPageSize(1);
        List<PO> select = this.query((QueryParam) params);
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
    default List<PO> query(QueryParam param) {
        return getMapper().query((QueryParam) param);
    }

    default int insert(PO entity) {
        return getMapper().insert((PO) entity);
    }

    default int insert(List<PO> entityList) {
        return getMapper().batchInsert(entityList);
    }

    default int delete(DeleteParam param) {
        return getMapper().delete((DeleteParam) param);
    }

    default int update(PO entity) {
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
        return getMapper().update((UpdateParam) updateParam);
    }

    default int saveOrUpdate(PO entity) {
        if (selectByPK((PK) entity.getId()) == null) {
           return insert(entity);
        } else {
           return update(entity);
        }
    }
}
