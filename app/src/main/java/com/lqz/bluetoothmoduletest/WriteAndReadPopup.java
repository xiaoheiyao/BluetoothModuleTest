package com.lqz.bluetoothmoduletest;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.UUID;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

/**
 * author : LQZ
 * e-mail : qzli@topxgun.com
 * date   : 2022/11/2 13:11
 * desc   :
 */
public class WriteAndReadPopup extends BasePopupWindow {

    private Button writeBtn; //写入操作
    private Button writeClearBtn; //写入删除操作
    private EditText writeEt; //写入框
    private Button readBtn; //读取按钮 （应该不需要）
    private Button readClearBtn; //读取清除按钮
    private TextView readTv; //读取框


    private String mMac;
    private UUID mService;
    private UUID mCharacter;

    public WriteAndReadPopup(Context context) {
        super(context);

        setPopupGravity(Gravity.BOTTOM); //设置在下面

        setContentView(createPopupById(R.layout.write_read_layout));

        initView();
    }

    public void setInfo(String mac, UUID service, UUID character) {
        mMac = mac;
        mService = service;
        mCharacter = character;
    }

    private void initView() {
        writeBtn = findViewById(R.id.write);
        writeClearBtn = findViewById(R.id.write_clear);
        writeEt = findViewById(R.id.write_data);

        readBtn = findViewById(R.id.read);
        readClearBtn = findViewById(R.id.read_clear);
        readTv = findViewById(R.id.read_data);

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 写入writeEt的数据
                ClientManager.getClient().write(mMac, mService, mCharacter,
                        ByteUtils.stringToBytes(writeEt.getText().toString()), mWriteRsp);
            }
        });
        writeClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeEt.setText(""); //清空数据
            }
        });

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //读取操作有问题，需要重新优化！！！！
                ClientManager.getClient().read(mMac, mService, mCharacter, mReadRsp);
            }
        });

        readClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readTv.setText(""); //清空数据
            }
        });
    }

    //读写操作的监听事件
    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
            }
        }
    };

    private final BleReadResponse mReadRsp = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data) {  //todo 这个每次只有点击按钮的时候才读取吗？可不可以一直读取？？
            if (code == REQUEST_SUCCESS) {
                readTv.setText(ByteUtils.byteToString(data)); //只读取当前结果！！！
//                mBtnRead.setText(String.format("read: %s", ByteUtils.byteToString(data)));
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
//                mBtnRead.setText("read");
            }
        }
    };

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_BOTTOM)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.TO_BOTTOM)
                .toDismiss();
    }
}
