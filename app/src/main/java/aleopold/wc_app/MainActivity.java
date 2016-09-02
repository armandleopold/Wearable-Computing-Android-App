package aleopold.wc_app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;

    private int currentSampleId = 0;
    private int nbrFileCreated = 0;
    private static FileOutputStream fos = null;
    private static FileOutputStream fos2 = null;

    private Handler handler2;
    private int interval= 1000; // read sensor data each 1000 ms

    private static int samplingSpeed = 1;

    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;

    private boolean bEnableLogging = false;


    private final Runnable processSensors = new Runnable() {
        public void run() {
            handler2.postDelayed(this,interval);
        }
    };


    public final void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        handler2 = new Handler();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setContentView(R.layout.activity_main);

        // Using the magnetometer
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
            System.out.println("Success! There's a magnetometer.");
        } else {
            System.out.println("Fail! There's no magnetometer");
        }

        // Use the accelerometer.
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
            System.out.println("Success! There's an accelerometer.");
        } else {
            System.out.println("Fail! There's no accelerometer");
        }

        // Use the gyroscope
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
            System.out.println("Success! There's a gyroscope.");
        } else {
            System.out.println("Fail! There's no gyroscope");
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_FASTEST);
            System.out.println("Success! There's a heart rate");
        } else {
            System.out.println("Fail! There's no heart rate");
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
            System.out.println("Success! There's a light");
        } else {
            System.out.println("Fail! There's no light");
        }

        button  = (Button) findViewById(R.id.start);
        button2 = (Button) findViewById(R.id.stop);
        button3  = (Button) findViewById(R.id.start2);
        button4 = (Button) findViewById(R.id.start3);

        button.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(true);
        button4.setEnabled(true);

        button2.setOnClickListener(handler);
        button.setOnClickListener(handler);
        button3.setOnClickListener(handler);
        button4.setOnClickListener(handler);
    }

    private View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.start:
                    System.out.println("Start fast");
                    samplingSpeed = 1;
                    MainActivity.super.onResume();
                    bEnableLogging = true;
                    button.setEnabled(false);
                    button3.setEnabled(false);
                    button4.setEnabled(false);
                    button2.setEnabled(true);
                    break;
                case R.id.start2:
                    System.out.println("Start medium");
                    samplingSpeed = 2;
                    MainActivity.super.onResume();
                    bEnableLogging = true;
                    button.setEnabled(false);
                    button3.setEnabled(false);
                    button4.setEnabled(false);
                    button2.setEnabled(true);
                    break;
                case R.id.start3:
                    System.out.println("Start slow");
                    samplingSpeed = 3;
                    MainActivity.super.onResume();
                    bEnableLogging = true;
                    button.setEnabled(false);
                    button3.setEnabled(false);
                    button4.setEnabled(false);
                    button2.setEnabled(true);
                    break;
                case R.id.stop:
                    System.out.println("Stop");

                    MainActivity.super.onPause();
                    bEnableLogging = false;
                    button.setEnabled(true);
                    button3.setEnabled(true);
                    button4.setEnabled(true);
                    button2.setEnabled(false);
                    break;
            }
        }
    };

    public final void onSensorChanged(SensorEvent event) {

        // Updating views
        // accelerometer
        TextView tAX = (TextView) findViewById(R.id.txtViewAxValue);
        TextView tAY = (TextView) findViewById(R.id.txtViewAyValue);
        TextView tAZ = (TextView) findViewById(R.id.txtViewAzValue);

        // magnetic field
        TextView tMX = (TextView) findViewById(R.id.txtViewMx);
        TextView tMY = (TextView) findViewById(R.id.txtViewMy);
        TextView tMZ = (TextView) findViewById(R.id.txtViewMz);
        TextView nbfFiles = (TextView) findViewById(R.id.nbfFiles);
        TextView sampleInput = (TextView) findViewById(R.id.sampleInput);

        int maxSample = 36000;
        long tempStartRecord = 0;
        if (bEnableLogging && (currentSampleId < maxSample)) {

            if(currentSampleId == 0) {

                // Opening files
                try {
                    fos = openFileOutput("temp_file_accelerometer.csv", Context.MODE_APPEND);
                    nbrFileCreated++;
                    nbfFiles.setText(Integer.toString(nbrFileCreated));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    fos2 = openFileOutput("temp_file_magnetometer.csv", Context.MODE_APPEND);
                    nbrFileCreated++;
                    nbfFiles.setText(Integer.toString(nbrFileCreated));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Getting timestamp for the file name
                 TimeUnit.NANOSECONDS.toNanos(tempStartRecord);

                // Writing headers
                try {
                    fos.write("sensor_type,device_type,timestamps,x,y,z\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    fos2.write("sensor_type,device_type,timestamps,x,y,z\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float[] accelerometerdata = event.values;

                tAX.setText(Double.toString(accelerometerdata[0]));
                tAY.setText(Double.toString(accelerometerdata[1]));
                tAZ.setText(Double.toString(accelerometerdata[2]));

                int tempTime = 0;
                TimeUnit.NANOSECONDS.toNanos(tempTime);

                String s1 = String.format(Locale.US, "%.2f",accelerometerdata[0]);
                String s2 = String.format(Locale.US, "%.2f",accelerometerdata[1]);
                String s3 = String.format(Locale.US, "%.2f",accelerometerdata[2]);

                try {
                    fos.write(String.format("accelerometer,smartphone,%d,%s,%s,%s\n",tempTime,s1,s2,s3).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                float[] magneticmatrixdata = event.values;

                tMX.setText(Double.toString(magneticmatrixdata[0]));
                tMY.setText(Double.toString(magneticmatrixdata[1]));
                tMZ.setText(Double.toString(magneticmatrixdata[2]));

                long tempTime = 0L;
                TimeUnit.NANOSECONDS.toNanos(tempTime);

                String s4 = String.format(Locale.US, "%.2f",magneticmatrixdata[0]);
                String s5 = String.format(Locale.US, "%.2f",magneticmatrixdata[1]);
                String s6 = String.format(Locale.US, "%.2f",magneticmatrixdata[2]);


                try {
                    fos2.write(String.format("magnetometer,smartphone,%d,%s,%s,%s\n", tempTime,s4,s5,s6).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            currentSampleId++;

        } else if(bEnableLogging && (currentSampleId == maxSample)) {

            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                assert fos2 != null;
                fos2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            long tempStopRecord = 0;
            TimeUnit.NANOSECONDS.toNanos(tempStopRecord);

            File dir = Environment.getExternalStorageDirectory();
            if(dir.exists()) {

                String location = "temp_file_accelerometer.csv";
                File file12 = new File(location);
                FileOutputStream f;

                try {
                    f = new FileOutputStream(file12);
                    String tempString = "sensor_type,device_type,timestamps,x,y,z\\n";
                    f.write(tempString.getBytes());
                    f.flush();
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("The report is now available at "+file12.getAbsolutePath());

                // Rename file
                File file = new File("/temp_file_accelerometer.csv");
                if(file.exists() || file.canExecute()){
                    System.out.println("Acc temp exist");
                } else {
                    System.out.println("Acc temp doesn't exist");
                }
                File file2 = new File("/0_Accelerometer-" + tempStopRecord + "_" + tempStartRecord + ".csv");
                file.renameTo(file2);

                File file3 = new File("/temp_file_magnetometer.csv");
                if(file3.exists()){
                    System.out.println("Magn temp exist");
                } else {
                    System.out.println("Magn temp doesn't exist");
                }
                File file4 = new File("/0_Magnetometer-" + tempStopRecord + "_" + tempStartRecord + ".csv");
                file3.renameTo(file4);


                if(file2.exists()){
                    System.out.println("Acc 0 exist");
                } else {
                    System.out.println("Acc 0 doesn't exist");
                }

                if(file4.exists()){
                    System.out.println("Magn 0 exist");
                } else {
                    System.out.println("Magn 0 doesn't exist");
                }
            }

            currentSampleId = 0;
        }

        sampleInput.setText(Integer.toString(currentSampleId));
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {

        super.onResume();

        switch (samplingSpeed){
            case 1 :
                interval = 1;
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case 2 :
                interval = 200;
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case 3 :
                interval = 1000;
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_UI);
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_UI);
                break;
        }

        handler2.post(processSensors);

        File extStore = Environment.getExternalStorageDirectory();
        System.out.println("Path : "+extStore);
    }

    protected void onPause() {

        handler2.removeCallbacks(processSensors);
        super.onPause();
        mSensorManager.unregisterListener(this);
        currentSampleId = 0;
    }

}


