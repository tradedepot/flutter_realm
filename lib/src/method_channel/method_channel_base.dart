part of flutter_realm;

final MethodChannel _baseChannel =
    const MethodChannel('plugins.it_nomads.com/flutter_realm')
      ..setMethodCallHandler(BaseMethodChannel._handleMethodCall);

class BaseMethodChannel {
  // ignore: close_sinks
  static final _methodCallController = StreamController<MethodCall>.broadcast();

  static Future<dynamic> _handleMethodCall(MethodCall call) {
    _methodCallController.add(call);
    return null;
  }
}
