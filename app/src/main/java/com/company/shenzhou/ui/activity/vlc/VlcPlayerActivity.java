package com.company.shenzhou.ui.activity.vlc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.company.shenzhou.R;
import com.company.shenzhou.bean.SwitchVideoModel;
import com.company.shenzhou.ui.activity.MainActivity;
import com.company.shenzhou.ui.broadcast.ConnectionChangeReceiver;
import com.company.shenzhou.ui.broadcast.PowerScreenReceiver;
import com.company.shenzhou.utils.CommonUtil;
import com.company.shenzhou.utils.FileUtil;
import com.company.shenzhou.utils.ScreenSizeUtil;
import com.company.shenzhou.utils.VlcUtils;
import com.company.shenzhou.view.ENDownloadView;
import com.company.shenzhou.view.ENPlayView;
import com.company.shenzhou.view.dialog.WaitDialog;
import com.company.shenzhou.view.gsyplayer.SwitchVideoTypeDialog;
import com.company.shenzhou.view.vlc.MyVlcVideoView;
import com.hjq.base.BaseDialog;
import com.pedro.rtplibrary.rtmp.RtmpOnlyAudio;
import com.vlc.lib.RecordEvent;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.MediaListenerEvent;
import com.yun.common.utils.DensityUtils;
import com.yun.common.utils.LogUtils;
import com.yun.common.utils.SharePreferenceUtil;
import com.yun.common.utils.StatusBarUtil;
import com.yun.common.utils.StatusBarUtils;
import com.yun.common.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.videolan.libvlc.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;


/**
 * LoveLin
 * <p>  VLC ????????????
 * Describe
 */
public class VlcPlayerActivity extends AppCompatActivity implements View.OnClickListener, ConnectCheckerRtmp {
    // public static final String path = "http://121.18.168.149/cache.ott.ystenlive.itv.cmvideo.cn:80/000000001000/1000000001000010606/1.m3u8?stbId=005301FF001589101611549359B92C46&channel-id=ystenlive&Contentid=1000000001000010606&mos=jbjhhzstsl&livemode=1&version=1.0&owaccmark=1000000001000010606&owchid=ystenlive&owsid=5474771579530255373&AuthInfo=2TOfGIahP4HrGWrHbpJXVOhAZZf%2B%2BRvFCOimr7PCGr%2Bu3lLj0NrV6tPDBIsVEpn3QZdNn969VxaznG4qedKIxPvWqo6nkyvxK0SnJLSEP%2FF4Wxm5gCchMH9VO%2BhWyofF";
    //public static final String path = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    //public static final String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.0";
    // public static final String path = "rtmp://58.200.131.2:1935/livetv/jxhd";
    //public static final String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";

    public String path = "rtmp://58.200.131.2:1935/livetv/jxhd";
    private final String tag = "VlcPlayer";
    //public String path = "rtmp://ossrs.net/efe/eilfb";
    //private String path = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    private VlcVideoView vlcVideoView;
    private TextView recordStart;
    private TextView change_live;
    private TextView mChangeFull;
    private LinearLayout layout_top, linear_contral;
    private BaseDialog mPusherLoading;
    //private RelativeLayout mRelativeStatue;
    private ImageView lock_screen;
    private TextView error_text;
    private ENPlayView startView;
    private ENDownloadView loading;
    private boolean isFirstLoading = true;
    private List<SwitchVideoModel> mUrlList = new ArrayList<>();
    public boolean isFullscreen = true;
    private boolean isStarting = true;
    private RecordEvent recordEvent = new RecordEvent();
    //?????????
    private int mSourcePosition = 0;
    private String mTypeText = "??????";
    //??????
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "CME");   //???????????? ????????????-????????????/Pictures/
    //private File videoFile = new File(Environment.getExternalStorageDirectory(), "LCare" + ".mp4");
    private String directory = recordFile.getAbsolutePath();
    //???????????????????????????getExternalStorageDirectory
    private String mRecordFilePath = CommonUtil.getSaveDirectory() + File.separator + System.currentTimeMillis() + ".mp4";
    //vlc??????????????????
    private File takeSnapshotFile = new File(Environment.getExternalStorageDirectory(), "CME");
    private boolean isPlayering = false;   //??????????????????????????????
    private TextView snapShot;
    private MyVlcVideoView mPlayerView;
    private ImageView back;
    private String url01;
    private String url02;
    public String mResponse;
    private TextView photos;
    private PowerScreenReceiver receiver;
    private ConnectionChangeReceiver mConnectionReceiver;
    private VlcVideoView vlc_video_view;
    private boolean isOnPauseExit = false;
    private boolean mFlag_Record = false;
    private boolean mFlag_MicOnLine = false;
    private String urlType;
    private TextView tv_current_time;
    private TextView mPusher;
    private TextView mTitle;
    private String mTitleData;
    private String mPusherUrl;
    private String mIp;
    private String mMicPort;
    private String currentUserName;
    private static final int FirstError = 100;
    private static final int Pusher_Start = 101;
    private static final int Pusher_Stop = 102;
    private static final int Record_Start = 103;
    private static final int Record_Stop = 104;
    private static final int Pusher_Error = 105;
    private static final int Send_Toast = 106;
    private static final int Send_UrlType = 107;
    private static final int Type_Loading_Visible = 108;
    private static final int Type_Loading_InVisible = 109;
    private static final int Try_Again_onlin = 116;
    private static final int Show_UrL_Type = 110;
    private static final int Show_Lock = 111;
    private static final int Show_Unlock = 112;
    private static final int Show_Control_InVisible = 113;
    private static final int Show_Control_Visible = 114;
    private static final int Error_Steam = 115;
    private static final int VR_SETTING = 117;
    private static final int PHONE_SETTING = 118;
    private String currentTime = "0";
    private String indexTime = "0";
    private int indexEventIntTime = 0;
    private Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mLastTime = VlcUtils.stringForTime(Integer.parseInt(currentTime));
                    indexEventIntTime = Integer.parseInt(currentTime);
                    tv_current_time.setText("" + mLastTime);
                    currentIntTime.setText("" + indexEventIntTime);
