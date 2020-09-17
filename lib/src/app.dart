part of flutter_realm;

final _uuid = Uuid();

class RealmApp {
  static bool _initialized = false;
  final String appId;

  static RealmApp _instance;
  final MethodChannelAuth _channel;
  StreamController<RealmUserAuthState> _authStateChangesListeners;

  static RealmApp get instance {
    assert(_instance != null);
    return _instance;
  }

  RealmApp._(this.appId)
      : _channel = MethodChannelAuth(appId) {
    _authStateChangesListeners =
        StreamController<RealmUserAuthState>.broadcast();
    if (_initialized) return;
    _channel.invokeMethod<Map>('Auth#registerChangeListeners');
    _channel.methodCallStream.listen(_handleMethodCall);
  }

  Stream<RealmUserAuthState> authStateChanges() =>
      _authStateChangesListeners.stream;

  static Future<RealmApp> initialize(String appId) async {
    if (_initialized) return RealmApp._(appId);
    await _baseChannel.invokeMethod('initialize', {"appId": appId});
    _instance = RealmApp._(appId);
    _initialized = true;
    return _instance;
  }

  void _handleMethodCall(MethodCall call) {
    switch (call.method) {
      case 'Auth#onLoggedIn':
        final userMap = call.arguments['user'];
        // ignore: close_sinks
        final StreamController<RealmUserAuthState> streamController =
            _authStateChangesListeners;
        final RealmUser user =
            userMap != null ? RealmUser._fromMap(userMap) : null;
        streamController
            .add(RealmUserAuthState._(user: user, isLoggedIn: true));
        break;
      case 'Auth#onLoggedOut':
        final userMap = call.arguments['user'];
        final RealmUser user =
            userMap != null ? RealmUser._fromMap(userMap) : null;
        // ignore: close_sinks
        final StreamController<RealmUserAuthState> streamController =
            _authStateChangesListeners;
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
    _channel.invokeMethod('logOut');
  }

  Future<void> reset() {
    _channel.invokeMethod('reset');
    _authStateChangesListeners?.close();
  }

  Future<void> dispose() {
     _channel.invokeMethod('dispose');
    _authStateChangesListeners?.close();
  }
}