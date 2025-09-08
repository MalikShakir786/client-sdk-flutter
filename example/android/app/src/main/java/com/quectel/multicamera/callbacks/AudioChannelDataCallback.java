/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera.callbacks;

import com.quectel.qcarapi.cb.IQCarAudioDataCB;
import com.quectel.qcarapi.util.QCarLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioChannelDataCallback implements IQCarAudioDataCB {
    private String TAG = "AudioChannelDataCB";
    private FileOutputStream fileOutputStream0 = null;
    private FileOutputStream fileOutputStream1 = null;
    private FileOutputStream fileOutputStream2 = null;
    private FileOutputStream fileOutputStream3 = null;

    @Override
    public void onAudioChannelStream(int channel, byte[] pBuf, int dataLen) {
        if (fileOutputStream0 == null) {
            try {
                fileOutputStream0 = new FileOutputStream(new File("/data/channel0.pcm"));
            }catch (IOException io) {
                io.printStackTrace();
            }
        }

        if (fileOutputStream1 == null) {
            try {
                fileOutputStream1 = new FileOutputStream(new File("/data/channel1.pcm"));
            }catch (IOException io) {
                io.printStackTrace();
            }
        }

        if (fileOutputStream2 == null) {
            try {
                fileOutputStream2 = new FileOutputStream(new File("/data/channel2.pcm"));
            }catch (IOException io) {
                io.printStackTrace();
            }
        }

        if (fileOutputStream3 == null) {
            try {
                fileOutputStream3 = new FileOutputStream(new File("/data/channel3.pcm"));
            }catch (IOException io) {
                io.printStackTrace();
            }
        }

        QCarLog.i(QCarLog.LOG_MODULE_APP,  TAG, " onAudioChannelStream channel = " + channel + " dataLen = " + dataLen);
        try {
            if (channel == 0)
                fileOutputStream0.write(pBuf);
            else if (channel == 1)
                fileOutputStream1.write(pBuf);
            else if (channel == 2)
                fileOutputStream2.write(pBuf);
            else if (channel == 3)
                fileOutputStream3.write(pBuf);

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
