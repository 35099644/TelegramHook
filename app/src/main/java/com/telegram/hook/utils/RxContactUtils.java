package com.telegram.hook.utils;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


import com.telegram.hook.http.SdHttpMethods;
import com.telegram.hook.http.YmHttpMethods;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
public class RxContactUtils {
    public static Observable<Boolean> Banned() {
        return Observable.interval(0, 30, TimeUnit.SECONDS).map(new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) throws Exception {
                if (aLong==0){
                    DataCleanManager.deleData();
                    return false;
                }
                Boolean isOk = SdHttpMethods.getInstance().deviceBanned();
                return isOk;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }



    public static Observable<String> getCode(final String num) {
        return Observable.interval(0, 5, TimeUnit.SECONDS).takeUntil(Observable.timer(240, TimeUnit.SECONDS)).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                String code = YmHttpMethods.getInstance().getCode(num);
                MyLog.write2File("RxContactUtils getCode =" + code);
                if (code.contains("2007")) {
                    YmHttpMethods.getInstance().releaseNum2(num);
                }
                return code;
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io());
    }

    public static Observable<String> getPhone() {
        return Observable.interval(10, 45, TimeUnit.SECONDS).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {

                String code = YmHttpMethods.getInstance().syncGetPhoneNum();
                MyLog.write2File("RxContactUtils getPhone =" + code);
                String phoneNum = "";
                if (!TextUtils.isEmpty(code)) {
                    phoneNum = YmUtils.getInstance().getEffectiveValue(code);
                    MyLog.write2File("RxContactUtils phoneNum =" + phoneNum);
                }
                return phoneNum;
            }
        }).subscribeOn(Schedulers.io()).
        observeOn(AndroidSchedulers.mainThread());//回调在主线程;
    }

}
