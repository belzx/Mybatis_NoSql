//package com.lizhi.builder;
//
//public enum SqlEnum {
//
//    INSERT(0x1,""),
//    SELECT(0x2,"SELECT %s FROM %s "),
//    UPDATE(0x4,"UPDATE %s SET %s"),
//    DELETE(0x8,"DELETE FROM %s"),
//    ORDER(0x10,"ORDER BY"),
//    BATCH(0x20,"BATCH"),
//    GROUP(0x40,"GROUP BY");
//
//    public int type;
//
//    public String typeName;
//
//    SqlEnum(int type, String typeName) {
//        this.type = type;
//        this.typeName = typeName;
//    }
//
//    public static String getTypeName(int type){
//        for(SqlEnum s : SqlEnum.values()){
//            if(s.type == type){
//                return s.typeName;
//            }
//        }
//        return  null;
//    }
//
//    public static int getType(String typeName){
//        for(SqlEnum s : SqlEnum.values()){
//            if(s.typeName.equals(typeName)){
//                return s.type;
//            }
//        }
//        return  -1;
//    }
//}