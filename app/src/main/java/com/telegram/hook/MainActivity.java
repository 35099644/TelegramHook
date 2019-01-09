package com.telegram.hook;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.telegram.hook.config.ConstantAction;
import com.telegram.hook.service.BridgeService;
import com.telegram.hook.utils.LaunUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void setNum(View view){
        LaunUtils.startPeiFeng();
//        BridgeService.controllerTelegram(ConstantAction.loginNum,"13438155680");
    }

    public void setCode(View view){
        BridgeService.controllerTelegram(ConstantAction.loginCode,"3");
    }


}
