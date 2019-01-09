package com.telegram.hook.http;

import android.text.TextUtils;


import com.telegram.hook.App;
import com.telegram.hook.bean.PickPhoneBean;
import com.telegram.hook.config.PeiFengContant;
import com.telegram.hook.listener.PeiFengHttpCallBack;
import com.telegram.hook.utils.MyLog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SdHttpMethods {
    String TAG = "SdHttpMethods";
    private static final SdHttpMethods ourInstance = new SdHttpMethods();


    public static SdHttpMethods getInstance() {
        return ourInstance;
    }

    public String pickPhoneArea() {
        MyLog.write2File(PeiFengContant.SD_HTTP_PICKPHONEAREA + PeiFengContant.getImei());
        Request request = new Request.Builder()
                .url(PeiFengContant.SD_HTTP_PICKPHONEAREA + PeiFengContant.getImei())
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
            MyLog.write2File(e.toString());
            return "";
        }
        return "";

    }



    public Boolean registeredPhones(String phones, PickPhoneBean pickPhoneBean, String examingPhone) {
        MyLog.write2File("registeredPhones Url " + getRegisteredPhonesUrl(phones, pickPhoneBean.getPhoneArea(), pickPhoneBean.getStart(), pickPhoneBean.getEnd(), examingPhone));
        Request request = new Request.Builder()
                .url(getRegisteredPhonesUrl(phones, pickPhoneBean.getPhoneArea(), pickPhoneBean.getStart(), pickPhoneBean.getEnd(), examingPhone))
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                if (!TextUtils.isEmpty(body) && "ok".equals(body)) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        return false;
    }

    public Boolean deviceBanned() {
        MyLog.write2File(getBannedUrl());
        Request request = new Request.Builder()
                .url(getBannedUrl())
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                if (!TextUtils.isEmpty(body) && "ok".equals(body)) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.write2File(e.toString());
            return false;
        }
        return false;
    }

    public Boolean registeredPhones(String phones, String phoneArea, String startNum, String endNum, String examingPhone) {
        MyLog.write2File("registeredPhones Url " + getRegisteredPhonesUrl(phones, phoneArea, startNum, endNum, examingPhone));
        Request request = new Request.Builder()
                .url(getRegisteredPhonesUrl(phones, phoneArea, startNum, endNum, examingPhone))
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                if (!TextUtils.isEmpty(body) && "ok".equals(body)) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        return false;
    }

    public void registeredPhones(String phones, String phoneArea, String startNum, String endNum, String examingPhone, final PeiFengHttpCallBack<String> callBack) {
        MyLog.write2File("registeredPhones Url " + getRegisteredPhonesUrl(phones, phoneArea, startNum, endNum, examingPhone));
        Request request = new Request.Builder()
                .url(getRegisteredPhonesUrl(phones, phoneArea, startNum, endNum, examingPhone))
                .build();
        App.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Boolean isSuccess = response.isSuccessful();
                if (isSuccess) {
                    String body = response.body().string();
                    callBack.onSuccess(body);
                } else {
                    callBack.onFail("");

                }
            }
        });

    }

    public String getRegisteredPhonesUrl(String phones, String phoneArea, String startNum, String endNum, String examingPhone) {
        return PeiFengContant.SD_HTTP_REGISTEREDPHONES + phones + "&phoneArea=" + phoneArea + "&startNumber=" + startNum + "&endNumber=" + endNum + "&examingPhone=" + examingPhone + "&deviceId=" + PeiFengContant.getImei();
    }

    public String getRegisteredPhonesUrl(String phones, String phoneArea, Long startNum, Long endNum, String examingPhone) {
        return PeiFengContant.SD_HTTP_REGISTEREDPHONES + phones + "&phoneArea=" + phoneArea + "&startNumber=" + startNum + "&endNumber=" + endNum + "&examingPhone=" + examingPhone + "&deviceId=" + PeiFengContant.getImei();
    }

    public String getExamPhoneUrl(String num) {
        return PeiFengContant.SD_HTTP_BASE_URL + "getExamPhone?currentUsePhone=" + num + "&deviceId=" + PeiFengContant.getImei();
    }

    public String getNewPhoneUselessUrl(String num, String registeredPhone) {
        return PeiFengContant.SD_HTTP_BASE_URL + "newPhoneUseless?uselessPhone=" + num + "&registeredPhone=" + registeredPhone + "&type=0" + "&deviceId=" + PeiFengContant.getImei();
    }

    public String getBannedUrl() {
        return PeiFengContant.SD_HTTP_BASE_URL + "/deviceBanned?deviceId=" + PeiFengContant.getImei() + "&type=" + PeiFengContant.PEIFENG_STATUS+"&appId=";
    }

    public String getExamPhone(String num) {
        MyLog.write2File(getExamPhoneUrl(num));
        Request request = new Request.Builder()
                .url(getExamPhoneUrl(num))
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void NewPhoneUseless(String phoneNum, String registeredPhone) {
        Request request = new Request.Builder()
                .url(getNewPhoneUselessUrl(phoneNum, registeredPhone))
                .build();
        App.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MyLog.write2File(" NewPhoneUseless onFailure e=" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Boolean isSuccess = response.isSuccessful();
                if (isSuccess) {
                    String body = response.body().string();
                    MyLog.write2File(" NewPhoneUseless onResponse body=" + body);
                } else {
                    MyLog.write2File(" NewPhoneUseless onResponse isSuccess=false");
                }
            }
        });
    }
}
