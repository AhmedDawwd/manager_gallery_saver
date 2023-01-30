const List<String> videoFormats = [
  '.mp4',
  '.mov',
  '.avi',
  '.wmv',
  '.3gp',
  '.3gpp',
  '.mkv',
  '.flv'
];
const List<String> imageFormats = [
  '.jpeg',
  '.png',
  '.jpg',
  '.gif',
  '.webp',
  '.tif',
  '.heic'
];
const http = 'http';

bool isLocalFilePath(String path) {
  Uri uri = Uri.parse(path);
  return !uri.scheme.contains(http);
}

bool isVideo(String path) {
  bool output = false;
  videoFormats.forEach((videoFormat) {
    if (path.toLowerCase().contains(videoFormat)) output = true;
  });
  return output;
}

bool isImage(String path) {
  bool output = false;
  imageFormats.forEach((imageFormat) {
    if (path.toLowerCase().contains(imageFormat)) output = true;
  });
  return output;
}

enum Environment {
  // DIRECTORY_MUSIC,
  // DIRECTORY_PODCASTS,
  // DIRECTORY_RINGTONES,
  // DIRECTORY_ALARMS,
  // DIRECTORY_NOTIFICATIONS,
  DIRECTORY_PICTURES,
  DIRECTORY_MOVIES,
  //DIRECTORY_DOWNLOADS,
  DIRECTORY_DCIM,
  // DIRECTORY_DOCUMENTS,
  // DIRECTORY_SCREENSHOTS,
  // DIRECTORY_AUDIOBOOKS
}

// const String dirMUSIC = 'DIRECTORY_MUSIC';
// const String dirPODCASTS = 'DIRECTORY_PODCASTS';
// const String dirRINGTONES = 'DIRECTORY_RINGTONES';
// const String dirALARMS = 'DIRECTORY_ALARMS';
// const String dirNOTIFICATIONS = 'DIRECTORY_NOTIFICATIONS';
const String dirPICTURES = 'DIRECTORY_PICTURES';
const String dirMOVIES = 'DIRECTORY_MOVIES';
//const String dirDOWNLOADS = 'DIRECTORY_DOWNLOADS';
const String dirDCIM = 'DIRECTORY_DCIM';
// const String dirDOCUMENTS = 'DIRECTORY_DOCUMENTS';
// const String dirSCREENSHOTS = 'DIRECTORY_SCREENSHOTS';
// const String dirAUDIOBOOKS = 'DIRECTORY_AUDIOBOOKS';

String getEnvironmentDir(Environment environment) {
  switch (environment) {
    case Environment.DIRECTORY_DCIM:
      return dirDCIM;
    // case Environment.DIRECTORY_ALARMS:
    //   return dirALARMS;
    // case Environment.DIRECTORY_AUDIOBOOKS:
    //   return dirAUDIOBOOKS;
    // case Environment.DIRECTORY_DOCUMENTS:
    //   return dirDOCUMENTS;
    // case Environment.DIRECTORY_DOWNLOADS:
    //   return dirDOWNLOADS;
    case Environment.DIRECTORY_MOVIES:
      return dirMOVIES;
    // case Environment.DIRECTORY_MUSIC:
    //   return dirMUSIC;
    // case Environment.DIRECTORY_NOTIFICATIONS:
    //   return dirNOTIFICATIONS;
    case Environment.DIRECTORY_PICTURES:
      return dirPICTURES;
    // case Environment.DIRECTORY_RINGTONES:
    //   return dirRINGTONES;
    // case Environment.DIRECTORY_PODCASTS:
    //   return dirPODCASTS;
    // case Environment.DIRECTORY_SCREENSHOTS:
    //   return dirSCREENSHOTS;
    default:
      return throw UnimplementedError('null String');
  }
}
