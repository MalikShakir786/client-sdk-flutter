/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera.callbacks;

import android.media.MediaCodec;
import android.util.Log;

import com.quectel.qcarapi.cb.IQcarAudioAacCB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class AudioAacCallback implements IQcarAudioAacCB {
    private String TAG = "AudioAacCallback";

    @Override
    public void onAudioAacCB(int channel, ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
        Log.d(TAG, "onAudioAacCB||channel =" + channel);
        File file0 = new File("/data/aac_channel_0.aac");
        File file1 = new File("/data/aac_channel_1.aac");
        try {
            FileChannel fileChannel0 = new FileOutputStream(file0, true).getChannel();
            FileChannel fileChannel1 = new FileOutputStream(file1, true).getChannel();
            Log.d(TAG, "onAudioAacCB||channel =" + channel + "||buffer.size =" + buffer.remaining());
            if (channel == 0) {
                fileChannel0.write(buffer);
                fileChannel0.close();
                buffer.flip();
            } else if (channel == 1) {
                fileChannel1.write(buffer);
                fileChannel1.close();
                buffer.flip();
            }
        } catch (IOException e) {
            Log.d(TAG, "onAudioAacCB||exception");
        }
    }
}
