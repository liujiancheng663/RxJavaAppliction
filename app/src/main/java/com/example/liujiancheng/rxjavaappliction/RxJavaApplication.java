package com.example.liujiancheng.rxjavaappliction;

import android.app.Application;

/**
 * @version V1.0 <RxJavaApplication>
 * @FileName: com.example.liujiancheng.rxjavaappliction.RxJavaApplication.java
 * @author: liu jiancheng
 * @date: 2018-04-02 15:59
 */
public class RxJavaApplication extends Application{

    public static RxJavaApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        if(instance == null){
            instance = this;
        }
    }

    public static RxJavaApplication getInstance(){
        return instance;
    }
}
