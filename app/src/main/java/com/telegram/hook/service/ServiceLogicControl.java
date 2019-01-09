package com.telegram.hook.service;

import android.text.TextUtils;

import com.telegram.hook.App;
import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.http.YmHttpSdk;
import com.telegram.hook.listener.PeiFengHttpCallBack;
import com.telegram.hook.listener.TListener;
import com.telegram.hook.utils.LaunUtils;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.MyLog;
import com.telegram.hook.utils.RxContactUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
/**
 *  在services接收telegram 应用发过来的状态
 *  通过判断telegram 状态 让其进行下一步操作
 * */
class ServiceLogicControl {
    private TListener listener;
    private static final ServiceLogicControl ourInstance = new ServiceLogicControl();
    private String num;

    private ServiceLogicControl() {
    }

    static ServiceLogicControl getInstance() {
        return ourInstance;
    }

    Disposable phoneDisposable;

    public void setLisener(TListener lisener) {
        this.listener = lisener;
    }

    public void DealData(int status) {
        if (listener == null) {
            LogUtil.d("ServiceLogicControl listener ");
            return;
        }
        if (status == ConstantStatus.serviceConnection) {
            LogUtil.d("BridgeService currentStatus serviceConnection=" + ConstantStatus.serviceConnection);
            listener.onDataCome(ConstantAction.startLoginOrFilterUser, "");
            return;
        }
        if (status == ConstantStatus.enterPhoneView || status == ConstantStatus.bannedPhoneNumber || status == ConstantStatus.phoneNumberInvalid) {
            LogUtil.d("BridgeService currentStatus enterPhoneView or banned or numberInvalid status=" + status);
//             getPhoneNum();
//            listener.onDataCome(ConstantAction.loginNum,"13664447163");
            return;
        }
        if (status == ConstantStatus.enterSmsView) {
            LogUtil.d("BridgeService currentStatus enterSmsView");
//            getCode();
            return;
        }
        if (status == ConstantStatus.loginSuccess) {
            LogUtil.d("BridgeService currentStatus loginSuccess");
            return;
        }
        if (status == ConstantStatus.tooManyAttempts) {
            LogUtil.d("BridgeService currentStatus tooManyAttempts");
            App.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    LaunUtils.startPeiFeng();
                }
            }, 2000);
            return;
        }
        if (status==ConstantStatus.stopEnterTwoStepCodeView){
            LogUtil.d("BridgeService currentStatus stopEnterTwoStepCodeView");
            App.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listener.onDataCome(ConstantAction.closeSmsView, "");
                }
            }, 5000);
            return;
        }


    }

    public void removeLiserner() {
        listener = null;
    }

    public void getPhoneNum() {
        if (phoneDisposable != null) {
            LogUtil.d("LoginActivity getPhoneNum disposable not not null");
            phoneDisposable.dispose();
        }
        phoneDisposable = RxContactUtils.getPhone().subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s)) {
                    num = s;
                    MyLog.write2File("LoginActivity getPhoneNum num=" + s);
                    listener.onDataCome(ConstantAction.loginNum, s);
                    phoneDisposable.dispose();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                MyLog.write2File("获取手机号码 异常了");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                MyLog.write2File("获取手机号码 循环完成了，但是海没获取到号码");

            }
        });
    }

    public void getCode() {
        YmHttpSdk.getCode(num, new PeiFengHttpCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (result.contains("2007")) {
                    listener.onDataCome(ConstantAction.closeSmsView, "");
                } else {
                    listener.onDataCome(ConstantAction.loginCode, result);
                }
            }
            @Override
            public void onFail(String str) {

            }
        });
    }

}
