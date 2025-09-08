/*
 * Copyright (c) 2021, Quectel Wireless Solutions Co., Ltd. All rights reserved.
 * Quectel Wireless Solutions Proprietary and Confidential.
 */
package com.quectel.multicamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.quectel.qcarapi.stream.QCarCamera;
import com.quectel.qcarapi.util.QCarLog;


public class SurfaceViewFragment extends Fragment {
    private static final String TAG = "PreviewFragment";
    //    private SurfaceView preview;
    private TextureView preview;
    private SurfaceHolder surfaceHolder;
    private int mChannel;
    private QCarCamera qCarCamera;
    private int mIsPreview;
    private int preWidth;
    private int preHeight;


    public SurfaceViewFragment() {

    }

    @SuppressLint("ValidFragment")
    public SurfaceViewFragment(QCarCamera qCarCamera, int channel, int ispreview) {
        this.qCarCamera = qCarCamera;
        this.mChannel = channel;
        this.mIsPreview = ispreview;
    }

    public void setPreviewSize(int width, int height) {
        preWidth = width;
        preHeight = height;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (qCarCamera != null) {
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "get csi num " + qCarCamera.getCsiNum() + " channel " + mChannel + " IsPreview " + mIsPreview);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_surface_view, container, false);
        preview = (TextureView) rootView.findViewById(R.id.preview);
        preview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                if (mIsPreview == 1) {
                    Surface mSurface = new Surface(surfaceTexture);
                    qCarCamera.startPreview(mChannel, mSurface, preWidth, preHeight, QCarCamera.YUV420_NV21);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (mIsPreview == 1) {
                    qCarCamera.stopPreview(mChannel);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    Log.e(TAG, "sleep exception");
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "onDestroy");
        super.onDestroy();
    }
}
