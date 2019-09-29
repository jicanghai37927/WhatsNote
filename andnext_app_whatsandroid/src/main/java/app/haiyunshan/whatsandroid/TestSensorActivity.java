package app.haiyunshan.whatsandroid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class TestSensorActivity extends AppCompatActivity {

    LinearLayout dataLayout;

    ArrayList<Sensor> sensorList;
    SensorEventListener sensorEventListener;

    HashMap<Integer, Consumer<SensorEvent>> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor);

        this.dataLayout = findViewById(R.id.data_layout);

        this.sensorList = new ArrayList<>();
        this.sensorEventListener = new MySensorEventListener();

        this.map = new HashMap<>();
        map.put(Sensor.TYPE_ACCELEROMETER, TestSensorActivity.this::onAccelerometer);
        map.put(Sensor.TYPE_STEP_COUNTER, TestSensorActivity.this::onStepCounter);
        map.put(Sensor.TYPE_PROXIMITY, TestSensorActivity.this::onProximity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        register();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregister();
    }

    void register() {
        SensorManager manager = (SensorManager)(this.getSystemService(Context.SENSOR_SERVICE));

        Iterator<Integer> inter = map.keySet().iterator();

        while (inter.hasNext()) {
            int type = inter.next();
            Sensor sensor = manager.getDefaultSensor(type);
            if (sensor != null) {
                manager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                sensorList.add(sensor);
            }
        }
    }

    void unregister() {
        SensorManager manager = (SensorManager)(this.getSystemService(Context.SENSOR_SERVICE));
        for (Sensor s : sensorList) {
            manager.unregisterListener(sensorEventListener, s);
        }
    }

    void onAccelerometer(SensorEvent event) {
        float[] values = event.values.clone();

        StringBuilder sb = new StringBuilder();
        sb.append("Accelerometer: \n");

        sb.append("X = " + values[0]);
        sb.append('\n');
        sb.append("Y = " + values[1]);
        sb.append('\n');
        sb.append("Z = " + values[2]);
        sb.append('\n');

        getTextView(event).setText(sb);

//        String hint = sb.toString().replace('\n', ' ');
//        Log.w("Sensor", hint);
    }

    void onStepCounter(SensorEvent event) {
        float[] values = event.values.clone();

        StringBuilder sb = new StringBuilder();
        sb.append("Step Counter: \n");

        sb.append("Step = " + values[0]);
        sb.append('\n');

        getTextView(event).setText(sb);
    }

    void onProximity(SensorEvent event) {
        float[] values = event.values.clone();

        StringBuilder sb = new StringBuilder();
        sb.append("Proximity: \n");

        sb.append("Proximity = " + values[0]);
        sb.append('\n');

        getTextView(event).setText(sb);
    }

    TextView getTextView(SensorEvent event) {
        int type = event.sensor.getType();

        for (int i = 0, size = dataLayout.getChildCount(); i < size; i++) {
            View view = dataLayout.getChildAt(i);
            Integer value = (Integer)view.getTag();
            if (value == type) {
                return (TextView)view;
            }
        }

        TextView view = new TextView(this);
        view.setTextAppearance(android.R.style.TextAppearance_Medium);
        view.setTag(type);
        view.setGravity(Gravity.TOP|Gravity.LEFT);
        dataLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return view;
    }

    /**
     *
     */
    private class MySensorEventListener implements SensorEventListener {

        MySensorEventListener() {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Consumer<SensorEvent> c = map.get(event.sensor.getType());
            if (c != null) {
                c.accept(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
