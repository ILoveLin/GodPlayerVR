package com.company.shenzhou.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.company.shenzhou.R;
import com.company.shenzhou.base.ActivityCollector;
import com.company.shenzhou.base.BaseFragment;
import com.company.shenzhou.bean.Constants;
import com.company.shenzhou.bean.dbbean.UserDBRememberBean;
import com.company.shenzhou.bean.setDefaultParametersEvent;
import com.company.shenzhou.ui.activity.PowerExplainActivity;
import com.company.shenzhou.ui.activity.login.LoginAnimatorActivity;
import com.company.shenzhou.utils.FileUtil;
import com.company.shenzhou.utils.FileUtils;
import com.company.shenzhou.utils.ScreenSizeUtil;
import com.company.shenzhou.utils.db.UserDBRememberBeanUtils;
import com.company.shenzhou.view.PasswordV2EditText;
import com.company.shenzhou.view.PopupWindowInputChangeTowPassword;
import com.company.shenzhou.view.SettingBar;
import com.hanlyjiang.library.fileviewer.FileViewer;
import com.yun.common.utils.DensityUtils;
import com.yun.common.utils.LogUtils;
import com.yun.common.utils.ScreenUtils;
import com.yun.common.utils.SharePreferenceUtil;
import com.yun.common.utils.ToastUtil;
import com.yun.common.utils.popupwindow.PopupWindowTwoButton;
import com.yun.common.utils.popupwindow.PopupWindowVersionTwoButton;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;

/**
 * LoveLin
 * <p>
 * Describe
 */
public class MineFragment extends BaseFragment {
    public static String FILE_DIR = "/sdcard/Downloads/test/";
    @BindView(R.id.bar_mine_change_password)
    SettingBar mChangePassword;
    @BindView(R.id.bar_mine_power_level)
    SettingBar bar_mine_power_level;//????????????
    @BindView(R.id.bar_mine_power_explain)
    SettingBar bar_mine_power_explain;//????????????
    @BindView(R.id.bar_mine_username)
    SettingBar bar_mine_username;
    //    @BindView(R.id.togo)
//    SettingBar togo;
    @BindView(R.id.bar_mine_version)
    SettingBar bar_mine_version;
    @BindView(R.id.bar_mine_use_pace)
    SettingBar bar_mine_use_pace;
    @BindView(R.id.bar_mine_how_use)
    SettingBar bar_mine_how_use;
    @BindView(R.id.bar_mine_exit)
    SettingBar bar_mine_exit;
    @BindView(R.id.linear_all_mine)
    LinearLayout mLinearAll;
    private PopupWindowInputChangeTowPassword changePop;
    private PopupWindowTwoButton exitPop;
    private String popType = "";
    private PopupWindowVersionTwoButton versionPop;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init(ViewGroup rootView) {
        setTitleBarVisibility(View.VISIBLE);
        setTitleLeftBtnVisibility(View.GONE);
        initView();
        setPageStateView();
        showContent();
        responseListener();
        initData();
    }

    private void initData() {
        FILE_DIR = new File(getActivity().getFilesDir(), "test").getAbsolutePath() + File.separator;
        try {
            FileUtils.copyAssetsDir(getActivity(), "test", FILE_DIR);
        } catch (IOException e) {
            Log.e("QbSdk", "IOException:");
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        LogUtils.e("?????????==height==" + ScreenSizeUtil.getScreenHeight(getActivity()));
        LogUtils.e("?????????==width=" + ScreenSizeUtil.getScreenWidth(getActivity()));
        setTitleName("??????");
        String username = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
        //0??????  1??????  2????????????
        int userType = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserType, 0);
        bar_mine_username.setRightText(username + "");
        switch (userType) {
            case 0:
                bar_mine_power_level.setRightText("????????????");
                break;
            case 1:
                bar_mine_power_level.setRightText("????????????");
                break;
            case 2:
                bar_mine_power_level.setRightText("???????????????");
                break;
        }
        String romAvailableSize = FileUtil.getROMAvailableSize(getActivity());
        String romTotalSize = FileUtil.getROMTotalSize(getActivity());
        LogUtils.e("?????????===" + romTotalSize);
        LogUtils.e("????????????===" + romAvailableSize);
        bar_mine_use_pace.setRightText("" + FileUtil.getROMAvailableSize(getActivity()));
    }

