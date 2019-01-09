package com.telegram.hook.config;

public class ConstantStatus {
    private static int totalEvents = 1;
    public static final int serviceConnection=totalEvents++;
    public static final int enterPhoneView=totalEvents++;
    public static final int setPhoneNumAndNext=totalEvents++;
    public static final int enterSmsView=totalEvents++;
    public static final int stopEnterTwoStepCodeView=totalEvents++;
    public static final int loginSuccess=totalEvents++;





//    -------------------login status---------------------------
    public static final int tooManyAttempts=totalEvents++;
    public static final int bannedPhoneNumber=totalEvents++;
    public static final int phoneNumberInvalid=totalEvents++;

    //------------------------------------------------------
    public static final int test3Times=totalEvents++;
}
