package com.example.ugd89_c_10692_project1

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.math.abs


class MainActivity : AppCompatActivity(), SensorEventListener{
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101
    private var mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK
    lateinit var sensorManager: SensorManager
    lateinit var proximitySensor: Sensor

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    if(mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
                        mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                    else {
                        mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    if (mCameraView != null){
                        mCamera?.stopPreview()
                    }
                    mCamera?.release()
                    try {
                        mCamera = Camera.open(mCameraID)
                    } catch (e : Exception){
                        Log.d("Error","Failed to get Camera"+ e.message)
                    }
                    if (mCamera !=null){
                        mCameraView = CameraView(applicationContext, mCamera!!)
                        val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camera_view.addView(mCameraView)
                    }
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpSensorStuff()
        createNotificationChannel()
        try {
            mCamera = Camera.open()
        }catch (e: Exception){
            Log.d("Error","Failed to get Camera"+ e.message)
        }
        if(mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener { view: View? -> System.exit(0)}

        // on below line we are initializing our sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // on below line we are initializing our proximity sensor variable
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        // on below line we are checking if the proximity sensor is null
        if (proximitySensor == null) {
            // on below line we are displaying a toast if no sensor is available
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // on below line we are registering
            // our sensor with sensor manager
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{ accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val leftRight = event.values[0]
            val upDown = event.values[1]
            if(abs(leftRight.toInt()) > 5  || abs(upDown.toInt()) > 5){
                sendNotifiaction1()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotifiaction1(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_note_add_24)
            .setContentTitle("Modul89_C_10692_PROJECT2")
            .setContentText("Selamat anda sudah berhasil Modul 8 dan 9 ")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1,builder.build())
        }
    }
}