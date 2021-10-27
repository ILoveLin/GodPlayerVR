package com.company.shenzhou.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.dbbean.VideoDBBean01;
import com.company.shenzhou.utils.ClearEditText;
import com.hjq.base.BaseDialog;
import com.yun.common.utils.LogUtils;

import java.util.HashMap;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 设备输入   修改对话框
 */
public final class AdviceReInputDialog {

    public static final class Builder
            extends UIDialog.Builder<Builder>
            implements BaseDialog.OnShowListener {

        private OnListener mListener;
        //        private final EditText mInputView;
        private ClearEditText cet_cme_account;
        private ClearEditText cet_cme_password;
        private ClearEditText cet_cme_title;
        private ClearEditText cet_cme_ip;
        private ClearEditText cet_cme_note_message;
        private ClearEditText cet_cme_port;
        private ClearEditText cet_cme_mic_port;
        private ClearEditText cet_cme_start_type;
        private TextView makeSure;
        private TextView makeCancle;
        private Context context;
        private VideoDBBean01 bean;
//        private LinearLayout linear_pop_change;
//
//        private interface onReInputTypeListener {
//            void oneInputTypeClick(TextView mType);
//        }

        public Builder(Context context, VideoDBBean01 bean) {
            super(context);
            setCustomView(R.layout.advice_input_dialog);
            this.context = context;
//            cet_cme_account = (ClearEditText) findViewById(R.id.cet_cme_account);
//            cet_cme_password = (ClearEditText) findViewById(R.id.cet_cme_password);
//            cet_cme_title = (ClearEditText) findViewById(R.id.cet_cme_title);
            cet_cme_ip = (ClearEditText) findViewById(R.id.cet_cme_ip);
//            cet_cme_note_message = (ClearEditText) findViewById(R.id.cet_cme_note_message);
            cet_cme_port = (ClearEditText) findViewById(R.id.cet_cme_port);
//            cet_cme_mic_port = (ClearEditText) findViewById(R.id.cet_cme_mic_port);
            cet_cme_start_type = (ClearEditText) findViewById(R.id.cet_cme_start_type);
            cet_cme_start_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onReInputTypeClick(cet_cme_start_type);
                }
            });
            if (null != bean) {
//                cet_cme_account.setText("" + bean.getAccount());
//                cet_cme_password.setText("" + bean.getPassword());
//                cet_cme_title.setText("" + bean.getTitle());
                cet_cme_ip.setText("" + bean.getIp());
//                cet_cme_note_message.setText("" + bean.getMakeMessage());
                cet_cme_port.setText("" + bean.getPort());
                cet_cme_start_type.setText("" + bean.getType());
//                cet_cme_mic_port.setText("" + bean.getMicport());
                LogUtils.e("修改===dialog里面的bean==="+bean.toString());

            }

            addOnShowListener(this);
        }

//        public TextView getAccountView() {
//            return cet_cme_account;
//        }
//
//        public TextView getPasswordView() {
//            return cet_cme_password;
//        }
//
//        public TextView getTitleView() {
//            return cet_cme_title;
//        }

        public ClearEditText getIPView() {
            return cet_cme_ip;
        }

//        public TextView getMessageView() {
//            return cet_cme_note_message;
//        }

        public TextView getPortView() {
            return cet_cme_port;
        }

//        public TextView getMicPortView() {
//            return cet_cme_mic_port;
//        }

        public ClearEditText getTypeView() {
            return cet_cme_start_type;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */
        @Override
        public void onShow(BaseDialog dialog) {
//            postDelayed(() -> getSystemService(InputMethodManager.class).showSoftInput(mInputView, 0), 500);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ui_confirm:
                    autoDismiss();
                    if (mListener != null) {
//                        String account = cet_cme_account.getText().toString().trim();
//                        String password = cet_cme_password.getText().toString().trim();
//                        String title = cet_cme_title.getText().toString().trim();
                        String ip = cet_cme_ip.getText().toString().trim();
//                        String makeMessage = cet_cme_note_message.getText().toString().trim();
                        String port = cet_cme_port.getText().toString().trim();
                        //获取当前类型  是2 的时候表示url链接 账号密码  端口可以为空
                        String type = cet_cme_start_type.getText().toString().trim();
//                        String micport = cet_cme_mic_port.getText().toString().trim();

//                        HashMap<String, String> mMap = new HashMap<>();
//                        mMap.put("account", bean.getAccount());
//                        mMap.put("password", bean.getPassword());
//                        mMap.put("title", bean.getTitle());
//                        mMap.put("ip", ip);
//                        mMap.put("makeMessage", bean.getMakeMessage());
//                        mMap.put("port", port);
//                        mMap.put("type", type);
//                        mMap.put("micport", micport);



                        HashMap<String, String> mMap = new HashMap<>();
                        LogUtils.e("DATA===type====="+type);
                        //0--HD3  1--一体机   2--url
                        if (type.equals("HD3")){//
                            mMap.put("account", "admin");
                            mMap.put("password", "123456");
                            mMap.put("title", "HD3的标题");
                            mMap.put("ip", ip);
                            mMap.put("makeMessage", "HD3的备注信息");
                            mMap.put("port", port);
                            mMap.put("micport", "7789");
                            mMap.put("type", type);
                        }else if (type.equals("一体机")){
                            mMap.put("account", "root");
                            mMap.put("password", "root");
                            mMap.put("title", "一体机的标题");
                            mMap.put("ip", ip);
                            mMap.put("makeMessage", "一体机的备注信息");
                            mMap.put("port", port);
                            mMap.put("micport", "7789");
                            mMap.put("type", type);
                        }else if (type.equals("自定义URL")){
                            mMap.put("account", "admin");
                            mMap.put("password", "123456");
                            mMap.put("title", "自定义URL的标题");
                            mMap.put("ip", ip);
                            mMap.put("makeMessage", "自定义URL的备注信息");
                            mMap.put("port", port);
                            mMap.put("micport", "7789");
                            mMap.put("type", type);
                        }
                        mListener.onConfirm(getDialog(), mMap);
                    }
                    break;
                case R.id.tv_ui_cancel:
                    autoDismiss();
                    if (mListener != null) {
                        mListener.onCancel(getDialog());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void dismissDialog() {
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, HashMap<String, String> mMap);

        /**
         * 点击确定时回调
         */
        void onReInputTypeClick(TextView mType);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }
    }


}