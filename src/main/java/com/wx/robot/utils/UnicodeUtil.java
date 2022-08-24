package com.wx.robot.utils;

/**
 * @author wangxiang
 * @date 2022/8/19 10:31
 * @description
 */
public class UnicodeUtil {
    /**
     * 解码
     *
     * @param unicode
     * @return
     */
    public static String unicode2String(String unicode) {
        if (unicode == null || "".equals(unicode)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i;
        int pos = 0;
        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode, pos, i);
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }
        sb.append(unicode.substring(pos));
        return sb.toString();
    }

}
