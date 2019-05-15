package com.custom.mutil;

public class StringUtil {

    public static final boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }

    public static final boolean isNotEmpty(final String s) {
        return !isEmpty(s);
    }

    /**
     * 中文字符串转Unicode：theString-字符串，escapeSpace-是否省略空格
     */
    public static String toUnicode(String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);
        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if (aChar > 61 && aChar < 127) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if (aChar < 0x0020 || aChar > 0x007e) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex(aChar >> 12 & 0xF));
                        outBuffer.append(toHex(aChar >> 8 & 0xF));
                        outBuffer.append(toHex(aChar >> 4 & 0xF));
                        outBuffer.append(toHex(aChar & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static char toHex(int nibble) {
        return hexDigit[nibble & 0xF];
    }

    /**
     * Unicode编码转中文字符串：in-str.toCharArray()
     */
    public static String unicodeTo(char[] in) {
        int off = 0;
        char c;
        char[] out = new char[in.length];
        int outLen = 0;
        try {
            while (off < in.length) {
                c = in[off++];
                if (c == '\\') {
                    if (in.length > off) { // 是否有下一个字符
                        c = in[off++]; // 取出下一个字符
                    } else {
                        out[outLen++] = '\\'; // 末字符为'\'，返回
                        break;
                    }
                    if (c == 'u') { // 如果是"\\u"
                        int value = 0;
                        if (in.length > off + 4) { // 判断"\\u"后边是否有四个字符
                            boolean isUnicode = true;
                            for (int i = 0; i < 4; i++) { // 遍历四个字符
                                c = in[off++];
                                switch (c) {
                                    case '0':
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7':
                                    case '8':
                                    case '9':
                                        value = (value << 4) + c - '0';
                                        break;
                                    case 'a':
                                    case 'b':
                                    case 'c':
                                    case 'd':
                                    case 'e':
                                    case 'f':
                                        value = (value << 4) + 10 + c - 'a';
                                        break;
                                    case 'A':
                                    case 'B':
                                    case 'C':
                                    case 'D':
                                    case 'E':
                                    case 'F':
                                        value = (value << 4) + 10 + c - 'A';
                                        break;
                                    default:
                                        isUnicode = false; // 判断是否为unicode码
                                }
                            }
                            if (isUnicode) { // 是unicode码转换为字符
                                out[outLen++] = (char) value;
                            } else { // 不是unicode码把"\\uXXXX"填入返回值
                                off = off - 4;
                                out[outLen++] = '\\';
                                out[outLen++] = 'u';
                                out[outLen++] = in[off++];
                            }
                        } else { // 不够四个字符则把"\\u"放入返回结果并继续
                            out[outLen++] = '\\';
                            out[outLen++] = 'u';
                            continue;
                        }
                    } else {
                        switch (c) { // 判断"\\"后边是否接特殊字符，回车，tab一类的
                            case 't':
                                c = '\t';
                                out[outLen++] = c;
                                break;
                            case 'r':
                                c = '\r';
                                out[outLen++] = c;
                                break;
                            case 'n':
                                c = '\n';
                                out[outLen++] = c;
                                break;
                            case 'f':
                                c = '\f';
                                out[outLen++] = c;
                                break;
                            default:
                                out[outLen++] = '\\';
                                out[outLen++] = c;
                                break;
                        }
                    }
                } else {
                    out[outLen++] = c;
                }
            }
        } catch (Exception e) {
            return String.valueOf(in);
        }
        return new String(out, 0, outLen);
    }

}
