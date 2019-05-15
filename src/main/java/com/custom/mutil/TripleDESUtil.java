package com.custom.mutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TripleDESUtil {

    private static final Logger log = LoggerFactory.getLogger(TripleDESUtil.class);
    //
    private static final String ALGORITHM = "TripleDES";
    private static final String TRANSFORMATION = ALGORITHM + "/ECB/PKCS5Padding";
    private final static char[] HEXDICT = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private final static char PADDING = '0';
    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public static String byteArrayToHexString(byte[] byteArray) {
        String result = "";
        int len = byteArray.length;
        for (int i = 0; i < len; i++) {
            byte b = byteArray[i];
            result += HEXDICT[(b >>> 4) & 0x0F];
            result += HEXDICT[b & 0x0F];
        }
        return result;
    }

    /**
     * 3DES加密算法
     * */
    public static String encrypt(String text, String key) {
        Cipher cipher;
        try {
            int lg = key.length();
            if (lg > 24) {
                key = key.substring(0, 24);
            } else if (lg < 24) {
                StringBuffer encryptKey = new StringBuffer(key);

                for (int i = encryptKey.length(); i < 24; i++) {
                    encryptKey.append(PADDING);
                }
                key = encryptKey.toString();
            }
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), ALGORITHM));
            return byteArrayToHexString(cipher.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 3DES解密算法
     * */
    public static String decrypt(String text, String key) {
        // 密钥长度不足24 字节时，右补 ASCII 字符“0”
        key = key == null ? "" : key;
        int lg = key.length();
        if (lg > 24) {
            key = key.substring(0, 24);
        } else if (lg < 24) {
            StringBuffer encryptKey = new StringBuffer(key);

            for (int i = encryptKey.length(); i < 24; i++) {
                encryptKey.append(PADDING);
            }
            key = encryptKey.toString();
        }
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), ALGORITHM));
            return new String(cipher.doFinal(hexStringToByteArray(text)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptThreeDESECB2(String src, String key) throws Exception {
        // --通过base64,将字符串转成byte数组
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytesrc = decoder.decodeBuffer(src);
        // --解密的key
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);
        // --Chipher对象解密
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    public static String decryptThreeDESECB(String src, String password) {
        try {
            StringBuffer encryptKey = new StringBuffer(password == null ? "" : password);
            for (int i = encryptKey.length(); i < 24; i++) {
                encryptKey.append("0");
            }
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytesrc = decoder.decodeBuffer(src);
            SecureRandom sr = new SecureRandom();
            DESedeKeySpec dks = new DESedeKeySpec(encryptKey.toString().getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String str) {
        byte[] byteArray = new byte[str.length() / 2];
        int len = byteArray.length;
        int j = 0;
        for (int i = 0; i < len; i++) {
            j = (i << 1);
            byteArray[i] = 0;
            char c = str.charAt(j);
            if ('0' <= c && c <= '9') {
                byteArray[i] |= ((c - '0') << 4);
            } else if ('A' <= c && c <= 'F') {
                byteArray[i] |= ((c - 'A' + 10) << 4);
            } else if ('a' <= c && c <= 'f') {
                byteArray[i] |= ((c - 'a' + 10) << 4);
            } else {

            }
            j++;
            c = str.charAt(j);
            if ('0' <= c && c <= '9') {
                byteArray[i] |= (c - '0');
            } else if ('A' <= c && c <= 'F') {
                byteArray[i] |= (c - 'A' + 10);
            } else if ('a' <= c && c <= 'f') {
                byteArray[i] |= (c - 'a' + 10);
            } else {

            }
        }
        return byteArray;
    }

    public static String decryptThreeDESECB1(String src, String password) {
        try {
            StringBuffer encryptKey = new StringBuffer(password == null ? "" : password);
            for (int i = encryptKey.length(); i < 24; i++) {
                encryptKey.append("0");
            }
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytesrc = decoder.decodeBuffer(src);
            SecureRandom sr = new SecureRandom();
            DESedeKeySpec dks = new DESedeKeySpec(encryptKey.toString().getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte);
        } catch (Exception e) {
            // log.error("decrypt failed - " + e.toString());
        }
        return null;
    }

    public static String ecrypt(String data, String key) {
        //
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            return Base64Util.encode(rawHmac);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static String ecryptThreeDESECB(String data, String key) {
        //
        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            byte[] b = cipher.doFinal(data.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static void main(String[] args) {
        String account = "account_query_accountList";
        String password = "123456";
        StringBuffer pwd = new StringBuffer();
        pwd.append("1bfec4eaa85607cbe53a4b349fee57c8$").append("172.16.16.16");
        String ecrypt = TripleDESUtil.encrypt(pwd.toString(), account);
        System.out.println(ecrypt);
        String decrypt = TripleDESUtil
                .decrypt(
                        "CE73CFDDB785D5E42E8B5CB630B51EB11FDCEA0CB4D4359704241F04C4DBE4FE6E51E7FF5CEEED2C55FCC561CC52B0FAC2631BBC4B88CFDF7855A43713E192C54952CDE17B90BD2FCB10040668B3F3131F9E8E6E2DFC0880EB483568540DE506A99A5DDAC7DDD44C6DB123CA2DD8AC3D41DC0367A671F56E0E29A5DD3D0B75B9D2D0D886AC504C825006A0A2BE2DE60D7F1FDB06083181B0",
                        account);
        System.out.println(decrypt);
    }
}
