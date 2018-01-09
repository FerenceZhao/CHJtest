package com.example.denic.chjtest;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by Denic on 2017/12/26.
 */

public class MyDialog extends Dialog implements View.OnClickListener{
    public MyDialog(Context context) {
        super(context,R.style.myDialog);
        //初始化布局
        setContentView(R.layout.layout_select_photo);
        Window dialogWindow = getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);

        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_gallery).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_camera:
                onButtonClickListener.camera();
                break;
            case R.id.btn_gallery:
                onButtonClickListener.gallery();
                break;
            case R.id.btn_cancel:
                onButtonClickListener.cancel();
                break;

            default:
                break;
        }
    }
    /**
     * 按钮的监听器
     * @author Orathee
     * @date 2014年3月20日 下午4:28:39
     */
    public interface OnButtonClickListener{
        void camera();
        void gallery();
        void cancel();
    }
    private OnButtonClickListener onButtonClickListener;

    public OnButtonClickListener getOnButtonClickListener() {
        return onButtonClickListener;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
