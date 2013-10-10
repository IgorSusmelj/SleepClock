package com.pps.sleepcalc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.os.Environment;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class sensorService extends IntentService implements SensorEventListener{

	private SensorManager mSensorManager;
	
	private Sensor mGyro;
	private Sensor mAccelo;
	private Sensor mLinear;
	private Sensor mRotation;
	
	private File fos;
	
	
	public sensorService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("SleepCalcServiceTag", "Service started");
		
		//set up sensors 
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mAccelo = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		
		mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mAccelo, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mLinear, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_FASTEST);
		
		//initalise file io
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			//everyting's fine
			fos = new File(getExternalFilesDir("SleepCalc"),"filename.txt");
		}else{
			//no permission to write or no external storage found
		}
		
		
		//never ending loop!?
		while(true){
			Log.e("SleepCalcServiceTag", "Service still running...");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			break;
		case Sensor.TYPE_GYROSCOPE:
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			break;
		default:
			Log.e("SleepCalcServiceTag", "None of my sensors!?!?");
			break;
				
		}
		
	}

}
