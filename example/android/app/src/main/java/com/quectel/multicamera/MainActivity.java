/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.demo.gpiodlib.NativeGpioD;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.PreviewParams;
import com.quectel.multicamera.utils.RecorderParams;
import com.quectel.multicamera.utils.ShellUtils;
import com.quectel.multicamera.utils.VoldReceiver;
import com.quectel.qcarapi.cb.IQCarCamInStatusCB;
import com.quectel.qcarapi.helper.QCarCamInDetectHelper;
import com.quectel.qcarapi.stream.QCarAudio;
import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.osd.QCarOsd;
import com.quectel.qcarapi.image.QCarPicWriter;
import com.quectel.qcarapi.util.QCarLog;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements IQCarCamInStatusCB {
    private static String TAG = "MainActivity";

    public int[] isPreviw = new int[8];

    public static int CSI1_IPUTNUM = 0;
    public static int CSI2_IPUTNUM = 0;
    public static int CAMERANUM = 4;
    public static int csi1InputType = 0;
    public static int csi2InputType = 4;

    /******UI Component****/
    Button srButton = null;  //开启录像的Button
    /******UI Component****/

    /*****Record Service && Vold Receiver******/
    private VoldReceiver vReceiver;  // 广播接收器，接受SD卡插拔状态信息
    private RecordService service = null;
    private boolean isBind = false;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            RecordService.RecordBinder myBinder = (RecordService.RecordBinder) binder;
            service = myBinder.getService();
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "ActivityA - onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "ActivityA - onServiceDisconnected");
        }
    };
    /*****Record Service && Vold Receiver******/

    private boolean m_bTakePicFinshFlag = true;
    private QCarCamInDetectHelper detectInsert;

    public static int csi1phy_num = -1;
    public static int csi2phy_num = -1;

    private PreviewParams pParams = GUtilMain.getPreviewParams();
    private RecorderParams rParams = GUtilMain.getRecorderParams();
    private RecorderParams rMParams = GUtilMain.getMergeRecorderParams();

    /******Main OSD: preview stream && recoder Stream*****/
    private boolean bOsdFlag = false;
    /******Main OSD: preview stream && recoder Stream*****/

    public static int i = 0;

    private int previewCSI;

    private static void addGPUTask(int taskNum) {
        for (int i = 0; i < taskNum; i++) {
            new Thread() {
                float f1 = 27.177177797f;
                float f2 = 172.14038401131414f;
                float f3 = 13.1231231f;

                @Override
                public void run() {
                    while (true) {
                        float f = f1 * f2 * f3;
                    }
                }
            }.start();
        }
    }

    private LinearLayout mLin1, mLin2, mLin3;
    private FrameLayout container1_0, container1_1, container1_2, container1_3, container2_0, container2_1;
    private boolean isGpio = true;
    private int amplifyChannel;
    private int previewNum;
    private boolean isHand;
    private ScreenReceiver screenReceiver;
    private Button take_pic_button = null, start_lock_video_button = null, settings_button = null;
    private boolean button_visible = true;

    static {
        System.loadLibrary("mmqcar_qcar_jni");
        if (Build.VERSION.SDK_INT == 28) {
            GUtilMain.MEDIA_OUTPUT_FORMAT_TS = 4;
        } else if (Build.VERSION.SDK_INT == 25) {
            GUtilMain.MEDIA_OUTPUT_FORMAT_TS = 2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GUtilMain.setqContext(getApplicationContext());
        Log.d("qwe", "onCreate: ");
        NativeGpioD.setGpioStatus(41, 1);

        //startAadasAndDms();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("com.quectel.multicamera.RecordService.onclick");
        registerReceiver(mReceiver, filter2);

        srButton = (Button) findViewById(R.id.srButton);
        mLin1 = (LinearLayout) findViewById(R.id.lin1);
        mLin2 = (LinearLayout) findViewById(R.id.lin2);
        mLin3 = (LinearLayout) findViewById(R.id.lin3);
        container1_0 = (FrameLayout) findViewById(R.id.container1_0);
        container1_1 = (FrameLayout) findViewById(R.id.container1_1);
        container1_2 = (FrameLayout) findViewById(R.id.container1_2);
        container1_3 = (FrameLayout) findViewById(R.id.container1_3);
        container2_0 = (FrameLayout) findViewById(R.id.container2_0);
        container2_1 = (FrameLayout) findViewById(R.id.container2_1);
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "预览个数" + pParams.getPreviewNum());
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "录像个数" + rParams.getRecorderNums());
        previewNum = pParams.getPreviewNum();
        if (previewNum == 1) {
            mLin2.setVisibility(View.GONE);
            mLin3.setVisibility(View.GONE);
            container1_1.setVisibility(View.GONE);
        } else if (previewNum == 2) {
            mLin2.setVisibility(View.GONE);
            mLin3.setVisibility(View.GONE);
        } else if (previewNum == 4) {
            mLin3.setVisibility(View.GONE);
        }

        csi1InputType = -1;
        csi2InputType = -1;
        csi1phy_num = -1;
        csi2phy_num = -1;

        if (pParams.getPreviewNum() >= rParams.getRecorderNums()) {
            if (pParams.getPreviewNum() <= CAMERANUM) {
                CSI1_IPUTNUM = pParams.getPreviewNum();
                CSI2_IPUTNUM = 0;
            } else {
                CSI1_IPUTNUM = CAMERANUM;
                if (pParams.getPreviewNum() - CAMERANUM < CAMERANUM)
                    CSI2_IPUTNUM = pParams.getPreviewNum() - CAMERANUM;
                else
                    CSI2_IPUTNUM = CAMERANUM;
            }
        } else {
            if (rParams.getRecorderNums() <= CAMERANUM) {
                CSI1_IPUTNUM = rParams.getRecorderNums();
                CSI2_IPUTNUM = 0;
            } else {
                CSI1_IPUTNUM = CAMERANUM;
                if (rParams.getRecorderNums() - CAMERANUM < CAMERANUM)
                    CSI2_IPUTNUM = rParams.getRecorderNums() - CAMERANUM;
                else
                    CSI2_IPUTNUM = CAMERANUM;
            }
        }

        for (i = 0; i < 8; i++) {
            if (pParams.getPreviewNum() <= CAMERANUM) {
                if (i < pParams.getPreviewNum())
                    isPreviw[i] = 1;
                else
                    isPreviw[i] = 0;
            } else {
                if (i < CAMERANUM) {
                    isPreviw[i] = 1;
                } else if ((i - CAMERANUM) >= 0 && (i - CAMERANUM) < CAMERANUM && ((i - 4) < (pParams.getPreviewNum() - CAMERANUM))) {
                    isPreviw[i] = 1;
                } else {
                    isPreviw[i] = 0;
                }
            }

            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " recorder nums = " + rParams.getRecorderNums() + ", isPreviw[" + i + "] =" + isPreviw[i]);
        }

        csi1phy_num = 1;
        csi1InputType = pParams.getInput1TypeNum();
        openInitCamera(csi1phy_num, pParams.getPreviewNum() == 2 ? 2 : 4, csi1InputType);
        addCameraDetect(csi1phy_num, pParams.getPreviewNum() == 2 ? 2 : 4);
        if (previewNum>4) {
            csi2phy_num = 2;
            csi2InputType = pParams.getInput2TypeNum();
            openInitCamera(csi2phy_num, pParams.getPreviewNum() == 2 ? 2 : 4, csi2InputType);
            addCameraDetect(csi2phy_num, pParams.getPreviewNum() == 2 ? 2 : 4);
        }

        detectInsert.startDetectThread(); // 启动热插拔检测线程

        //注册异常回调
        addMainOsd();
        startShow();

