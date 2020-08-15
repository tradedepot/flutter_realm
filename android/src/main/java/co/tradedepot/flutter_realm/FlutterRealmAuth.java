package co.tradedepot.flutter_realm;

import android.provider.ContactsContract;
import android.util.Log;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmFieldType;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AuthenticationListener;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.UserIdentity;

class FlutterRealmAuth {
    private App app;
    private final MethodChannel channel;

    private static final HashMap<String, AuthenticationListener> authListeners =
            new HashMap<>();

    FlutterRealmAuth(App app, MethodChannel channel) {
        this.app = app;
        this.channel = channel;
    }

    void onMethodCall(MethodCall call, MethodChannel.Result result) {
        try {
            switch (call.method) {
                case "Auth#currentUser": {
                    User user = app.currentUser();
                    Map userMap = null;
                    if (user != null) {
                        userMap = mapUserToObject(user);
                    }
                    result.success(userMap);
                    break;
                }
                case "Auth#logInWithJWTToken": {
                    String token = call.argument("token");
                    if (token != null) {
                        // fetch JWT from custom provider
                        Credentials customJWTCredentials = Credentials.jwt(token);
                        app.loginAsync(customJWTCredentials, it -> {
                            if (it.isSuccess()) {
                               //  Log.v(TAG, "Successfully authenticated using a custom JWT.");
                                User user = app.currentUser();
                                result.success(mapUserToObject(user));
                            } else {
                                it.getError().printStackTrace();
                                result.error("auth/login_failed", it.getError().toString(), it.getError().getStackTrace().toString());
                            }
                        });
                    } else {
                        result.error("auth/login_failed", "token cannot be empty", "Auth#logInWithJWTToken");
                    }
                    break;
                }
                case "Auth#registerChangeListeners": {
                    AuthenticationListener authStateListener = authListeners.get(app.getConfiguration().getAppId());

                    final Map<String, Object> event = new HashMap<>();
                    event.put("appId", app.getConfiguration().getAppId());
                    Log.d("AUTH", "REGISTER AUTH LISTENERS");

                    if (authStateListener == null) {
                        AuthenticationListener newAuthStateListener = new AuthenticationListener() {
                            @Override
                            public void loggedIn(User user) {
                                event.put("user",  mapUserToObject(user));
                                Log.d("AUTH", "LOGGED IN");
                                channel.invokeMethod("Auth#onLoggedIn", event);
                            }

                            @Override
                            public void loggedOut(User user) {
                                Log.d("AUTH", "LOGGED OUT");
                                event.put("user",  mapUserToObject(user));
                                channel.invokeMethod("Auth#onLoggedOut", event);
                            }
                        };
                        app.addAuthenticationListener(newAuthStateListener);
                        authListeners.put(app.getConfiguration().getAppId(), newAuthStateListener);
                        Log.d("AUTH", "REGISTER AUTH LISTENER SUCCESS");
                    }
                    result.success(null);
                    break;
                }
                case "Auth#unRegisterChangeListeners": {
                    AuthenticationListener authStateListener = authListeners.get(app.getConfiguration().getAppId());
                    if (authStateListener != null) {
                        app.removeAuthenticationListener(authStateListener);
                        authListeners.remove(app.getConfiguration().getAppId());
                    }
                    result.success(null);
                    break;
                }
                case "Auth#logOut": {
                    User user = app.currentUser();
                    if (user != null) {
                        user.logOutAsync( it -> {
                            if (it.isSuccess()) {
                                result.success(null);
                            } else {
                                result.error("auth/logout_failed", it.getError().toString(), it.getError().getStackTrace().toString());
                            }
                        });
                    } else {
                        result.success(null);
                    }
                    break;
                }
                default:
                    result.notImplemented();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.error(e.getMessage(), e.getMessage(), e.getStackTrace().toString());
        }
    }

    private HashMap mapUserToObject(User user) {
        HashMap userMap = new HashMap();
        //Document customData = user.refreshCustomData();
        Document customData = user.getCustomData();
        String name = customData.getString("profile.fullname");
        String phoneNumber = customData.getString("profile.phoneNumber");
        String tenantId = customData.getString("group");
        String _id = customData.getString("_id");
        Log.d("AUTH", customData.toString());
        userMap.put("userId", user.getId());
        userMap.put("accessToken", user.getAccessToken());
        userMap.put("tdUserId", _id);
        userMap.put("name", name);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("tenantId", tenantId);
        return userMap;
    }

    public void reset() {
        removeEventListeners();
        authListeners.clear();
    }

    private void removeEventListeners() {
        Iterator<?> authListenerIterator = authListeners.entrySet().iterator();

        while (authListenerIterator.hasNext()) {
            Map.Entry<?, ?> pair = (Map.Entry<?, ?>) authListenerIterator.next();
            AuthenticationListener authListener =
                    (AuthenticationListener) pair.getValue();
            if (authListener != null) {
                app.removeAuthenticationListener(authListener);
            }
            authListenerIterator.remove();
        }
    }
}
