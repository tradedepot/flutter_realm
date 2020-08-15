part of flutter_realm;

final MethodChannel _channel =
    const MethodChannel('plugins.it_nomads.com/flutter_realm')
      ..setMethodCallHandler(BaseMethodChannel._handleMethodCall);

class BaseMethodChannel {
  // ignore: close_sinks
  static final _methodCallController = StreamController<MethodCall>.broadcast();

  static Future<dynamic> _handleMethodCall(MethodCall call) {
    print("GOT METHOD CALL: $call");
    _methodCallController.add(call);
    return null;
  }
}
