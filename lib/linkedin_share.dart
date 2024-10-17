import 'linkedin_share_platform_interface.dart';

class LinkedinShare {
  /// Shares text to LinkedIn.
  Future<void> shareText(String text) {
    return LinkedinSharePlatform.instance.shareText(text);
  }

  /// Shares a file with accompanying text to LinkedIn.
  Future<void> shareFileWithText(String text, String filePath) {
    return LinkedinSharePlatform.instance.shareFileWithText(text, filePath);
  }

  /// Shares multiple files with accompanying text to LinkedIn.
  Future<void> shareMultipleFilesWithText(String text, List<String> filePaths) {
    return LinkedinSharePlatform.instance.shareMultipleFilesWithText(text, filePaths);
  }

  /// Initiates login with LinkedIn.
  Future<bool> login() {
    return LinkedinSharePlatform.instance.login();
  }

  /// Logs out from LinkedIn.
  Future<void> logout() {
    return LinkedinSharePlatform.instance.logout();
  }

  /// Retrieves the package hash.
  Future<String?> showPackageHash() {
    return LinkedinSharePlatform.instance.showPackageHash();
  }
}
