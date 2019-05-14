package com.custom.mutil;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作对象工具类
 */
public class ObjectUtil {

    /**
     * 通过JSON方式深克隆，速度较快
     *
     * @param source
     * @param sourceCls
     * @param <T>
     * @return
     */
    public static <T> T deepCopyByJson(T source, Class<T> sourceCls) {
        if (null == sourceCls || null == source) {
            return null;
        }
        String srcStr = JSON.toJSONString(source);
        T target = JSON.parseObject(srcStr, sourceCls);
        return target;
    }

    /**
     * 通过序列化方式深克隆
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deepCoypBySerializable(T source) {
        if (null == source) {
            return null;
        }
        return SerializationUtils.clone(source);
    }

    /**
     * 浅克隆
     * @param source
     * @param targetCls
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T getTargetBean(T source, Class<T> targetCls) throws Exception {
        if (null == source || null == targetCls) {
            return null;
        }
        T targetBean = targetCls.newInstance();
        Class<?> clazz = source.getClass();
        //
        //原对象的所有get方法
        Map<String, Method> sourceMethodMap = new HashMap<>();
        Method[] sourceMethods = clazz.getMethods();
        for (Method method : sourceMethods) {
            String methodName = method.getName();
            if (!methodName.startsWith("get")) {
                continue;
            }
            sourceMethodMap.put(methodName, method);
        }
        //
        Method[] methods = targetCls.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (!methodName.startsWith("set")) {
                continue;
            }
            String getMethodName = "get" + methodName.substring(3);
            Method getMethod = sourceMethodMap.get(getMethodName);
            if (null == getMethod) {
                continue;
            }
            Object value = getMethod.invoke(source);
            if (null == value) {
                continue;
            }
            // 目标参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> parameterType = parameterTypes[0];
            if (parameterTypes[0].equals(value.getClass())) {
                // 对目标对象调用set方法装入属性值
                method.invoke(targetBean, new Object[]{value});
            } else {
                if (parameterType.equals(java.util.Date.class) && value.getClass().equals(java.sql.Timestamp.class)) {
                    java.util.Date temp = (java.util.Date) value;
                    method.invoke(targetBean, new Object[]{temp});
                }
            }
        }
        return targetBean;
    }

}
