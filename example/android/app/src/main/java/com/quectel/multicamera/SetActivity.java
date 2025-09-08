/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.PreviewParams;
import com.quectel.multicamera.utils.RecorderParams;
import com.quectel.qcarapi.util.QCarLog;


import java.util.ArrayList;
import java.util.List;

public class SetActivity extends AppCompatActivity {
    private String TAG = "SetActivity";
    private Spinner preNumSpinner;
    private Spinner csiNumSpinner;

    //Video encoder
    private Spinner childRecorderSizeSpinner;
    private Spinner recorderSSP;  //分段大小选项
    private Spinner codecSpinner; //编码格式选项
    private Spinner containerSpinner; //录像格式
    private Spinner mainstreamerSpinner; //主码率
    private Spinner substreamerSpinner; //次码率

    private Switch switch_cr; //是否开启录像子码流
    private Switch switch_audio; //是否开启录像音频

    /**************************四合一录制参数配置****************************/
    private Spinner csiNumMergeSpinner;
    private Spinner recorderResolutionMergeSpinner;
    private Spinner recorderSMergeSPinner;  //分段大小选项
    private Spinner codecMergeSpinner; //编码格式选项
    private Spinner containerMergeSpinner; //录像格式
    private Spinner bitrateMergeSpinner; //主码率
    private Switch audioMergeSw; //是否开启录像音频
    private Switch subMergeSwitch;
    private Spinner subBitrateMergeSpinner;
    private Spinner subSizeMergeSpinner;
    /**************************四合一录制参数配置****************************/

    private Switch debugEnableSwitch;

