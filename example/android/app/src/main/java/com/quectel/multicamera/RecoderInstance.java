/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.quectel.multicamera.callbacks.QCarGetVideoEncoderStream;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.PreviewParams;
import com.quectel.multicamera.utils.RecorderParams;
import com.quectel.qcarapi.stream.QCarAudio;
import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.osd.QCarOsd;
import com.quectel.qcarapi.recorder.QCarEncParam;
import com.quectel.qcarapi.recorder.QCarRecorderSetting;
import com.quectel.qcarapi.recorder.QCarRecorder;
import com.quectel.qcarapi.util.QCarLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class RecoderInstance {
    private String TAG = "RecoderInstance";

    private static RecoderInstance instance = null;

    private Object rLock = new Object();
    ;
    private Vector<QCarRecorder> carRecorders;
    private Vector<QCarGetVideoEncoderStream> objGetVideoEncoderStreams; // 用于子码流存储

    private PreviewParams pParams = GUtilMain.getPreviewParams();
    private RecorderParams rParams = GUtilMain.getRecorderParams();
    /*************** Add audio Record ****************/
    private QCarAudio carAudio = null;
    /*************** Add audio Record ****************/

    /***************Recorder Setting*********************/
    private QCarRecorderSetting qCarRecorderSetting = null;
    /***************Recorder Setting*********************/

    /******Child Stream OSD********/
    private boolean subStreamOsdInited = false;
    private boolean bChildOsdFlag = false;


    private boolean isRecoderStarted = false;

    public boolean isRecoderStarted() {
        return isRecoderStarted;
    }

    private int childWidth;
    private int childHeight;
    private int mainRate;
    private int subRate;
    /****merge child stream******/
    private int childMergeWidth;
    public static int CAMERANUM = 4;
    private int csi1phy_num, csi2phy_num;
    private boolean[] isRecord = new boolean[6];

    public static RecoderInstance getInstance() {
        if (instance == null) {
            instance = new RecoderInstance();
        }
        return instance;
    }

    public void initRecoder(int csi1_num, int csi2_num) {
        if (rParams.getChild_size() == 0) {
            childWidth = 640;
            childHeight = 480;
        } else if (rParams.getChild_size() == 1) {
            childWidth = 352;
            childHeight = 288;
        } else if (rParams.getChild_size() == 2) {
            childWidth = 1280;
            childHeight = 720;
        }

        if (rParams.getMainRatePosition() == 0) {
            mainRate = GUtilMain.BITRATE_4M;
        } else if (rParams.getMainRatePosition() == 1) {
            mainRate = GUtilMain.BITRATE_2M;
        } else if (rParams.getMainRatePosition() == 2) {
            mainRate = GUtilMain.BITRATE_1M;
        }

        if (rParams.getSubRatePosition() == 0) {
            subRate = GUtilMain.BITRATE_2M;
        } else if (rParams.getSubRatePosition() == 1) {
            subRate = GUtilMain.BITRATE_1M;
        } else if (rParams.getSubRatePosition() == 2) {
            subRate = GUtilMain.BITRATE_512K;
        }

        for (int i = 0; i < 6; i++) {
            if (rParams.getRecordState(i + 1)) {
                isRecord[i] = true;
            } else {
                isRecord[i] = false;
            }
        }

        this.csi1phy_num = csi1_num;
        this.csi2phy_num = csi2_num;

        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "csi1phy_num " + csi1phy_num + " csi2phy_num " + csi2phy_num);
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " childRecordEnable = " + rParams.isChildRecordEnable());

        if (qCarRecorderSetting == null) {
            qCarRecorderSetting = new QCarRecorderSetting();
            qCarRecorderSetting.startScanDirectory(GUtilMain.getqContext()); //该接口只需要第一次的时候调用一次
        }
    }

    public void startRecorder() {
        synchronized (rLock) {
            if (isRecoderStarted == false) {
                startRecord();
                isRecoderStarted = true;
            }
        }
    }

    private void startRecord() {
        carRecorders = new Vector<>();
        objGetVideoEncoderStreams = new Vector<>();
        /*************** Add audio Record: add by Les --> Start ****************/
        if (rParams.isAudioRecordEnable()) {
            carAudio = QCarAudio.getInstance();
            if (carAudio != null) {
                QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, "The class of audio recorder create success");
            }
            carAudio.configureAudioParam(QCarAudio.QUEC_SAMPLINGRATE_24, 1,
                    QCarAudio.QUEC_PCMSAMPLEFORMAT_FIXED_16, QCarAudio.QUEC_SPEAKER_FRONT_LEFT, QCarAudio.QUEC_BYTEORDER_LITTLEENDIAN);
            carAudio.startRecorder();
        }
        /*************** Add audio Record: add by Les --> End ****************/
        for (int i = 0, j = 0; i < 6; i++, j++) {
            QCarRecorder avcEncoder = null;
            if (isRecord[i]) {
                if (i > 3) {
                    avcEncoder = initChannelRecord(j, i == 4 ? 0 : 1, csi2phy_num, isRecord[i], rParams.isChildRecordEnable(), rParams.isAudioRecordEnable());
                } else {
                    avcEncoder = initChannelRecord(j, i, csi1phy_num, isRecord[i], rParams.isChildRecordEnable(), rParams.isAudioRecordEnable());
                }
                carRecorders.add(avcEncoder);
            }
        }
    }

    public void startCollision() {
        for (QCarRecorder avcEncoder : carRecorders) {
            avcEncoder.handleCollision();
        }
    }

    public void startLockVideo() {
        for (QCarRecorder carRecorder : carRecorders) {
            carRecorder.setLockVideo(true, 1, 60 * 1000); // 1min时长
        }
    }

    public void stopLockVideo() {
        for (QCarRecorder carRecorder : carRecorders) {
            carRecorder.stopLockVideo();
        }
    }

    private QCarRecorder initChannelRecord(int audioChannel, int channel, int csiphyNum, boolean recordEnable, boolean subStreamEnable, boolean audioRecordEnable) {
        // 针对每个通道的预览视频数据格式进行设置
        QCarCamera qCarCamera = GUtilMain.getQCamera(csiphyNum);
        qCarCamera.setVideoColorFormat(channel, QCarCamera.YUV420_NV12);

        QCarRecorder avcEncoder = null;
        //avcEncoder = new AvcEncoderAdasDms(getActivity(), mModule);
        if (recordEnable) {
            avcEncoder = new QCarRecorder(GUtilMain.getqContext());
            avcEncoder.setQCarCamera(qCarCamera);
            QCarEncParam.EncVideoParam encoderParam = new QCarEncParam.EncVideoParam(); //创建EncoderParam类
            encoderParam.setCsiNumAndVideoChannel(csiphyNum, channel);
            encoderParam.setResolution(rParams.getWidth(csiphyNum, channel), rParams.getHeight(csiphyNum, channel));
            encoderParam.setBitrate(mainRate);
//            encoderParam.setProfileType(MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
//            encoderParam.setProfileLevel(MediaCodecInfo.CodecProfileLevel.AVCLevel4);
            //encoderParam.setKeyColorFormat(MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar);
            encoderParam.setKeyColorFormat(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
            encoderParam.setKeyIFrameInterval(2);
            encoderParam.setKeyFrameRate(25);
            encoderParam.setEncoderMineType(GUtilMain.getMinetypeWithCodecPosition(rParams.getCodecTypePosition()));
            encoderParam.setStreamOutputFormat(GUtilMain.getOutputFormatWidthContainerPosition(rParams.getCtPosition()));
            encoderParam.setBitrateMode(2); //VBR模式 可以手动设置
            avcEncoder.setMainEncVideoParam(encoderParam); //设置encoder参数
            avcEncoder.setFileSegmentThreshold(GUtilMain.getSegmentFlag(rParams.getSegmentSizePosition()), GUtilMain.getSegmentSizeWithPosition(rParams.getSegmentSizePosition()));

            if (audioRecordEnable) {
                QCarEncParam.EncAudioParam eAparam = new QCarEncParam.EncAudioParam(); //创建EncoderParam类
                eAparam.setChannels(1);
                eAparam.setSampleRate(24000);
                eAparam.setMineType(MediaFormat.MIMETYPE_AUDIO_AAC);
                eAparam.setProfile(MediaFormat.KEY_AAC_PROFILE);
                eAparam.setProfileLevel(MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                eAparam.setBitRate(64000);
                avcEncoder.setEncAudioParam(eAparam);
                avcEncoder.setAudioEnable(true);
                avcEncoder.setAudioChannel(audioChannel);
                avcEncoder.setQCarAudio(carAudio);
            }

            //avcEncoder.registerRecorderVideoPathCB(GUtilMain.getQRVPDemo());
            avcEncoder.startRecorder();

            if (subStreamEnable) {
                QCarEncParam.EncVideoParam subEncoderParam = new QCarEncParam.EncVideoParam(); //创建EncoderParam类
                //子码流编码参数配置跟主码流配置参数是独立的，可以不一样
                subEncoderParam.setCsiNumAndVideoChannel(csiphyNum, channel);
                subEncoderParam.setResolution(childWidth, childHeight);
                subEncoderParam.setBitrate(subRate);
                subEncoderParam.setKeyColorFormat(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);

                subEncoderParam.setKeyIFrameInterval(2);
                subEncoderParam.setKeyFrameRate(25);
                subEncoderParam.setEncoderMineType(encoderParam.getEncoderMineType());
                subEncoderParam.setBitrateMode(1); //VBR模式 可以手动设置
                avcEncoder.setSubEncVideoParam(subEncoderParam); //设置encoder参数
                //取编码后的aac数据
//                avcEncoder.registerAudioAacCB(new AudioAacCallback());
                QCarGetVideoEncoderStream objGetVES = new QCarGetVideoEncoderStream(csiphyNum, channel, avcEncoder);

                if (objGetVES != null)
                    objGetVideoEncoderStreams.add(objGetVES);
                avcEncoder.startSubRecorder();
                if (!subStreamOsdInited)
                    addSubStreamOsd();
            }
        }

        return avcEncoder;
    }

    public void stopRecorder() {
        synchronized (rLock) {
            if (isRecoderStarted == true) {
                stopRecord();

                bChildOsdFlag = false; //关闭水印
                //closeMergeThread();  //添加针对四合一的水印，默认不使用该方式
                isRecoderStarted = false;
            }
        }
    }

    //AudioRecoder 是用来获取音频数据
    private void stopAudioRecoder() {
        /*************** Add audio Record: add by Les --> Start ****************/
        if (rParams.isAudioRecordEnable()) {
            //先停Audio的数据回调，回调中会用到编码
            if (carAudio != null) {
                carAudio.stopRecorder();
                carAudio.release();
                carAudio = null;
            }
        }
        /*************** Add audio Record: add by Les --> End ****************/
    }

    private void stopRecord() {
        stopAudioRecoder();
        Vector<Thread> encoderThreads = new Vector<Thread>();
        for (final QCarRecorder carRecorder : carRecorders) {
            Thread encoderCloseThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (rParams.isChildRecordEnable()) {
                        carRecorder.stopSubRecorder();
                    }
                    carRecorder.stopRecorder();
                }
            });
            encoderCloseThread.start();
            encoderThreads.add(encoderCloseThread);
        }

        for (Thread thread : encoderThreads) {
            try {
                thread.join();
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
        }

        encoderThreads.clear();

        for (QCarGetVideoEncoderStream qCarGetVideoEncoderStream : objGetVideoEncoderStreams) {
            qCarGetVideoEncoderStream.release();
        }

        objGetVideoEncoderStreams.clear();
        carRecorders.clear();
    }

    private void addSubStreamOsd() {
        subStreamOsdInited = true;
        new Thread(new Runnable() {
            Date date;
            String dateStr, dateStr2;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH时-mm分-ss秒");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH-mm-ss");
            int tIndex2 = -1;
            String font_path = "/system/fonts/Song.ttf";
            int font_size = 2;
            QCarOsd qCarOsd;

            @Override
            public void run() {
                qCarOsd = new QCarOsd();
                qCarOsd.setOsdResolution(childWidth, childHeight);
                qCarOsd.initOsd(font_path.getBytes(), font_size);

                if (csi1phy_num >= 0) {
                    GUtilMain.getQCamera(csi1phy_num).setSubOsd(qCarOsd);
                }

                if (csi2phy_num >= 0) {
                    GUtilMain.getQCamera(csi2phy_num).setSubOsd(qCarOsd);
                }

                bChildOsdFlag = true;
                while (bChildOsdFlag) {
                    date = new Date();
                    dateStr = sdf.format(date);
                    dateStr2 = sdf2.format(date);
                    tIndex2 = qCarOsd.setOsd(-1, dateStr.getBytes(), tIndex2, 32, 96);

                    try {
                        Thread.sleep(980); // 时间字幕刷新频率
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }

                qCarOsd.deinitOsd();
                subStreamOsdInited = false;
            }
        }).start();
    }
}
