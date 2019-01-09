package com.telegram.hook.ui;


import android.os.Bundle;

import com.telegram.hook.App;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.config.ConstantTelegramView;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.TelegramLoginStatusUtils;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

/**
 * telegram 登录界面
 * 本身是一个自定义fragment
 * 用户所看到的登录都在其里面完成
 * 含有 9 个子界面
 * 字界面继承 自定义 SlideView
 */
public class LoginActivity {
    private static Object currentView;
    private static Object loginObject;

    public static void login() {
        LogUtil.d("LoginActivity login");
        hookNextPage();
        IntroActivity.go2LoginView();//如果有欢迎界面 则关闭
        ListenOnShow();
        ListenAlertDialog();
    }

    private static void ListenAlertDialog() {
        Class cl = XposedHelpers.findClass("org.telegram.ui.LoginActivity", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "needShowAlert", String.class, String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                for (int i = 0; i < param.args.length; i++) {
                    TelegramLoginStatusUtils.isTooManyAttempts(param.args[i] + "");
                }
                return null;
            }
        });

        XposedHelpers.findAndHookMethod(cl, "needShowInvalidAlert", String.class, boolean.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                boolean type = (boolean) param.args[1];
                TelegramLoginStatusUtils.isBannedOrinvalid(type);
                return null;
            }
        });
    }

    public static void hookNextPage() {
        LogUtil.d("hookNextPage");
        Class cl = XposedHelpers.findClass("org.telegram.ui.LoginActivity", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "setPage", int.class, boolean.class, Bundle.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int page= (int) param.args[0];
                LogUtil.d("beforeHookedMethod page="+page);
                if (page==6){
                    XposedHelpers.callMethod(currentView,"onBackPressed");
                    param.args[0]=0;
                    param.args[1]=true;
                    param.args[2]=null;
                    param.args[3]=true;
                    param.setResult(true);
                    closeSmsView(5000);
                }
            }
        });
    }

    /**
     * 监听登录界面的子fragment
     * 当 子界面显示的时候，做出相应逻辑
     */
    public static void ListenOnShow() {
        LogUtil.d("LoginActivity ListenOnShow");
        Class cl = XposedHelpers.findClass("org.telegram.ui.Components.SlideView", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "onShow", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                currentView = param.thisObject;
                LogUtil.d("LoginActivity ListenOnShow class=" + currentView.getClass().getName());
                if (currentView.getClass().getName().equals(ConstantTelegramView.phoneView)) {
                    HookController.getInstance().sendStatus(ConstantStatus.enterPhoneView);
                    return;
                }
                if (currentView.getClass().getName().equals(ConstantTelegramView.smsView)) {
//                   SmsView.isClose(currentView);
                    return;
                }
                if (currentView.getClass().getName().equals(ConstantTelegramView.twoStepCodeView)) {
                    LogUtil.d("LoginActivity ListenOnShow class=twoStepCodeView");
                    return;
                }
                if (currentView.getClass().getName().equals(ConstantTelegramView.registerView)) {
                    RegisterView view = new RegisterView();
                    view.start(currentView);
                    return;
                }
            }
        });
    }

    /**
     * 输入手机号码并登录
     */
    public static void setNum(final String num) {
        PhoneView.setNumAndNext(currentView, num);
    }

    /**
     * 输入手机号码的验证码
     */
    public static void setCode(final String code) {
        SmsView.setCode(currentView, code);
    }

    /**
     * 关闭摄入验证码界面
     */
    public static void closeSmsView(int time) {
        App.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("LoginActivity closeSmsView ");
                SmsView.close(currentView);
            }
        },time);
    }

}
