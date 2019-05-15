package com.custom.mutil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlUtil {

    /**
     * 将传入xml文本转换成Java对象
     *
     * @param xmlStr
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String xmlStr, Class<T> cls) {
        if (StringUtil.isNotEmpty(xmlStr)) {
            xmlStr = xmlStr.trim();
        }
        //
        XStream xstream = new XStream(new DomDriver());
        xstream.ignoreUnknownElements();
        xstream.processAnnotations(cls);
        T obj = (T) xstream.fromXML(xmlStr);
        return obj;
    }
}
