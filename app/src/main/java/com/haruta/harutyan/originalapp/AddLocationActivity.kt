package com.haruta.harutyan.originalapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.haruta.harutyan.originalapp.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.haruta.harutyan.originalapp.PermissionUtils.isPermissionGranted
import com.haruta.harutyan.originalapp.databinding.ActivityAddLocationBinding

class AddLocationActivity : AppCompatActivity(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var binding: ActivityAddLocationBinding

    //マップ
    private lateinit var map: GoogleMap
    private  var marker: Marker? = null

    //Room
    lateinit var db: AppDatabase

    private var name = "名称未設定"
    private var latitude = -1.0
    private var longitude = -1.0

    //位置情報
    private var permissionDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //フラグメントに対するハンドルを取得してコールバックを登録
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 2. データベースの初期化
        db = AppDatabase.getInstance(this.applicationContext)!!

        binding.saveFab.setOnClickListener {
            name = binding.locationNameTextEdit.text.toString()

            AlertDialog.Builder(this)
                .setTitle("${name}を登録します")
                .setPositiveButton("OK"){ dialog, which ->
                    //保存するデータの変数を作成
                    val location: Location = Location(
                        name = name,
                        latitude = latitude,
                        longitude = longitude,
                    )
                    //Daoのinsertを呼び出して保存したいUserを渡す
                    db.locationDao().insert(location)
                    finish()
                }
                .setNegativeButton("キャンセル"){  dialog, which ->

                }
                .show()
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        //初期化の遅延
        map = googleMap

        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()

        val nagoya = LatLng(35.1814, 136.9063)
        //起動時の表示場所を設定
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nagoya))
        //起動時の縮尺を設定
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nagoya, 15f))
        //拡大・縮小ボタンを表示
        //実機で地図の拡大がしにくいので実装。リリース時には消す
        googleMap.uiSettings.isZoomControlsEnabled = true

        //長押しされた時のActionを指示
        googleMap.setOnMapLongClickListener { longpushLocation: LatLng ->
            marker?.remove()

            val newlocation = LatLng(longpushLocation.latitude, longpushLocation.longitude)
            marker = googleMap.addMarker(
                MarkerOptions().position(newlocation)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newlocation, 14f))
        }

        //ピンのクリックを取得
        googleMap.setOnMapClickListener {
            latitude = it.latitude
            longitude = it.longitude
        }

    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        return false
    }

    override fun onMyLocationClick(location: android.location.Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            enableMyLocation()
        } else {
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

    }
}