//                    LogUtils.e("TAG" + "??????=======??????===indexEventIntTime===" + indexEventIntTime);
//                    LogUtils.e("TAG" + "??????=======??????===indexIntTime===" + indexIntTime);

                    break;
                case Error_Steam:
                    flagTime = Integer.parseInt(currentIntTime.getText().toString().trim());
                    if (indexEventIntTime == flagTime && indexEventIntTime == indexIntTime) {
                        timer.cancel();
                        mHandler.sendEmptyMessage(Try_Again_onlin);
                    } else {
                        isFirstCommonTime = 0;
                    }

                    LogUtils.e("TAG" + "??????=======??????===indexEventIntTime===" + indexEventIntTime);
                    LogUtils.e("TAG" + "??????=======??????===indexIntTime===" + indexIntTime);
                    LogUtils.e("TAG" + "??????=======??????===flagTime===" + flagTime);
                    break;
                case Send_Toast:
                    ToastUtil.showToastCenter(VlcPlayerActivity.this, (String) msg.obj);
                    break;
                case Pusher_Start:
                    mPusher.setText("??????");
                    mPusher.setTextColor(getResources().getColor(R.color.color_007AFF));
                    Drawable topstart = getResources().getDrawable(R.drawable.icon_mic_pre);
                    mPusher.setCompoundDrawablesWithIntrinsicBounds(null, topstart, null, null);
//                    String replace = mResponse.replace("", "");
//                    int i = replace.indexOf("<body>");
//                    int i1 = replace.lastIndexOf("</body>");
//                    mPusherUrl = mResponse.substring(i + 6, i1);
                    Log.e("TAG", "rtmpCamera3.isStreaming()==response==mPusherUrl========" + mPusherUrl);
                    if (!"".equals(mPusherUrl)) {
                        if (rtmpOnlyAudio.prepareAudio()) {
                            Log.e("TAG", "rtmpCamera3.isStreaming()==pusherStart==????????????=" + mPusherUrl);
                            mPusher.setTag("startStream");
                            rtmpOnlyAudio.startStream(mPusherUrl);
                        } else {
                            startSendToast("Error preparing stream, This device cant do it");
                        }
                    }
                    break;

                case Pusher_Stop:
                    mPusher.setTag("stopStream");
                    rtmpOnlyAudio.stopStream();
                    mPusher.setText("??????");
                    mPusher.setTextColor(getResources().getColor(R.color.white));
                    Drawable topend = getResources().getDrawable(R.drawable.icon_mic_nor);
                    mPusher.setCompoundDrawablesWithIntrinsicBounds(null, topend, null, null);
                    break;
                case Record_Start:
                    mFlag_MicOnLine = true;
                    setTextColor(getResources().getColor(R.color.colorAccent), "??????", false);
                    Drawable record_start = getResources().getDrawable(R.drawable.icon_record_pre);
                    recordStart.setCompoundDrawablesWithIntrinsicBounds(null, record_start, null, null);
                    break;
                case Record_Stop:
                    mFlag_MicOnLine = false;
                    setTextColor(getResources().getColor(R.color.white), "??????", true);
                    ToastUtil.showToastCenter(VlcPlayerActivity.this, "????????????");
                    Drawable record_end = getResources().getDrawable(R.drawable.icon_record_nore);
                    recordStart.setCompoundDrawablesWithIntrinsicBounds(null, record_end, null, null);
                    break;
                case Pusher_Error:
                    mPusher.setText("??????");
                    mPusher.setTextColor(getResources().getColor(R.color.white));
                    Drawable error = getResources().getDrawable(R.drawable.icon_mic_nor);
                    mPusher.setCompoundDrawablesWithIntrinsicBounds(null, error, null, null);
                    break;
                case Send_UrlType:
                    change_live.setText(mTypeText + "");
                    Drawable urlTypeSD = getResources().getDrawable(R.drawable.icon_url_type_sd);
                    Drawable urlTypeHD = getResources().getDrawable(R.drawable.icon_url_type_hd);
                    if ("??????".equals(mTypeText)) {
                        change_live.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeHD, null, null);
                    } else if ("??????".equals(mTypeText)) {
                        change_live.setCompoundDrawablesWithIntrinsicBounds(null, urlTypeSD, null, null);
                    }
                    break;
                case Show_UrL_Type:   //???????????????
                    showSwitchDialog();
                    break;
                case Type_Loading_Visible:   //????????? ??????
                    loading.setVisibility(View.VISIBLE);
                    break;
                case Type_Loading_InVisible: //?????? ?????????
                    loading.setVisibility(View.INVISIBLE);
                    break;
                case Try_Again_onlin: //  ????????????
                    LogUtils.e("path=====Start:=====" + "?????????????????????url====Try_Again_onlin==?????????????????????====????????????==" + "????????????");
                    error_text.setVisibility(View.VISIBLE);
                    startView.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    isFirstCommonTime = 0;
