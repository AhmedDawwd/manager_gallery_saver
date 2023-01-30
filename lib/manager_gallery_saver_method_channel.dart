import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'manager_gallery_saver_platform_interface.dart';
import 'utlis.dart';

/// An implementation of [ManagerGallerySaverPlatform] that uses method channels.
class MethodChannelManagerGallerySaver extends ManagerGallerySaverPlatform {
  String pleaseProvidePath = 'Please provide valid file path.';
  String fileIsNotVideo = 'File on path is not a video.';
  String fileIsNotImage = 'File on path is not an image.';

  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('manager_gallery_saver');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<List<String>> getExternalStorageDirectories() async {
    final List externalStorageDirs =
        await methodChannel.invokeMethod('getExternalStorageDirectories');

    List<String> storageInfos = externalStorageDirs
        .map((storageInfoMap) => ExStoragePath01.getRootDir(storageInfoMap))
        .toList();
    return storageInfos;
  }

  @override
  Future<String> getExternalStorageDefaultDirectoriesPath(
      String albumType) async {
    return await methodChannel.invokeMethod(
        'getExternalStorageDefaultDirectoriesPath', {'albumType': albumType});
  }

  @override
  Future<bool?> createAlbum({
    String? albumName,
    String? albumType,
  }) async {
    return await methodChannel.invokeMethod(
        'createAlbum', {'albumName': albumName, 'albumType': albumType});
  }

  @override
  Future<String?> getPathAlbum({
    String? albumName,
    String? albumType,
  }) async {
    return await methodChannel.invokeMethod(
        'getPathAlbum', {'albumName': albumName, 'albumType': albumType});
  }

  @override
  Future<String?> getPathFileInAlbum(
      {required String albumName,
      required String albumType,
      required String fileName}) async {
    return await methodChannel.invokeMethod('getPathFileInAlbum',
        {'albumName': albumName, 'albumType': albumType, 'fileName': fileName});
  }

  @override
  Future<bool?> saveImage({
    required String path,
    required String albumName,
    required String albumType,
    Map<String, String>? headers,
  }) async {
    File? tempFile;
    if (path.isEmpty) {
      throw ArgumentError(pleaseProvidePath);
    }
    if (!isImage(path)) {
      throw ArgumentError(fileIsNotImage);
    }
    // if (!isLocalFilePath(path)) {
    //   tempFile = await _downloadFile(path, headers: headers);
    //   path = tempFile.path;
    // }

    bool? result = await methodChannel.invokeMethod(
      'saveImage',
      <String, dynamic>{
        'path': path,
        'albumName': albumName,
        'albumType': albumType,
      },
    );
    if (tempFile != null) {
      tempFile.delete();
    }

    return result;
  }

  ///saves video from provided temp path and optional album name in gallery
  @override
  Future<bool?> saveVideo({
    required String path,
    required String albumName,
    required String albumType,
    Map<String, String>? headers,
  }) async {
    File? tempFile;
    if (path.isEmpty) {
      throw ArgumentError(pleaseProvidePath);
    }
    if (!isVideo(path)) {
      throw ArgumentError(fileIsNotVideo);
    }
    // if (!isLocalFilePath(path)) {
    //   tempFile = await _downloadFile(path, headers: headers);
    //   path = tempFile.path;
    // }
    bool? result = await methodChannel.invokeMethod(
      'saveVideo',
      <String, dynamic>{
        'path': path,
        'albumName': albumName,
        'albumType': albumType,
      },
    );
    if (tempFile != null) {
      tempFile.delete();
    }
    return result;
  }

  @override
  Future<bool?> hasExternalStorageDirectoryWithPath(
      {required String path}) async {
    return await methodChannel
        .invokeMethod('hasExternalStorageDirectoryWithPath', {'path': path});
  }

  @override
  Future<bool?> hasExternalStorageFileWithPath(
      {required String albumName, required String path}) async {
    return await methodChannel.invokeMethod('hasExternalStorageFileWithPath',
        {'albumName': albumName, 'path': path});
  }

  @override
  Future<bool?> hasExternalStoragePrivateFile(
      {required String albumName, required String albumType}) async {
    return await methodChannel.invokeMethod('hasExternalStoragePrivateFile',
        {'albumName': albumName, 'albumType': albumType});
  }

  @override
  Future<String?> getAlbumFolderPathWithCall(
      {required String albumName, required String albumType}) async {
    return await methodChannel.invokeMethod('getAlbumFolderPathWithCall', {
      'albumName': albumName,
      'albumType': albumType,
    });
  }
}

class ExStoragePath01 {
  static String getRootDir(String appFilesDir) {
    return appFilesDir
        .split("/")
        .sublist(0, appFilesDir.split("/").length - 4)
        .join("/");
  }
}
