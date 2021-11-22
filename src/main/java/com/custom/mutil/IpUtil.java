package com.custom.mutil;

import java.math.BigInteger;

/**
 * @author jocken
 * @date 2021/11/22
 */
public class IpUtil {

    private static final byte[] EMPTY_BYTES = new byte[0];
    // IPV6地址的分段
    private static final int IPV6Length = 8;
    // 一个IPV6分段占的长
    private static final int IPV6ParmLength = 4;

    /**
     * IPV6转化为十六进制串
     *
     * @param ipAddress
     * @return
     */
    private static String buildKey(String ipAddress) {
        String key = "";
        // ipv6标识 。判断是否是ipv6地址
        int colonFlag = ipAddress.indexOf(":");
        // ipv6标识 。判断是否是简写的ipv6地址
        int dColonFlag = ipAddress.indexOf("::");
        // 将v6或v4的分隔符用&代替
        ipAddress = ipAddress.replace(":", "&");
        //
        // ipv6 address,将ipv6地址转换成16进制,全8组
        if (dColonFlag == -1 && colonFlag != -1) {
            String[] arrParams = ipAddress.split("&");
            // 将v6地址转成十六进制
            for (int i = 0; i < IPV6Length; i++) {
                // 将ipv6地址中每组不足4位的补0
                for (int k = 0; k < (IPV6ParmLength - arrParams[i].length()); k++) {
                    key += "0";
                }
                key += arrParams[i];
            }
        }
        // ipv6 address,将ipv6地址转换成16进制,含简写
        if (dColonFlag != -1) {
            String[] arr = ipAddress.split("&");
            String[] arrParams = new String[IPV6Length];
            int indexFlag = 0;
            if ("".equals(arr[0])) {
                for (int j = 0; j < (IPV6Length - (arr.length - 2)); j++) {
                    arrParams[j] = "0000";
                    indexFlag++;
                }
                for (int i = 2; i < arr.length; i++) {
                    arrParams[indexFlag] = arr[i];
                    i++;
                    indexFlag++;
                }
            } else {
                for (int i = 0; i < arr.length; i++) {
                    if ("".equals(arr[i])) {
                        for (int j = 0; j < (IPV6Length - arr.length + 1); j++) {
                            arrParams[indexFlag] = "0000";
                            indexFlag++;
                        }
                    } else {
                        arrParams[indexFlag] = arr[i];
                        indexFlag++;
                    }
                }
            }
            for (int i = 0; i < IPV6Length; i++) {
                for (int k = 0; k < (IPV6ParmLength - arrParams[i].length()); k++) {
                    key += "0";
                }
                key += arrParams[i];
            }
        }
        return key;
    }

    /**
     * 十六进制串转化为IP地址
     *
     * @param key
     * @return
     */
    private static String splitKey(String key) {
        String ipv6Address = "";
        String ipAddress = "";
        String strKey = "";
        String ip1 = key.substring(0, 24);
        String tIP1 = ip1.replace("0000", "").trim();
        if (!"".equals(tIP1) && !"FFFF".equals(tIP1)) {
            // 将ip按：分隔
            while (!"".equals(key)) {
                strKey = key.substring(0, 4);
                key = key.substring(4);
                if ("".equals(ipv6Address)) {
                    ipv6Address = strKey;
                } else {
                    ipv6Address += ":" + strKey;
                }
            }
            ipAddress = ipv6Address;
        }
        return ipAddress;
    }

    /**
     * 将ipv6转成字节数组
     *
     * @param ip 2001:2:0:aab1:8951:612b:5d76:4d9e
     * @return byte[]
     */
    public static byte[] ipv6ToByte(String ip) {
        // 将ip地址转换成16进制
        String key = buildKey(ip);
        // 将16进制转换成ip地址
        String ip6 = splitKey(key);
        // 将v6f地址存以":"分隔存放到数组中
        String[] ip6Str = ip6.split(":");
        if (ip6Str.length != 8) {
            return EMPTY_BYTES;
        }
        String[] ipStr = new String[16];
        byte[] ip6Byte = new byte[16];
        // 将数组中的每两位取存到长度为16的字符串数组中
        for (int j = 0, i = 0; i < ip6Str.length; j = j + 2, i++) {
            ipStr[j] = ip6Str[i].substring(0, 2);
            ipStr[j + 1] = ip6Str[i].substring(2, 4);
        }
        // 将ipStr中的十六进制数转成十进制，再转成byte类型存放到16个字的数组中
        for (int i = 0; i < ip6Byte.length; i++) {
            ip6Byte[i] = (byte) Integer.parseInt(ipStr[i], 16);
        }
        return ip6Byte;
    }

    /**
     * 左补齐0
     *
     * @param str
     * @param len
     * @return
     */
    private static String leftAppend(String str, int len) {
        if (str != null && str.length() < len) {
            StringBuilder sb = new StringBuilder();
            int need = len - str.length();
            for (int i = 0; i < need; i++) {
                sb.append("0");
            }
            return sb.append(str).toString();
        }
        return str;
    }

    /**
     * 验证IPv4是否属于某个IP段
     *
     * @param ipSection IP段（以'-'分隔）
     * @param ip        所验证的IP号码
     */
    public static boolean ipExistsInRange(String ip, String ipSection) {
        ipSection = ipSection.trim();
        ip = ip.trim();
        int idx = ipSection.indexOf('-');
        String beginIP = ipSection.substring(0, idx);
        String endIP = ipSection.substring(idx + 1);
        return getIp2long(beginIP) <= getIp2long(ip) && getIp2long(ip) <= getIp2long(endIP);
    }

    public static boolean ipv6ExistsInRange(String ip, String ipSegment) {
        if (StringUtil.isEmpty(ip) || StringUtil.isEmpty(ipSegment)) {
            return false;
        }
        int idx = ipSegment.indexOf("-");
        String s = ipSegment.substring(0, idx);
        String e = ipSegment.substring(idx + 1);
        BigInteger ipBig = new BigInteger(ipv6ToByte(ip));
        BigInteger start = new BigInteger(ipv6ToByte(s));
        BigInteger end = new BigInteger(ipv6ToByte(e));
        //
        if ((ipBig.compareTo(start) > -1) && (ipBig.compareTo(end) < 1)) {
            return true;
        }
        return false;
    }

    /**
     * ipv4转long
     *
     * @param ip ip
     * @return long
     */
    public static long getIp2long(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip2long = 0L;
        for (int i = 0; i < 4; ++i) {
            ip2long = ip2long << 8 | Integer.parseInt(ips[i]);
        }
        return ip2long;
    }

    /**
     * 当前ipv4+1
     *
     * @param ip current Ip
     * @return nextIp
     */
    public static String nextIp(String ip) {
        String str[] = ip.split("\\.");

        Integer value = Integer.valueOf(str[str.length - 1]);
        String subStr = null;
        if (str.length > 1) {
            subStr = ip.substring(0, ip.lastIndexOf("."));
        } else {
            return ++value + "";
        }

        if (value >= 253 && str.length == 4) {
            return nextIp(subStr) + ".1";
        } else if (value == 255) {
            return nextIp(subStr) + ".0";
        } else {
            return subStr + "." + (++value);
        }
    }

}
