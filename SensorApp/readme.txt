

01-02 00:03:36.935  3613  3675 D AmlogicVideoDecoderAwesome: ~AmlogicVideoDecoder
01-02 00:03:36.935  3514  4138 I gralloc : ddebug, pair (share_fd=57, user_hnd=8, ion_client=34)
01-02 00:03:36.936  3778  3794 W System.err: 	at android.hardware.SystemSensorManager.registerListenerImpl(SystemSensorManager.java:146)
01-02 00:03:36.936  3778  3794 W System.err: 	at android.hardware.SensorManager.registerListener(SensorManager.java:852)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.policy.WindowOrientationListener.enable(WindowOrientationListener.java:161)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.policy.PhoneWindowManager.updateOrientationListenerLp(PhoneWindowManager.java:1267)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.policy.PhoneWindowManager.finishScreenTurningOn(PhoneWindowManager.java:7170)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.policy.PhoneWindowManager.finishWindowsDrawn(PhoneWindowManager.java:7163)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.policy.PhoneWindowManager.access$400(PhoneWindowManager.java:312)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.policy.PhoneWindowManager$PolicyHandler.handleMessage(PhoneWindowManager.java:898)
01-02 00:03:36.936  3778  3794 W System.err: 	at android.os.Handler.dispatchMessage(Handler.java:106)
01-02 00:03:36.936  3778  3794 W System.err: 	at android.os.Looper.loop(Looper.java:193)
01-02 00:03:36.936  3778  3794 W System.err: 	at android.os.HandlerThread.run(HandlerThread.java:65)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.ServiceThread.run(ServiceThread.java:44)
01-02 00:03:36.936  3778  3794 W System.err: 	at com.android.server.UiThread.run(UiThread.java:43)
01-02 00:03:36.936  3778  3794 D SensorManager: klein---registerListenerImpl, sensor = {Sensor name="ACCELEROMETER", vendor="QUECTEL", version=1, type=1, maxRange=39.2266, resolution=0.0012, power=0.001, minDelay=10000}
01-02 00:03:36.936  3613  3675 D AmlogicVideoDecoderAwesome: ~AmlogicVideoDecoder, mOmxInstancenum 0




01-01 12:16:26.815  4971  4971 W System.err: java.lang.Exception: klein---onSensorChanged
01-01 12:16:26.816  4971  4971 W System.err: 	at com.example.klein.mydemo.MainActivity$MySensorEventListener.onSensorChanged(MainActivity.java:72)
01-01 12:16:26.816  4971  4971 W System.err: 	at android.hardware.SystemSensorManager$SensorEventQueue.dispatchSensorEvent(SystemSensorManager.java:833)
01-01 12:16:26.816  4971  4971 W System.err: 	at android.os.MessageQueue.nativePollOnce(Native Method)
01-01 12:16:26.816  4971  4971 W System.err: 	at android.os.MessageQueue.next(MessageQueue.java:326)
01-01 12:16:26.816  4971  4971 W System.err: 	at android.os.Looper.loop(Looper.java:160)
01-01 12:16:26.816  4971  4971 W System.err: 	at android.app.ActivityThread.main(ActivityThread.java:6669)
01-01 12:16:26.816  4971  4971 W System.err: 	at java.lang.reflect.Method.invoke(Native Method)
01-01 12:16:26.816  4971  4971 W System.err: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
01-01 12:16:26.816  4971  4971 W System.err: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
01-01 12:16:26.816  4971  4971 D sensor-klein: x = 68.0, y = -163.0, z_new = -60.0, delta186.5288181488319
