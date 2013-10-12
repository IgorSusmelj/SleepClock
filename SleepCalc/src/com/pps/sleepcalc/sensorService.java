package com.pps.sleepcalc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class sensorService extends Service implements SensorEventListener {

	private SensorManager mSensorManager;
	
	private Sensor mGyro;
	private Sensor mAccelo;
	private Sensor mLinear;
	private Sensor mRotation;
	
	private BufferedOutputStream acceloOut;
	private BufferedOutputStream linearOut;
	private BufferedOutputStream gyroOut;
	private BufferedOutputStream rotationOut;
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
			Log.e("SleepCalcServiceTag", "output file created in"+getExternalFilesDir(null));
			
			try {
				acceloOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"accelo.txt")));
				linearOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"linear.txt")));
				gyroOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"gyro.txt")));
				rotationOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"rotation.txt")));
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			MediaScannerConnection.scanFile(this.getApplicationContext(), new String[]{getExternalFilesDir(null).toString()}, null, 
					new MediaScannerConnection.OnScanCompletedListener() {
						@Override
						public void onScanCompleted(String path, Uri uri) {
							Log.e("SleepCalcServiceTag", "Media scanner found file in: "+path);
							// TODO Auto-generated method stub
							
						}
					});
		}else{
			//no permission to write or no external storage found
		}
		
		
		
		return Service.START_STICKY;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			try {
				acceloOut.write(event.values.toString().getBytes());
				Log.e("SleepCalcServiceTag", "Wrote from acceloSensor");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	@Override
	public void onDestroy(){
		
		mSensorManager.unregisterListener(this);
		
		try {
			acceloOut.flush();
			acceloOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("SleepCalcServiceTag", "Service stopped");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
