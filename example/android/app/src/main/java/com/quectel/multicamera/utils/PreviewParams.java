/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera.utils;

import android.util.Log;

public class PreviewParams {
    private int previewNum = 4;
    private int previewMaxNum = 6;
    private int csiNum = 2; //default csi0+csi1
    private int width1 = 1280;
    private int width2 = 1280;
    private int width3 = 1280;
    private int width4 = 1280;
    private int width5 = 1280;
    private int width6 = 1280;
    private int height1 = 720;
    private int height2 = 720;
    private int height3 = 720;
    private int height4 = 720;
    private int height5 = 720;
    private int height6 = 720;

    public int getPreviewNum() {
        previewNum = GUtilMain.mSharedPreferences.getInt("pre_num", previewNum);
        return previewNum > previewMaxNum ? previewMaxNum : previewNum;
    }

    public void setPreviewNum(int previewNum) {
        this.previewNum = previewNum;
        GUtilMain.mEditor.putInt("pre_num", this.previewNum);
        GUtilMain.mEditor.commit();
    }

    public int getCsiNum() {
        csiNum = GUtilMain.mSharedPreferences.getInt("pre_csi_num", csiNum);
        return csiNum;
    }

    public void setCsiNum(int csiNum) {
        this.csiNum = csiNum;
        GUtilMain.mEditor.putInt("pre_csi_num", this.csiNum);
        GUtilMain.mEditor.commit();
    }

    private int cameraType1 = 0, cameraType2 = 0, cameraType3 = 0, cameraType4 = 0, cameraType5 = 0, cameraType6 = 0;

