package com.pps.sleepcalc;

import java.util.Calendar;

import com.pps.sleepcalc.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity implements TimePicker.OnTimeChangedListener, SeekBar.OnSeekBarChangeListener{
	
	private Intent sensorService;

	private int wakeupHours;
	private int wakeupMinutes;
	
	private static final String SHARED_PREF_NAME = "SleepClock Prefs";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        
        
        //setContentView(R.layout.activity_main);
        //set up view
        setContentView(R.layout.test);
        
        //add gui elements
        final TimePicker timepick = (TimePicker) findViewById(R.id.timePicker);
        timepick.setOnTimeChangedListener(this);
        
        //gui elements for delta wakeup time
        final TextView wakeupDelta = (TextView) findViewById(R.id.wakeupDelta);
        final SeekBar wakeupDeltaBar = (SeekBar) findViewById(R.id.wakupDeltaBar);
        
        //gui elements for settings
        final EditText triggerDelay = (EditText) findViewById(R.id.triggerDelay);
        final EditText gyroSensorTrigger = (EditText) findViewById(R.id.gyroSensorTrigger);
        final EditText kalmanGain = (EditText) findViewById(R.id.kalmanGain);

        
        //switch for sensor precision
        Switch sensorPrecisionSwitch = (Switch) findViewById(R.id.sensorPrecisionSwitch);
        
        //save settings button
        Button saveSettings = (Button) findViewById(R.id.saveSettings);
        saveSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
        
        
        //add tabbing
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


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

    
}
