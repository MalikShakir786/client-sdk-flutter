import 'dart:typed_data';

import 'package:flutter/material.dart';

import 'ahdcamera.dart';

class CameraPreviewScreen extends StatefulWidget {
  @override
  _CameraPreviewScreenState createState() => _CameraPreviewScreenState();
}

class _CameraPreviewScreenState extends State<CameraPreviewScreen> {
  bool _isCameraInitialized = false;

  @override
  void initState() {
    super.initState();
    _initializeCamera();
  }

  Future<void> _initializeCamera() async {
    bool initialized = await AHDCamera.initialize();
    if (initialized) {
      setState(() {
        _isCameraInitialized = true;
      });
      await AHDCamera.startPreview();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('AHD Camera')),
      body: Center(
        child: _isCameraInitialized
            ? Container()
            : CircularProgressIndicator(),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _captureImage,
        child: Icon(Icons.camera),
      ),
    );
  }

  void _captureImage() async {
    Uint8List? imageData = await AHDCamera.captureImage();
    if (imageData != null) {
      // Handle captured image
    }
  }
}