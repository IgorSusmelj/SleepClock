package com.pps.sleepcalc;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity implements TimePicker.OnTimeChangedListener{
	
	private Intent sensorService;

	private int wakeupHours;
	private int wakeupMinutes;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        
        
        //setContentView(R.layout.activity_main);
        
        setContentView(R.layout.test);
        
        TimePicker timepick = (TimePicker) findViewById(R.id.timePicker);
        timepick.setOnTimeChangedListener(this);
        
        TabHost tabHost = (TabHost) findViewById(R.id.TabHost);
        tabHost.setup();
        
        TabSpec specMain = tabHost.newTabSpec("Main");
        specMain.setContent(R.id.tabMain);
        specMain.setIndicator("Start");
        
        TabSpec specSettings = tabHost.newTabSpec("Settings");
        specSettings.setContent(R.id.tabSettings);
        specSettings.setIndicator("Settings");
        
        tabHost.addTab(specMain);
        tabHost.addTab(specSettings);
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
    	sensorService.putExtra("wakeupHours", wakeupHours);
    	sensorService.putExtra("wakeupMinutes", wakeupMinutes);
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


	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		Log.e("SleepCalcTag", "Hour: "+hourOfDay+" minute: "+minute);
		// TODO Auto-generated method stub
		
	}
    
}
