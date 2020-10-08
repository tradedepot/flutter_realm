part of flutter_realm;

class RealmAuth {
  final String appId;

  static Map<String, StreamController<RealmUserAuthState>>
      _authStateChangesListeners =
      <String, StreamController<RealmUserAuthState>>{};

  static Map<String, RealmAuth> _authInstances = <String, RealmAuth>{};

  static RealmAuth instanceFor(String appId) {
    // _auth != null ? _auth :
    RealmAuth auth = _authInstances[appId];
    if (auth != null) {
      return auth;
    }
    _authInstances[appId] = RealmAuth._(appId);
    return _authInstances[appId];
  }

  final MethodChannelAuth _channel;
  static bool _initialized = false;

  RealmAuth._(this.appId) : _channel = MethodChannelAuth(appId) {
    _authStateChangesListeners[appId] =
        StreamController<RealmUserAuthState>.broadcast();
    if (_initialized) return;
    _channel.invokeMethod<Map>('Auth#registerChangeListeners');
    _channel.methodCallStream.listen(_handleMethodCall);
    _initialized = true;
  }

  Stream<RealmUserAuthState> authStateChanges() =>
      _authStateChangesListeners[appId].stream;

  void _handleMethodCall(MethodCall call) {
    switch (call.method) {
      case 'Auth#onLoggedIn':
        final appId = call.arguments['appId'];
        final userMap = call.arguments['user'];
        // ignore: close_sinks
        final StreamController<RealmUserAuthState> streamController =
            _authStateChangesListeners[appId];
        final RealmUser user =
            userMap != null ? RealmUser._fromMap(userMap) : null;
        streamController
            .add(RealmUserAuthState._(user: user, isLoggedIn: true));
        break;
      case 'Auth#onLoggedOut':
        final appId = call.arguments['appId'];
        final userMap = call.arguments['user'];
        final RealmUser user =
            userMap != null ? RealmUser._fromMap(userMap) : null;
        // ignore: close_sinks
        final StreamController<RealmUserAuthState> streamController =
            _authStateChangesListeners[appId];
        streamController
            .add(RealmUserAuthState._(user: user, isLoggedIn: false));
        break;
      default:
        throw ('Unknown method: $call');
        break;
    }
  }

  Future<RealmUser> logInWithJWTToken({
    @required String token,
  }) async {
    final userMap = await _channel.invokeMethod<Map>(
      'Auth#logInWithJWTToken',
      {
        'token': token,
      },
    );
    return userMap == null ? null : RealmUser._fromMap(userMap);
  }

  Future<RealmUser> currentUser() async {
    final userMap = await _channel.invokeMethod<Map>('Auth#currentUser');
    return userMap == null ? null : RealmUser._fromMap(userMap);
  }

  Future<void> logOut() {
    return _channel.invokeMethod('Auth#logOut');
  }
}
