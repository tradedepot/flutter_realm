package co.tradedepot.flutter_realm;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

/** FlutterRealmPlugin */
public class FlutterRealmPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private HashMap<String, FlutterRealm> realms = new HashMap<>();
  private List<MethodSubHandler> handlers;
  private FlutterRealmAuth auth;
  private App app;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    String channelName = "plugins.it_nomads.com/flutter_realm";
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), channelName);
    channel.setMethodCallHandler(this);
    Realm.init(flutterPluginBinding.getApplicationContext());
  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    try {
      Map arguments = (Map) call.arguments;
      switch (call.method) {
        case "initialize": {
          onInitialize(result, arguments);
          break;
        }
        case "reset":
          onReset(result);
          break;
//        case "asyncOpenWithConfiguration":
//          onAsyncOpenWithConfiguration(arguments, result);
//          break;
//        case "syncOpenWithConfiguration":
//          onSyncOpenWithConfiguration(arguments, result);
//          break;
        default: {
          if (call.method.startsWith("Auth")) {
            if (auth == null) {
              String message = "Method " + call.method + ":" + arguments.toString();
              result.error("initialize must be called before any other method can be  called:", message, null);
              return;
            }
            auth.onMethodCall(call, result);
            return;
          }
          String realmId = (String) arguments.get("realmId");
          FlutterRealm flutterRealm = realms.get(realmId);
          if (flutterRealm == null) {
            String message = "Method " + call.method + ":" + arguments.toString();
            result.error("Realm not found", message, null);
            return;
          }
          flutterRealm.onMethodCall(call, result);
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    clear();
  }

  private void onInitialize(Result result, Map arguments) {
    if (this.app != null) {
      result.success(null);
      return;
    }
    String realmId = (String) arguments.get("realmId");
    String appID = (String) arguments.get("appId");
    this.app = new App(new AppConfiguration.Builder(appID)
            .build());
    FlutterRealm flutterRealm = new FlutterRealm(channel, realmId, arguments);
    auth = new FlutterRealmAuth(app, channel);
    realms.put(realmId, flutterRealm);
    result.success(null);
  }

  private void onReset(Result result) {
    clear();
    result.success(null);
  }

  private void clear() {
    for (FlutterRealm realm : realms.values()) {
      realm.reset();
    }
    if (auth != null) {
      auth.reset();
    }
    realms.clear();
  }

//  private void onAsyncOpenWithConfiguration(Map arguments, final Result result) {
//    final String realmId = (String) arguments.get("realmId");
//    final SyncConfiguration configuration = getSyncConfiguration(arguments);
//
//    Realm.getInstanceAsync(configuration, new Realm.Callback() {
//      @Override
//      public void onSuccess(Realm realm) {
//        FlutterRealm flutterRealm = new FlutterRealm(channel, realmId, realm);
//        realms.put(realmId, flutterRealm);
//        result.success(null);
//      }
//
//      @Override
//      public void onError(Throwable exception) {
//        result.error(exception.getLocalizedMessage(), exception.getMessage(), exception);
//      }
//
//    });
//
//  }

//  private void onSyncOpenWithConfiguration(Map arguments, Result result) {
//    String realmId = (String) arguments.get("realmId");
//    SyncConfiguration configuration = getSyncConfiguration(arguments);
//
//    FlutterRealm flutterRealm = new FlutterRealm(channel, realmId, configuration);
//    realms.put(realmId, flutterRealm);
//    result.success(null);
//  }

//  private SyncConfiguration getSyncConfiguration(Map arguments) {
//    String syncServerURL = (String) arguments.get("syncServerURL");
//    boolean fullSynchronization = (boolean) arguments.get("fullSynchronization");
//
//    assert syncServerURL != null;
//
//    SyncConfiguration.Builder builder = SyncUser.current().createConfiguration(syncServerURL);
//    if (fullSynchronization) {
//      builder.fullSynchronization();
//    }
//
//
//    return builder.build();
//  }
}
