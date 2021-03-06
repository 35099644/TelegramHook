package com.telegram.hook.ui;

import android.widget.EditText;
import android.widget.TextView;

import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;


/**
 * 设置验证码界面
 */
public class SmsView {
    public static void setCode(Object thisObject, String code) {
        LogUtil.d("SmsView  setCode code="+code );
        EditText codeField = (EditText) ReflectUtil.getDeclaredField(thisObject, "codeField");
        codeField.setText(code);
    }
    /**
     * 关闭当前界面
     * 返回上个界面
     */
    public static void close(Object thisObject) {
        LogUtil.d("SmsView  close" );
        TextView wrongNumber = (TextView) ReflectUtil.getDeclaredField(thisObject, "wrongNumber");
        wrongNumber.callOnClick();
    }
    /**
     * 判断是否该关闭输入验证码界面
     * 如果不关闭 则上报telegram 状态给自己应用
     * 自己应用获取验证码 在传回来
     *
     * 否则关闭输入验证码界面
     */
    public static void isClose(final Object thisObject) {
        int currentType = (int) ReflectUtil.getDeclaredField(thisObject, "currentType");
        LogUtil.d("e=" + currentType);
        if (currentType!=1){
            HookController.getInstance().sendStatus(ConstantStatus.enterSmsView);
        }else {
            HookController.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    close(thisObject);
                }
            },5000);
        }
    }


}
