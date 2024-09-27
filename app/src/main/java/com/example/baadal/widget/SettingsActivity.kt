package com.example.baadal.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baadal.databinding.ActivitySettingsBinding
import com.example.baadal.screen.MainActivity

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}