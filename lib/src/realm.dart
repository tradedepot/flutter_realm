part of flutter_realm;

class Realm {
  final _unSubscribing = Set<String>();
  final String id;
  bool _isDisposed = false;
  MethodChannelRealm _channelRealm;
  Realm._(this.id) {
    _channelRealm = MethodChannelRealm(id);
    _channelRealm.methodCallStream.listen(_handleMethodCall);
  }

  static Future<Realm> initRealm(RealmConfiguration configuration) async {
    final realm = Realm._(configuration.realmId);
    await realm._invokeMethod('initializeRealm', configuration.toMap());
    return realm;
  }

  static Future<Realm> asyncOpenWithRealmConfiguration({
    @required String partitionValue,
  }) async {
    final realm = Realm._(partitionValue);
    await realm._invokeMethod('asyncOpenWithConfiguration', {
      'partitionValue': partitionValue,
    });
    return realm;
  }

  static Future<Realm> syncOpenWithRealmConfiguration({
    @required String partitionValue,
  }) async {
    final realm = Realm._(partitionValue);
    await realm._invokeMethod('syncOpenWithConfiguration', {
      'partitionValue': partitionValue,
    });
    return realm;
  }

  void _handleMethodCall(MethodCall call) {
    switch (call.method) {
      case 'onResultsChange':
        final subscriptionId = call.arguments['subscriptionId'];
        if (_unSubscribing.contains(subscriptionId)) {
          return;
        }

        if (subscriptionId == null ||
            !_subscriptions.containsKey(subscriptionId)) {
          throw ('Unknown subscriptionId: [$subscriptionId]. Call: $call');
        }
        // ignore: close_sinks
        final controller = _subscriptions[subscriptionId];
        final List results = call.arguments['results'];
        controller.value = results.cast<Map>();
        break;
      default:
        throw ('Unknown method: $call');
        break;
    }
  }

  Future<void> deleteAllObjects() => _channelRealm.invokeMethod('deleteAllObjects');

  Future<void> _dispose() => _channelRealm.invokeMethod('disposeRealm');

  Future<void> close() async {
    if (_isDisposed) {
      return null;
    }
    final ids = _subscriptions.keys.toList();
    for (final subscriptionId in ids) {
      _unsubscribe(subscriptionId);
    }
    _subscriptions.clear();
    await _dispose();
    _isDisposed = true;
  }

  Future<T> _invokeMethod<T>(String method, [dynamic arguments]) =>
      _channelRealm.invokeMethod(method, arguments);

  final Map<String, BehaviorSubject<List<Map>>> _subscriptions = {};

  Future<List> allObjects(String className) =>
      _invokeMethod('allObjects', {'\$': className});

  Stream<List<Map>> subscribeAllObjects(String className, {int limit = -1, String orderBy, bool ascending = true}) {
    final subscriptionId =
        'subscribeAllObjects:' + className + ':' + _uuid.v4();

    final controller = BehaviorSubject<List<Map>>(onCancel: () {
      _unsubscribe(subscriptionId);
    });

    _subscriptions[subscriptionId] = controller;
    _invokeMethod('subscribeAllObjects', {
      '\$': className,
      'subscriptionId': subscriptionId,
      'limit': limit,
      'orderBy': orderBy, 'ascending': ascending
    });

    return controller;
  }

  Stream<List> subscribeObjects(Query query, {int limit = -1, String orderBy, bool ascending = true}) {
    final subscriptionId =
        'subscribeObjects:' + query.className + ':' + _uuid.v4();

    // ignore: close_sinks
    final controller = BehaviorSubject<List<Map>>(onCancel: () {
      _unsubscribe(subscriptionId);
    });

    _subscriptions[subscriptionId] = controller;
    _invokeMethod('subscribeObjects', {
      '\$': query.className,
      'predicate': query._container,
      'subscriptionId': subscriptionId,
      'limit': limit,
      'orderBy': orderBy,
      'ascending': ascending
    });

    return controller.stream;
  }

  Future<List> objects(Query query, {int limit = -1, String orderBy, bool ascending = true}) => _invokeMethod(
      'objects', {'\$': query.className, 'predicate': query._container, 'limit': limit,
    'orderBy': orderBy, 'ascending': ascending});

  Future<Map> createObject(String className, Map<String, dynamic> object) =>
      _invokeMethod(
          'createObject', <String, dynamic>{'\$': className}..addAll(object));

  Future _unsubscribe(String subscriptionId) async {
    if (!_subscriptions.containsKey(subscriptionId)) {
      return;
    }
    _subscriptions[subscriptionId].close();
    _subscriptions.remove(subscriptionId);

    _unSubscribing.add(subscriptionId);
    await _invokeMethod('unsubscribe', {'subscriptionId': subscriptionId});
    _unSubscribing.remove(subscriptionId);
  }

  Future<Map> update(String className,
      {@required dynamic primaryKey, @required Map<String, dynamic> value}) {
    assert(value['uuid'] == null);
    return _invokeMethod('updateObject', {
      '\$': className,
      'primaryKey': primaryKey,
      'value': value,
    });
  }

  Future delete(String className, {@required dynamic primaryKey}) {
    return _invokeMethod('deleteObject', {
      '\$': className,
      'primaryKey': primaryKey,
    });
  }

  Future<String> filePath() => _invokeMethod('filePath');

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Realm && runtimeType == other.runtimeType && id == other.id;

  @override
  int get hashCode => id.hashCode;
}

class Query {
  final String className;

  List _container = <dynamic>[];

  Query(this.className);

  Query greaterThan(String field, dynamic value) =>
      _pushThree('greaterThan', field, value);

  Query greaterThanOrEqualTo(String field, dynamic value) =>
      _pushThree('greaterThanOrEqualTo', field, value);

  Query lessThan(String field, dynamic value) =>
      _pushThree('lessThan', field, value);

  Query lessThanOrEqualTo(String field, dynamic value) =>
      _pushThree('lessThanOrEqualTo', field, value);

  Query equalTo(String field, dynamic value) =>
      _pushThree('equalTo', field, value);

  Query contains(String field, String value) =>
      _pushThree('contains', field, value);

  Query containsIgnoreCase(String field, String value) =>
      _pushThree('contains(c)', field, value);

  Query notEqualTo(String field, dynamic value) =>
      _pushThree('notEqualTo', field, value);

  Query _pushThree(String operator, dynamic left, dynamic right) {
    _container.add([operator, left, right]);
    return this;
  }

  Query _pushOne(String operator) {
    _container.add([operator]);
    return this;
  }

  Query and() => this.._pushOne('and');

  Query or() => this.._pushOne('or');

  Query isIn(String field, List value) => _pushThree('in', field, value);

  @override
  String toString() {
    return 'RealmQuery{className: $className, _container: $_container}';
  }
}

class RealmConfiguration {
  final String inMemoryIdentifier;
  final String realmId;
  const RealmConfiguration({this.inMemoryIdentifier, this.realmId});

  Map<String, dynamic> toMap() => {
        'inMemoryIdentifier': inMemoryIdentifier,
        'realmId': realmId,
      };

  static const RealmConfiguration defaultRealmConfiguration =
      const RealmConfiguration();
}