//                    startLive(path);
                    LogUtils.e("path=====Start:=====" + "?????????????????????url====Try_Again_onlin==?????????????????????====????????????==" + "????????????");

                    break;
                case Show_Lock: //??????????????????
                    lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_close_ic));
                    lock_screen.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    layout_top.setVisibility(View.INVISIBLE);
                    mChangeFull.setVisibility(View.INVISIBLE);
                    linear_contral.setVisibility(View.INVISIBLE);
                    tv_current_time.setVisibility(View.INVISIBLE);
                    break;
                case Show_Unlock: //??????????????????
                    lock_screen.setImageDrawable(getResources().getDrawable(R.drawable.video_lock_open_ic));
                    lock_screen.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    layout_top.setVisibility(View.VISIBLE);
                    mChangeFull.setVisibility(View.VISIBLE);
                    linear_contral.setVisibility(View.VISIBLE);
                    tv_current_time.setVisibility(View.VISIBLE);
                    break;
                case Show_Control_InVisible: //???????????????????????????
                    layout_top.setVisibility(View.INVISIBLE);
                    mChangeFull.setVisibility(View.INVISIBLE);
                    linear_contral.setVisibility(View.INVISIBLE);
                    tv_current_time.setVisibility(View.INVISIBLE);
                    break;
                case Show_Control_Visible: //???????????????????????????
                    layout_top.setVisibility(View.VISIBLE);
                    mChangeFull.setVisibility(View.VISIBLE);
                    linear_contral.setVisibility(View.VISIBLE);
                    tv_current_time.setVisibility(View.VISIBLE);
                    break;
                case VR_SETTING: //AR??????
