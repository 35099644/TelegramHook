package com.telegram.hook.ui;


import android.widget.EditText;

import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import com.telegram.hook.utils.ReflectUtil;
import de.robv.android.xposed.XposedHelpers;
/**登录界面设置手机号码*/
public class PhoneView {
    public  static void setNumAndNext(Object thisObject,String num){
        LogUtil.d("PhoneView setNumAndNext","thisOject="+thisObject,"num="+num);
        EditText codeField= (EditText) ReflectUtil.getDeclaredField(thisObject, "codeField");
        codeField.setText("86");
        EditText phoneField= (EditText) ReflectUtil.getDeclaredField(thisObject, "phoneField");
        phoneField.setText(num);
        XposedHelpers.callMethod(thisObject,"onNextPressed");
        HookController.getInstance().sendStatus(ConstantStatus.setPhoneNumAndNext);
    }

}
