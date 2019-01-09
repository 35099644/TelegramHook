package com.telegram.hook.http;


import android.text.TextUtils;

import com.telegram.hook.App;
import com.telegram.hook.listener.PeiFengHttpCallBack;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.YmUtils;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 易码平台http 请求
 */
public class YmHttpMethods {
    private static final YmHttpMethods ourInstance = new YmHttpMethods();

   public static YmHttpMethods getInstance() {
        return ourInstance;
    }



    public  void getPhoneNum(final PeiFengHttpCallBack<String> callBack) {
        Request request = new Request.Builder()
                .url(YmUtils.getInstance().getPhoneNumUrl())
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
                    String phoneNum = YmUtils.getInstance().getEffectiveValue(body);
                    callBack.onSuccess(phoneNum);
                } else {
                    callBack.onFail("");

                }
            }
        });
    }
    public  String syncGetPhoneNum() {
        Request request = new Request.Builder()
                .url(YmUtils.getInstance().getPhoneNumUrl())
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public  String getCode(String num) {
        Request request = new Request.Builder()
                .url(YmUtils.getInstance().getMsgCodeUrl(num))
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public  void releaseNum(final String num) {

        if (TextUtils.isEmpty(num)){
            return;
        }
        Request request = new Request.Builder()
                .url(YmUtils.getInstance().getReleaseNumUrl(num))
                .build();
        App.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               MyLog.write2File("releaseNum IOException="+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Boolean isSuccess = response.isSuccessful();
                if (isSuccess) {
                    String body = response.body().string();
                    MyLog.write2File("releaseNum num="+num+" body"+body);
                }else {
                    MyLog.write2File("releaseNum fail  num="+num);
                }
            }
        });
    }
    public  String releaseNum2(String num) {
        if (TextUtils.isEmpty(num)){
            return "";
        }
        Request request = new Request.Builder()
                .url(YmUtils.getInstance().getReleaseNumUrl(num))
                .build();
        try {
            Response response = App.getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public  void ignore(final String num) {
        Request request = new Request.Builder()
                .url(YmUtils.getInstance().getIgnoreUrl(num))
                .build();
        App.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Boolean isSuccess = response.isSuccessful();
                if (isSuccess) {
                    String body = response.body().string();
                    MyLog.write2File("ignore num="+num+" body"+body);
                }else {
                    MyLog.write2File("ignore fail  num="+num);
                }

            }
        });
    }
}