//                    1280*960   AR?????????
                    setCommonOrArMode(1280, 960);
                    break;
                case PHONE_SETTING: //????????????
                    int screenWidth = ScreenSizeUtil.getScreenWidth(VlcPlayerActivity.this);
                    int screenHeight = ScreenSizeUtil.getScreenHeight(VlcPlayerActivity.this);
                    setCommonOrArMode(screenWidth, screenHeight);
                    break;


            }
        }
    };
    //    private ImageView mBackStatue;
    private TextView mTvStatue;  //?????????????????????
    private RtmpOnlyAudio rtmpOnlyAudio;
    private RelativeLayout mRelativeAll;
    private FrameLayout mFrameAll;
    private String mLastTime;
    private TextView currentIntTime;
    private int flagTime;
    private TextView mVRSetting;
    private RelativeLayout relative_live;
    private TextView mPhoneSetting;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????????????????????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //???????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_vlc_player);
        initView();
        initData();
        responseListener();


    }

    private void initData() {
        String name = "??????";
        String name2 = "??????";
        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, url01);
        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, url02);
        mUrlList = new ArrayList<>();
        mUrlList.add(switchVideoModel);
        mUrlList.add(switchVideoModel2);
        mSourcePosition = 0;  //??????
        mTitle.setText("" + mTitleData);
        Log.e("TAG", "mTitleData=====mTitleDatamTitleDatamTitleData===" + mTitleData);

        boolean hd3 = mTitleData.contains("HD3");
        Log.e("TAG", "hd3=====hd3hd3hd3hd3hd3hd3===" + hd3);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void responseListener() {
        mChangeFull.setOnClickListener(this);
        back.setOnClickListener(this);
//        mVRSetting.setOnClickListener(this);
//        mPhoneSetting.setOnClickListener(this);
        recordStart.setOnClickListener(this);
        change_live.setOnClickListener(this);
        lock_screen.setOnClickListener(this);
        snapShot.setOnClickListener(this);
        photos.setOnClickListener(this);
        startView.setOnClickListener(this);
        mPusher.setOnClickListener(this);
        // ?????????????????????????????????????????????,????????????????????????????????????
        registerPowerReceiver();
        registerConnectionReceiver();
//        mRelativeAll.setOnClickListener((View v) -> {
//            //????????????????????????????????????   ????????????
//            if (lock_screen.getVisibility() == View.VISIBLE) {
//                lock_screen.setVisibility(View.INVISIBLE);
//                if (lock_screen.getTag().equals("Lock")) {
//                    mHandler.sendEmptyMessage(Show_Control_InVisible);
//                } else {
//                    mHandler.sendEmptyMessage(Show_Control_InVisible);
//                }
//            } else {
//                lock_screen.setVisibility(View.VISIBLE);
//                if (lock_screen.getTag().equals("Lock")) {
//                    mHandler.sendEmptyMessage(Show_Control_InVisible);
//
//                } else {
//                    mHandler.sendEmptyMessage(Show_Control_Visible);
//
//                }
//            }
//        });
//
//


        vlc_video_view = vlcVideoView.findViewById(R.id.vlc_video_view);
        vlc_video_view.setOnClickListener((View v) -> {
            //????????????????????????????????????   ????????????
            if (lock_screen.getVisibility() == View.VISIBLE) {
                lock_screen.setVisibility(View.INVISIBLE);
                if (lock_screen.getTag().equals("Lock")) {
                    mHandler.sendEmptyMessage(Show_Control_InVisible);
                } else {
                    mHandler.sendEmptyMessage(Show_Control_InVisible);
                }
            } else {
                lock_screen.setVisibility(View.VISIBLE);
                if (lock_screen.getTag().equals("Lock")) {
                    mHandler.sendEmptyMessage(Show_Control_InVisible);

                } else {
                    mHandler.sendEmptyMessage(Show_Control_Visible);

                }
            }
        });
        vlcVideoView.setMediaListenerEvent(new MediaListenerEvent() {
            @Override
            public void eventBuffing(int event, float buffing) {
                if (buffing < 100) {
                    loading.start();
                    mHandler.sendEmptyMessage(Type_Loading_Visible);
                } else if (buffing == 100) {
                    isPlayering = true;
                    loading.release();
                    mHandler.sendEmptyMessage(Type_Loading_InVisible);
                }


//                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventBuffing======" + buffing);

            }

            @Override
            public void eventStop(boolean isPlayError) {
                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventStop======" + isPlayError);
                if (isPlayError) {

                    if (mFlag_Record) { //??????????????????????????????
                        vlcRecordOver();
                        LogUtils.e("path=====Start:=====" + "?????????????????????url===eventStop===?????????????????????====????????????==");

                    }
//                    if (isPlayering && mFlag_Record) {
//                        vlcRecordOver();
//                    }

                    if (mFlag_MicOnLine) {//??????????????????????????????
                        pusherStop("Common");
                        LogUtils.e("path=====Start:=====" + "?????????????????????url===eventStop===?????????????????????====????????????==");

                    }
                    mHandler.sendEmptyMessage(Type_Loading_InVisible);
                    startView.setVisibility(View.VISIBLE);
                    error_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void eventError(int event, boolean show) {
                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventError======" + isPlayering);

                if (isPlayering) {
//                    startLive(path);
                    if (mFlag_Record) {
                        vlcRecordOver();
                    }
                    mHandler.sendEmptyMessage(FirstError);
                } else {
                    isPlayering = false;
                    startView.setVisibility(View.VISIBLE);
                    error_text.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(Type_Loading_InVisible);
//                    loading.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void eventPlay(boolean isPlaying) {
                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventPlay======" + isPlaying);

//                if (hd3){
//                    getIfOffLine();
//                }

                //TODO HD3??????????????????????????????????????????
//                Runtime.getRuntime().gc();  //??????????????????
//                getIfOffLine();

            }

            @Override
            public void eventSystemEnd(String isStringed) {  //?????????????????????
                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventSystemEnd======" + isStringed);
                if ("EndReached".equals(isStringed)) {
                    if (mFlag_Record) { //??????????????????????????????
                        vlcRecordOver();
                        LogUtils.e("path=====Start:=====" + "?????????????????????url======?????????????????????====????????????==");

                    }
//                    if (isPlayering && mFlag_Record) {
//                        vlcRecordOver();
//                    }

                    if (mFlag_MicOnLine) {//??????????????????????????????
                        pusherStop("Common");
                        LogUtils.e("path=====Start:=====" + "?????????????????????url======?????????????????????====????????????==");
                    }

                }
                LogUtils.e("path=====Start:=====" + "?????????????????????url====Try_Again_onlin==?????????????????????====????????????==" + "??????????????????,????????????");

                mHandler.sendEmptyMessageDelayed(Try_Again_onlin, 1000);

            }

            @Override
            public void eventCurrentTime(String time) {
                currentTime = time;
                mHandler.sendEmptyMessageDelayed(0, 1000);
//                mHandler.sendEmptyMessage(0);

            }

            @Override
            public void eventPlayInit(boolean openClose) {
                startView.setVisibility(View.INVISIBLE);
                error_text.setVisibility(View.INVISIBLE);
                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventPlayInit======" + openClose);
                LogUtils.e("path=====Start:=====" + "?????????????????????url======eventPlayInit===url===" + path);

            }

        });


    }


    @SuppressLint("NewApi")
    private void initView() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.black), 0);
        StatusBarUtil.darkMode(this, false);  //?????????????????????????????????
        recordFile.mkdirs();
        mPlayerView = findViewById(R.id.player);
        mPusher = findViewById(R.id.pusher);
        mTitle = findViewById(R.id.tv_top_title);
        relative_live = findViewById(R.id.relative_live);
//        mVRSetting = findViewById(R.id.vr_setting);
//        mPhoneSetting = findViewById(R.id.phone_setting);
        currentIntTime = findViewById(R.id.tv_current_int_time);
        mRelativeAll = findViewById(R.id.activity_vlc_player);
        mFrameAll = findViewById(R.id.ff_all);
        tv_current_time = findViewById(R.id.tv_current_time);
        vlcVideoView = findViewById(R.id.vlc_video_view);
        mChangeFull = findViewById(R.id.change);
        lock_screen = findViewById(R.id.lock_screen);
        photos = findViewById(R.id.photos);
        recordStart = findViewById(R.id.recordStart);
        change_live = findViewById(R.id.change_live);
        layout_top = findViewById(R.id.layout_top);
        linear_contral = findViewById(R.id.linear_contral);
        error_text = findViewById(R.id.error_text);
        error_text.setVisibility(View.INVISIBLE);
        snapShot = findViewById(R.id.snapShot);
        startView = findViewById(R.id.start);
        loading = findViewById(R.id.loading);
        back = findViewById(R.id.back);
        lock_screen.setTag("unLock");
        url01 = getIntent().getStringExtra("url01");
        url02 = getIntent().getStringExtra("url02");
        urlType = getIntent().getStringExtra("urlType");
        mTitleData = getIntent().getStringExtra("mTitle");
        mIp = getIntent().getStringExtra("ip");
        mMicPort = getIntent().getStringExtra("micport");
        LogUtils.e("url==01===" + url01);
        LogUtils.e("url==02===" + url02);
        path = url01;
        mHandler.sendEmptyMessage(Type_Loading_Visible);
//        loading.setVisibility(View.VISIBLE);
//        rtmpCamera3 = new RtmpCamera3(this, this);
        rtmpOnlyAudio = new RtmpOnlyAudio(this);

        int screenWidth = ScreenSizeUtil.getScreenWidth(VlcPlayerActivity.this);
        int screenHeight = ScreenSizeUtil.getScreenHeight(VlcPlayerActivity.this);

        if (true)
        //if (screenWidth == 1280 && screenHeight == 960) {
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//????????????
//        layoutParams.width = width;
//        layoutParams.height = height;
            layoutParams.width = 1280;
            layoutParams.height = 960;
//                    layoutParams.topMargin = -180;
//                    1280*960   AR?????????
            relative_live.setLayoutParams(layoutParams);
        } else {

        }

    }

    @SuppressLint("NewApi")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start://????????????
                startLive(path);
                break;
