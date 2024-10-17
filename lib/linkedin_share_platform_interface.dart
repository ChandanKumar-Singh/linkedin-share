import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'linkedin_share_method_channel.dart';

abstract class LinkedinSharePlatform extends PlatformInterface {
  /// Constructs a LinkedinSharePlatform.
  LinkedinSharePlatform() : super(token: _token);

  static final Object _token = Object();

  static LinkedinSharePlatform _instance = MethodChannelLinkedinShare();

  /// The default instance of [LinkedinSharePlatform] to use.
  ///
  /// Defaults to [MethodChannelLinkedinShare].
  static LinkedinSharePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [LinkedinSharePlatform] when
  /// they register themselves.
  static set instance(LinkedinSharePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Method to share text to LinkedIn. Must be implemented by subclasses.
  Future<void> shareText(String text) {
    throw UnimplementedError('shareText() has not been implemented.');
  }

  /// Method to share a file with accompanying text. Must be implemented by subclasses.
  Future<void> shareFileWithText(String text, String filePath) {
    throw UnimplementedError('shareFileWithText() has not been implemented.');
  }

  /// Method to share multiple files with accompanying text. Must be implemented by subclasses.
  Future<void> shareMultipleFilesWithText(String text, List<String> filePaths) {
    throw UnimplementedError('shareMultipleFilesWithText() has not been implemented.');
  }

  /// Method to initiate login with LinkedIn. Must be implemented by subclasses.
  Future<bool> login() {
    throw UnimplementedError('login() has not been implemented.');
  }

  /// Method to log out from LinkedIn. Must be implemented by subclasses.
  Future<void> logout() {
    throw UnimplementedError('logout() has not been implemented.');
  }

  /// Method to show the package hash. Must be implemented by subclasses.
  Future<String?> showPackageHash() {
    throw UnimplementedError('showPackageHash() has not been implemented.');
  }
}
