package com.haruta.harutyan.originalapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.haruta.harutyan.originalapp.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding

    // SensorManager
    private lateinit var sensorManager: SensorManager

    // Sensor
    private lateinit var mAccelerometerSensor: Sensor
    private lateinit var mMagneticFieldSensor: Sensor

    private var mAccelerometerValue: FloatArray = FloatArray(3)
    private var mMagneticFieldValue: FloatArray = FloatArray(5)

    private var mMagneticFiledFlg: Boolean = false

    private lateinit var locationManager: LocationManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 使用が許可された
            locationStart()

        } else {
            // それでも拒否された時の対応
            val toast = Toast.makeText(this,
                "これ以上なにもできません", Toast.LENGTH_SHORT)
            toast.show()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val location = Location

        // SensorManagerのインスタンスを生成
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 加速度センサーを取得する
        mAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // 地磁気センサーを取得する
        mMagneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val addLocationIntent: Intent = Intent(this, AddLocationActivity::class.java)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationStart()
        }

        directionCalculation(location)

        binding.transitionFab.setOnClickListener {
            startActivity(addLocationIntent)
        }

    }

    // センサーの精度が変化した時の処理
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /* 何もしない */
    }

    // センサーの値が変化した時の処理
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            holdSensorEventValues(event)

            // 地磁気センサーの値を取得できている場合のみ処理をする
            if (mMagneticFiledFlg) {
                val degree = calculateDegree()
                Log.d("DEGREE", "$degree")

                rotateCompass(calculateDegree())
            }
        }

    }

    override fun onResume() {
        super.onResume()

        setSensorEventListener()
    }

    override fun onPause() {
        super.onPause()
        // リスナーを解除する
        sensorManager.unregisterListener(this@MainActivity)
    }

    private fun setSensorEventListener() {
        mAccelerometerSensor.also { sensor: Sensor ->
            sensorManager.registerListener(
                this@MainActivity,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        mMagneticFieldSensor.also { sensor: Sensor ->
            sensorManager.registerListener(
                this@MainActivity,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun holdSensorEventValues(event: SensorEvent) {
        // 値が変わったセンサーの値を保存する
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (event.values != null) {
                    mAccelerometerValue = event.values
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                if (event.values != null) {
                    mMagneticFieldValue = event.values
                    mMagneticFiledFlg = true
                }
            }
        }
    }

    private fun calculateDegree(): Float {
        // 方位を出すための変換行列
        val rotate = FloatArray(16)
        val inclination = FloatArray(16)

        // 回転角
        val orientation = FloatArray(3)

        // 行列化
        SensorManager.getRotationMatrix(
            rotate,
            inclination,
            mAccelerometerValue,
            mMagneticFieldValue
        )

        // 回転角を取得
        SensorManager.getOrientation(
            rotate,
            orientation
        )

        // 角度を求める
        val doubleOrientation = orientation[0].toDouble()
        return Math.toDegrees(doubleOrientation).toFloat()
    }

    private fun rotateCompass(degree: Float) {
        // Viewの回転方向と方位の回転方向が逆なので、マイナスをかけて反転する
        binding.southImage.rotation = -degree
    }

    private fun directionCalculation(location: Location) {
        lateinit var db: AppDatabase
        var locationList: List<Location> = emptyList()

        db = AppDatabase.getInstance(this.applicationContext)!!
        locationList = db.locationDao().getAll()

        val countNumber: Int = locationList.size
        val latitudeCurrentLocation:Double =  location.latitude
        Log.d("😘", latitudeCurrentLocation.toString())

        var arrayList: ArrayList<Double> = ArrayList()

        for (i in 0..countNumber) {
            var latitudePoint:Double = locationList[i].latitude
            var latitudeDifference = latitudePoint - latitudeCurrentLocation

            var directionCalculationNumber: Double = (cos(latitudePoint) * sin(latitudeDifference)) - (sin(latitudeCurrentLocation) * cos(latitudePoint) * cos(latitudeDifference)) + (cos(latitudePoint) * sin(latitudePoint))
            var absoluteValue = abs(directionCalculationNumber)

            arrayList.add(absoluteValue)
        }
    }

    private fun locationStart() {
        Log.d("debug", "locationStart()")

        // Instances of LocationManager class must be obtained using Context.getSystemService(Class)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled")
        } else {
            // to prompt setting up GPS
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)

            Log.d("debug", "checkSelfPermission false")
            return
        }

    }
}