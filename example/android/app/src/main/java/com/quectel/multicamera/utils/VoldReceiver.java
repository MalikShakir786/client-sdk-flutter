/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.quectel.multicamera.RecoderInstance;
import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.util.QCarError;
import com.quectel.qcarapi.util.QCarLog;

public class VoldReceiver extends BroadcastReceiver {

    String TAG = "VoldReceiver";

    public VoldReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onReceive：Action " + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
                    //SD卡拔出

                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, " sd out ");

                if (RecoderInstance.getInstance().isRecoderStarted()) {
                    RecoderInstance.getInstance().stopRecorder();

                    if (QCarCamera.getOnErrorCB() != null) {
                        QCarCamera.getOnErrorCB().onError(QCarError.QCAR_ERROR_TYPE_AIS_ACTIVITY, QCarError.QCAR_ERROR_CODE_SDCARD_OUT_IN_RECODER,
                                QCarError.getErrTextByCode(QCarError.QCAR_ERROR_CODE_SDCARD_OUT_IN_RECODER).getBytes(), -1, -1);
                    }
                }

            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
                    //SD卡可读
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

