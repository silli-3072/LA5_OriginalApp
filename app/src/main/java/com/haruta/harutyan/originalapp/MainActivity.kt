package com.haruta.harutyan.originalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.haruta.harutyan.originalapp.databinding.ActivityAddLocationBinding
import com.haruta.harutyan.originalapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val addLocationIntent: Intent = Intent(this, AddLocationActivity::class.java)

        binding.transitionFav.setOnClickListener {
            startActivity(addLocationIntent)
        }

    }
}