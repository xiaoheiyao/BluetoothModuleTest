package com.lqz.bluetoothmoduletest;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;

import java.util.ArrayList;
import java.util.List;

/**
 * author : LQZ
 * e-mail : qzli@topxgun.com
 * date   : 2022/9/27 13:58
 * desc   :
 */
public class DeviceBlueAdapter extends RecyclerView.Adapter<DeviceBlueAdapter.ViewHolder> {

    private List<BluetoothDevice> mList;

    private List<DetailItem> mDataList;

    public DeviceBlueAdapter(List<BluetoothDevice> list) {
        mList = list;
        mDataList = new ArrayList<DetailItem>();
    }

    private void setDataList(List<DetailItem> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void setGattProfile(BleGattProfile profile) {
        List<DetailItem> items = new ArrayList<DetailItem>();

        List<BleGattService> services = profile.getServices();

        for (BleGattService service : services) {
            items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
            List<BleGattCharacter> characters = service.getCharacters();
            for (BleGattCharacter character : characters) {
                items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
            }
        }

        setDataList(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //todo 填充视图！！！
        holder.updateData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView addressTv;
        Button connectBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            //
            nameTv = itemView.findViewById(R.id.bluetooth_name);
            addressTv = itemView.findViewById(R.id.bluetooth_address);
            connectBtn = itemView.findViewById(R.id.bluetooth_connect);
        }

        @SuppressLint("MissingPermission")
        public void updateData(BluetoothDevice device) {
            nameTv.setText(device.getName() + ""); //这里需要一个权限检查操作
            addressTv.setText(device.getAddress());
            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo 点击连接按钮
                    if (deviceListener!=null){
                        deviceListener.clickDevice(device);
                    }
                }
            });
        }
    }

    private BlueToothDeviceListener deviceListener = null;

    public void setBluetoothDeviceListener(BlueToothDeviceListener listener) {
        deviceListener = listener;
    }

    //设备接口
    public interface BlueToothDeviceListener {
        /**
         * 连接的设备
         *
         * @param device
         */
        public void clickDevice(BluetoothDevice device);
    }
}