    private RecorderParams rParams = GUtilMain.getRecorderParams();
    private static RecorderParams rMParams = GUtilMain.getMergeRecorderParams();
    private PreviewParams pParams = GUtilMain.getPreviewParams();
    private Spinner cameraTypeSpinner1, cameraTypeSpinner2, cameraTypeSpinner3, cameraTypeSpinner4, cameraTypeSpinner5, cameraTypeSpinner6;
    private Spinner preResolutionSpinner1, preResolutionSpinner2, preResolutionSpinner3, preResolutionSpinner4, preResolutionSpinner5, preResolutionSpinner6;
    private int ch0res, ch0type, ch1res, ch1type, ch2res, ch2type, ch3res, ch3type, ch4res, ch4type, ch5res, ch5type;
    private int ch0Rec, ch1Rec, ch2Rec, ch3Rec, ch4Rec, ch5Rec;
    private int previewCSI = 2;
    private int previewNum = 4;
    private LinearLayout mLinPre2, mLinPre3, mLinPre4, mLinPre5, mLinPre6, mLinRec2, mLinRec3, mLinRec4, mLinRec5, mLinRec6;
    private Spinner videoResolutionSpinner1, videoResolutionSpinner2, videoResolutionSpinner3, videoResolutionSpinner4, videoResolutionSpinner5, videoResolutionSpinner6;
    private CheckBox recordChannel1, recordChannel2, recordChannel3, recordChannel4, recordChannel5, recordChannel6;
    private boolean isRec1, isRec2, isRec3, isRec4, isRec5, isRec6, isVideoMirror, isVideoRec,isVideoSub,isVideoAudio;
    private Switch videoMirror, videoRec;
    private int codec,mainkps,container,subkps,child,segmentSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_set);
        preNumSpinner = (Spinner) findViewById(R.id.spinner3);
        mLinPre2 = (LinearLayout) findViewById(R.id.channel2);
        mLinPre3 = (LinearLayout) findViewById(R.id.channel3);
        mLinPre4 = (LinearLayout) findViewById(R.id.channel4);
        mLinPre5 = (LinearLayout) findViewById(R.id.channel5);
        mLinPre6 = (LinearLayout) findViewById(R.id.channel6);
        mLinRec2 = (LinearLayout) findViewById(R.id.rChannel2);
        mLinRec3 = (LinearLayout) findViewById(R.id.rChannel3);
        mLinRec4 = (LinearLayout) findViewById(R.id.rChannel4);
        mLinRec5 = (LinearLayout) findViewById(R.id.rChannel5);
        mLinRec6 = (LinearLayout) findViewById(R.id.rChannel6);
        previewNum = pParams.getPreviewNum();
        preNumSpinner.setSelection(previewNum == 1 ? 0 : (previewNum == 2 ? 1 : (previewNum == 4 ? 2 : 3)));
        if (previewNum == 1) {
            mLinPre2.setVisibility(View.GONE);
            mLinPre3.setVisibility(View.GONE);
            mLinPre4.setVisibility(View.GONE);
            mLinPre5.setVisibility(View.GONE);
            mLinPre6.setVisibility(View.GONE);
            mLinRec2.setVisibility(View.GONE);
            mLinRec3.setVisibility(View.GONE);
            mLinRec4.setVisibility(View.GONE);
            mLinRec5.setVisibility(View.GONE);
            mLinRec6.setVisibility(View.GONE);
        } else if (previewNum == 2) {
            mLinPre3.setVisibility(View.GONE);
            mLinPre4.setVisibility(View.GONE);
            mLinPre5.setVisibility(View.GONE);
            mLinPre6.setVisibility(View.GONE);
            mLinRec3.setVisibility(View.GONE);
            mLinRec4.setVisibility(View.GONE);
            mLinRec5.setVisibility(View.GONE);
            mLinRec6.setVisibility(View.GONE);
        } else if (previewNum == 4) {
            mLinPre5.setVisibility(View.GONE);
            mLinPre6.setVisibility(View.GONE);
            mLinRec5.setVisibility(View.GONE);
            mLinRec6.setVisibility(View.GONE);
        }
        csiNumSpinner = (Spinner) findViewById(R.id.csiNumSpinner);


        debugEnableSwitch = (Switch) findViewById(R.id.global_debug_switch);

        /********************** Mdvx Read Test: END ********************************/
        previewCSI = pParams.getCsiNum();
        csiNumSpinner.setSelection(previewCSI);
        csiNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pParams.setCsiNum(position);
                rParams.setCsiNum(position);
                previewCSI = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        preNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "选择了=" + position);
                if (position == 0) {
                    previewNum = 1;
                    mLinPre2.setVisibility(View.GONE);
                    mLinPre3.setVisibility(View.GONE);
                    mLinPre4.setVisibility(View.GONE);
                    mLinPre5.setVisibility(View.GONE);
                    mLinPre6.setVisibility(View.GONE);
                    mLinRec2.setVisibility(View.GONE);
                    mLinRec3.setVisibility(View.GONE);
                    mLinRec4.setVisibility(View.GONE);
                    mLinRec5.setVisibility(View.GONE);
                    mLinRec6.setVisibility(View.GONE);
                } else if (position == 1) {
                    previewNum = 2;
                    mLinPre2.setVisibility(View.VISIBLE);
                    mLinPre3.setVisibility(View.GONE);
                    mLinPre4.setVisibility(View.GONE);
                    mLinPre5.setVisibility(View.GONE);
                    mLinPre6.setVisibility(View.GONE);
                    mLinRec2.setVisibility(View.VISIBLE);
                    mLinRec3.setVisibility(View.GONE);
                    mLinRec4.setVisibility(View.GONE);
                    mLinRec5.setVisibility(View.GONE);
                    mLinRec6.setVisibility(View.GONE);
                } else if (position == 2) {
                    previewNum = 4;
                    mLinPre2.setVisibility(View.VISIBLE);
                    mLinPre3.setVisibility(View.VISIBLE);
                    mLinPre4.setVisibility(View.VISIBLE);
                    mLinPre5.setVisibility(View.GONE);
                    mLinPre6.setVisibility(View.GONE);
                    mLinRec2.setVisibility(View.VISIBLE);
                    mLinRec3.setVisibility(View.VISIBLE);
                    mLinRec4.setVisibility(View.VISIBLE);
                    mLinRec5.setVisibility(View.GONE);
                    mLinRec6.setVisibility(View.GONE);
                } else if (position == 3) {
                    previewNum = 6;
                    mLinPre2.setVisibility(View.VISIBLE);
                    mLinPre3.setVisibility(View.VISIBLE);
                    mLinPre4.setVisibility(View.VISIBLE);
                    mLinPre5.setVisibility(View.VISIBLE);
                    mLinPre6.setVisibility(View.VISIBLE);
                    mLinRec2.setVisibility(View.VISIBLE);
                    mLinRec3.setVisibility(View.VISIBLE);
                    mLinRec4.setVisibility(View.VISIBLE);
                    mLinRec5.setVisibility(View.VISIBLE);
                    mLinRec6.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        debugEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    QCarLog.setTagLogLevel(Log.DEBUG);
                } else {
                    QCarLog.setTagLogLevel(Log.INFO);
                }
            }
        });

        csiNumMergeSpinner = (Spinner) findViewById(R.id.csiNumMergeSpinner);
        recorderResolutionMergeSpinner = (Spinner) findViewById(R.id.recorderResolutionMergeSpinner);
        csiNumMergeSpinner.setEnabled(false);
        recorderResolutionMergeSpinner.setEnabled(false);
        recorderSMergeSPinner = (Spinner) findViewById(R.id.recorderSMergeSpinner);
        codecMergeSpinner = (Spinner) findViewById(R.id.codecMergeSpinner);
        containerMergeSpinner = (Spinner) findViewById(R.id.containerMergeSpinner);
        bitrateMergeSpinner = (Spinner) findViewById(R.id.bitrateMergeSpinner);
        audioMergeSw = (Switch) findViewById(R.id.audioMergeSw);
        subMergeSwitch = (Switch) findViewById(R.id.subMergeSwitch);
        subSizeMergeSpinner = (Spinner) findViewById(R.id.subSizeMergeSpinner);
        subBitrateMergeSpinner = (Spinner) findViewById(R.id.subBitrateMergeSpinner);

        csiNumMergeSpinner.setSelection(rMParams.getCsiNum());
