// import 'package:flutter_test/flutter_test.dart';
// import 'package:linkedin_share/linkedin_share.dart';
// import 'package:linkedin_share/linkedin_share_platform_interface.dart';
// import 'package:linkedin_share/linkedin_share_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockLinkedinSharePlatform
//     with MockPlatformInterfaceMixin
//     implements LinkedinSharePlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final LinkedinSharePlatform initialPlatform = LinkedinSharePlatform.instance;

//   test('$MethodChannelLinkedinShare is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelLinkedinShare>());
//   });

//   test('getPlatformVersion', () async {
//     LinkedinShare linkedinSharePlugin = LinkedinShare();
//     MockLinkedinSharePlatform fakePlatform = MockLinkedinSharePlatform();
//     LinkedinSharePlatform.instance = fakePlatform;

//     expect(await linkedinSharePlugin.getPlatformVersion(), '42');
//   });
// }
