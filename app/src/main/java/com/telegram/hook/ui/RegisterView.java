package com.telegram.hook.ui;

import android.widget.EditText;

import com.telegram.hook.config.ConstantStatus;
import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.ReflectUtil;
import java.util.Random;
import de.robv.android.xposed.XposedHelpers;
/**最后的注册界面*/
public class RegisterView {
    public void start( Object thisObject){
        EditText firstNameField= (EditText) ReflectUtil.getDeclaredField(thisObject, "firstNameField");
        firstNameField.setText(getRandomName());
        EditText lastNameField= (EditText) ReflectUtil.getDeclaredField(thisObject, "lastNameField");
        lastNameField.setText("");
        XposedHelpers.callMethod(thisObject,"onNextPressed");
        HookController.getInstance().sendStatus(ConstantStatus.loginSuccess);
    }
    //length用户要求产生字符串的长度
    public static String getRandomName(){
        Random rd =new Random();
      int length=  rd.nextInt(12);
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}

