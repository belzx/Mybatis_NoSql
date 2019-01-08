package com.lizhi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CloneUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(CloneUtils.class);

    /**
     * 流复制（深复制，耗时）
     * 需要保证里面所有的对象都能序列化，否则会抛出序列化失败的错误
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T clone(T obj) throws NotSerializableException{
        T cloneObj = null;
        if (obj == null) {
            return cloneObj;
        }

        //写入字节流
        ByteArrayOutputStream out = null;
        ObjectOutputStream obs = null;
        ByteArrayInputStream ios = null;
        ObjectInputStream ois = null;
        try {
            //写入字节流
            out = new ByteArrayOutputStream();
            obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            //分配内存，写入原始对象，生成新对象
            ios = new ByteArrayInputStream(out.toByteArray());
            ois = new ObjectInputStream(ios);
            //返回生成的新对象
            cloneObj = (T) ois.readObject();
        } catch (NotSerializableException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            StreamUtils.close(ois, ios, obs, out);
        }
        return cloneObj;
    }

}