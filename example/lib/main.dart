// ignore_for_file: use_build_context_synchronously

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:async';
import 'package:linkedin_share/linkedin_share.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override 
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _linkedinSharePlugin = LinkedinShare();

  @override
  void initState() {
    super.initState();
  }

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

  Future<void> shareMultipleImages(BuildContext context) async {
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

  Future<void> _login(BuildContext context) async {
    try {
    var res =  await _linkedinSharePlugin.login();
      if(res) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Logged in to LinkedIn!")));
      }
    } catch (e) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text("Failed to log in.")));
    }
  }

  Future<void> _logout(BuildContext context) async {
    try {
      await _linkedinSharePlugin.logout();
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Logged out from LinkedIn!")));
    } catch (e) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text("Failed to log out.")));
    }
  }

  Future<void> _showPackageHash(BuildContext context) async {
    try {
      String? hash = await _linkedinSharePlugin.showPackageHash();
      print("Package Hash: $hash");
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text("Package Hash: $hash")));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Failed to show package hash.")));
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Builder(builder: (context) {
        return Scaffold(
          appBar: AppBar(
            title: const Text('LinkedIn Share Plugin Example'),
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
                  onPressed: () => shareMultipleImages(context),
                  child: const Text('Share Multiple Files to LinkedIn'),
                ),
                Divider(),
                ElevatedButton(
                  onPressed: () => _login(context),
                  child: const Text('Login to LinkedIn'),
                ),
                ElevatedButton(
                  onPressed: () => _logout(context),
                  child: const Text('Logout from LinkedIn'),
                ),
                ElevatedButton(
                  onPressed: () => _showPackageHash(context),
                  child: const Text('Show Package Hash'),
                ),
              ],
            ),
          ),
        );
      }),
    );
  }
}
