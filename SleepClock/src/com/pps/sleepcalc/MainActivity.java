package com.pps.sleepcalc;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Intent sensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void startClicked(View view){
    	//create and start service
    	sensorService = new Intent(this, sensorService.class);
    	startService(sensorService);
    	
    	//Toast and log output for user/developper notification
    	Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    	Log.e("SleepCalcTag", "start clicked");
    }
    
    public void stopClicked(View view){
    	//stop service
    	stopService(sensorService);
    	
    	//Toast and log output for user/developper notification
    	Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    	Log.e("SleepCalcTag", "stop clicked");
    }
    
}