    public int getCameraType(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                cameraType1 = GUtilMain.mSharedPreferences.getInt("pre_camera_type1", cameraType1);
                return cameraType1;
            case 2:
                cameraType2 = GUtilMain.mSharedPreferences.getInt("pre_camera_type2", cameraType2);
                return cameraType2;
            case 3:
                cameraType3 = GUtilMain.mSharedPreferences.getInt("pre_camera_type3", cameraType3);
                return cameraType3;
            case 4:
                cameraType4 = GUtilMain.mSharedPreferences.getInt("pre_camera_type4", cameraType4);
                return cameraType4;
            case 5:
                cameraType5 = GUtilMain.mSharedPreferences.getInt("pre_camera_type5", cameraType5);
                return cameraType5;
            case 6:
                cameraType6 = GUtilMain.mSharedPreferences.getInt("pre_camera_type6", cameraType6);
                return cameraType6;
            default:
                return -1;
        }
    }

    public void setCameraType(int channel, int type) {
        if (channel > 6 || channel < 1 || type > 1) {
            System.out.println("zyz --> channel value is out of range !");
            return;
        }
        switch (channel) {
            case 1:
                cameraType1 = type;
                GUtilMain.mEditor.putInt("pre_camera_type1", cameraType1);
                break;
            case 2:
                cameraType2 = type;
                GUtilMain.mEditor.putInt("pre_camera_type2", cameraType2);
                break;
            case 3:
                cameraType3 = type;
                GUtilMain.mEditor.putInt("pre_camera_type3", cameraType3);
                break;
            case 4:
                cameraType4 = type;
                GUtilMain.mEditor.putInt("pre_camera_type4", cameraType4);
                break;
            case 5:
                cameraType5 = type;
                GUtilMain.mEditor.putInt("pre_camera_type5", cameraType5);
                break;
            case 6:
                cameraType6 = type;
                GUtilMain.mEditor.putInt("pre_camera_type6", cameraType6);
                break;
        }
        GUtilMain.mEditor.commit();
    }

    private int resolution1 = 0, resolution2 = 0, resolution3 = 0, resolution4 = 0, resolution5 = 0, resolution6 = 0;

    public int getResolution(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                resolution1 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution1", resolution1);
                return resolution1;
            case 2:
                resolution2 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution2", resolution2);
                return resolution2;
            case 3:
                resolution3 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution3", resolution3);
                return resolution3;
            case 4:
                resolution4 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution4", resolution4);
                return resolution4;
            case 5:
                resolution5 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution5", resolution5);
                return resolution5;
            case 6:
                resolution6 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution6", resolution6);
                return resolution6;
            default:
                return -1;
        }
    }

    public void setResolution(int channel, int resolution) {
        if (channel > 6 || channel < 1 || resolution > 2) {
            System.out.println("zyz --> channel value is out of range !");
            return;
        }
        switch (channel) {
            case 1:
                resolution1 = resolution;
                GUtilMain.mEditor.putInt("pre_camera_resolution1", resolution1);
                break;
            case 2:
                resolution2 = resolution;
                GUtilMain.mEditor.putInt("pre_camera_resolution2", resolution2);
                break;
            case 3:
                resolution3 = resolution;
                GUtilMain.mEditor.putInt("pre_camera_resolution3", resolution3);
                break;
            case 4:
                resolution4 = resolution;
                GUtilMain.mEditor.putInt("pre_camera_resolution4", resolution4);
                break;
            case 5:
                resolution5 = resolution;
                GUtilMain.mEditor.putInt("pre_camera_resolution5", resolution5);
                break;
            case 6:
                resolution6 = resolution;
                GUtilMain.mEditor.putInt("pre_camera_resolution6", resolution6);
                break;
        }
        GUtilMain.mEditor.commit();
    }

    public int getInput1TypeNum() {
        cameraType1 = GUtilMain.mSharedPreferences.getInt("pre_camera_type1", cameraType1);
        cameraType2 = GUtilMain.mSharedPreferences.getInt("pre_camera_type2", cameraType2);
        cameraType3 = GUtilMain.mSharedPreferences.getInt("pre_camera_type3", cameraType3);
        cameraType4 = GUtilMain.mSharedPreferences.getInt("pre_camera_type4", cameraType4);
        resolution1 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution1", resolution1);
        resolution2 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution2", resolution2);
        resolution3 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution3", resolution3);
        resolution4 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution4", resolution4);

        return resolution1 * 2 + cameraType1 + (resolution2 * 2 + cameraType2) * 6 + (resolution3 * 2 + cameraType3) * 36 + (resolution4 * 2 + cameraType4) * 216;
    }

    public int getInput2TypeNum() {
        cameraType5 = GUtilMain.mSharedPreferences.getInt("pre_camera_type5", cameraType5);
        cameraType6 = GUtilMain.mSharedPreferences.getInt("pre_camera_type6", cameraType6);
        resolution5 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution5", resolution5);
        resolution6 = GUtilMain.mSharedPreferences.getInt("pre_camera_resolution6", resolution6);

        return (resolution5 * 2 + cameraType5 + (resolution6 * 2 + cameraType6) * 6) + 1296;
    }

    public int getWidth(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                getResolution(1);
                width1 = resolution1 == 0 ? 1280 : (resolution1 == 1 ? 1920 : 960);
                return width1;
            case 2:
                getResolution(2);
                width2 = resolution2 == 0 ? 1280 : (resolution2 == 1 ? 1920 : 960);
                return width2;
            case 3:
                getResolution(3);
                width3 = resolution3 == 0 ? 1280 : (resolution3 == 1 ? 1920 : 960);
                return width3;
            case 4:
                getResolution(4);
                width4 = resolution4 == 0 ? 1280 : (resolution4 == 1 ? 1920 : 960);
                return width4;
            case 5:
                getResolution(5);
                width5 = resolution5 == 0 ? 1280 : (resolution5 == 1 ? 1920 : 960);
                return width5;
            case 6:
                getResolution(6);
                width6 = resolution6 == 0 ? 1280 : (resolution6 == 1 ? 1920 : 960);
                return width6;
            default:
                return -1;
        }
    }

    public int getHeight(int channel) {
        if (channel > 6 || channel < 1)
            return -1;
        switch (channel) {
            case 1:
                getResolution(1);
                getCameraType(1);
                height1 = resolution1 == 0 ? 720 : (resolution1 == 1 ? 1080 : (cameraType1 == 0 ? 288 : 240));
                return height1;
            case 2:
                getResolution(2);
                getCameraType(2);
                height2 = resolution2 == 0 ? 720 : (resolution2 == 1 ? 1080 : (cameraType2 == 0 ? 288 : 240));
                return height2;
            case 3:
                getResolution(3);
                getCameraType(3);
                height3 = resolution3 == 0 ? 720 : (resolution3 == 1 ? 1080 : (cameraType3 == 0 ? 288 : 240));
                return height3;
            case 4:
                getResolution(4);
                getCameraType(4);
                height4 = resolution4 == 0 ? 720 : (resolution4 == 1 ? 1080 : (cameraType4 == 0 ? 288 : 240));
                return height4;
            case 5:
                getResolution(5);
                getCameraType(5);
                height5 = resolution5 == 0 ? 720 : (resolution5 == 1 ? 1080 : (cameraType5 == 0 ? 288 : 240));
                return height5;
            case 6:
                getResolution(6);
                getCameraType(6);
                height6 = resolution6 == 0 ? 720 : (resolution6 == 1 ? 1080 : (cameraType6 == 0 ? 288 : 240));
                return height6;
            default:
                return -1;
        }
    }
}
