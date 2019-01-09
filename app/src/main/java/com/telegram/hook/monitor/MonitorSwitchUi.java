package com.telegram.hook.monitor;

import com.telegram.hook.hook.HookController;
import com.telegram.hook.utils.LogUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * 监听telegram ui 切换
 * */
public class MonitorSwitchUi {
    public static void startMonitor(){
        Class cl = XposedHelpers.findClass("org.telegram.ui.ActionBar.ActionBarLayout", HookController.mClassLoader);
        Class cl2 = XposedHelpers.findClass("org.telegram.ui.ActionBar.BaseFragment", HookController.mClassLoader);
        XposedHelpers.findAndHookMethod(cl, "addFragmentToStack",cl2, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String ui=param.args[0]+"";
                if (ui.contains("org.telegram.ui.LoginActivity")){
                }
                LogUtil.d("MonitorSwitchUi baseFragment="+param.args[0]);
            }
        });
    }
}
