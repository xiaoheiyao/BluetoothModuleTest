package com.lqz.bluetoothmoduletest;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothContext;

/**
 * author : LQZ
 * e-mail : qzli@topxgun.com
 * date   : 2022/9/27 10:34
 * desc   :
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BluetoothContext.set(this);

    }
}
