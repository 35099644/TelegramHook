package com.telegram.hook.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utils {

    private static String bytesToHexString(byte[] bytes) {
        if(bytes == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] encodeMD5(String strSrc) {
        byte[] returnByte = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            returnByte = md5.digest(strSrc.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnByte;
    }

    public static String encodeMD5ToString(String src) {
        if(TextUtils.isEmpty(src))
            return src;
        String s = bytesToHexString(encodeMD5(src));
        LogUtil.d("encodeMD5ToString", "src = " + src, "\n result = " + s);
        return s;
    }

    public static String encodeMD5Params(Object... params) {
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
        }
        String src = sb.toString();
        String s = encodeMD5ToString(src);
        LogUtil.d("encodeMD5Params, src = " + src, "s = " + s);
        return s;
    }


    /** 格式化时间 **/
    private static final String DATE_TIME_PATTERN = "MM-dd HH:mm:ss";
    private static String formatDate(long time) {
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date date = new Date(time);
        return bartDateFormat.format(date);
    }

    public static String getTime() {
        return "[" + formatDate(System.currentTimeMillis()) + "] ";
    }


    public static String getImei(Context context) {
        if(context == null) return UUID.randomUUID().toString();
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            boolean hasPermission = Build.VERSION.SDK_INT < 23 || permissionCheck == PackageManager.PERMISSION_GRANTED;
            if (!hasPermission) {
                return UUID.randomUUID().toString();
            }
            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyMgr != null) {
                return telephonyMgr.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UUID.randomUUID().toString();
    }

    /**
     * 为json对象注入一个参数
     * @param originJson 原json字符串
     * @param key   注入的key
     * @param value 注入的value
     * @return  注入后的json字符串
     */
    public static String injectParams(String originJson, String key, String value) {
        if(originJson == null || !originJson.startsWith("{"))
            return originJson;
        String kv = "\"" + key + "\":" + value + ",";
        return "{" + kv + originJson.substring(1);
    }

}
