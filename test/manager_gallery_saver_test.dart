import 'package:flutter_test/flutter_test.dart';
import 'package:manager_gallery_saver/manager_gallery_saver.dart';
import 'package:manager_gallery_saver/manager_gallery_saver_platform_interface.dart';
import 'package:manager_gallery_saver/manager_gallery_saver_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockManagerGallerySaverPlatform
    with MockPlatformInterfaceMixin
    implements ManagerGallerySaverPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<bool?> createAlbum({String? albumName, String? albumType}) {
    // TODO: implement createAlbum
    throw UnimplementedError();
  }

  @override
  Future<String> getExternalStorageDefaultDirectoriesPath(String albumType) {
    // TODO: implement getExternalStorageDefaultDirectoriesPath
    throw UnimplementedError();
  }

  @override
  Future<List<String>> getExternalStorageDirectories() {
    // TODO: implement getExternalStorageDirectories
    throw UnimplementedError();
  }

  @override
  Future<String?> getPathAlbum({String? albumName, String? albumType}) {
    // TODO: implement getPathAlbum
    throw UnimplementedError();
  }

  @override
  Future<String?> getPathFileInAlbum(
      {String? albumName, String? albumType, String? fileName}) {
    // TODO: implement getPathFileInAlbum
    throw UnimplementedError();
  }

  @override
  Future<bool?> hasExternalStoragePrivateFile(
      {String? albumName, String? albumType}) {
    // TODO: implement hasExternalStoragePrivateFile
    throw UnimplementedError();
  }

  @override
  Future<String?> getAlbumFolderPathWithCall(
      {required String albumName, required String albumType}) {
    // TODO: implement getAlbumFolderPathWithCall
    throw UnimplementedError();
  }

  @override
  Future<bool?> hasExternalStorageDirectoryWithPath({required String path}) {
    // TODO: implement hasExternalStorageDirectoryWithPath
    throw UnimplementedError();
  }

  @override
  Future<bool?> hasExternalStorageFileWithPath(
      {required String albumName, required String path}) {
    // TODO: implement hasExternalStorageFileWithPath
    throw UnimplementedError();
  }

  @override
  Future<bool?> saveImage(
      {required String path,
      required String albumName,
      required String albumType,
      Map<String, String>? headers}) {
    // TODO: implement saveImage
    throw UnimplementedError();
  }

  @override
  Future<bool?> saveVideo(
      {required String path,
      required String albumName,
      required String albumType,
      Map<String, String>? headers}) {
    // TODO: implement saveVideo
    throw UnimplementedError();
  }
}

void main() {
  final ManagerGallerySaverPlatform initialPlatform =
      ManagerGallerySaverPlatform.instance;

  test('$MethodChannelManagerGallerySaver is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelManagerGallerySaver>());
  });

  test('getPlatformVersion', () async {
    ManagerGallerySaver managerGallerySaverPlugin = ManagerGallerySaver();
    MockManagerGallerySaverPlatform fakePlatform =
        MockManagerGallerySaverPlatform();
    ManagerGallerySaverPlatform.instance = fakePlatform;

    expect(await managerGallerySaverPlugin.getPlatformVersion(), '42');
  });
}
