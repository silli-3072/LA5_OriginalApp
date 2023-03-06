package com.haruta.harutyan.originalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.haruta.harutyan.originalapp.databinding.ActivityAddLocationBinding
import com.haruta.harutyan.originalapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var db: AppDatabase

    var locationList: List<Location> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getInstance(this.applicationContext)!!
        locationList = db.locationDao().getAll()
        Log.d("room",locationList.size.toString())

        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val addLocationIntent: Intent = Intent(this, AddLocationActivity::class.java)

        binding.transitionFab.setOnClickListener {
            startActivity(addLocationIntent)
        }

    }
}