    private void responseListener() {
        //????????????
        mChangePassword.setOnClickListener((View v) -> {
            showChangeCurrentUserPasswordPop();
        });
        //?????????????????????
        bar_mine_version.setOnClickListener((View v) -> {
            showPopVersion();
        });
//        togo.setOnClickListener((View v) -> {
//            Intent intent = new Intent(getActivity(), TestVlcPlayerActivity.class);
//            intent.putExtra("mTitle", "?????????????????????!");
//            startActivity(intent);
//        });
        //??????
        bar_mine_exit.setOnClickListener((View v) -> {
            showExitPop();
        });
        //????????????
        bar_mine_power_explain.setOnClickListener((View v) -> {
            startActivity(new Intent(getActivity(), PowerExplainActivity.class));
        });
        //????????????
        bar_mine_how_use.setOnClickListener((View v) -> {
            String fileName = getFilePath("TestPDF.pdf");
            startMuPDFActivityWithExampleFile(fileName);
        });
    }

    @NonNull
    private String getFilePath(String fileName) {
        return new File(FILE_DIR + fileName).getAbsolutePath();
    }

    public void startMuPDFActivityWithExampleFile(String fileName) {
        File file = new File(fileName);
        if (!file.isFile()) {
            ToastUtil.showToastCenter(getActivity(), "???????????????");
            return;
        }
        Uri uri = Uri.fromFile(file);
        FileViewer.startMuPDFActivityByUri(getActivity(), uri);
    }

    ///                                 sdcard/Downloads/test/TestPDF.pdf
    private String getVersionName() {
        // ??????packagemanager?????????
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()???????????????????????????0???????????????????????????
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//??????
            int width = ScreenUtils.getScreenWidth(getActivity()) - DensityUtils.dp2px(getActivity(), 100);
            setLayoutParams(70, width);
        } else {                                                           //??????
            int width = ScreenUtils.getScreenWidth(getActivity()) - DensityUtils.dp2px(getActivity(), 60);
            setLayoutParams(50, width);

        }
    }

    private void showPopVersion() {
        String showCopyrightYear = "";
        String versionName = getVersionName();
        int year = Calendar.getInstance().get(Calendar.YEAR);

        if ("2020".equals(year + "")) {
            showCopyrightYear = "2020";
        } else {
            showCopyrightYear = "2020" + "-" + year;
        }
        popType = "version";
        versionPop = new PopupWindowVersionTwoButton((Activity) getActivity());
        versionPop.getTv_version().setText("?????????" + versionName);
        versionPop.getTv_copyright().setText("????????????(C)???" + showCopyrightYear);
        versionPop.getTv_company().setText("????????????????????????????????????");
        versionPop.getTv_update_date().setText("???????????????2021???5???");
        versionPop.getTv_ok().setText("??????");
        versionPop.getTv_ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionPop.dismiss();
            }
        });
        int screenWidth = ScreenSizeUtil.getScreenWidth(getActivity());
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (screenWidth >= 1600) {
            mParams.leftMargin = 300;
            mParams.rightMargin = 300;
        } else if (screenWidth == 1080) {
            mParams.leftMargin = 130;
            mParams.rightMargin = 130;
        }
        if (versionPop != null) {
            versionPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            versionPop.getLinear_pop_change().setLayoutParams(mParams);
            versionPop.showPopupWindow(mLinearAll, Gravity.CENTER);
        }
    }

    private void setLayoutParams(int i, int width) {
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams.leftMargin = i;
        mParams.rightMargin = i;
        switch (popType) {
            case "exit":
                if (exitPop != null && exitPop.isShowing()) {
                    exitPop.dismiss();
                    exitPop.setWidth(width);
                    exitPop.getLinear_pop_change().setLayoutParams(mParams);
                    exitPop.showPopupWindow(mLinearAll, Gravity.CENTER);
                }
                break;
            case "version":
                if (versionPop != null && versionPop.isShowing()) {
                    versionPop.dismiss();
                    versionPop.setWidth(width);
                    versionPop.getLinear_pop_change().setLayoutParams(mParams);
                    versionPop.showPopupWindow(mLinearAll, Gravity.CENTER);
                }
                break;
            case "change":
                if (changePop != null && changePop.isShowing()) {
                    changePop.dismiss();
                    changePop.setWidth(width);
                    changePop.getLinear_pop_change().setLayoutParams(mParams);
                    changePop.showPopupWindow(mLinearAll, Gravity.CENTER);
                }
                break;
        }
    }


    private void showChangeCurrentUserPasswordPop() {
        popType = "change";
        changePop = new PopupWindowInputChangeTowPassword(getActivity());
        changePop.getMakeSure().setText("??????");
        changePop.getMakeCancle().setText("??????");
        PasswordV2EditText cet_user_old_password = changePop.getCet_user_old_password();
        PasswordV2EditText cet_user_password = changePop.getCet_user_new_password();
        String mCurrentUsername = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
        String mCurrentPassword = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Password, "");
