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

	//my sensor manager
	private SensorManager mSensorManager;
	
	
	//my sensors
	private Sensor mGyro;
	private Sensor mAccelo;
	private Sensor mLinear;
	private Sensor mRotation;
	
	
	//my IO streams for saving data
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
		
		//register sensor with specific delay
		mSensorManager.registerListener(this, mGyro, 500000);
		mSensorManager.registerListener(this, mAccelo, 500000);
		mSensorManager.registerListener(this, mLinear, 500000);
		mSensorManager.registerListener(this, mRotation, 500000);
		
		//initalise file io
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			//everyting's fine
			Log.e("SleepCalcServiceTag", "output file created in"+getExternalFilesDir(null));
			
			try {
				acceloOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"accelo.csv"),false));
				linearOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"linear.csv")));
				gyroOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"gyro.csv")));
				rotationOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"rotation.csv")));
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
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
		
		//check sensor type
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			
			//wirte to specific IO stream
			try {
				
				float x,y,z;
				
				x=event.values[0];
				y=event.values[1];
				z=event.values[2];
				
				acceloOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				Log.e("SleepCalcServiceTag", "Wrote from accelo Sensor");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case Sensor.TYPE_GYROSCOPE:
			
			//wirte to specific IO stream
			try {
				
				float x,y,z;
				
				x=event.values[0];
				y=event.values[1];
				z=event.values[2];
				
				gyroOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				Log.e("SleepCalcServiceTag", "Wrote from gyro Sensor");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			
			//wirte to specific IO stream
			try {
				
				float x,y,z;
				
				x=event.values[0];
				y=event.values[1];
				z=event.values[2];
				
				rotationOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				Log.e("SleepCalcServiceTag", "Wrote from rotation Sensor");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			
			//wirte to specific IO stream
			try {
				
				float x,y,z;
				
				x=event.values[0];
				y=event.values[1];
				z=event.values[2];
				
				linearOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				Log.e("SleepCalcServiceTag", "Wrote from linear Sensor");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			Log.e("SleepCalcServiceTag", "None of my sensors!?!?");
			break;
				
		}
		
		
	}
	
	@Override
	public void onDestroy(){
		
		//start media scanner to search for the new file so it gets displayed
		MediaScannerConnection.scanFile(this.getApplicationContext(), new String[]{getExternalFilesDir(null).toString()}, null, 
			new MediaScannerConnection.OnScanCompletedListener() {
				@Override
				public void onScanCompleted(String path, Uri uri) {
					Log.e("SleepCalcServiceTag", "Media scanner found file in: "+path);
					// TODO Auto-generated method stub
					
				}
		});
		
		//unregister sensor so no more events gets triggered
		mSensorManager.unregisterListener(this);
		
		
		//flush and close all IO streams so all data gets written correctly
		try {
			acceloOut.flush();
			acceloOut.close();
			
			gyroOut.flush();
			gyroOut.close();
			
			rotationOut.flush();
			rotationOut.close();
			
			linearOut.flush();
			linearOut.close();
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
