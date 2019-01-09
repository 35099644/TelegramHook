package com.telegram.hook.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.TextView;

import com.telegram.hook.App;
import com.telegram.hook.PeifengController;
import com.telegram.hook.TelegramListener;

import com.telegram.hook.listener.TListener;
import com.telegram.hook.utils.CrashHandler;
import com.telegram.hook.utils.LogUtil;



public class BridgeService extends Service implements TListener {
    public static RemoteCallbackList<PeifengController> mRemotelist = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLogicControl.getInstance().setLisener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IBinder stub = new TelegramListener.Stub() {
        @Override
        public void currentStatus(int status) throws RemoteException {
            LogUtil.d("BridgeService currentStatus=" + status);
            ServiceLogicControl.getInstance().DealData(status);
        }

        @Override
        public void registerController(PeifengController controller) throws RemoteException {
            LogUtil.d("BridgeService registerController");
            mRemotelist = new RemoteCallbackList<PeifengController>();
            mRemotelist.register(controller);
            mRemotelist.beginBroadcast();
            mRemotelist.finishBroadcast();
        }
    };

    public static void controllerTelegram(int action,String loginInfo) {
        if (mRemotelist == null) {
            LogUtil.e("controllerTelegram mRemotelist ==null");
            return;
        }
        try {
            int num = mRemotelist.beginBroadcast();
            LogUtil.e("controllerTelegram Handler num=" + num+" logInfo="+loginInfo);
            for (int i = 0; i < num; ++i) {
                PeifengController controller = mRemotelist
                        .getBroadcastItem(i);
                controller.currentAction(action,loginInfo);
            }
            mRemotelist.finishBroadcast();
        } catch (Exception e) {
            LogUtil.e("controllerTelegram Exception=" + e.toString());
            CrashHandler.getInstance().handleException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("BridgeService onDestroy");
        ServiceLogicControl.getInstance().removeLiserner();
    }

    @Override
    public void onDataCome(final int action,final String loginInfo) {
        controllerTelegram(action,loginInfo);
    }

}
