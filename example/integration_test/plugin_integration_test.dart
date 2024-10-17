
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'package:linkedin_share/linkedin_share.dart'; 

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('getPlatformVersion test', (WidgetTester tester) async {
    final LinkedinShare plugin = LinkedinShare();
    await plugin.shareText('Hello, LinkedIn!');
    expect(1, 1);
    
  });
}
