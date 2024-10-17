<p align="center">
  <img src="https://electrovese.com/images/home/Logo.png" alt="Electrovese Logo" width="300" style="background-color: white; border-radius: 15px; padding: 10px;"/>
</p>


# LinkedIn Share Flutter Plugin

The `linkedin_share` Flutter plugin enables seamless sharing of text, single files (images), and multiple files (images) directly to LinkedIn from your Flutter application. This plugin is designed for both Android and iOS platforms, making it simple to integrate LinkedIn sharing features into your app.

## Features

- Share text directly to LinkedIn.
- Share single files (e.g., images) with a caption.
- Share multiple files (e.g., images) with a caption.
- Easy-to-use API that integrates smoothly into your Flutter projects.

## Installation

1. Add `linkedin_share` to your `pubspec.yaml` file:

```yaml
dependencies:
  linkedin_share:
    git:
      url: https://github.com/electrovese/linkedin_share.git
```

2. Fetch the package using the command:

```bash
flutter pub get
```

## Platform Setup

### Android

1. Update the `AndroidManifest.xml` to add `FileProvider`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

2. Create the `res/xml/file_paths.xml` file in your Android project:

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="cache" path="." />
</paths>
```

3. Add required permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

### iOS

*Setup instructions for iOS coming soon...*

## Usage

### Import the Plugin

```dart
import 'package:linkedin_share/linkedin_share.dart';
```

### Share Text to LinkedIn

You can easily share text using the following method:

```dart
LinkedInShare.shareText("Check out my LinkedIn plugin!");
```

### Share a Single Image to LinkedIn

To share an image with some text, you can call the `shareFileWithText` method:

```dart
LinkedInShare.shareFileWithText(
  "Sharing this image on LinkedIn!",
  "/path/to/your/image.jpg"
);
```

### Share Multiple Files to LinkedIn

You can also share multiple images or files along with a text description:

```dart
LinkedInShare.shareMultipleFilesWithText(
  "Sharing multiple files to LinkedIn!",
  ["/path/to/first_image.jpg", "/path/to/second_image.jpg"]
);
```

## Example

Here’s an example Flutter app demonstrating how to use the plugin:

```dart
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:linkedin_share/linkedin_share.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _linkedinSharePlugin = LinkedinShare();

  Future<void> _shareText(BuildContext context) async {
    const textToShare = "Check out my LinkedIn plugin!";
    try {
      await _linkedinSharePlugin.shareText(textToShare);
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Text shared to LinkedIn!")));
    } catch (e) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text("Failed to share text.")));
    }
  }

  Future<void> _shareFile(BuildContext context) async {
    try {
      var image = await ImagePicker().pickImage(source: ImageSource.gallery);
      if (image == null) return;
      await _linkedinSharePlugin.shareFileWithText(
          "Here's a file I want to share:", image.path);
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("File shared to LinkedIn!")));
    } catch (e) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text("Failed to share file.")));
    }
  }

  Future<void> _shareMultipleFiles(BuildContext context) async {
    try {
      var images = await ImagePicker().pickMultiImage();
      List<String> imagePaths = images.map((image) => image.path).toList();
      if (imagePaths.isEmpty) return;
      await _linkedinSharePlugin.shareMultipleFilesWithText(
          "Here are some files I want to share:", imagePaths);
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Files shared to LinkedIn!")));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Failed to share files.")));
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('LinkedIn Share Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () => _shareText(context),
                child: const Text('Share Text to LinkedIn'),
              ),
              ElevatedButton(
                onPressed: () => _shareFile(context),
                child: const Text('Share File to LinkedIn'),
              ),
              ElevatedButton(
                onPressed: () => _shareMultipleFiles(context),
                child: const Text('Share Multiple Files to LinkedIn'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### Share Text Example

```dart
LinkedInShare.shareText("Check out this new post!");
```

### Share Image Example

```dart
LinkedInShare.shareFileWithText(
  "Sharing an image on LinkedIn!",
  "/path/to/image.jpg"
);
```

## Android Permissions

For Android versions lower than API 30, you may need to request permissions for reading external storage:

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

## Contributing

Contributions are welcome! If you encounter any issues or have ideas for improvements, please submit an issue or create a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

This `README.md` includes instructions for both Android setup and a placeholder for the iOS setup, providing a clear guide for users integrating the plugin.# linkedin-share
