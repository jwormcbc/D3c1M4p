package com.bitcoreit.sistemast1;

import com.bitcoreit.sistemast1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashInicio extends Activity {
private long ms = 0;
private long splashDuration = 2000;
private boolean splashActive = true;
private boolean paused = false;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_splash_inicio);
Thread mythread = new Thread() {
public void run() {
try {
while (splashActive && ms < splashDuration) {
if (!paused)
ms = ms + 100;
sleep(100);
}
} catch (Exception e) {
} finally {
Intent intent = new Intent(SplashInicio.this,
MainActivity.class);
startActivity(intent);
}
}
};
mythread.start();
}
}
