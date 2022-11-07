package com.lqz.bluetoothmoduletest;

import com.inuker.bluetooth.library.BluetoothClient;

/**
 * author : LQZ
 * e-mail : qzli@topxgun.com
 * date   : 2022/9/27 14:29
 * desc   :
 */
public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(MyApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}
