import 'manager_gallery_saver_platform_interface.dart';
import 'utlis.dart';

class ManagerGallerySaver {
  Future<String?> getPlatformVersion() {
    return ManagerGallerySaverPlatform.instance.getPlatformVersion();
  }

  Future<List<String>> getExternalStorageDirectories() async {
    return await ManagerGallerySaverPlatform.instance
        .getExternalStorageDirectories();
  }

  Future<String> getExternalStorageDefaultDirectoriesPath(
      {required Environment environment}) async {
    return await ManagerGallerySaverPlatform.instance
        .getExternalStorageDefaultDirectoriesPath(
            getEnvironmentDir(environment));
  }

  Future<bool?> createAlbum({
    required String albumName,
    required Environment environment,
  }) async {
    return await ManagerGallerySaverPlatform.instance.createAlbum(
        albumName: albumName, albumType: getEnvironmentDir(environment));
  }

  Future<String?> getPathAlbum({
    required String albumName,
    required Environment environment,
  }) async {
    return await ManagerGallerySaverPlatform.instance.getPathAlbum(
        albumName: albumName, albumType: getEnvironmentDir(environment));
  }

  Future<String?> getPathFileInAlbum(
      {required String albumName,
      required Environment environment,
      required String fileName}) async {
    return await ManagerGallerySaverPlatform.instance.getPathFileInAlbum(
        albumName: albumName,
        albumType: getEnvironmentDir(environment),
        fileName: fileName);
  }

  Future<bool?> saveVideo({
    required String path,
    required String albumName,
    required Environment environment,
    Map<String, String>? headers,
  }) async {
    return await ManagerGallerySaverPlatform.instance.saveVideo(
        path: path,
        albumName: albumName,
        albumType: getEnvironmentDir(environment));
  }

  Future<bool?> saveImage({
    required String path,
    required String albumName,
    required Environment environment,
    Map<String, String>? headers,
  }) async {
    return await ManagerGallerySaverPlatform.instance.saveImage(
        path: path,
        albumName: albumName,
        albumType: getEnvironmentDir(environment),
        headers: headers);
  }

  Future<bool?> hasExternalStorageDirectoryWithPath({
    required String path,
  }) async {
    return await ManagerGallerySaverPlatform.instance
        .hasExternalStorageDirectoryWithPath(
      path: path,
    );
  }

  Future<bool?> hasExternalStorageFileWithPath({
    required String albumName,
    required String path,
  }) async {
    return await ManagerGallerySaverPlatform.instance
        .hasExternalStorageFileWithPath(
      path: path,
      albumName: albumName,
    );
  }

  Future<bool?> hasExternalStoragePrivateFile({
    required String albumName,
    required Environment environment,
  }) async {
    return await ManagerGallerySaverPlatform.instance
        .hasExternalStoragePrivateFile(
            albumName: albumName, albumType: getEnvironmentDir(environment));
  }

  Future<String?> getAlbumFolderPathWithCall({
    required String albumName,
    required Environment environment,
  }) async {
    return await ManagerGallerySaverPlatform.instance
        .getAlbumFolderPathWithCall(
            albumName: albumName, albumType: getEnvironmentDir(environment));
  }
}
