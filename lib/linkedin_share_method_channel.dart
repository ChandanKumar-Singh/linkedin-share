import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'linkedin_share_platform_interface.dart';

/// An implementation of [LinkedinSharePlatform] that uses method channels.
class MethodChannelLinkedinShare extends LinkedinSharePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('linkedin_share');
  final sessionMethodChannel = const MethodChannel('linkedin_session');

  @override
  Future<void> shareText(String text) async {
    try {
      await methodChannel.invokeMethod('shareText', {'text': text});
    } on PlatformException catch (e) {
      debugPrint("Failed to share text: '${e.message}'.");
    }
  }

  @override
  Future<void> shareFileWithText(String text, String filePath) async {
    try {
      await methodChannel.invokeMethod('shareFileWithText', {
        'text': text,
        'fileUri': filePath,
      });
    } on PlatformException catch (e) {
      debugPrint("Failed to share file with text: '${e.message}'.");
    }
  }

  @override
  Future<void> shareMultipleFilesWithText(
      String text, List<String> filePaths) async {
    try {
      await methodChannel.invokeMethod('shareMultipleFilesWithText', {
        'text': text,
        'fileUris': filePaths,
      });
    } on PlatformException catch (e) {
      debugPrint("Failed to share multiple files with text: '${e.message}'.");
    }
  }

  /// Method to initiate login with LinkedIn.
  @override
  Future<bool> login() async {
    try {
      var res =await sessionMethodChannel.invokeMethod('login');
      print('$runtimeType  res: $res');
      return true;
    } on PlatformException catch (e) {
      debugPrint("Login failed: '${e.message}'.");
      return false;
    }
  }

  /// Method to log out from LinkedIn.
  @override
  Future<void> logout() async {
    try {
      await sessionMethodChannel.invokeMethod('logout');
    } on PlatformException catch (e) {
      debugPrint("Logout failed: '${e.message}'.");
    }
  }

  /// Method to show the package hash.
  @override
  Future<String?> showPackageHash() async {
    try {
      final String? packageHash =
          await sessionMethodChannel.invokeMethod('showPackageHash');
      return packageHash;
    } on PlatformException catch (e) {
      debugPrint("Failed to get package hash: '${e.message}'.");
      return null;
    }
  }
}