//        //detection gpio
        if (previewNum > 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isGpio) {
                        if (!isHand) {
                            if (previewNum == 2) {
                                judge(1, 85);
                                judge(2, 43);
                            }
                            if (previewNum == 4) {
                                judge(1, 85);
                                judge(2, 43);
                                judge(3, 119);
                                judge(4, 44);
                            }
                            if (previewNum == 6) {
                                judge(1, 85);
                                judge(2, 43);
                                judge(3, 119);
                                judge(4, 44);
                                judge(5, 22);
                                judge(6, 125);
                            }
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }

        container1_0.setOnClickListener(view -> {
            if (pParams.getPreviewNum() != 1) {
                hand(1);
            }
        });
        container1_1.setOnClickListener(view -> {
            hand(2);
        });
        container1_2.setOnClickListener(view -> {
            hand(3);
        });
        container1_3.setOnClickListener(view -> {
            hand(4);
        });
        container2_0.setOnClickListener(view -> {
            hand(5);
        });
        container2_1.setOnClickListener(view -> {
            hand(6);
        });

        startMirrorImage();
        if (rParams.getRecordState()) {
            if (getStoragePath(MainActivity.this, true) != null && getStoragePath(MainActivity.this, true).contains("storage"))
                new Handler().postDelayed(mStartRecordService, 1000);
            else
                Toast.makeText(getApplicationContext(), "SD card is not available", Toast.LENGTH_SHORT).show();
        }

        take_pic_button = (Button) findViewById(R.id.takePic);
        start_lock_video_button = (Button) findViewById(R.id.startLockVideo);
        settings_button = (Button) findViewById(R.id.setting);
    }

    private boolean isRecord = false;

    private Runnable mStartRecordService = new Runnable() {
        @Override
        public void run() {
            startVideoRecoder();
            isRecord = true;
        }
    };

    private void hand(int channel) {
        if (amplifyChannel == 0) {
            isHand = true;
            amplifyChannel = channel;
            amplify(amplifyChannel);
        } else if (amplifyChannel == channel) {
            isHand = false;
            amplifyChannel = 0;
            amplify(amplifyChannel);
        }
    }

    private void judge(int channel, int gpio) {
        if (NativeGpioD.getGpioStatus(gpio) == 1) {
            if (amplifyChannel != channel) {
                amplifyChannel = channel;
                amplify(amplifyChannel);
            }
        } else {
            if (amplifyChannel == channel) {
                amplifyChannel = 0;
                amplify(amplifyChannel);
            }
        }
    }

    private void openInitCamera(int csiNum, int inputNum, int inputType) {
        int count = 0;
        QCarCamera qCarCamera = GUtilMain.getQCamera(csiNum);
        while (count < 10) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " csiNum = " + csiNum + " inputNum = " + inputNum + " inputType = " + inputType);
            int ret = qCarCamera.cameraOpen(inputNum, inputType);
            if (ret == 0) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " Open csi " + csiNum + " Success");
                break;
            } else {
                count++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, " Open Failed, cameraOpen csi " + csiNum + " return = " + ret);
            }
        }
        qCarCamera.registerOnErrorCB(GUtilMain.getErrorHandler());
    }

    // 初始化Camera热插拔状态检测函数
    private void addCameraDetect(int csiNum, int inputNum) {
        QCarCamInDetectHelper.InputParam inputParam = new QCarCamInDetectHelper.InputParam();
        inputParam.detectTime = 800;  // 800ms
        inputParam.inputNum = inputNum;
        inputParam.qCarCamera = GUtilMain.getQCamera(csiNum);
        detectInsert = QCarCamInDetectHelper.getInstance(this);
        detectInsert.setInputParam(inputParam);
    }

    @Override
    public void statusCB(int csi_num, int channel_num, int detectResult, boolean isInsert) {
        // 返回热插拔状态
        Log.e("QCarDetectCamInsert", "csi_num = "+ csi_num + ", channel_num = " + channel_num + ", isInsert = "+isInsert);
    }

    private void addMainOsd() {
        new Thread(new Runnable() {
            Date date;
            String dateStr, dateStr2;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

            SimpleDateFormat sdf2 = new SimpleDateFormat("HH-mm-ss");
            int tIndex1 = -1;
            int tIndex2 = -1;
            String font_path = "/system/fonts/Song.ttf";
            int font_size = 4;
            QCarOsd qCarOsd1, qCarOsd2;

            @Override
            public void run() {
                qCarOsd1 = new QCarOsd();
                qCarOsd1.setOsdResolution(1280, 720);
                qCarOsd1.initOsd(font_path.getBytes(), font_size);
                qCarOsd1.setOsdColor(72, 170, 120);
                qCarOsd2 = new QCarOsd();
                qCarOsd2.setOsdResolution(1280, 720);
                qCarOsd2.initOsd(font_path.getBytes(), font_size);
                qCarOsd2.setOsdColor(72, 170, 120);

                if (csi1phy_num >= 0) {
                    GUtilMain.getQCamera(csi1phy_num).setMainOsd(qCarOsd1);
                }

                if (csi2phy_num >= 0) {
                    GUtilMain.getQCamera(csi2phy_num).setMainOsd(qCarOsd2);
                }
                bOsdFlag = true;
                for (int i = 0; i < pParams.getPreviewNum(); i++) {
                    if (i < CAMERANUM) {
                        qCarOsd1.setOsd(i, (i + "channel").getBytes(), -1, 640, 48);
                    } else {
                        qCarOsd2.setOsd(i == 4 ? 0 : 1, (i + "channel").getBytes(), -1, 640, 48);
                    }
                }

                while (bOsdFlag) {
                    date = new Date();
                    dateStr = sdf.format(date);
                    dateStr2 = sdf2.format(date);

                    tIndex1 = qCarOsd1.setOsd(-1, dateStr.getBytes(), tIndex1, 32, 48);

                    tIndex2 = qCarOsd2.setOsd(-1, dateStr.getBytes(), tIndex2, 32, 48);
                    try {
                        Thread.sleep(980); // 时间字幕刷新频率
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }

                qCarOsd1.deinitOsd();
                qCarOsd2.deinitOsd();
            }
        }).start();
    }

    public void startCollision(View view) {
        RecoderInstance.getInstance().startCollision();
        Toast.makeText(getApplicationContext(), "Start Collision", Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.quectel.multicamera.RecordService.onclick")) {
                isRecord = false;
                RecoderInstance.getInstance().stopRecorder();
                stopRecordService();
            }
        }
    };

    public void startLockVideo(View view) {
        if (getStoragePath(MainActivity.this, true) != null && getStoragePath(MainActivity.this, true).contains("storage")) {
            if (isRecord) {
                RecoderInstance.getInstance().startLockVideo();
                Toast.makeText(getApplicationContext(), "Start LockVideo", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Please record video first", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "SD card is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        int pCsiNum = csi1phy_num;
        if (csi1phy_num < 0) {
            pCsiNum = csi2phy_num;
        }
        SurfaceViewFragment instance_1 = new SurfaceViewFragment(GUtilMain.getQCamera(pCsiNum), 0, isPreviw[0]);
        instance_1.setPreviewSize(pParams.getWidth(1), pParams.getHeight(1));

        SurfaceViewFragment instance_2 = new SurfaceViewFragment(GUtilMain.getQCamera(pCsiNum), 1, isPreviw[1]);
        instance_2.setPreviewSize(pParams.getWidth(2), pParams.getHeight(2));

        SurfaceViewFragment instance_3 = new SurfaceViewFragment(GUtilMain.getQCamera(pCsiNum), 2, isPreviw[2]);
        instance_3.setPreviewSize(pParams.getWidth(3), pParams.getHeight(3));

        SurfaceViewFragment instance_4 = new SurfaceViewFragment(GUtilMain.getQCamera(pCsiNum), 3, isPreviw[3]);
        instance_4.setPreviewSize(pParams.getWidth(4), pParams.getHeight(4));

        SurfaceViewFragment instance_5 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 0, isPreviw[4]);
        instance_5.setPreviewSize(pParams.getWidth(5), pParams.getHeight(5));

        SurfaceViewFragment instance_6 = new SurfaceViewFragment(GUtilMain.getQCamera(csi2phy_num), 1, isPreviw[5]);
        instance_6.setPreviewSize(pParams.getWidth(6), pParams.getHeight(6));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container1_0, instance_1)
                .replace(R.id.container1_1, instance_2)
                .replace(R.id.container1_2, instance_3)
                .replace(R.id.container1_3, instance_4)
                .replace(R.id.container2_0, instance_5)
                .replace(R.id.container2_1, instance_6)
                .commit();


    }

    public void startShow() {
        initView();
    }

    public void startVideoRecoder() {
        if (getStoragePath(MainActivity.this, true) != null && getStoragePath(MainActivity.this, true).contains("storage")) {
            startRecordService();
            if (rParams.getRecorderNums() > 0) {
                RecoderInstance.getInstance().initRecoder(csi1phy_num, csi2phy_num);
                RecoderInstance.getInstance().startRecorder();
            }
        } else {
            Toast.makeText(getApplicationContext(), "SD card is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecordService() {
        Intent recordService = new Intent(GUtilMain.getqContext(), RecordService.class);
        startService(recordService);
        registerStopVoldReceiver();
    }

    private void stopRecordService() {
        Intent recordService = new Intent(GUtilMain.getqContext(), RecordService.class);
        stopService(recordService);

        unRegisterStopVoldReceiver();
    }

    private void closeCamera() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "closeCamera csi1phy_num = " + csi1phy_num + " csi2phy_num = " + csi2phy_num);
        Thread camCloseThread0 = null;
        Thread camCloseThread1 = null;

        if (csi1phy_num >= 0) {  //N4配置
            camCloseThread0 = new Thread(new Runnable() {
                @Override
                public void run() {
                    GUtilMain.getQCamera(csi1phy_num).cameraClose();  // 关闭ais_server，必须保证最后关闭
                    GUtilMain.getQCamera(csi1phy_num).release();  // 关闭ais_server，必须保证最后关闭
                    GUtilMain.removeQCamera(csi1phy_num);
                }
            });
            camCloseThread0.start();  // 关闭ais_server，必须保证最后关闭
        }

        if (csi2phy_num >= 0) {
            camCloseThread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    GUtilMain.getQCamera(csi2phy_num).cameraClose(); // 关闭ais_server，必须保证最后关闭
                    GUtilMain.getQCamera(csi2phy_num).release();  // 关闭ais_server，必须保证最后关闭
                    GUtilMain.removeQCamera(csi2phy_num);
                }
            });
            camCloseThread1.start();  // 关闭ais_server，必须保证最后关闭
        }

        try {
            if (camCloseThread0 != null) {
                camCloseThread0.join();
            }
            if (camCloseThread1 != null) {
                camCloseThread1.join();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void registerStopVoldReceiver() {
        vReceiver = new VoldReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);

        filter.addDataScheme("file"); //必须加上该条，否则无法接收命令
        GUtilMain.getqContext().registerReceiver(vReceiver, filter);

        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " register media mounted and eject receiver");
    }

    private void unRegisterStopVoldReceiver() {
        if (vReceiver != null) {
            GUtilMain.getqContext().unregisterReceiver(vReceiver);
            vReceiver = null;
        }
    }


    int nMirrorCount = -1;

    public void startMirrorImage() {
        nMirrorCount++;
        if (nMirrorCount >= 10000)
            nMirrorCount = 0;
        for (i = 0; i < 4; i++) {
            if (csi1phy_num >= 0) {
                GUtilMain.getQCamera(csi1phy_num).setPreviewMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi1phy_num).setPreviewStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi1phy_num).setVideoStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi1phy_num).setSubStreamMirror(i, rParams.getVideoMirror());
            }

            if (csi2phy_num >= 0) {
                GUtilMain.getQCamera(csi2phy_num).setPreviewMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi2phy_num).setPreviewStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi2phy_num).setVideoStreamMirror(i, rParams.getVideoMirror());
                GUtilMain.getQCamera(csi2phy_num).setSubStreamMirror(i, rParams.getVideoMirror());
            }
        }
    }

    static int nCount = -1;

    public void setAudioMute(View view) {
        Button item_bt = (Button) view;
        nCount++;
        if (nCount >= 10000)
            nCount = 0;
        if (0 == nCount % 2) {
            item_bt.setText("Open audio");
            QCarAudio.getInstance().setMute(true);
            Toast.makeText(getApplicationContext(), "Audio Mute", Toast.LENGTH_SHORT).show();
        } else {
            item_bt.setText("Mute");
            QCarAudio.getInstance().setMute(false);
            Toast.makeText(getApplicationContext(), "Open audio", Toast.LENGTH_SHORT).show();
        }

    }

    public synchronized void startTakePic(View view) {
        if (getStoragePath(MainActivity.this, true) != null && getStoragePath(MainActivity.this, true).contains("storage")) {
            if (m_bTakePicFinshFlag == false) {
                return;
            }
            m_bTakePicFinshFlag = false;
            Vector<QCarPicWriter> qCarPicWriterVector = new Vector<>();
            QCarPicWriter jpegEncoder;
            for (int i = 0; i < 4; i++) {
                if (csi1phy_num >= 0) {
                    int width = i == 0 ? pParams.getWidth(1) : (i == 1) ? pParams.getWidth(2) : (i == 2 ? pParams.getWidth(3) : pParams.getWidth(4));
                    int height = i == 0 ? pParams.getHeight(1) : (i == 1) ? pParams.getHeight(2) : (i == 2 ? pParams.getHeight(3) : pParams.getHeight(4));
                    jpegEncoder = new QCarPicWriter(this, GUtilMain.getQCamera(csi1phy_num), i, width, height, -1);
                    jpegEncoder.startJpegEncoderThread();
                    qCarPicWriterVector.add(jpegEncoder);
                }
                if (csi2phy_num >= 0 && i < 2) {
                    int width = i == 0 ? pParams.getWidth(5) : pParams.getWidth(6);
                    int height = i == 0 ? pParams.getHeight(5) : pParams.getHeight(6);
                    jpegEncoder = new QCarPicWriter(this, GUtilMain.getQCamera(csi2phy_num), i, width, height, -1);
                    jpegEncoder.startJpegEncoderThread();
                    qCarPicWriterVector.add(jpegEncoder);
                }
            }

            for (QCarPicWriter qCarPicWriter : qCarPicWriterVector) {
                qCarPicWriter.waitJpenEncorderEnd();
            }
            //连续快速点击拍照，会导致崩溃，主要是Toast显示的问题，上一个Toast没有显示完，这个一个Toast又在显示，会导致崩溃
            Toast.makeText(getApplicationContext(), "Take picture End ", Toast.LENGTH_SHORT).show();
            m_bTakePicFinshFlag = true;
        } else {
            Toast.makeText(getApplicationContext(), "SD card is not available", Toast.LENGTH_SHORT).show();
        }
    }

    public synchronized void setting(View view) {
        Intent intent = new Intent(this, SetActivity.class);
        startActivityForResult(intent, 10086);
    }

    private boolean isReStart;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10086 && resultCode == 10086) {
            if (data != null) {
                String result = data.getStringExtra("result");
                if (result.equals("restart")) {
                    isReStart = true;
                    detectInsert.stopDetectThread();  //关闭热插拔检测线程
                    detectInsert.clearInputParam();
                    bOsdFlag = false;
                    isGpio = false;
                    RecoderInstance.getInstance().stopRecorder();

                    onDestroy();
                }
            }
        }
    }

    /**
     * 获取SD卡路径
     *
     * @param mContext
     * @param is_removable SD卡是否可移除，不可移除的是内置SD卡，可移除的是外置SD卡
     * @return
     */
    public static String getStoragePath(Context mContext, boolean is_removable) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;

        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                String state = (String) getState.invoke(storageVolumeElement);

                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);

                if (is_removable == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPause() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onStop");
        super.onStop();  //会执行previewFragment stop
    }

    @Override
    protected void onDestroy() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onDestroy");
        super.onDestroy();
        closeCamera();

        stopRecordService();

        unregisterReceiver(screenReceiver);
        unregisterReceiver(mReceiver);

        if (isReStart) {
            isReStart = false;
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        ShellUtils shellUtils=new ShellUtils();
        ShellUtils.CommandResult commandResult = shellUtils.execCommand("sync", false);
        if (commandResult.result == 0) {
            Log.d("qwe", "onDestroy: sync success");
        } else {
            Log.d("qwe", "onDestroy: sync failed");
        }


        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            take_pic_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            start_lock_video_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            settings_button.setVisibility(button_visible ? View.GONE : View.VISIBLE);
            button_visible = !button_visible;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void amplify(int channel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (channel == 1 || channel == 2) {
                    mLin2.setVisibility(View.GONE);
                    mLin3.setVisibility(View.GONE);
                    if (channel == 1) {
                        container1_1.setVisibility(View.GONE);
                    } else {
                        container1_0.setVisibility(View.GONE);
                    }
                } else if (channel == 3 || channel == 4) {
                    mLin1.setVisibility(View.GONE);
                    mLin3.setVisibility(View.GONE);
                    if (channel == 3) {
                        container1_3.setVisibility(View.GONE);
                    } else {
                        container1_2.setVisibility(View.GONE);
                    }
                } else if (channel == 5 || channel == 6) {
                    mLin1.setVisibility(View.GONE);
                    mLin2.setVisibility(View.GONE);
                    if (channel == 5) {
                        container2_1.setVisibility(View.GONE);
                    } else {
                        container2_0.setVisibility(View.GONE);
                    }
                } else {
                    if (previewNum == 1) {
                        mLin1.setVisibility(View.VISIBLE);
                        container1_0.setVisibility(View.VISIBLE);
                    } else if (previewNum == 2) {
                        mLin1.setVisibility(View.VISIBLE);
                        container1_0.setVisibility(View.VISIBLE);
                        container1_1.setVisibility(View.VISIBLE);
                    } else if (previewNum == 4) {
                        mLin1.setVisibility(View.VISIBLE);
                        mLin2.setVisibility(View.VISIBLE);
                        container1_0.setVisibility(View.VISIBLE);
                        container1_1.setVisibility(View.VISIBLE);
                        container1_2.setVisibility(View.VISIBLE);
                        container1_3.setVisibility(View.VISIBLE);
                    } else {
                        mLin1.setVisibility(View.VISIBLE);
                        mLin2.setVisibility(View.VISIBLE);
                        mLin3.setVisibility(View.VISIBLE);
                        container1_0.setVisibility(View.VISIBLE);
                        container1_1.setVisibility(View.VISIBLE);
                        container1_2.setVisibility(View.VISIBLE);
                        container1_3.setVisibility(View.VISIBLE);
                        container2_0.setVisibility(View.VISIBLE);
                        container2_1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.d("ScreenReceiver", "屏幕已开启");
                NativeGpioD.setGpioStatus(41, 1);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.d("ScreenReceiver", "屏幕已关闭");
                detectInsert.stopDetectThread();  //关闭热插拔检测线程
                detectInsert.clearInputParam();
                bOsdFlag = false;
                isGpio = false;
                RecoderInstance.getInstance().stopRecorder();
                NativeGpioD.setGpioStatus(41, 0);
                onDestroy();
            }
        }
    }
}
