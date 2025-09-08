/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera.utils;

public class RecorderParams {
    private int codecTypePosition = 0;
    private int width = 1280, width1 = 1280, width2 = 1280, width3 = 1280, width4 = 1280, width5 = 1280, width6 = 1280;
    private int height = 720, height1 = 720, height2 = 720, height3 = 720, height4 = 720, height5 = 720, height6 = 720;
    private int csiNum = 2;
    private boolean recordState = false;

    public void setRecordState(boolean record){
        this.recordState = record;
        GUtilMain.mEditor.putBoolean("video_record", recordState);
        GUtilMain.mEditor.commit();
    }

    public boolean getRecordState(){
        recordState = GUtilMain.mSharedPreferences.getBoolean("video_record", recordState);
        return recordState;
    }

    private boolean videoMirror = false;
    public void setVidoeMirror(boolean mirror){
        this.videoMirror = mirror;
        GUtilMain.mEditor.putBoolean("video_mirror", videoMirror);
        GUtilMain.mEditor.commit();
    }
    public boolean getVideoMirror(){
        videoMirror = GUtilMain.mSharedPreferences.getBoolean("video_mirror", videoMirror);
        return videoMirror;
    }

    public int getWidth(int csi_num, int channel) {
        if (csi_num == 1){
            if (channel == 0){
                width1 = GUtilMain.mSharedPreferences.getInt("recorder_width1", width1);
                return width1;
            }else if (channel == 1){
                width2 = GUtilMain.mSharedPreferences.getInt("recorder_width2", width2);
                return width2;
            }else if (channel == 2){
                width3 = GUtilMain.mSharedPreferences.getInt("recorder_width3", width3);
                return width3;
            }else  if (channel == 3){
                width3 = GUtilMain.mSharedPreferences.getInt("recorder_width4", width3);
                return width3;
            }else{
                return 0;
            }
        }else if (csi_num == 2){
            if (channel == 0){
                width4 = GUtilMain.mSharedPreferences.getInt("recorder_width5", width4);
                return width4;
            }else if (channel == 1){
                width5 = GUtilMain.mSharedPreferences.getInt("recorder_width6", width5);
                return width5;
            }else {
                return 0;
            }
        }
        return 0;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight(int csi_num, int channel) {
        if (csi_num == 1){
            if (channel == 0){
                height1 = GUtilMain.mSharedPreferences.getInt("recorder_height1", height1);
                return height1;
            }else if (channel == 1){
                height2 = GUtilMain.mSharedPreferences.getInt("recorder_height2", height2);
                return height2;
            }else if (channel == 2){
                height3 = GUtilMain.mSharedPreferences.getInt("recorder_height3", height3);
                return height3;
            }else if (channel == 3){
                height3 = GUtilMain.mSharedPreferences.getInt("recorder_height4", height3);
                return height3;
            }else {
                return 0;
            }
        }else if (csi_num == 2){
            if (channel == 0){
                height4 = GUtilMain.mSharedPreferences.getInt("recorder_height4", height4);
                return height4;
            }else if (channel == 1){
                height5 = GUtilMain.mSharedPreferences.getInt("recorder_height6", height5);
                return height5;
            }else {
                return 0;
            }
        }
        return 0;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCodecTypePosition() {
        codecTypePosition = GUtilMain.mSharedPreferences.getInt("codec_type_position", codecTypePosition);
        return codecTypePosition;
    }
    public void setCodecTypePosition(int codecTypePosition) {
        this.codecTypePosition = codecTypePosition;
        GUtilMain.mEditor.putInt("codec_type_position", codecTypePosition);
        GUtilMain.mEditor.commit();
    }

    private int ssPosition = 5;  //分段录像位置
    public int getSegmentSizePosition() {
        ssPosition = GUtilMain.mSharedPreferences.getInt("ss_position", ssPosition);
        return ssPosition;
    }
    public void setSegmentSizePosition(int ssPosition) {
        this.ssPosition = ssPosition;
        GUtilMain.mEditor.putInt("ss_position", ssPosition);
        GUtilMain.mEditor.commit();
    }

    private int child_size = 0; //default 640x480
    private int vsPosition1 = 0,vsPosition2 = 0,vsPosition3 = 0,vsPosition4 = 0,vsPosition5 = 0,vsPosition6 = 0; //video分辨率位置

    public int getVsPosition(int channel) {
        if (channel<1 || channel>6){
            System.out.println("zyz --> channel is out of range 0-6 !");
            return -1;
        }
        switch (channel){
            case 1:
                vsPosition1 = GUtilMain.mSharedPreferences.getInt("vs_position1", vsPosition1);
                return vsPosition1;
            case 2:
                vsPosition2 = GUtilMain.mSharedPreferences.getInt("vs_position2", vsPosition2);
                return vsPosition2;
            case 3:
                vsPosition3 = GUtilMain.mSharedPreferences.getInt("vs_position3", vsPosition3);
                return vsPosition3;
            case 4:
                vsPosition4 = GUtilMain.mSharedPreferences.getInt("vs_position4", vsPosition4);
                return vsPosition4;
            case 5:
                vsPosition5 = GUtilMain.mSharedPreferences.getInt("vs_position5", vsPosition5);
                return vsPosition5;
            case 6:
                vsPosition6 = GUtilMain.mSharedPreferences.getInt("vs_position6", vsPosition6);
                return vsPosition6;
            default:
                return -1;
        }
    }

    public void setVsPosition(int vsPosition, int channel) {
        if (channel<1 || channel>6){
            System.out.println("zyz --> channel is out of range 0-6 !");
            return;
        }
        switch (channel){
            case 1:
                this.vsPosition1 = vsPosition;
                GUtilMain.mEditor.putInt("vs_position1", vsPosition);
                GUtilMain.mEditor.commit();
                break;
            case 2:
                this.vsPosition2 = vsPosition;
                GUtilMain.mEditor.putInt("vs_position2", vsPosition);
                GUtilMain.mEditor.commit();
                break;
            case 3:
                this.vsPosition3 = vsPosition;
                GUtilMain.mEditor.putInt("vs_position3", vsPosition);
                GUtilMain.mEditor.commit();
                break;
            case 4:
                this.vsPosition4 = vsPosition;
                GUtilMain.mEditor.putInt("vs_position4", vsPosition);
                GUtilMain.mEditor.commit();
                break;
            case 5:
                this.vsPosition5 = vsPosition;
                GUtilMain.mEditor.putInt("vs_position5", vsPosition);
                GUtilMain.mEditor.commit();
                break;
            case 6:
                this.vsPosition6 = vsPosition;
                GUtilMain.mEditor.putInt("vs_position6", vsPosition);
                GUtilMain.mEditor.commit();
                break;
        }
    }


    public void adjustResolutionWidthReValue(int index, int channel) {
        if (channel<1 || channel>6){
            System.out.println("zyz --> channel is out of range 0-6 !");
            return;
        }
        switch (channel){
            case 1:
                if (index==1) {
                    width1 = 1920;
                    height1 = 1080;
                } else if (index==0) {
                    width1 = 1280;
                    height1 = 720;
                }else if (index==3){
                    width1 = 960;
                    height1 = 240;
                }else if (index==2){
                    width1 = 960;
                    height1 = 280;
                }
                GUtilMain.mEditor.putInt("recorder_width1", width1);
                GUtilMain.mEditor.putInt("recorder_height1", height1);
                break;
            case 2:
                if (index==1) {
                    width2 = 1920;
                    height2 = 1080;
                } else if (index==0) {
                    width2 = 1280;
                    height2 = 720;
                }else if (index==3){
                    width2 = 960;
                    height2 = 240;
                }else if (index==2){
                    width2 = 960;
                    height2 = 280;
                }
                GUtilMain.mEditor.putInt("recorder_width2", width2);
                GUtilMain.mEditor.putInt("recorder_height2", height2);
                break;
            case 3:
                if (index==1) {
                    width3 = 1920;
                    height3 = 1080;
                } else if (index==0) {
                    width3 = 1280;
                    height3 = 720;
                }else if (index==3){
                    width3 = 960;
                    height3 = 240;
                }else if (index==2){
                    width3 = 960;
                    height3 = 280;
                }
                GUtilMain.mEditor.putInt("recorder_width3", width3);
                GUtilMain.mEditor.putInt("recorder_height3", height3);
                break;
            case 4:
                if (index==1) {
                    width4 = 1920;
                    height4 = 1080;
                } else if (index==0) {
                    width4 = 1280;
                    height4 = 720;
                }else if (index==3){
                    width4 = 960;
                    height4 = 240;
                }else if (index==2){
                    width4 = 960;
                    height4 = 280;
                }
                GUtilMain.mEditor.putInt("recorder_width4", width4);
                GUtilMain.mEditor.putInt("recorder_height4", height4);
                break;
            case 5:
                if (index==1) {
                    width5 = 1920;
                    height5 = 1080;
                } else if (index==0) {
                    width5 = 1280;
                    height5 = 720;
                }else if (index==3){
                    width5 = 960;
                    height5 = 240;
                }else if (index==2){
                    width5 = 960;
                    height5 = 280;
                }
                GUtilMain.mEditor.putInt("recorder_width5", width5);
                GUtilMain.mEditor.putInt("recorder_height5", height5);
                break;
            case 6:
                if (index==1) {
                    width6 = 1920;
                    height6 = 1080;
                } else if (index==0) {
                    width6 = 1280;
                    height6 = 720;
                }else if (index==3){
                    width6 = 960;
                    height6 = 240;
                }else if (index==2){
                    width6 = 960;
                    height6 = 280;
                }
                GUtilMain.mEditor.putInt("recorder_width6", width6);
                GUtilMain.mEditor.putInt("recorder_height6", height6);
                break;
        }
        GUtilMain.mEditor.commit();
    }

    public int getChild_size() {
        child_size = GUtilMain.mSharedPreferences.getInt("child_size", child_size);
        return child_size;
    }
    public void setChild_size(int child_size) {
        this.child_size = child_size;
        GUtilMain.mEditor.putInt("child_size", child_size);
        GUtilMain.mEditor.commit();
    }

    private int recorderNums = 0; //默认录像的个数为0，需要选标准录像或者四合一录像
    public int getRecorderNums() {
        recorderNums = GUtilMain.mSharedPreferences.getInt("recorder_nums", recorderNums);
        return recorderNums;
    }
    public void setRecorderNums(int recorderNums) {
        this.recorderNums = recorderNums;
        GUtilMain.mEditor.putInt("recorder_nums", recorderNums);
        GUtilMain.mEditor.commit();
    }

    private boolean recordState1=false,recordState2=false,recordState3=false,recordState4=false,recordState5=false,recordState6=false;
    public boolean getRecordState(int channel){
        switch (channel){
            case 1:
                recordState1 = GUtilMain.mSharedPreferences.getBoolean("recorder_state_channe_1", recordState1);
                return recordState1;
            case 2:
                recordState2 = GUtilMain.mSharedPreferences.getBoolean("recorder_state_channe_2", recordState2);
                return recordState2;
            case 3:
                recordState3 = GUtilMain.mSharedPreferences.getBoolean("recorder_state_channe_3", recordState3);
                return recordState3;
            case 4:
                recordState4 = GUtilMain.mSharedPreferences.getBoolean("recorder_state_channe_4", recordState4);
                return recordState4;
            case 5:
                recordState5 = GUtilMain.mSharedPreferences.getBoolean("recorder_state_channe_5", recordState5);
                return recordState5;
            case 6:
                recordState6 = GUtilMain.mSharedPreferences.getBoolean("recorder_state_channe_6", recordState6);
                return recordState6;
            default:
                return false;
        }
    }
    public void setRecordState(int channel, boolean state){
        switch (channel){
            case 1:
                this.recordState1 = state;
                GUtilMain.mEditor.putBoolean("recorder_state_channe_1", recordState1);
                break;
            case 2:
                this.recordState2 = state;
                GUtilMain.mEditor.putBoolean("recorder_state_channe_2", recordState2);
                break;
            case 3:
                this.recordState3 = state;
                GUtilMain.mEditor.putBoolean("recorder_state_channe_3", recordState3);
                break;
            case 4:
                this.recordState4 = state;
                GUtilMain.mEditor.putBoolean("recorder_state_channe_4", recordState4);
                break;
            case 5:
                this.recordState5 = state;
                GUtilMain.mEditor.putBoolean("recorder_state_channe_5", recordState5);
                break;
            case 6:
                this.recordState6 = state;
                GUtilMain.mEditor.putBoolean("recorder_state_channe_6", recordState6);
                break;
        }
        GUtilMain.mEditor.commit();
    }

    private boolean childRecordEnable = false; //默认不开启录像子码流
    public boolean isChildRecordEnable() {
        childRecordEnable = GUtilMain.mSharedPreferences.getBoolean("child_record_enable", childRecordEnable);
        return childRecordEnable;
    }

    public void setChildRecordEnable(boolean childRecordEnable) {
        this.childRecordEnable = childRecordEnable;
        GUtilMain.mEditor.putBoolean("child_record_enable", childRecordEnable);
        GUtilMain.mEditor.commit();
    }

    private boolean audioRecordEnable = false; //默认不开启录像子码流
    public boolean isAudioRecordEnable() {
        audioRecordEnable = GUtilMain.mSharedPreferences.getBoolean("audio_record_enable", audioRecordEnable);
        return audioRecordEnable;
    }

    public void setAudioRecordEnable(boolean audioRecordEnable) {
        this.audioRecordEnable = audioRecordEnable;
        GUtilMain.mEditor.putBoolean("audio_record_enable", audioRecordEnable);
        GUtilMain.mEditor.commit();
    }

    private int ctPosition = 0; //录像文件格式位置
    public int getCtPosition() {
        ctPosition = GUtilMain.mSharedPreferences.getInt("ct_position", ctPosition);
        return ctPosition;
    }
    public void setCtPosition(int ctPosition) {
        this.ctPosition = ctPosition;
        GUtilMain.mEditor.putInt("ct_position", ctPosition);
        GUtilMain.mEditor.commit();
    }

    private int mainRatePosition = 0; //主码率
    public int getMainRatePosition() {
        mainRatePosition = GUtilMain.mSharedPreferences.getInt("main_rate_position", mainRatePosition);
        return mainRatePosition;
    }
    public void setMainRatePosition(int mrPosition) {
        this.mainRatePosition = mrPosition;
        GUtilMain.mEditor.putInt("main_rate_position", mainRatePosition);
        GUtilMain.mEditor.commit();
    }

    private int subRatePosition = 0; //子码率
    public int getSubRatePosition() {
        subRatePosition = GUtilMain.mSharedPreferences.getInt("sub_rate_position", subRatePosition);
        return subRatePosition;
    }
    public void setSubRatePosition(int srPosition) {
        this.subRatePosition = srPosition;
        GUtilMain.mEditor.putInt("sub_rate_position", subRatePosition);
        GUtilMain.mEditor.commit();
    }

    public void setCsiNum(int csiNum) {
        this.csiNum = csiNum;
        GUtilMain.mEditor.putInt("recorder_csi_num", csiNum);
        GUtilMain.mEditor.commit();
    }

    public int getCsiNum() {
        csiNum = GUtilMain.mSharedPreferences.getInt("recorder_csi_num", csiNum);
        return csiNum;
    }
}
