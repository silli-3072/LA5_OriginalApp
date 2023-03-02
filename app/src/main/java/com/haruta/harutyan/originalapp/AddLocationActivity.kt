package com.haruta.harutyan.originalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.haruta.harutyan.originalapp.databinding.ActivityAddLocationBinding

class AddLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAddLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //フラグメントに対するハンドルを取得してコールバックを登録
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val nagoya = LatLng( 35.1814,136.9063)
        //起動時の表示場所を設定
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nagoya))
        //起動時の縮尺を設定
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nagoya,15f))
        //拡大・縮小ボタンを表示
        googleMap.uiSettings.isZoomControlsEnabled = true

        //googleMap.uiSettings.isMyLocationButtonEnabled = true

        //長押しされた時のActionを指示
        googleMap.setOnMapClickListener { longpushLocation: LatLng ->
            var newlocation = LatLng(longpushLocation.latitude, longpushLocation.longitude)
            googleMap.addMarker(
                MarkerOptions().position(newlocation)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newlocation, 14f))
        }

    }
}