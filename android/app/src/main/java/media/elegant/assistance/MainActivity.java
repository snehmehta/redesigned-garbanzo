package media.elegant.assistance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.util.Log;

public class MainActivity extends FlutterActivity {

  private String sharedText;
  private static final String CHANNEL = "app.channel.shared.data";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_VIEW.equals(action) && type != null) {
      handleSendText(intent);
    }
  }

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    GeneratedPluginRegistrant.registerWith(flutterEngine);

    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler((call, result) -> {
          if (call.method.contentEquals("getSharedText")) {
            result.success(sharedText);
            sharedText = null;
          }
          if (call.method.contentEquals("getTest")) {
            result.success("test");
          }
        });
  }

  void handleSendText(Intent intent) {
    FlutterEngine flutterEngine = new FlutterEngine(this);

    sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    MethodChannel channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "app.channel.shared.data");
    channel.invokeMethod("sharedText", sharedText, new MethodChannel.Result() {
      @Override
      public void success(Object result) {
        Log.d("Result", result.toString());
        sharedText = null;
      }

      @Override
      public void error(String errorCode, String errorMessage, Object errorDetails) {
        sharedText = null;
      }

      @Override
      public void notImplemented() {
        sharedText = null;
      }
    });

  }
}
