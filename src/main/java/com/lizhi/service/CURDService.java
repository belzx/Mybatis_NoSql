//package com.lizhi.service;
//
//import com.lizhi.bean.CustomEntity;
//import com.lizhi.bean.CURDParam;
//
//import java.util.List;
//
//public interface CURDService <Po extends CustomEntity,PK > {
//
//    Po insert( Po po);
//
//    int deleteByPK(PK id);
//
//    int delete(CURDParam param);
//
//    int update(CURDParam param);
//
//    Po selectByPK(PK id);
//
//    List<Po> selectByPK(List<PK> ids);
//
//    List<Po> query(CURDParam param);
//
//    int count(CURDParam param);
//}
