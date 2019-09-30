package com.prj.sdk.algo;

import android.text.TextUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Algorithm3DES {

    private static final Logger logger = Logger.getLogger(Algorithm3DES.class.getSimpleName());

    private static String ALGORITHM = "DESede"; //采用3des算法
    private static int num = 168;//24字节
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 加密
     *
     * @param data
     */
    public static void encryptMode(AlgorithmData data) {
        if (null == data) {
            return;
        }

        try {
            String keyStr = data.getKey();
            SecretKey secretKey;
            if (TextUtils.isEmpty(keyStr)) {
                KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
                kg.init(num);
                secretKey = kg.generateKey();
                data.setKey(byte2Hex(secretKey.getEncoded()));
            } else {
                secretKey = new SecretKeySpec(hex2Byte(keyStr), ALGORITHM);
            }
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encoded = c1.doFinal(data.getDataMing().getBytes());
            data.setDataMi(byte2Hex(encoded));
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.log(Level.ALL, "不存在对应的算法实现", e);
        } catch (javax.crypto.NoSuchPaddingException e) {
            e.printStackTrace();
            logger.log(Level.ALL, "对应的填充机制未提供", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ALL, "3DES加密产生异常", e);
        }
    }

    public static void decryptMode(AlgorithmData data) {
        if (null == data) {
            return;
        }

        try {
            byte[] key = hex2Byte(data.getKey());
            byte[] src = hex2Byte(data.getDataMi());
            SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] res = c1.doFinal(src);
            String ming = new String(res, "utf-8");
            data.setDataMing(ming);
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.log(Level.ALL, "不存在对应的算法实现", e);
        } catch (javax.crypto.NoSuchPaddingException e) {
            e.printStackTrace();
            logger.log(Level.ALL, "对应的填充机制未提供", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ALL, "3DES加密产生异常", e);
        }
    }

    /**
     * 把byte数组转换成十六进制的字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return buf.toString();
    }

    /**
     * 把十六进制字符串转成byte数组
     *
     * @param str
     * @return
     */
    private static byte[] hex2Byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
