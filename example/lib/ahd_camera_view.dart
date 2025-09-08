import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AHDCameraPlatformView extends StatelessWidget {
  final double? width;
  final double? height;

  const AHDCameraPlatformView({super.key, this.width, this.height});

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return SizedBox(
        width: width,
        height: height,
        child: const AndroidView(
          viewType: 'ahd_camera_view',
          creationParams: null,
          creationParamsCodec: StandardMessageCodec(),
        ),
      );
    }
    return const Center(child: Text('AHD Camera view is only available on Android'));
  }
} 