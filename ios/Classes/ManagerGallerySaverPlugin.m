#import "ManagerGallerySaverPlugin.h"
#if __has_include(<manager_gallery_saver/manager_gallery_saver-Swift.h>)
#import <manager_gallery_saver/manager_gallery_saver-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "manager_gallery_saver-Swift.h"
#endif

@implementation ManagerGallerySaverPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftManagerGallerySaverPlugin registerWithRegistrar:registrar];
}
@end