//        recorderResolutionMergeSpinner.setSelection(rMParams.getVsPosition());
        recorderSMergeSPinner.setSelection(rMParams.getSegmentSizePosition());
        codecMergeSpinner.setSelection(rMParams.getCodecTypePosition());
        containerMergeSpinner.setSelection(rMParams.getCtPosition());
        bitrateMergeSpinner.setSelection(rMParams.getMainRatePosition());
        audioMergeSw.setChecked(rMParams.isAudioRecordEnable());
        subMergeSwitch.setChecked(rMParams.isChildRecordEnable());
        subSizeMergeSpinner.setSelection(rMParams.getChild_size());
        subBitrateMergeSpinner.setSelection(rMParams.getSubRatePosition());

        csiNumMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setCsiNum(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        recorderResolutionMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                rMParams.setVsPosition(position);
//                rMParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        recorderSMergeSPinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setSegmentSizePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        codecMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setCodecTypePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        containerMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setCtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bitrateMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setMainRatePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        audioMergeSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch (buttonView.getId()) {
//                    case R.id.audioMergeSw:
                if (isChecked) {
                    rMParams.setAudioRecordEnable(true);
                } else {
                    rMParams.setAudioRecordEnable(false);
                }
//                }
            }
        });

        subMergeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch (buttonView.getId()) {
//                    case R.id.subMergeSwitch:
                if (isChecked) {
                    rMParams.setChildRecordEnable(true);
                } else {
                    rMParams.setChildRecordEnable(false);
                }
//                }
            }
        });

        subBitrateMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setSubRatePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        subSizeMergeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rMParams.setChild_size(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ch0res = pParams.getResolution(1);
        ch1res = pParams.getResolution(2);
        ch2res = pParams.getResolution(3);
        ch3res = pParams.getResolution(4);
        ch4res = pParams.getResolution(5);
        ch5res = pParams.getResolution(6);
        ch0type = pParams.getCameraType(1);
        ch1type = pParams.getCameraType(2);
        ch2type = pParams.getCameraType(3);
        ch3type = pParams.getCameraType(4);
        ch4type = pParams.getCameraType(5);
        ch5type = pParams.getCameraType(6);
        cameraTypeSpinner1 = (Spinner) findViewById(R.id.cameraTypeSpinner1);
        cameraTypeSpinner2 = (Spinner) findViewById(R.id.cameraTypeSpinner2);
        cameraTypeSpinner3 = (Spinner) findViewById(R.id.cameraTypeSpinner3);
        cameraTypeSpinner4 = (Spinner) findViewById(R.id.cameraTypeSpinner4);
        cameraTypeSpinner5 = (Spinner) findViewById(R.id.cameraTypeSpinner5);
        cameraTypeSpinner6 = (Spinner) findViewById(R.id.cameraTypeSpinner6);
        cameraTypeSpinner1.setSelection(ch0type);
        cameraTypeSpinner2.setSelection(ch1type);
        cameraTypeSpinner3.setSelection(ch2type);
        cameraTypeSpinner4.setSelection(ch3type);
        cameraTypeSpinner5.setSelection(ch4type);
        cameraTypeSpinner6.setSelection(ch5type);
        cameraTypeSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("qwe", "onItemSelected: ------------" + position);
                ch0type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch1type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch2type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch3type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cameraTypeSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch4type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cameraTypeSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch5type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        preResolutionSpinner1 = (Spinner) findViewById(R.id.spinnerChannel1);
        preResolutionSpinner2 = (Spinner) findViewById(R.id.spinnerChannel2);
        preResolutionSpinner3 = (Spinner) findViewById(R.id.spinnerChannel3);
        preResolutionSpinner4 = (Spinner) findViewById(R.id.spinnerChannel4);
        preResolutionSpinner5 = (Spinner) findViewById(R.id.spinnerChannel5);
        preResolutionSpinner6 = (Spinner) findViewById(R.id.spinnerChannel6);
        videoResolutionSpinner1 = (Spinner) findViewById(R.id.rSpinnerChannel1);
        videoResolutionSpinner2 = (Spinner) findViewById(R.id.rSpinnerChannel2);
        videoResolutionSpinner3 = (Spinner) findViewById(R.id.rSpinnerChannel3);
        videoResolutionSpinner4 = (Spinner) findViewById(R.id.rSpinnerChannel4);
        videoResolutionSpinner5 = (Spinner) findViewById(R.id.rSpinnerChannel5);
        videoResolutionSpinner6 = (Spinner) findViewById(R.id.rSpinnerChannel6);
        recordChannel1 = (CheckBox) findViewById(R.id.recordChannel1);
        recordChannel2 = (CheckBox) findViewById(R.id.recordChannel2);
        recordChannel3 = (CheckBox) findViewById(R.id.recordChannel3);
        recordChannel4 = (CheckBox) findViewById(R.id.recordChannel4);
        recordChannel5 = (CheckBox) findViewById(R.id.recordChannel5);
        recordChannel6 = (CheckBox) findViewById(R.id.recordChannel6);
        isRec1 = rParams.getRecordState(1);
        isRec2 = rParams.getRecordState(2);
        isRec3 = rParams.getRecordState(3);
        isRec4 = rParams.getRecordState(4);
        isRec5 = rParams.getRecordState(5);
        isRec6 = rParams.getRecordState(6);
        recordChannel1.setChecked(isRec1);
        recordChannel2.setChecked(isRec2);
        recordChannel3.setChecked(isRec3);
        recordChannel4.setChecked(isRec4);
        recordChannel5.setChecked(isRec5);
        recordChannel6.setChecked(isRec6);
        preResolutionSpinner1.setSelection(ch0res);
        preResolutionSpinner2.setSelection(ch1res);
        preResolutionSpinner3.setSelection(ch2res);
        preResolutionSpinner4.setSelection(ch3res);
        preResolutionSpinner5.setSelection(ch4res);
        preResolutionSpinner6.setSelection(ch5res);
        videoMirror = (Switch) findViewById(R.id.video_mir_switch);
        videoRec = (Switch) findViewById(R.id.record_video_switch);
        isVideoMirror = rMParams.getVideoMirror();
        isVideoRec = rMParams.getRecordState();
        videoMirror.setChecked(isVideoMirror);
        videoRec.setChecked(isVideoRec);
        videoMirror.setOnCheckedChangeListener((compoundButton, b) -> {
            isVideoMirror = b;
        });
        videoRec.setOnCheckedChangeListener((compoundButton, b) -> {
            isVideoRec = b;
        });
        preResolutionSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ch0res = i;
                if (i == 2) {
                    if (cameraTypeSpinner1.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner1.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner1.setSelection(2);
                        }
                    } else {
                        if (videoResolutionSpinner1.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner1.setSelection(3);
                        }
                    }
                } else if (i == 0) {
                    if (videoResolutionSpinner1.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner1.setSelection(0);
                    }
                } else if (i == 1) {
                    if (videoResolutionSpinner1.getSelectedItemPosition() == 2 || videoResolutionSpinner1.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner1.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        preResolutionSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ch1res = i;
                if (i == 2) {
                    if (cameraTypeSpinner2.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner2.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner2.setSelection(2);
                        }
                    } else {
                        if (videoResolutionSpinner2.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner2.setSelection(3);
                        }
                    }
                } else if (i == 0) {
                    if (videoResolutionSpinner2.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner2.setSelection(0);
                    }
                } else if (i == 1) {
                    if (videoResolutionSpinner2.getSelectedItemPosition() == 2 || videoResolutionSpinner2.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner2.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        preResolutionSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ch2res = i;
                if (i == 2) {
                    if (cameraTypeSpinner3.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner3.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner3.setSelection(2);
                        }
                    } else {
                        if (videoResolutionSpinner3.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner3.setSelection(3);
                        }
                    }
                } else if (i == 0) {
                    if (videoResolutionSpinner3.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner3.setSelection(0);
                    }
                } else if (i == 1) {
                    if (videoResolutionSpinner3.getSelectedItemPosition() == 2 || videoResolutionSpinner3.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner3.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        preResolutionSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ch3res = i;
                if (i == 2) {
                    if (cameraTypeSpinner4.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner4.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner4.setSelection(2);
                        }
                    } else {
                        if (videoResolutionSpinner4.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner4.setSelection(3);
                        }
                    }
                } else if (i == 0) {
                    if (videoResolutionSpinner4.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner4.setSelection(0);
                    }
                } else if (i == 1) {
                    if (videoResolutionSpinner4.getSelectedItemPosition() == 2 || videoResolutionSpinner4.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner4.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        preResolutionSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ch4res = i;
                if (i == 2) {
                    if (cameraTypeSpinner5.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner5.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner5.setSelection(2);
                        }
                    } else {
                        if (videoResolutionSpinner5.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner5.setSelection(3);
                        }
                    }
                } else if (i == 0) {
                    if (videoResolutionSpinner5.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner5.setSelection(0);
                    }
                } else if (i == 1) {
                    if (videoResolutionSpinner5.getSelectedItemPosition() == 2 || videoResolutionSpinner5.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner5.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        preResolutionSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ch5res = i;
                if (i == 2) {
                    if (cameraTypeSpinner6.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner6.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner6.setSelection(2);
                        }
                    } else {
                        if (videoResolutionSpinner6.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner6.setSelection(3);
                        }
                    }
                } else if (i == 0) {
                    if (videoResolutionSpinner6.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner6.setSelection(0);
                    }
                } else if (i == 1) {
                    if (videoResolutionSpinner6.getSelectedItemPosition() == 2 || videoResolutionSpinner6.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner6.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ch0Rec = rParams.getVsPosition(1);
        ch1Rec = rParams.getVsPosition(2);
        ch2Rec = rParams.getVsPosition(3);
        ch3Rec = rParams.getVsPosition(4);
        ch4Rec = rParams.getVsPosition(5);
        ch5Rec = rParams.getVsPosition(6);
        videoResolutionSpinner1.setSelection(ch0Rec);
        videoResolutionSpinner2.setSelection(ch1Rec);
        videoResolutionSpinner3.setSelection(ch2Rec);
        videoResolutionSpinner4.setSelection(ch3Rec);
        videoResolutionSpinner5.setSelection(ch4Rec);
        videoResolutionSpinner6.setSelection(ch5Rec);
        videoResolutionSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 1 选择了=" + position);
                if (preResolutionSpinner1.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner1.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner1.setSelection(ch0Rec);
                            Toast.makeText(SetActivity.this, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner1.setSelection(ch0Rec);
                            Toast.makeText(SetActivity.this, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner1.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner1.setSelection(ch0Rec);
                        Toast.makeText(SetActivity.this, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner1.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner1.setSelection(ch0Rec);
                        Toast.makeText(SetActivity.this, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ch0Rec = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 2 选择了=" + position);
                if (preResolutionSpinner2.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner2.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner2.setSelection(ch1Rec);
                            Toast.makeText(SetActivity.this, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner2.setSelection(ch1Rec);
                            Toast.makeText(SetActivity.this, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner2.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner2.setSelection(ch1Rec);
                        Toast.makeText(SetActivity.this, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner2.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner2.setSelection(ch1Rec);
                        Toast.makeText(SetActivity.this, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ch1Rec = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 3 选择了=" + position);
                if (preResolutionSpinner3.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner3.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner3.setSelection(ch2Rec);
                            Toast.makeText(SetActivity.this, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner3.setSelection(ch2Rec);
                            Toast.makeText(SetActivity.this, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner3.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner3.setSelection(ch2Rec);
                        Toast.makeText(SetActivity.this, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner3.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner3.setSelection(ch2Rec);
                        Toast.makeText(SetActivity.this, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ch2Rec = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 4 选择了=" + position);
                if (preResolutionSpinner4.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner4.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner4.setSelection(ch3Rec);
                            Toast.makeText(SetActivity.this, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner4.setSelection(ch3Rec);
                            Toast.makeText(SetActivity.this, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner4.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner4.setSelection(ch3Rec);
                        Toast.makeText(SetActivity.this, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner4.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner4.setSelection(ch3Rec);
                        Toast.makeText(SetActivity.this, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ch3Rec = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 5 选择了=" + position);
                if (preResolutionSpinner5.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner5.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner5.setSelection(ch4Rec);
                            Toast.makeText(SetActivity.this, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner5.setSelection(ch4Rec);
                            Toast.makeText(SetActivity.this, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner5.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner5.setSelection(ch4Rec);
                        Toast.makeText(SetActivity.this, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner5.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner5.setSelection(ch4Rec);
                        Toast.makeText(SetActivity.this, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ch4Rec = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 6 选择了=" + position);
                if (preResolutionSpinner6.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner6.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner6.setSelection(ch5Rec);
                            Toast.makeText(SetActivity.this, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner6.setSelection(ch5Rec);
                            Toast.makeText(SetActivity.this, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner6.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner6.setSelection(ch5Rec);
                        Toast.makeText(SetActivity.this, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner6.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner6.setSelection(ch5Rec);
                        Toast.makeText(SetActivity.this, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                ch5Rec = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recordChannel1.setOnCheckedChangeListener((compoundButton, b) -> {
            isRec1 = b;
        });
        recordChannel2.setOnCheckedChangeListener((compoundButton, b) -> {
            isRec2 = b;
        });
        recordChannel3.setOnCheckedChangeListener((compoundButton, b) -> {
            isRec3 = b;
        });
        recordChannel4.setOnCheckedChangeListener((compoundButton, b) -> {
            isRec4 = b;
        });
        recordChannel5.setOnCheckedChangeListener((compoundButton, b) -> {
            isRec5 = b;
        });
        recordChannel6.setOnCheckedChangeListener((compoundButton, b) -> {
            isRec6 = b;
        });

        // Video encoder
        codec=rMParams.getCodecTypePosition();
        mainkps=rMParams.getMainRatePosition();
        container=rMParams.getCtPosition();
        subkps=rMParams.getSubRatePosition();
        isVideoSub=rMParams.isChildRecordEnable();
        child=rMParams.getChild_size();
        segmentSize=rMParams.getSegmentSizePosition();
        isVideoAudio=rMParams.isAudioRecordEnable();

        childRecorderSizeSpinner = (Spinner) findViewById(R.id.child_spinner);
        codecSpinner = (Spinner) findViewById(R.id.codecSpinner);
        containerSpinner = (Spinner) findViewById(R.id.containerSpinner);
        recorderSSP = (Spinner) findViewById(R.id.recorderSSpinner);
        mainstreamerSpinner = (Spinner) findViewById(R.id.mainkps); //主码率
        substreamerSpinner = (Spinner) findViewById(R.id.subkps);   //次码率
        switch_cr = (Switch) findViewById(R.id.switch_1);
        switch_audio = (Switch) findViewById(R.id.audiosw);

        switch_cr.setChecked(isVideoSub);
        switch_audio.setChecked(isVideoAudio);
        childRecorderSizeSpinner.setSelection(child);
        recorderSSP.setSelection(segmentSize);
        mainstreamerSpinner.setSelection(mainkps);
        substreamerSpinner.setSelection(subkps);
        codecSpinner.setSelection(codec);
        containerSpinner.setSelection(container);

        childRecorderSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                child=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recorderSSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                segmentSize=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mainstreamerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainkps=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        substreamerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subkps=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        codecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codec=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switch_cr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isVideoSub=isChecked;
            }
        });
        switch_audio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isVideoAudio=isChecked;
            }
        });

        containerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                container=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void save(View view) {
        showDialog();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                pParams.setResolution(1, ch0res);
                pParams.setResolution(2, ch1res);
                pParams.setResolution(3, ch2res);
                pParams.setResolution(4, ch3res);
                pParams.setResolution(5, ch4res);
                pParams.setResolution(6, ch5res);

                pParams.setCameraType(1, ch0type);
                pParams.setCameraType(2, ch1type);
                pParams.setCameraType(3, ch2type);
                pParams.setCameraType(4, ch3type);
                pParams.setCameraType(5, ch4type);
                pParams.setCameraType(6, ch5type);

                pParams.setCsiNum(previewCSI);
                pParams.setPreviewNum(previewNum);

                rMParams.setRecordState(1, isRec1);
                rMParams.setRecordState(2, isRec2);
                rMParams.setRecordState(3, isRec3);
                rMParams.setRecordState(4, isRec4);
                rMParams.setRecordState(5, isRec5);
                rMParams.setRecordState(6, isRec6);
                rMParams.setRecorderNums((isRec1?1:0)+(isRec2?1:0)+(isRec3?1:0)+(isRec4?1:0)+(isRec5?1:0)+(isRec6?1:0));
                rMParams.setVsPosition(ch0Rec, 1);
                rMParams.setVsPosition(ch1Rec, 2);
                rMParams.setVsPosition(ch2Rec, 3);
                rMParams.setVsPosition(ch3Rec, 4);
                rMParams.setVsPosition(ch4Rec, 5);
                rMParams.setVsPosition(ch5Rec, 6);
                rMParams.adjustResolutionWidthReValue(ch0Rec,1);
                rMParams.adjustResolutionWidthReValue(ch1Rec,2);
                rMParams.adjustResolutionWidthReValue(ch2Rec,3);
                rMParams.adjustResolutionWidthReValue(ch3Rec,4);
                rMParams.adjustResolutionWidthReValue(ch4Rec,5);
                rMParams.adjustResolutionWidthReValue(ch5Rec,6);

                rMParams.setRecordState(isVideoRec);
                rMParams.setVidoeMirror(isVideoMirror);

                rMParams.setCodecTypePosition(codec);
                rMParams.setMainRatePosition(mainkps);
                rMParams.setCtPosition(container);
                rMParams.setSubRatePosition(subkps);
                rMParams.setChildRecordEnable(isVideoSub);
                rMParams.setChild_size(child);
                rMParams.setSegmentSizePosition(segmentSize);
                rMParams.setAudioRecordEnable(isVideoAudio);


                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "restart");
                setResult(10086, returnIntent);
                finish();
            }
        }, 500);

    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();// 得到属性
        params.gravity = Gravity.CENTER;// 显示在中间
        params.width = (int) (1280 * 0.9);// 设置对话框的宽度为手机屏幕的0.8
        params.height = (int) (720 * 0.8);// 设置对话框的高度为手机屏幕的0.25
        dialog.getWindow().setAttributes(params);// 設置屬性
        dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.show_tip_dialog, null));
        TextView textView = dialog.getWindow().findViewById(R.id.message);
        textView.setText("Savings changes. Please wait.");
    }
}
