import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:manager_gallery_saver/manager_gallery_saver_method_channel.dart';

void main() {
  MethodChannelManagerGallerySaver platform = MethodChannelManagerGallerySaver();
  const MethodChannel channel = MethodChannel('manager_gallery_saver');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
