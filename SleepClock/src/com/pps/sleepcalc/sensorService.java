package com.pps.sleepcalc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DateFormat;
import java.util.Date;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;

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

	//constants
	private final static int timeLogCounterMAX=10000;
	private static int deltaOutTrigger=100;
	
	//triggers for sensor data
	private final static float LinearSensorTrigger = 0.3f;
	private final static float GyroSensorTrigger   = 0.1f;
	
	//sensor data buffer for computing average
	private final static int SensorDataBuffMax=200; //change this value for better average values
	
	//my sensor manager
	private SensorManager mSensorManager;
	
	//power manager
	private PowerManager.WakeLock wakelock;
	
	
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
	private BufferedOutputStream timeLogOut;
	
	//my IO stream for result
	private BufferedOutputStream resLinearOut;
	private BufferedOutputStream resGyroOut;
	
	//last Motion data
	private int lastLinearOut=0;
	private int lastGyroOut=0;
	
	
	//counter for each sensor
	private int acceloCount=0;
	private int linearCount=0;
	private int gyroCount=0;
	private int rotationCount=0;
	
	
	private int LinearDataCounter = 0;
	
	private int GyroDataCounter = 0;
	private float[] linearDataBuffX = new float[SensorDataBuffMax];
	private float[] linearDataBuffY = new float[SensorDataBuffMax];
	private float[] linearDataBuffZ = new float[SensorDataBuffMax];
	
	private float[] gyroDataBuffX = new float[SensorDataBuffMax];
	private float[] gyroDataBuffY = new float[SensorDataBuffMax];
	private float[] gyroDataBuffZ = new float[SensorDataBuffMax];
	
	
	//average value of sensors
	private float linearAverageX=0.0f;
	private float linearAverageY=0.0f;
	private float linearAverageZ=0.0f;
	
	private float gyroAverageX=0.0f;
	private float gyroAverageY=0.0f;
	private float gyroAverageZ=0.0f;
	

	private int timeLogCounter=timeLogCounterMAX;

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
				acceloOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"accelo.csv"),false),131072);
				linearOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"linear.csv")),131072);
				gyroOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"gyro.csv")),131072);
				rotationOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"rotation.csv")),131072);
				
				resLinearOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"linearRresult.csv")));
				resGyroOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"gyroRresult.csv")));
				
				timeLogOut = new BufferedOutputStream(new FileOutputStream(new File(getExternalFilesDir(null),"timelog.csv")));
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}else{
			//no permission to write or no external storage found
		}
		
		
		//initiate WakeLock
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake tag");
		
		wakelock.acquire();
		
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
				//define temp variables for x,y,z component of value vector
				float x,y,z;
				
				x=event.values[0];
				y=event.values[1];
				z=event.values[2];
				
				acceloOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				acceloCount++;

				//Log.e("SleepCalcServiceTag", "Wrote from accelo Sensor");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case Sensor.TYPE_GYROSCOPE:
			
			//write to specific IO stream
			try {
				
				float x,y,z;
				
				x=event.values[0];
				y=event.values[1];
				z=event.values[2];

				//check sleep cycle
				if(gyroAverageX!=0 && gyroCount-lastGyroOut>deltaOutTrigger){
					if(Math.abs(x-gyroAverageX)>GyroSensorTrigger){
						//Motion detected
						lastGyroOut=gyroCount;
						resGyroOut.write((Float.toString(gyroCount)+","+Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
						Log.e("SleepCalcServiceTag", "Motion detected by gyroX: "+x);
					}else if(Math.abs(y-gyroAverageY)>GyroSensorTrigger){
						//Motion detected
						lastGyroOut=gyroCount;
						resGyroOut.write((Float.toString(gyroCount)+","+Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
						Log.e("SleepCalcServiceTag", "Motion detected by gyroY: "+y);
					}else if(Math.abs(z-gyroAverageZ)>GyroSensorTrigger){
						//Motion detected
						lastGyroOut=gyroCount;
						resGyroOut.write((Float.toString(gyroCount)+","+Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
						Log.e("SleepCalcServiceTag", "Motion detected by gyroZ: "+z);
					}
				}				
				
				//add to data buffer
				gyroDataBuffX[GyroDataCounter]=x;
				gyroDataBuffY[GyroDataCounter]=y;
				gyroDataBuffZ[GyroDataCounter]=z;
				
				GyroDataCounter++;
				if(GyroDataCounter>=SensorDataBuffMax){
					GyroDataCounter=0;
					
					gyroAverageX=computeAverage(gyroDataBuffX,SensorDataBuffMax);
					gyroAverageY=computeAverage(gyroDataBuffY,SensorDataBuffMax);
					gyroAverageZ=computeAverage(gyroDataBuffZ,SensorDataBuffMax);
					
					Log.e("SleepCalcServiceTag", "gyro Average x: "+gyroAverageX+" y: "+gyroAverageY+" z: "+gyroAverageZ);
					
				}
				
				gyroOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				gyroCount++;
			
				
				//set perTimeCounter down and check if 0 or below
				timeLogCounter--;
				
				if(timeLogCounter<=0){
					//output actual time and sensor counters
					timeLogOut.write((DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()).toString()+","+Integer.toString(acceloCount)+","+Integer.toString(gyroCount)+","+Integer.toString(rotationCount)+","+Integer.toString(linearCount)+";").getBytes());
					timeLogCounter=timeLogCounterMAX;
				}
				//Log.e("SleepCalcServiceTag", "Wrote from gyro Sensor");
				
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
				rotationCount++;
				//Log.e("SleepCalcServiceTag", "Wrote from rotation Sensor");
				
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
				
				//check sleep cycle
				if(linearAverageX!=0 && linearCount-lastLinearOut>deltaOutTrigger){
					if(Math.abs(x-linearAverageX)>LinearSensorTrigger){
						//Motion detected
						lastLinearOut=linearCount;
						resLinearOut.write((Float.toString(linearCount)+","+Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
						Log.e("SleepCalcServiceTag", "Motion detected by LinearX: "+x);
					}else if(Math.abs(y-linearAverageY)>LinearSensorTrigger){
						//Motion detected
						lastLinearOut=linearCount;
						resLinearOut.write((Float.toString(linearCount)+","+Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
						Log.e("SleepCalcServiceTag", "Motion detected by LinearY: "+y);
					}else if(Math.abs(z-linearAverageZ)>LinearSensorTrigger){
						//Motion detected
						lastLinearOut=linearCount;
						resLinearOut.write((Float.toString(linearCount)+","+Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
						Log.e("SleepCalcServiceTag", "Motion detected by LinearZ: "+z);
					}
				}
				
				//add to data buffer
				linearDataBuffX[LinearDataCounter]=x;
				linearDataBuffY[LinearDataCounter]=y;
				linearDataBuffZ[LinearDataCounter]=z;
				
				LinearDataCounter++;
				if(LinearDataCounter>=SensorDataBuffMax){
					LinearDataCounter=0;
					
					linearAverageX=computeAverage(linearDataBuffX,SensorDataBuffMax);
					linearAverageY=computeAverage(linearDataBuffY,SensorDataBuffMax);
					linearAverageZ=computeAverage(linearDataBuffZ,SensorDataBuffMax);
					
					Log.e("SleepCalcServiceTag", "Linear Average x: "+linearAverageX+" y: "+linearAverageY+" z: "+linearAverageZ);
					
				}
				
				linearOut.write((Float.toString(x)+","+Float.toString(y)+","+Float.toString(z)+";").getBytes());
				linearCount++;
				//Log.e("SleepCalcServiceTag", "Wrote from linear Sensor");
				
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
	
	private float computeAverage(float[] x, int size){
		float tmp = 0.0f;
		for(int i=0;i<size;i++){
			tmp+=x[i];
		}
		return tmp/size;
	}
	
	
	@Override
	public void onDestroy(){
		
		wakelock.release();
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
			
			resLinearOut.flush();
			resLinearOut.close();
			
			resGyroOut.flush();
			resGyroOut.close();
			
			timeLogOut.flush();
			timeLogOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start media scanner to search for the new file so it gets displayed
		MediaScannerConnection.scanFile(this.getApplicationContext(), new String[]{getExternalFilesDir(null).toString()}, null, 
			new MediaScannerConnection.OnScanCompletedListener() {
				@Override
				public void onScanCompleted(String path, Uri uri) {
					Log.e("SleepCalcServiceTag", "Media scanner found file in: "+path);
					// TODO Auto-generated method stub
					
				}
		});
		
		Log.e("SleepCalcServiceTag", "Service stopped");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