//            case R.id.vr_setting://VR  ?????????
//
//                mHandler.sendEmptyMessage(VR_SETTING);
//                break;
//            case R.id.phone_setting://??????  ?????????
//                mHandler.sendEmptyMessage(PHONE_SETTING);
//                break;
            case R.id.back://??????
                if (mFlag_Record) {
                    vlcRecordOver();
                }
                vlcVideoView.setAddSlave(null);
                vlcVideoView.onDestroy();
                if (rtmpOnlyAudio.isStreaming()) {
                    rtmpOnlyAudio.stopStream();
                }
                pusherStop("Back");
                if (timer != null) {
                    timer.cancel();
                }
                isFirstCommonTime = 0;
                finish();
                break;
            case R.id.pusher:  //??????
                LogUtils.e("pusherStart====111===" + rtmpOnlyAudio.isStreaming());    //true   ???????????????
                LogUtils.e("pusherStart====222===" + rtmpOnlyAudio.prepareAudio());   //true
                if (!rtmpOnlyAudio.isStreaming()) {
                    if (rtmpOnlyAudio.prepareAudio()) {
                        if (CommonUtil.isFastClick()) {
                            if ("?????????".equals(mTitleData.substring(0, 3))) {
                                if (isPlayering) {
                                    pusherStart();
                                } else {
                                    startSendToast("??????????????????????????????,????????????????????????!");
                                }
                            } else {
                                startSendToast("??????????????????????????????!");
                            }
                        }
                    } else {
                        startSendToast("Error preparing stream, This device cant do it");
                    }
                } else {
                    if (CommonUtil.isFastClick()) {
                        pusherStop("Common");
                    }
                }
                break;
            case R.id.photos:  //????????????
                if (isPlayering) {
                    if (mFlag_Record) { //???????????????????????????
                        vlcRecordOver();
                    }
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivity(intent);
                } else {
                    startSendToast("??????????????????????????????????????????");
                }
                break;
            case R.id.lock_screen:  //??????
                LogUtils.e("TAG" + "===lock_screen==????????????====");
                if (lock_screen.getTag().equals("unLock")) {
                    lock_screen.setTag("Lock");
                    mHandler.sendEmptyMessage(Show_Lock);
                } else {
                    lock_screen.setTag("unLock");
                    mHandler.sendEmptyMessage(Show_Unlock);

                }
                break;
            case R.id.change: //????????????
                //ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,//????????????
                //ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//????????????
                //ActivityInfo.SCREEN_ORIENTATION_USER,//??????????????????
                //ActivityInfo.SCREEN_ORIENTATION_NOSENSOR,//??????????????????
                //ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,//??????????????????
                //ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,//??????????????????
                //ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,//????????????????????????
                isFullscreen = !isFullscreen;
                if (isFullscreen) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); //??????????????????
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//??????????????????
                }
                break;

            case R.id.change_live: //???????????????
                if (!"02".equals(urlType)) {
                    mHandler.sendEmptyMessage(Show_UrL_Type);
                } else {
                    startSendToast("url???????????????????????????!");
                }
                break;
            case R.id.recordStart: //??????
                if (isPlayering) {
                    if (isStarting && vlcVideoView.isPrepare()) {
                        mFlag_Record = true;
                        mHandler.sendEmptyMessage(Record_Start);
//                        vlcVideoView.getMediaPlayer().record(directory);
                        LogUtils.e("path=====??????--??????:=====" + directory); //   /storage/emulated/0/1604026573438.mp4
                        recordEvent.startRecord(vlcVideoView.getMediaPlayer(), directory, "cme.mp4");
                    } else {
                        vlcRecordOver();
                    }
                } else {
                    startSendToast("????????????????????????????????????!");
                }
                break;
            case R.id.snapShot://??????
                if (isPlayering) {
                    if (vlcVideoView.isPrepare()) {
                        Media.VideoTrack videoTrack = vlcVideoView.getVideoTrack();
                        if (videoTrack != null) {
                            //vlcVideoView.getMediaPlayer().updateVideoSurfaces();
                            startSendToast("????????????");
                            //??????
                            LogUtils.e("path=====????????????:=====" + takeSnapshotFile.getAbsolutePath()); //   /storage/emulated/0/1604026573438.mp4
                            File localFile = new File(takeSnapshotFile.getAbsolutePath());
                            if (!localFile.exists()) {
                                localFile.mkdir();
                            }
                            recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), 0, 0);
                            //???????????? ?????????????????????????????????
                            MediaStore.Images.Media.insertImage(getContentResolver(), vlcVideoView.getBitmap(), "", "");
                            //???????????????
                            //recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
                        }
                    }
                    //?????????????????? ??????Bitmap?????????
                    //thumbnail.setImageBitmap(vlcVideoView.getBitmap());
                    //Bitmap bitmap = vlcVideoView.getBitmap();
                    //saveBitmap("", bitmap);
                } else {
                    startSendToast("????????????????????????????????????!");
                }
                break;
        }
    }

    private void startSendToast(String toastStr) {
        Message tempMsg = mHandler.obtainMessage();
        tempMsg.what = Send_Toast;
        tempMsg.obj = toastStr;
        mHandler.sendMessage(tempMsg);
    }


    private void pusherStop(String Type) {
        try {
            mPusherLoading = new WaitDialog.Builder(this)
                    // ??????????????????????????????
                    .setMessage(getString(R.string.common_stop_loading))
                    .show();
            LogUtils.e("???????????????====pusher====??????-????????????===" + "http://" + mIp + ":" + mMicPort + "/stop");
            OkHttpUtils.get()
//                .url("http://192.168.64.13:7789/stop")
                    .url("http://" + mIp + ":" + mMicPort + "/stop")
                    .addParams("username", currentUserName + "_??????")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            mPusherLoading.dismiss();
                            LogUtils.e("???????????????====pusher====??????-????????????=====" + "onError=====e======" + e);

                            if (!"Back".equals(Type)) {
                                startSendToast("??????????????????: 500");
                            }
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            mPusherLoading.dismiss();
                            LogUtils.e("???????????????====pusher====??????-????????????=====" + "onResponse===" + response);
                            mHandler.sendEmptyMessage(Pusher_Stop);
                        }
                    });
        } catch (Exception e) {
            LogUtils.e("???????????????====pusher====??????-????????????=====" + "Exception===" + e);

            mPusherLoading.dismiss();
        }
    }

    //1.???????????????http://ip:7789/start?username=xxx
    //2.???????????????http://ip:7789/stop?username=xxx
    @SuppressLint("NewApi")
    private void pusherStart() {
        try {
            LogUtils.e("???????????????====pusher====??????????????????===" + "http://" + mIp + ":" + mMicPort + "/start");
            /**
             * ????????????
             * ??????????????????: 300  html??????????????????
             * ??????????????????: 500  ?????????????????????
             * ????????????
             * ??????????????????: 500 ?????????????????????
             */
            Log.e("TAG", "????????????====pusher====??????????????????===" + "http://" + mIp + ":" + mMicPort + "/start");

            currentUserName = (String) SharePreferenceUtil.get(this, SharePreferenceUtil.Current_Username, "??????");
            mPusherLoading = new WaitDialog.Builder(this)
                    // ??????????????????????????????
                    .setMessage(getString(R.string.common_loading))
                    .show();
            OkHttpUtils.get()
                    .url("http://" + mIp + ":" + mMicPort + "/start")
                    .addParams("username", currentUserName + "_??????")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            mPusherLoading.dismiss();
                            LogUtils.e("????????????====pusher====??????????????????===" + "onError=====e======" + e);

                            startSendToast("??????????????????: 500");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            LogUtils.e("????????????====pusher====??????????????????===onResponse====" + response);
//                            <html><head><title></title></head><body>rtmp://192.168.128.134:8350/live/94726</body></html>
                            String replace = response.replace("", "");
                            int i = replace.indexOf("<body>");
                            int i1 = replace.lastIndexOf("</body>");
                            mPusherUrl = response.substring(i + 6, i1);
                            mPusherLoading.dismiss();
                            if (-1 != mPusherUrl.indexOf("null")) { //??????????????????url
                                startSendToast("??????????????????: 300");
                            } else {
                                mHandler.sendEmptyMessage(Pusher_Start);
                            }
                        }
                    });
        } catch (Exception e) {
            LogUtils.e("????????????====pusher====??????????????????===Exception====" + e);

            mPusherLoading.dismiss();
            startSendToast("????????????????????????!");
        }
    }

    /**
     * ????????????
     *
     * @param path
     */
    private void startLive(String path) {
        LogUtils.e("path=====--startLive:=====" + path);
        vlcVideoView.setPath(path);
        vlcVideoView.startPlay();
        error_text.setVisibility(View.INVISIBLE);
        mHandler.sendEmptyMessage(Type_Loading_Visible);
        loading.start();
        currentIntTime.setText("1");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("path=====??????--onResume:=====");
        LogUtils.e("path=====??????--onResume=path:=====" + path);
        isOnPauseExit = false;
        isFirstCommonTime = 0;
        startLive(path);
        vlcVideoView.startPlay();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        path = url01;
        //????????????stop ?????????ANR
        vlcVideoView.onStop();
        mHandler.sendEmptyMessage(Type_Loading_InVisible);
        loading.release();
        isOnPauseExit = true;
        if (timer != null) {
            timer.cancel();
        }
        isFirstCommonTime = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("path=====??????--onDestroy:=====");
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (mConnectionReceiver != null) {
            unregisterReceiver(mConnectionReceiver);
        }
        //??????????????????
        vlcVideoView.setAddSlave(null);
        vlcVideoView.onDestroy();

        // ????????????!??????
        if (null != mPusher) {
            if ("startStream".equals(mPusher.getTag())) {
                mHandler.sendEmptyMessage(Pusher_Stop);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //??????????????????
        LogUtils.e("path=====??????--onStop:=====");
        vlcVideoView.setAddSlave(null);
        vlcVideoView.onStop();
        mHandler.sendEmptyMessage(Type_Loading_InVisible);
        loading.release();
    }

    private void vlcRecordOver() {
        mFlag_Record = false;
        mHandler.sendEmptyMessage(Record_Stop);
        vlcVideoView.getMediaPlayer().record(null);
//        FileUtil.scanFile(VlcPlayerActivity.this, directory);
        FileUtil.RefreshAlbum(directory, true, this);
    }

    public void setTextColor(int color, String message, boolean isStarting) {
        recordStart.setText(message);
        recordStart.setTextColor(color);
        this.isStarting = isStarting;
    }

    /**
     * ????????????
     */
    public final MediaScannerConnection msc = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
        public void onMediaScannerConnected() {
            msc.scanFile("/sdcard/image.jpg", "image/jpeg");
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.v("TAG", "scan completed");
            msc.disconnect();
        }
    });

    /**
     * ?????????????????????
     */
    private void showSwitchDialog() {
        SwitchVideoTypeDialog switchVideoTypeDialog = new SwitchVideoTypeDialog(this);
        switchVideoTypeDialog.initList(mUrlList, new SwitchVideoTypeDialog.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {  //position==0,name==??????
                final String name = mUrlList.get(position).getName();
                LogUtils.e("'switchVideoModel===position==" + position);
                LogUtils.e("'switchVideoModel===name==" + name);
                if (mSourcePosition != position) {  //???????????????
                    mTypeText = name;
                    mSourcePosition = position;
                    mHandler.sendEmptyMessage(Send_UrlType);
                    mHandler.sendEmptyMessage(Pusher_Stop);
                    //????????????????????????
                    if ("startStream".equals(mPusher.getTag())) {
                        pusherStop("Common");
                    }
                    if (isPlayering && mFlag_Record) {
                        mFlag_Record = false;
                        mHandler.sendEmptyMessage(Record_Stop);
//                        setTextColor(getResources().getColor(R.color.colorAccent), "??????", false);
//                        Drawable record_start = getResources().getDrawable(R.drawable.icon_record_pre);
//                        recordStart.setCompoundDrawablesWithIntrinsicBounds(null, record_start, null, null);
                        startSendToast("????????????!");
                        vlcVideoView.getMediaPlayer().record(null);
                        FileUtil.scanFile(VlcPlayerActivity.this, directory);
                    }
                    startLive(mUrlList.get(position).getUrl());
                    startSendToast("" + name);
                } else {
                    startSendToast("????????? " + name);
                }
            }
        });
        switchVideoTypeDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//??????
            Drawable record_end = getResources().getDrawable(R.drawable.nur_ic_fangxiao);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);
        } else {
            Drawable record_end = getResources().getDrawable(R.drawable.nur_ic_fangda);
            mChangeFull.setCompoundDrawablesWithIntrinsicBounds(record_end, null, null, null);//??????
        }
    }

    //wifi??????????????????????????????
    private void registerConnectionReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mConnectionReceiver = new ConnectionChangeReceiver();
        mConnectionReceiver.setOnWifiListener(new ConnectionChangeReceiver.onWifiListener() {
            @Override
            public void wifiOff() {
                if (isPlayering && mFlag_Record) {
                    vlcRecordOver();
                }
            }

            @Override
            public void wifiOn() {

            }
        });
        this.registerReceiver(mConnectionReceiver, filter);
    }

    //?????????????????????
    private void registerPowerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        receiver = new PowerScreenReceiver();
        receiver.setOnScreenListener(new PowerScreenReceiver.onScreenListener() {
            @Override
            public void screenOff() {
                if (isPlayering && mFlag_Record) {
                    LogUtils.e("path=====??????--screenOff:=====");
                    vlcRecordOver();
                }
            }
        });
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onConnectionSuccessRtmp() {
        Log.e("TAG", "RtmpOnlyAudio=====onConnectionSuccessRtmp");
        startSendToast("???????????? ");
    }

    @Override
    public void onConnectionFailedRtmp(String reason) {
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Log.e("TAG", "RtmpOnlyAudio=====" + reason);
                Log.e("TAG", "RtmpOnlyAudio=====onConnectionFailedRtmp");
                startSendToast("Connection failed. " + reason);
                rtmpOnlyAudio.stopStream();
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("TAG", "RtmpOnlyAudio=====onDisconnectRtmp");
                if (!isOnPauseExit) {
                    startSendToast("?????????????????????");
                }
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {
    }


    Timer timer;
    //    String beaginTime = "";
    int indexIntTime = 0;

    /**
     * ???????????????
     * ??????HD3??????????????????????????????????????????????????????
     */
    public void getIfOffLine() {
        //??????java???Timer???
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                flagTime = Integer.parseInt(currentIntTime.getText().toString().trim());
                LogUtils.e("TAG" + "??????==?????????===mLastTime====" + mLastTime);
                LogUtils.e("TAG" + "??????==?????????===indexIntTime====" + indexIntTime);
                LogUtils.e("TAG" + "??????==?????????===flagTime====" + flagTime);
                LogUtils.e("TAG" + "??????==?????????===indexEventIntTime===" + indexEventIntTime);
                if (indexEventIntTime == flagTime && indexEventIntTime == indexIntTime) {
                    LogUtils.e("TAG" + "??????===??????===");
                    LogUtils.e("TAG" + "??????===??????===");
                    if (3 == isFirstCommonTime) {
//                        timer.cancel();
//                        mHandler.sendEmptyMessage(Error_Steam);
                        mHandler.sendEmptyMessageDelayed(Error_Steam, 1011);
                        LogUtils.e("TAG" + "??????==???-2-?????????=====??????===");

                    } else {
                        int i = ++isFirstCommonTime;
                        LogUtils.e("TAG" + "??????==???" + i + "?????????===== isFirstCommonTime++==??????==");
                    }

                }
                if (indexEventIntTime != flagTime) {
                    LogUtils.e("TAG" + "??????===?????????===");
                    LogUtils.e("TAG" + "??????===?????????===");

                } else {
                    indexIntTime = flagTime;
                    LogUtils.e("TAG" + "??????===?????????????????????==indexIntTime===" + indexIntTime);
                    LogUtils.e("" + "??????------------------------------------------------");
                    LogUtils.e("" + "??????------------------------------------------------");
                }


            }
        };
        timer.schedule(task, 333, 3000);
    }

    //?????????????????????????????????true    ???????????????????????????
    private int isFirstCommonTime = 0;


    public void setCommonOrArMode(int width, int height) {
        int screenWidth = ScreenSizeUtil.getScreenWidth(VlcPlayerActivity.this);
        int screenHeight = ScreenSizeUtil.getScreenHeight(VlcPlayerActivity.this);
        LogUtils.e("ScreenSizeUtil==========screenWidth=====" + screenWidth);
        LogUtils.e("ScreenSizeUtil==========screenHeight====" + screenHeight);
        int widthc = relative_live.getWidth();
        int heightc = relative_live.getHeight();
        LogUtils.e("ScreenSizeUtil=====C=====widthc=====" + widthc);
        LogUtils.e("ScreenSizeUtil=====C=====widthc=====" + heightc);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//????????????
        layoutParams.width = width;
        layoutParams.height = height;
//                    1280*960   AR?????????
        relative_live.setLayoutParams(layoutParams);
        int widthB = relative_live.getWidth();
        int heightB = relative_live.getHeight();


        int widthAAAA = mRelativeAll.getWidth();
        int heightAAAA = mRelativeAll.getHeight();
        LogUtils.e("ScreenSizeUtil=====A=====widthAAAA=====" + widthAAAA);
        LogUtils.e("ScreenSizeUtil=====A=====heightAAAA=====" + heightAAAA);

        LogUtils.e("ScreenSizeUtil=====B=====widthB=====" + widthB);
        LogUtils.e("ScreenSizeUtil=====B=====heightB=====" + heightB);

    }

}
