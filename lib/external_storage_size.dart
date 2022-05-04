import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class ExternalStorageSize {
  static const MethodChannel _channel = MethodChannel('external_storage_size');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> get externalStorageSize async {
    if (Platform.isIOS) throw Exception("Not supported for iOS.");
    final String? result = await _channel.invokeMethod(
      'getExternalStorageSize',
    );
    return result;
  }
}
