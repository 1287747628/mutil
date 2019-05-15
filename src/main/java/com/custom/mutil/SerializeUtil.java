package com.custom.mutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class SerializeUtil {

    private static final Logger logger = LoggerFactory.getLogger(SerializeUtil.class);
    //
    /**
     * 序列化
     *
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("serialize obj error:{},{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T unserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            // 反序列化
            bis = new ByteArrayInputStream(bytes);
            is = new ObjectInputStream(bis);
            return (T) is.readObject();
        } catch (Exception e) {
            logger.error("unserialize obj error:{},{}", e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("unserialize obj error:{},{}", e.getMessage(), e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error("unserialize obj error:{},{}", e.getMessage(), e);
                }
            }
        }
        return null;
    }

    /**
     * list反序列化
     *
     * @param bytes
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> unserializeList(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            is = new ObjectInputStream(bis);
            return (List<T>) is.readObject();
        } catch (Exception e) {
            logger.error("unserialize obj error:{},{}", e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("unserialize obj error:{},{}", e.getMessage(), e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error("unserialize obj error:{},{}", e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
