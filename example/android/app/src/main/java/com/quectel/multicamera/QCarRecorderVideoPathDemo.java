/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera;

import android.content.Context;
import android.os.StatFs;
import android.util.Log;

import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.qcarapi.cb.IQCarRecorderVideoPathCB;
import com.quectel.qcarapi.recorder.QCarEncParam;
import com.quectel.qcarlib.db.RecorderSQLiteOpenHelper;
import com.quectel.qcarapi.util.QCarLog;
import com.quectel.qcarlib.utils.RecorderUtil;
import com.quectel.qcarlib.utils.StorageUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class QCarRecorderVideoPathDemo implements IQCarRecorderVideoPathCB {
    public static final int MEDIA_VIDEO_TYPE_MIAN_STRAM = 0;
    public static final int MEDIA_VIDEO_TYPE_MERGE_STREAM = 2;
    public static final int MEDIA_VIDEO_TYPE_SUB_STREAM = 2;
    public static final int MEDIA_VIDEO_TYPE_COLLISION_STREAM= 3;
    public static final int MEDIA_VIDEO_TYPE_PICTURE_JPEG= 4;
    private static String TAG = "QCarRecorderVideoPathDemo";

    private RecorderSQLiteOpenHelper sqLiteOpenHelper;
    public String stampToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    public static long geFreeCapacity(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSizeLong();
        long feeBlockCount = stat.getAvailableBlocksLong();
        return blockSize * feeBlockCount / 1024 / 1024;
    }

    @Override
    public String getRecorderVideoPath(Context mContext, int mediaType, QCarEncParam.EncVideoParam encoderParam) {
        String path;
        String rootPath; //default
        System.out.println("zyz -> getRecorderVideoPath ====================================== ");
        sqLiteOpenHelper = RecorderUtil.getRsqliteHelper(mContext);
        rootPath = StorageUtil.getStoragePath(mContext,true);
        System.out.println("zyz -> rootPath ====================================== "+rootPath);
        if (rootPath == null) {
            return null;
        }

        if (mediaType == MEDIA_VIDEO_TYPE_MIAN_STRAM) {
            path = rootPath + "/main_" + encoderParam.getCsiphyNum() + "_" + encoderParam.getChannel() + "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        }else if (mediaType == MEDIA_VIDEO_TYPE_MERGE_STREAM) {
            path = rootPath + "/merge_" + encoderParam.getCsiphyNum() +  "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        }else if (mediaType == MEDIA_VIDEO_TYPE_COLLISION_STREAM) {
            path = rootPath + "/collision_" + encoderParam.getCsiphyNum() + "_" + encoderParam.getChannel() + "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        }else if (mediaType == MEDIA_VIDEO_TYPE_SUB_STREAM) {
            path = rootPath + "/child_" + encoderParam.getCsiphyNum() + "_" + encoderParam.getChannel() + "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 1, encoderParam.getStreamOutputFormat());
        }else
            path = "/sdcard/DCIM/err.stream";


        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " path = " + path);
        if (geFreeCapacity(rootPath) < StorageUtil.getCapacityLeftInSdcard()) {
            while (geFreeCapacity(rootPath) < (StorageUtil.getCapacityLeftInSdcard() + StorageUtil.getCapacityRemoveLimit())) {
                String deletePath = sqLiteOpenHelper.queryFirstRecorderPathAndDelete();
                if (deletePath == null) {
                    QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, "内存卡满，录像不能启动");
                    return null;
                }
            }
        }
        sqLiteOpenHelper.insertRecorder(path, new Date().getTime());  //需要使用单例模式
        return path;
    }

    public String getRecorderLockVideoPath(Context mContext, int mediaType, QCarEncParam.EncVideoParam encoderParam) {
        String path;
        String rootPath; //default
        System.out.println("zyz -> 11111111111111111111111111111111111111111111111111");
        sqLiteOpenHelper = RecorderUtil.getRsqliteHelper(mContext);
        rootPath = StorageUtil.getStoragePath(mContext,true);
        if (rootPath == null) {
            return null;
        }
        rootPath += "/LockVideo";

        if (mediaType == MEDIA_VIDEO_TYPE_MIAN_STRAM) {
            path = rootPath + "/main_" + encoderParam.getCsiphyNum() + "_" + encoderParam.getChannel() + "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        }else if (mediaType == MEDIA_VIDEO_TYPE_MERGE_STREAM) {
            path = rootPath + "/merge_" + encoderParam.getCsiphyNum() +  "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        }else if (mediaType == MEDIA_VIDEO_TYPE_COLLISION_STREAM) {
            path = rootPath + "/collision_" + encoderParam.getCsiphyNum() + "_" + encoderParam.getChannel() + "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 0, encoderParam.getStreamOutputFormat());
        }else if (mediaType == MEDIA_VIDEO_TYPE_SUB_STREAM) {
            path = rootPath + "/child_" + encoderParam.getCsiphyNum() + "_" + encoderParam.getChannel() + "_" + stampToDate(System.currentTimeMillis()) + RecorderUtil.getSuffixName(encoderParam.getEncoderMineType(), 1, encoderParam.getStreamOutputFormat());
        }else
            path = "/sdcard/DCIM/err.stream";


        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " path = " + path);
        if (geFreeCapacity(rootPath) < StorageUtil.getCapacityLeftInSdcard()) {
            while (geFreeCapacity(rootPath) < (StorageUtil.getCapacityLeftInSdcard() + StorageUtil.getCapacityRemoveLimit())) {
                String deletePath = sqLiteOpenHelper.queryFirstRecorderPathAndDelete();
                if (deletePath == null) {
                    QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, "内存卡满，录像不能启动");
                    return null;
                }
            }
        }
        return path;
    }




    public void notifyRecoderVideoResult(QCarEncParam.EncVideoParam encoderParam, String path) {
        File file = new File(path); //指定文件名及路径
        if (file.renameTo(new File(GUtilMain.removeTempInPath(path)))) {
            //Log.e(TAG,"Rename file success!");
        } else {
            QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, "Rename file failed!");
        }
    }
}
