package com.example.baadal.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baadal.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favBinding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        favBinding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(favBinding.root)

        favBinding.backBtnFA.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()

    }
}