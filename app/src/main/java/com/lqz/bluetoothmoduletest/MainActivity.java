package com.lqz.bluetoothmoduletest;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends BaseActivity {

    private Button scanBtn; //搜索按钮
    private RecyclerView recyclerView;
    private List<BluetoothDevice> mDevices;

    private Button writeReadBtn; //读写按钮

    private Button disConnectBtn;//断开连接按钮

    private TextView connectBluetoothName;

    //蓝牙设备列表项适配器
    private DeviceBlueAdapter mDeviceAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        scanBtn = findViewById(R.id.bluetooth_scan_button);
        disConnectBtn = findViewById(R.id.disconnect_bluetooth);
        connectBluetoothName = findViewById(R.id.bluetooth_connect_name);
        recyclerView = findViewById(R.id.bluetooth_recycler_view);
        //设置LayoutManager，以LinearLayoutManager为例子进行线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mDevices = new ArrayList<>();
        mDeviceAdapter = new DeviceBlueAdapter(mDevices);
        recyclerView.setAdapter(mDeviceAdapter);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果原来在扫描，是否先停止扫描？？？
                //是否清空数据？？？
                searchDevice();
            }
        });

        //点击显示弹窗
        writeReadBtn = findViewById(R.id.write_and_read);
        writeReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCharacterPopup();
            }
        });

        disConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否连接
                //如果是就断开连接
                ClientManager.getClient().disconnect(MAC);
            }
        });
    }

    private WriteAndReadPopup writeAndReadPopup = null;

    private void startCharacterPopup(UUID service, UUID character) {
        if (writeAndReadPopup == null) {
            writeAndReadPopup = new WriteAndReadPopup(MainActivity.this);
        }
        writeAndReadPopup.setInfo(mDevice.getAddress(), service, character);
        writeAndReadPopup.showPopupWindow();
    }

    private void initData() {
        mDeviceAdapter.setBluetoothDeviceListener(new DeviceBlueAdapter.BlueToothDeviceListener() {
            @Override
            public void clickDevice(BluetoothDevice device) {
                //连接设备
                connectDevice();
            }
        });
    }

    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2) //扫描低功耗蓝牙两次，每次5S
                .searchBluetoothClassicDevice(5000, 2) //扫描经典蓝牙2次，每次5s
                .build();

        ClientManager.getClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            //todo 开始扫描
            mDevices.clear();
            mDeviceAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            //            BluetoothLog.w("MainActivity.onDeviceFounded " + device.device.getAddress());
            if (!mDevices.contains(device.getDevice())) {
                mDevices.add(device.getDevice());
                mDeviceAdapter.notifyDataSetChanged();
                Log.d("LQZ", "name = " + device.getName() + " address = " + device.getAddress());
//                mAdapter.setDataList(device);
            }
        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("MainActivity.onSearchStopped");
            //扫描结束
        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("MainActivity.onSearchCanceled");

            //取消扫描

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch(); //停止扫描
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            //连接状态的监听事件

            mConnected = (status == STATUS_CONNECTED);
        }
    };

    @Override
    protected void onDestroy() {
        ClientManager.getClient().disconnect(mDevice.getAddress());
        ClientManager.getClient().unregisterConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
        super.onDestroy();
    }


    //连接设备
    private void connectDevice() {

        //todo 正在连接！！！！
//        mTvTitle.setText(String.format("%s%s", getString(R.string.connecting), mDevice.getAddress()));
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().connect(mDevice.getAddress(), options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {

                if (code == REQUEST_SUCCESS) {
                    //todo 将下面的UI更新
                    //不对设备列表进行处理！！！！
                    connectBluetoothName.setText(mDevice.getName + "");
                    mDeviceAdapter.setGattProfile(profile);
                }
            }
        });
    }
}