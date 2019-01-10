package com.lizhi.util;


public class CommonsUtils {
    /**
     *
     * @param pageNumber 第几页，从0开始
     * @param pageSize 每一页的数量
     * @param total 当前查询的总数
     * @return
     */
    public static int parseSkip(int pageNumber, int pageSize, int total) {
        int skip = 0;
        if (pageSize > 0) {
            if (pageNumber * pageSize >= total) {
                int tmp = total / pageSize;
                pageNumber = total % pageSize == 0 ? tmp - 1 : tmp;
            }
            skip = pageNumber * pageSize;
        }
        return skip;
    }
}
