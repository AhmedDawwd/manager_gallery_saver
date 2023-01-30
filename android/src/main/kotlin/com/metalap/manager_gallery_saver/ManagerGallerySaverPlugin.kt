package com.metalap.manager_gallery_saver

import android.app.Activity
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** ManagerGallerySaverPlugin */
class ManagerGallerySaverPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var activity: Activity? = null
  private var managerGallerySaver: ManagerGallerySaver? = null
  private  var manageStorageDirectories :ManageDirectories?=null
  private  var filesUtils : FilesUtils? =null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "manager_gallery_saver")
    channel.setMethodCallHandler(this)
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
//    if (call.method == "getPlatformVersion") {
//      result.success("Android ${android.os.Build.VERSION.RELEASE}")
//    } else {
//      result.notImplemented()
//    }

    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "saveImage" -> managerGallerySaver?.checkPermissionAndSaveFile(call, result, MediaType.Image)
      "saveVideo" -> managerGallerySaver?.checkPermissionAndSaveFile(call, result, MediaType.Video)
      "getExternalStorageDirectories" -> manageStorageDirectories?.getExternalStorageDirectories()
      // "getExternalStorageDefaultDirectoriesPath" -> manageStorageDirectories?.getExternalStorageDefaultDirectoriesPath(call)
      "getPathAlbum" -> manageStorageDirectories?.getPathAlbum(call)
      "getPathFileInAlbum" -> manageStorageDirectories?.getPathFileInAlbum(call)
      "createAlbum" -> manageStorageDirectories?.createAlbum(call)
      "hasExternalStorageDirectoryWithPath" -> manageStorageDirectories?.hasExternalStorageDirectoryWithPath(call)
      "hasExternalStorageFileWithPath" -> manageStorageDirectories?.hasExternalStorageFileWithPath(call)
      "hasExternalStoragePrivateFile" -> manageStorageDirectories?.hasExternalStoragePrivateFile(call)
      "getAlbumFolderPath" -> filesUtils?.getAlbumFolderPathWithCall(call)
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity
    managerGallerySaver = ManagerGallerySaver(activity!!)
    binding.addRequestPermissionsResultListener(managerGallerySaver!!)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }
}
