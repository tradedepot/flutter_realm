part of flutter_realm;

class RealmUserAuthState {
  final RealmUser user;
  final bool isLoggedIn;

  RealmUserAuthState._({
    this.user,
    this.isLoggedIn,
  });

  Map<String, dynamic> toMap() {
    return {
      'user': user?.toMap(),
      'isLoggedIn': isLoggedIn,
    };
  }

  String toString() {
    return "${toMap()}";
  }
}

class RealmUser {
  final String name;
  final String phoneNumber;
  final String tenantId;
  final String userId;
  final String externalId;
  final String accessToken;

  RealmUser._(
      {this.name,
      this.phoneNumber,
      this.tenantId,
      this.userId,
      this.externalId,
      this.accessToken});

  factory RealmUser._fromMap(Map map) => RealmUser._(
      name: map['name'],
      phoneNumber: map['phoneNumber'],
      tenantId: map['tenantId'],
      userId: map['userId'],
      externalId: map['externalId'],
      accessToken: map['accessToken']);

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'phoneNumber': phoneNumber,
      'tenantId': tenantId,
      'userId': userId,
      'externalId': externalId,
      'accessToken': accessToken,
    };
  }

  String toString() {
    return "${toMap()}";
  }
}
