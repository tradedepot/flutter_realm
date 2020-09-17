part of flutter_realm;


class MethodChannelRealm {
  final String realmId;

  MethodChannelRealm(this.realmId);

  Stream<MethodCall> get methodCallStream =>
      BaseMethodChannel._methodCallController.stream.where(_equalRealmId);

  Future<T> invokeMethod<T>(String method, [Map arguments]) =>
      _baseChannel.invokeMethod<T>(method, _addRealmId(arguments));

  Map _addRealmId(Map arguments) {
    final map = (arguments ?? {});
    map['realmId'] = realmId;
    return map;
  }

  bool _equalRealmId(MethodCall call) => call.arguments['realmId'] == realmId;

  static Future<void> reset() => _baseChannel.invokeMethod('reset');
}
