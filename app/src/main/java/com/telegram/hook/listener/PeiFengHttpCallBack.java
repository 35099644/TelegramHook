package com.telegram.hook.listener;

public interface PeiFengHttpCallBack<T> {
    void onSuccess(T result);
    void onFail(String string);
}
