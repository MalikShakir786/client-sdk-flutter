/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera.callbacks;

import android.media.MediaCodec;

import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.qcarapi.cb.IQCarVideoEncoderStreamCB;
import com.quectel.qcarapi.recorder.QCarRecorder;
import com.quectel.qcarapi.util.QCarLog;
import com.quectel.qcarlib.utils.PathUtil;
import com.quectel.qcarlib.utils.RecorderUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class QCarGetVideoEncoderStream implements IQCarVideoEncoderStreamCB {

    private final static String TAG = "QCarGetVideoEncStream";

    private PathUtil subEncPathUtil = null;
    FileOutputStream subEncoderStreamFile = null;
    private FileChannel subEncoderStreamChannel;
    private int csi_num;
    private int channel_num;

    public QCarGetVideoEncoderStream(int csi_num, int channel_num, QCarRecorder objVideoEncoder){
        this.csi_num = csi_num;
        this.channel_num = channel_num;
        registVideoEncoderCB(objVideoEncoder);
//使用内部接口方便测试，后期应该使用客户自己的存储路径
        subEncPathUtil = RecorderUtil.getVideoPath(GUtilMain.getqContext(), RecorderUtil.MEDIA_VIDEO_TYPE_SUB_STREAM, objVideoEncoder.getSubEncVideoParam());
        if (subEncPathUtil.getPath() == null) {
            QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, "The file path that saved sub encode stream is null");
            return ;
        }

        File file = new File(subEncPathUtil.getPath());
        if (file.exists()) {
            file.delete();
        }
        try {
            subEncoderStreamFile = new FileOutputStream(file);
            subEncoderStreamChannel = subEncoderStreamFile.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void videoEncoderStreamCB(int csi_num, int channel_num, ByteBuffer encoderBuf, MediaCodec.BufferInfo encoderBufInfo){
        if(this.csi_num == csi_num && this.channel_num == channel_num) {
            //Log.e(TAG, "videoEncoderStreamCallBack ");
        }
    }

    @Override
    public void subEncoderStreamCB(int csi_num, int channel_num, ByteBuffer encoderBuf, MediaCodec.BufferInfo encoderBufInfo){

        if(this.csi_num == csi_num && this.channel_num == channel_num) {
            //Log.e(TAG, "subEncoderStreamCallBack: csi_num = " + this.csi_num + ", channel = "+ this.channel_num + ", size = " + encoderBufInfo.size );
            if (subEncoderStreamChannel !=null && encoderBufInfo.size > 0) {
                try {
                    subEncoderStreamChannel.write(encoderBuf);
                }catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private void registVideoEncoderCB(QCarRecorder objVideoEncoder) {
        objVideoEncoder.registerEncoderStreamCB(this);
    }

    public void release(){
        try {
            if (subEncoderStreamFile != null) {
                subEncoderStreamFile.close();
                subEncoderStreamFile = null;
            }

            if (subEncoderStreamChannel != null) {
                subEncoderStreamChannel.close();
                subEncoderStreamChannel = null;
            }

            File file=new File(subEncPathUtil.getPath()); //指定文件名及路径

            if (file.renameTo(new File(GUtilMain.removeTempInPath(subEncPathUtil.getPath())))) {
            } else {
                QCarLog.e(QCarLog.LOG_MODULE_APP, TAG,"Rename file failed!");
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
