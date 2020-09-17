part of flutter_realm;


class MethodChannelAuth {
  final String appId;

  MethodChannelAuth(this.appId);

  Stream<MethodCall> get methodCallStream =>
      BaseMethodChannel._methodCallController.stream.where(_equalAppId);

  Future<T> invokeMethod<T>(String method, [Map arguments]) =>
      _baseChannel.invokeMethod<T>(method, _addAppId(arguments));

  Map _addAppId(Map arguments) {
    final map = (arguments ?? {});
    map['appId'] = appId;
    return map;
  }

  bool _equalAppId(MethodCall call) => call.arguments['appId'] == appId;

  static Future<void> reset() => _baseChannel.invokeMethod('reset');
}
