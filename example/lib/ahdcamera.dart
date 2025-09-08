import 'package:flutter/services.dart';

class AHDCamera {
  static const MethodChannel _channel = MethodChannel('ahd_camera_sdk');

  static Future<bool> initialize() async {
    try {
      return await _channel.invokeMethod('initializeCamera');
    } on PlatformException catch (e) {
      print("Failed to initialize camera: '${e.message}'");
      return false;
    }
  }

  static Future<bool> startPreview() async {
    try {
      return await _channel.invokeMethod('startPreview');
    } on PlatformException catch (e) {
      print("Failed to start preview: '${e.message}'");
      return false;
    }
  }

  static Future<bool> stopPreview() async {
    try {
      return await _channel.invokeMethod('stopPreview');
    } on PlatformException catch (e) {
      print("Failed to stop preview: '${e.message}'");
      return false;
    }
  }

  static Future<Uint8List?> captureImage() async {
    try {
      return await _channel.invokeMethod('captureImage');
    } on PlatformException catch (e) {
      print("Failed to capture image: '${e.message}'");
      return null;
    }
  }
}