package com.telegram.hook.utils;

import com.telegram.hook.bean.TelegramActionBean;

public class ActionUtils {
    private static TelegramActionBean bean=null;
    public static void setAction(TelegramActionBean action){
      bean=action;
    }
    public static TelegramActionBean getAction(){
        return bean;
    }
    public static void removeAction(){
        bean=null;
    }
}
