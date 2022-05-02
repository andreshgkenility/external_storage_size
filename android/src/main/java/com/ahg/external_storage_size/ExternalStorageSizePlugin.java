package com.ahg.external_storage_size;

import android.os.Environment;
import android.os.StatFs;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** ExternalStorageSizePlugin */
public class ExternalStorageSizePlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "external_storage_size");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      return;
    }
    if (call.method.equals("getExternalStorageSize")) {
      
      result.success(getAvailableExternalMemorySizeString());
      return;
    }
      result.notImplemented();

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private static long getAvailableExternalMemorySize() {
    File path = Environment.getExternalStorageDirectory();
    StatFs stat = new StatFs(path.getPath());
    long blockSize = stat.getBlockSize();
    long availableBlocks = stat.getAvailableBlocks();
    return availableBlocks * blockSize;
  }

  public static String getAvailableExternalMemorySizeString() {
    if (externalMemoryAvailable()) {
      return formatSize(getAvailableExternalMemorySize()) + " free";
    } else {
      return "Not present";
    }
  }

  public static boolean externalMemoryAvailable() {
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      // We can read and write the media
      mExternalStorageAvailable = mExternalStorageWriteable = true;
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      // We can only read the media
      mExternalStorageAvailable = true;
      mExternalStorageWriteable = false;
    } else {
      // Something else is wrong. It may be one of many other states, but all we need
      //  to know is we can neither read nor write
      mExternalStorageAvailable = mExternalStorageWriteable = false;
    }

    return mExternalStorageAvailable;
  }

  public static String formatSize(long bytes) {
    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
    if (absB < 1024) {
      return bytes + " B";
    }
    long value = absB;
    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
      value >>= 10;
      ci.next();
    }
    value *= Long.signum(bytes);
    return String.format("%.1f %ciB", value / 1024.0, ci.current());
  }
}
