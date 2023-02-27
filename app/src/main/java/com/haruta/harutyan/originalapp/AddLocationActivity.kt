package com.haruta.harutyan.originalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
       //マーカーを追加
        googleMap.addMarker(
            //マーカーの設定
            MarkerOptions()
                .position(LatLng(36.0, 140.0))
                .title("Marker")
        )
    }
}