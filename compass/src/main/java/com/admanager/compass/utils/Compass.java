package com.admanager.compass.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";
    float[] gData = new float[3]; // gravity or accelerometer
    float[] mData = new float[3]; // magnetometer
    float[] rMat = new float[9];
    float[] iMat = new float[9];
    float[] orientation = new float[3];
    long lastTime = 0;
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private boolean noSensor;
    private float azimuth = 0f;
    private float lastAzimuth = 0; /* compass arrow to rotate*/
    private CompassChangedListener listener;

    public Compass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        noSensor = msensor == null || gsensor == null;
    }

    public CompassChangedListener getListener() {
        return listener;
    }

    public void setListener(CompassChangedListener listener) {
        this.listener = listener;
    }

    public boolean deviceCompatible() {
        return msensor != null && gsensor != null;
    }

    public void start() {
        if (noSensor) {
            updateListener();
        }
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.95f;

        synchronized (this) {

            switch (event.sensor.getType()) {
                case Sensor.TYPE_GRAVITY:
                case Sensor.TYPE_ACCELEROMETER:
                    this.gData[0] = alpha * this.gData[0] + (1 - alpha) * event.values[0];
                    this.gData[1] = alpha * this.gData[1] + (1 - alpha) * event.values[1];
                    this.gData[2] = alpha * this.gData[2] + (1 - alpha) * event.values[2];
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    this.mData[0] = alpha * this.mData[0] + (1 - alpha) * event.values[0];
                    this.mData[1] = alpha * this.mData[1] + (1 - alpha) * event.values[1];
                    this.mData[2] = alpha * this.mData[2] + (1 - alpha) * event.values[2];
                    break;
                default:
                    return;
            }

            if (System.currentTimeMillis() - lastTime < 50) {
                return;
            }
            lastTime = System.currentTimeMillis();

            if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {
                azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
                updateListener();
                this.lastAzimuth = this.azimuth;
            }

        }
    }

    private void updateListener() {
        if (listener != null) {
            listener.onChanged(this.lastAzimuth, this.azimuth, noSensor);
        }
    }

    public void request() {
        if (listener != null) {
            listener.onChanged(this.lastAzimuth, this.azimuth, noSensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface CompassChangedListener {
        void onChanged(float oldAzimuth, float newAzimuth, boolean noSensor);
    }
}