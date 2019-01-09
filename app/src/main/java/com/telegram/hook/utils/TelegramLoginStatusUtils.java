package com.telegram.hook.utils;

import android.text.TextUtils;

import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;

/**
 * telegram 弹窗 语句判断
 * 如果比较出来则上报
 * */
public class TelegramLoginStatusUtils {
    /**
     *判断弹窗信息
     * 如果弹窗显示 登录次数太多
     * 则删除data data 目录 并在启动 则登录其他号码不受影响
     * */
    public static void isTooManyAttempts(String text){
        if (TextUtils.isEmpty(text)){
            LogUtil.d("TelegramLoginStatusUtils text is null");
            return;
        }
        LogUtil.d("TelegramLoginStatusUtils   text="+text);
        if (text.contains("Too many attempts, please try again later")){
            LogUtil.d("TelegramLoginStatusUtils Too many attempts");
            DataCleanManager.deleData();
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("TelegramLoginStatusUtils Too many attempts2");
                    HookController.getInstance().sendStatus(ConstantStatus.tooManyAttempts);
                }
            },500);
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("TelegramLoginStatusUtils Too many attempts close app");
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            },1000);
            return;
        }
    }
    /**
     * 判断账号是被禁了
     * 还是手机号码不合法
     * */
    public static void isBannedOrinvalid(final Boolean type){
        HookController.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (type){
                    HookController.getInstance().sendStatus(ConstantStatus.bannedPhoneNumber);
                }else {
                    HookController.getInstance().sendStatus(ConstantStatus.phoneNumberInvalid);
                }
            }
        },5000);


    }
}
