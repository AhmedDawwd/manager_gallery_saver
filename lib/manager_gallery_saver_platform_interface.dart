import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'manager_gallery_saver_method_channel.dart';

abstract class ManagerGallerySaverPlatform extends PlatformInterface {
  /// Constructs a ManagerGallerySaverPlatform.
  ManagerGallerySaverPlatform() : super(token: _token);

  static final Object _token = Object();

  static ManagerGallerySaverPlatform _instance =
      MethodChannelManagerGallerySaver();

  /// The default instance of [ManagerGallerySaverPlatform] to use.
  ///
  /// Defaults to [MethodChannelManagerGallerySaver].
  static ManagerGallerySaverPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ManagerGallerySaverPlatform] when
  /// they register themselves.
  static set instance(ManagerGallerySaverPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<List<String>> getExternalStorageDirectories() {
    throw UnimplementedError(
        'getExternalStorageDirectories() has not been implemented.');
  }

  Future<String> getExternalStorageDefaultDirectoriesPath(String albumType) {
    throw UnimplementedError(
        'getExternalStorageDefaultDirectoriesPath() has not been implemented.');
  }

  Future<bool?> hasExternalStoragePrivateFile(
      {required String albumName, required String albumType}) {
    throw UnimplementedError(
        'hasExternalStoragePrivateFile() has not been implemented.');
  }

  Future<bool?> hasExternalStorageFileWithPath(
      {required String albumName, required String path}) {
    throw UnimplementedError(
        'hasExternalStorageFileWithPath() has not been implemented.');
  }

  Future<bool?> hasExternalStorageDirectoryWithPath({required String path}) {
    throw UnimplementedError(
        'hasExternalStorageDirectoryWithPath() has not been implemented.');
  }

  Future<bool?> createAlbum(
      {required String albumName, required String albumType}) {
    throw UnimplementedError('createAlbum() has not been implemented.');
  }

  Future<String?> getPathAlbum(
      {required String albumName, required String albumType}) {
    throw UnimplementedError('getPathAlbum() has not been implemented.');
  }

  Future<String?> getPathFileInAlbum(
      {required String albumName,
      required String albumType,
      required String fileName}) {
    throw UnimplementedError('getPathFileInAlbum() has not been implemented.');
  }

  Future<bool?> saveVideo({
    required String path,
    required String albumName,
    required String albumType,
    Map<String, String>? headers,
  }) {
    throw UnimplementedError('saveVideo() has not been implemented.');
  }

  Future<bool?> saveImage({
    required String path,
    required String albumName,
    required String albumType,
    Map<String, String>? headers,
  }) {
    throw UnimplementedError('saveImage() has not been implemented.');
  }

  Future<String?> getAlbumFolderPathWithCall(
      {required String albumName, required String albumType}) {
    throw UnimplementedError(
        'getAlbumFolderPathWithCall() has not been implemented.');
  }
}
