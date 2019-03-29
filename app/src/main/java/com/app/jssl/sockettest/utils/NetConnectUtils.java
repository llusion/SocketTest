package com.app.jssl.sockettest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Author: ls
 * Time:   2018/6/19 17:13
 * Desc:   This is NetConnectUtils：get net connecting info
 */
public class NetConnectUtils {

    /**
     * 检测是否有网络连接
     *
     * @param mContext
     * @return true
     */
    public static boolean hasNet(Context mContext) {
        ConnectivityManager mConnectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 检测当前网络类型
     *
     * @param mContext
     * @return
     */
    public static String checkNetType(Context mContext) {
        ConnectivityManager mConnectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        //检查网络连接
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null) {
            return "none";
        } else {
            int netType = info.getType();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            } else if (netType == ConnectivityManager.TYPE_MOBILE && !mTelephony.isNetworkRoaming()) {
                //TelephonyManager.NETWORK_TYPE_UMTS :3G
                //TelephonyManager.NETWORK_TYPE_LTE :4G
                return "mobile";
            }
        }
        return "";
    }
}
