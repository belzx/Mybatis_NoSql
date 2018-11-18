package com.lizhi.util;

import java.util.*;

public class SimpleReadWriteMontitor extends AbstractReadWriteMonitorService<String> {
    @Override
    protected int analys(ReadWriteMonitorObject<String> rsubmitObject) {
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return 2;
    }

    public static void main(String[] args) {
        SimpleReadWriteMonitorObjectManagement simpleReadWriteMonitorObjectManagement = new SimpleReadWriteMonitorObjectManagement();
        SimpleReadWriteMontitor simpleReadWriteMontitor = new SimpleReadWriteMontitor();
        simpleReadWriteMonitorObjectManagement.set(simpleReadWriteMontitor);
        while (true) {
            List<ReadWriteMonitorObject<String>> objectObjectHashMap = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                ReadWriteMonitorObject rsubmitObject = new ReadWriteMonitorObject(UUID.randomUUID().toString(), 2, 1, 600, null);
                objectObjectHashMap.add(rsubmitObject);
            }
            simpleReadWriteMontitor.put(objectObjectHashMap);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
