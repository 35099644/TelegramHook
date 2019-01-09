package com.telegram.hook.hook;
import com.telegram.hook.utils.LogUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

class PeiFengContactManager {

    private static final PeiFengContactManager ourInstance = new PeiFengContactManager();

    static PeiFengContactManager getInstance() {
        return ourInstance;
    }

    private PeiFengContactManager() {
    }
    public void start(){
        isClientActivated();
    }
    public void isClientActivated(){
        Class cl = XposedHelpers.findClass("org.telegram.messenger.UserConfig", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "isClientActivated", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Boolean isClientActivated= (Boolean) param.getResult();
                LogUtil.d("isClientActivated="+isClientActivated);
            }
        });
    }
}
