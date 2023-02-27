package com.haruta.harutyan.originalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.haruta.harutyan.originalapp.databinding.ActivityAddLocationBinding

class AddLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater).apply { setContentView(this.root) }
    }
}