//        current????????????===123  ???????????????
        LogUtils.e("TAG==current????????????===" + mCurrentUsername);
        int mCurrenType = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserType, 0);
//        UserDBRememberBean  UserDBRememberBeanUtils.queryListByMessageToGetPassword(username);
        UserDBRememberBean mBean = UserDBRememberBeanUtils.queryListByMessageToGetPassword(mCurrentUsername);
        ;
        LogUtils.e("TAG==Username===" + mBean.getUsername() + "====password==" + mBean.getPassword() +
                "====Type==" + mBean.getUserType() + "====mBean.getId()==" + mBean.getId());
        changePop.getMakeSure().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???DB??????????????????????????????
                String oldpassword = cet_user_old_password.getText().toString();
                String newpassword = cet_user_password.getText().toString().trim();
                String dbPassword = mBean.getPassword();
                if (oldpassword.equals(dbPassword)) {  //?????????????????????DB???????????????
//                    LogUtils.e("TAG==mCurrentUsername===" + mCurrentUsername);
                    LogUtils.e("TAG==?????????mCurrentUsername===" + mCurrentUsername + "====?????????oldpassword==" + oldpassword +
                            "====mCurrenType==" + mCurrenType + "====mBean.getId()==" + mBean.getId());
                    SharePreferenceUtil.put(getActivity(), SharePreferenceUtil.Current_Username, mCurrentUsername);
                    SharePreferenceUtil.put(getActivity(), SharePreferenceUtil.Current_Password, newpassword);
                    SharePreferenceUtil.put(getActivity(), SharePreferenceUtil.Current_ID, mBean.getId() + "");
                    UserDBRememberBean userRememberBean = new UserDBRememberBean();
                    userRememberBean.setUsername(mCurrentUsername);
                    userRememberBean.setPassword(newpassword);
                    userRememberBean.setUserType(mCurrenType);
                    userRememberBean.setId(mBean.getId());
                    //????????????
                    UserDBRememberBeanUtils.updateData(userRememberBean);
//                    UserDBUtils.insertOrReplaceData(userDBBean);
                    UserDBRememberBean mBean = UserDBRememberBeanUtils.queryListByMessageToGetPassword(mCurrentUsername);
                    LogUtils.e("TAG=??????DB??????????????????=Username===" + mBean.getUsername() + "====password==" + mBean.getPassword()
                            + "====Type==" + mBean.getUserType() + "====mBean.getId()==" + mBean.getId());
                    showToast("????????????");
                } else {
                    showToast("?????????????????????");
                }
                changePop.dismiss();
            }
        });
        changePop.getMakeCancle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????DB??????
                changePop.dismiss();
            }
        });
        changePop.showPopupWindow(mLinearAll, Gravity.CENTER);
    }

    private void showExitPop() {
        popType = "exit";
        exitPop = new PopupWindowTwoButton((Activity) getActivity());
        exitPop.getTv_content().setText("?????????????");
        exitPop.getTv_ok().setText("??????");
        exitPop.getTv_cancel().setText("??????");
        exitPop.getTv_ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceUtil.put(getActivity(), Constants.Is_Logined, false);
                startActivity(new Intent(getActivity(), LoginAnimatorActivity.class));
                String name = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Username, "");
                String password = (String) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_Password, "");
                int type = (int) SharePreferenceUtil.get(getActivity(), SharePreferenceUtil.Current_UserType, 0);
                SharePreferenceUtil.put(getActivity(), Constants.IS_Admin, true);  //????????????????????? ?????????,?????????admin ??????
                LogUtils.e("TAG====??????==username===" + name + "==password==" + password);
                ActivityCollector.removeAll();
                //?????????????????????

//                EventBus.getDefault().post(new setDefaultParametersEvent("default"));


                exitPop.dismiss();
            }
        });
        exitPop.getTv_cancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitPop.dismiss();
            }
        });
        exitPop.showPopupWindow(mLinearAll, Gravity.CENTER);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        setDismissPop();
    }

    public void setDismissPop() {
        if (null != changePop) {
            changePop.dismiss();
        }
        if (null != exitPop) {
            exitPop.dismiss();
        }
        if (null != versionPop) {
            versionPop.dismiss();
        }
    }


}


