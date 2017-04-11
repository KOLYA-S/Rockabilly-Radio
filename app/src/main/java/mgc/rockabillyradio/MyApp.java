package mgc.rockabillyradio;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.yandex.metrica.YandexMetrica;

import mgc.rockabillyradio.trafficcop.DataUsage;
import mgc.rockabillyradio.trafficcop.LogDataUsageAlertListener;
import mgc.rockabillyradio.trafficcop.SizeUnit;
import mgc.rockabillyradio.trafficcop.Threshold;
import mgc.rockabillyradio.trafficcop.TimeUnit;
import mgc.rockabillyradio.trafficcop.TrafficCop;

public class MyApp extends Application {
      @Override
      public void onCreate() {
          super.onCreate();
          // Инициализация AppMetrica SDK
          YandexMetrica.activate(getApplicationContext(), "6fb64ebc-4bb5-411f-8d57-4cb0e7522077");
          // Отслеживание активности пользователей
          YandexMetrica.enableActivityAutoTracking(this);

          new TrafficCop.Builder()
                  // Set a threshold for downloads
                  .downloadWarningThreshold(Threshold.of(100, SizeUnit.KILOBYTES).per(1, TimeUnit.SECOND))
                  // Set a threshold for uploads
                  .uploadWarningThreshold(Threshold.of(100, SizeUnit.KILOBYTES).per(1, TimeUnit.SECOND))
                  // Register callbacks to be alerted when the threshold is reached
                  .alert(new LogDataUsageAlertListener(), new LogDataUsageAlertListener() {
                      @Override
                      public void alertThreshold(Threshold threshold, DataUsage dataUsage) {
                          // Alert somehow!
                          Toast.makeText(MyApp.this, dataUsage.getHumanReadableSize(), Toast.LENGTH_SHORT).show();
                      }
                  })
                  // Pass a string that uniquely identifies this instance.
                  .register("myTrafficCop", this).startMeasuring();
      }
}