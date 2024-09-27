package com.example.baadal.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baadal.databinding.ActivityAboutBinding
import com.example.baadal.screen.MainActivity

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}