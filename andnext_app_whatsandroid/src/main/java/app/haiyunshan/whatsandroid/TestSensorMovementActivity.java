package app.haiyunshan.whatsandroid;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestSensorMovementActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor_movement);

        String TAG = getPackageName() + ":hello";
        PowerManager pm = (PowerManager)(this.getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        mWakeLock.acquire();

        attachListeners();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.w("AA", "Sensor onStop");
    }

    public void attachListeners() {

    //开启监听
        final SensorManager sm = getSensorManager();


        //检测是否功能打开
            mAnswerListener.reset();
            sm.registerListener(mAnswerListener,
                                        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                        SensorManager.SENSOR_DELAY_NORMAL);

            sm.registerListener(mPrSensorListener,
                                        sm.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                                        SensorManager.SENSOR_DELAY_NORMAL);


    }

    private SensorManager getSensorManager() {
    //获取sensor
        return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    public void detachListeners() {
    //关闭监听
        final SensorManager sm = getSensorManager();
        sm.unregisterListener(mAnswerListener);
        sm.unregisterListener(mPrSensorListener);
    }

    private interface ResettableSensorEventListener extends SensorEventListener {
        public void reset();
    }

    private static final int SENSOR_SAMPLES = 100;
    private static final int MIN_ACCEPT_COUNT = 5;
    private static final String TAG = "PickupCall";
    boolean mIsPositive;

    private final ResettableSensorEventListener mAnswerListener = new ResettableSensorEventListener() {
            // Our accelerometers are not quite accurate.
                    private static final int XY_GRAVITY_THRESHOLD = 10;
            private static final int Z_GRAVITY_THRESHOLD = 3;

            private boolean stopped;
            private boolean isInitialHorizontal = false;
            private boolean[] samples = new boolean[SENSOR_SAMPLES];
            private int index = 0;

            @Override
            public void onAccuracyChanged(Sensor sensor, int acc) {
            }

            @Override
            public void reset() {
                stopped = false;
                isInitialHorizontal = false;
                for (int i = 0; i < SENSOR_SAMPLES; i++) {
                    samples[i] = false;
                }
            }

            private boolean filterSamples() {
                int trues = 0;
                for (boolean sample : samples) {
                    if(sample) {
                        ++trues;
                    }
                }
                return trues >= MIN_ACCEPT_COUNT;
            }

            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[SensorManager.DATA_X];
                float y = event.values[SensorManager.DATA_Y];
                float z = event.values[SensorManager.DATA_Z];

                if (stopped) {
                    return;
                }

                if(!isInitialHorizontal){
                    isInitialHorizontal = (Math.abs(x) + Math.abs(y)) < 3;
                }

                samples[index] = ((Math.abs(x) + y) > XY_GRAVITY_THRESHOLD)
                        && (Math.abs(z) < Z_GRAVITY_THRESHOLD);

                Log.d(TAG, "mAnswerListener-samples[" +index + "]" + samples[index]);
                index = ((index + 1) % SENSOR_SAMPLES);
                if (filterSamples() && mIsPositive && (isInitialHorizontal && Math.abs(x) > 4)) {
                    stopped = true;
                    for (int i = 0; i < SENSOR_SAMPLES; i++) {
                        samples[i] = false;
                    }

                    handleAction();
                }
            }
        };

    private void handleAction(){
        Log.w(TAG, "handleAction");
    }

    private final SensorEventListener mPrSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
                final float distance = event.values[0];
                mIsPositive = distance >= 0.0f && distance < 5.0f;
                Log.d(TAG, "mProximitySensorListener-mIsPositive" + mIsPositive);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used.
        }
    };

}
