package com.example.hivian.my_contact_manager.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.hivian.my_contact_manager.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hivian on 6/20/17.
 */

public class SplashActivity extends Activity {
    private static final long DELAY = 1000;
    private boolean scheduled = false;
    private Timer splashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                SplashActivity.this.finish();
            }
        }, DELAY);
        scheduled = true;
    }

    @Override
    protected void onDestroy()  {
        super.onDestroy();
        if (scheduled) {
            splashTimer.cancel();
        }
        splashTimer.purge();
    }
}
