package com.lizhi.controller;

import com.lizhi.bean.CURDParam;
import com.lizhi.bean.CustomEntity;
import com.lizhi.bean.PagerResult;
import com.lizhi.bean.ResponseMessage;
import com.lizhi.service.CustomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static com.lizhi.bean.ResponseMessage.ok;


public interface CustomController <M extends CustomEntity,PK > {

    CustomService<M,PK> getService();

    @ResponseStatus(HttpStatus.CREATED)
    default ResponseMessage add(@RequestBody M data) {
        return ok(getService().insert(data));
    }

    @DeleteMapping(path = "/{id:.+}")
    default ResponseMessage deleteByPrimaryKey(@PathVariable PK id) {
        return ok(getService().deleteByPk(id));
    }

    @GetMapping
    default ResponseMessage<PagerResult<M>> list(CURDParam param) {
        return ok(getService().selectPager(param));
    }

    @GetMapping("/count")
    default ResponseMessage<Integer> count(CURDParam param) {
        return ok(Integer.valueOf(getService().count(param)));
    }

    @GetMapping(path = "/{id}")
    default ResponseMessage<M> getByPrimaryKey(@PathVariable PK id) {
        return ok(assertNotNull(getService().selectByPk(id)));
    }

//    @GetMapping(path = "/ids")
//    default ResponseMessage<List<M>> getByPrimaryKey(@RequestParam List<PK> ids) {
//        return ok(assertNotNull(getService().selectByPk(ids)));
//    }

    static <T> T assertNotNull(T obj) {
        if (null == obj) {
            throw new RuntimeException("{data_not_exist}");
        }
        return obj;
    }

//    @PutMapping(path = "/{id}")
//    default ResponseMessage<Integer> updateByPrimaryKey(@PathVariable PK id, @RequestBody M data) {
//        E entity = getService().createEntity();
//        return ResponseMessage.ok(getService().updateByPk(id, modelToEntity(data, entity)));
//    }
//
//    @PatchMapping
//    default ResponseMessage<PK> saveOrUpdate(@RequestBody M data) {
//        E entity = getService().createEntity();
//        return ResponseMessage.ok(getService().saveOrUpdate(modelToEntity(data, entity)));
//    }

}
