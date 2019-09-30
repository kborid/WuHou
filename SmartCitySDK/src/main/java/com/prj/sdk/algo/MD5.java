package com.prj.sdk.algo;

import java.security.MessageDigest;

import com.prj.sdk.util.DateUtil;

/**
 * 　MD5即Message-Digest Algorithm 5（信息-摘要算法 5），用于确保信息传输完整一致。是计算机广泛使用的杂凑算法之一（又译摘要算法、哈希算法），主流编程语言普遍已有MD5实现。
 *
 * @author LiaoBo
 */
public class MD5 {

    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 获取数据的MD5的编码
     *
     * @param data
     * @return 16/32位的二进制数据
     */
    public static String getMD5(byte[] data, boolean isLen32) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(data);

            byte b[] = messageDigest.digest();

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                int i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            if (isLen32) {
                return buf.toString();
            } else {
                return buf.toString().substring(8, 24);
            }
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 返回32位md5码
     *
     * @param data
     * @return
     */
    public static String getMD5(byte[] data) {
        return getMD5(data, true);
    }

    public static String getMD5(String data) {
        try {
            return getMD5(data.getBytes(CHARSET_NAME), true);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "";
    }

    /**
     * 时间戳混合加密 :登录名+密码+当天日期
     *
     * @param name
     * @param psw
     * @return
     */
    public static String getMD5(String name, String psw) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(psw).append(DateUtil.getCurDateStr("yyyyMMdd"));
            return getMD5(sb.toString().getBytes(CHARSET_NAME), true);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "";
    }
}
