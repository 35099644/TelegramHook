package com.telegram.hook.bean;

public class TelegramActionBean {
    int ation;
    /**
     * 如果在登录界面 info 表示手机号码
     * 如果在短信设置界面 info 表示验证码
     * */
    String loginInfo;
    String startSearchNum;
    String endSearchNum;
    public int getAtion() {
        return ation;
    }

    public void setAtion(int ation) {
        this.ation = ation;
    }

    public String getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(String loginInfo) {
        this.loginInfo = loginInfo;
    }

    public String getStartSearchNum() {
        return startSearchNum;
    }

    public void setStartSearchNum(String startSearchNum) {
        this.startSearchNum = startSearchNum;
    }

    public String getEndSearchNum() {
        return endSearchNum;
    }

    public void setEndSearchNum(String endSearchNum) {
        this.endSearchNum = endSearchNum;
    }


}
