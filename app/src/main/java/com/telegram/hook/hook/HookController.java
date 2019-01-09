package com.telegram.hook.hook;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;


import com.telegram.hook.PeifengController;
import com.telegram.hook.TelegramListener;
import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.ui.LoginActivity;
import com.telegram.hook.utils.CrashHandler;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookController {
    private static final String TAG = "HookController";
    public static HookController instance;
    public static ClassLoader mClassLoader;
    private Context mContext;
    private TelegramListener mListener;
    public Activity LaunchActivity = null;
    public static Handler handler = new Handler(Looper.getMainLooper());
    public static HookController getInstance() {
        if (instance == null)
            synchronized (HookController.class) {
                if (instance == null)
                    instance = new HookController();
            }
        return instance;
    }

    public void init(ClassLoader classLoader) {
        boolean isHook = this.mClassLoader == null && classLoader != null;
        this.mClassLoader = classLoader;
        LogUtil.d(TAG, "isHook = " + isHook);
        if (isHook) {
            hookApp();
        }
    }
    /**
     *hook application 的context 启动自己的services
     * 然后通过aidl 进行 自己应用和hook 的目标应用进行通信
     * */
    private void hookApp() {
        Class clazz = XposedHelpers.findClass("org.telegram.messenger.ApplicationLoader", mClassLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                mContext = (Context) ReflectUtil.getDeclaredField(param.thisObject, "applicationContext");
                connService();
            }
        });

    }

    private void connService() {
        LogUtil.d(TAG, "connService ");
        Intent intent = new Intent();
        intent.setClassName("com.telegram.hook", "com.telegram.hook.service.BridgeService");
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TelegramListener listener = TelegramListener.Stub.asInterface(service);
            if (listener != null) {
                try {
                    mListener = listener;
                    mListener.registerController(peifengController);
                    sendStatus(ConstantStatus.serviceConnection);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    LogUtil.d("onServiceConnected RemoteException registerController");
                }
            }
        }

        PeifengController peifengController = new PeifengController.Stub() {
            @Override
            public void currentAction(final int action,final String loginInfo) throws RemoteException {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        responseAction(action,loginInfo);
                    }
                },0);
            }
        };

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    /**
     *  自己应用通过aidl 操作telegram
     * */
    public void responseAction(int action,String loginInfo) {
        if (action == ConstantAction.startLoginOrFilterUser) {
            LogUtil.d("HookController responseAction startLogin");
//            LoginActivity.login();
            PeiFengContactManager.getInstance().start();
            LogUtil.stackTraces("isLogin");
            return;
        }
        if (action == ConstantAction.loginNum) {
            LogUtil.d("HookController responseAction loginNum="+loginInfo,"Thread="+Thread.currentThread().getName());
            LoginActivity.setNum(loginInfo);
            return;
        }
        if (action == ConstantAction.loginCode) {
            LogUtil.d("HookController responseAction loginCode="+loginInfo);
            LoginActivity.setCode(loginInfo);
            return;
        }
        if (action==ConstantAction.closeSmsView){
            LogUtil.d("HookController responseAction colseSmsView");
            LoginActivity.closeSmsView(5000);
        }
    }
    /**
     *通过aidl 把当前telegram 进程状态上报给自己应用
     * */
    public void sendStatus(int status) {
        if (mListener == null) {
            LogUtil.d("sendStatus currentStatus ==null ");
            return;
        }
        try {
            mListener.currentStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
            CrashHandler.getInstance().handleException(e);
            LogUtil.d(TAG, "RemoteException ");
        }
    }


    public void getLaunchActivity() {
        Class clazz = XposedHelpers.findClass("org.telegram.ui.LaunchActivity", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.thisObject == null) {
                    LogUtil.d("LaunchActivity", "【LaunchActivity == null 】");
                    return;
                }
                LaunchActivity = (Activity) param.thisObject;
            }
        });
    }
    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            handler.post(runnable);
        } else {
            handler.postDelayed(runnable, delay);
        }
    }
}
