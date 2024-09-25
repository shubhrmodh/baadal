package com.example.baadal.screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.baadal.adapter.FavouriteAdapter
import com.example.baadal.databinding.ActivityFavoriteBinding
import com.example.baadal.model.Music
import com.example.baadal.model.checkPlaylist

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favAdapter: FavouriteAdapter

    companion object {
        var favouriteSongs : ArrayList<Music> = ArrayList()
        var favChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favouriteSongs = checkPlaylist(favouriteSongs)
        binding.backBtnFA.setOnClickListener { finish() }
        binding.favRV.setHasFixedSize(true)
        binding.favRV.setItemViewCacheSize(12)
        binding.favRV.layoutManager = GridLayoutManager(this, 4)
        favAdapter = FavouriteAdapter(this, favouriteSongs)
        binding.favRV.adapter = favAdapter

        favChanged = false

        if (favouriteSongs.size < 1) binding.shuffleBtnFA.visibility = View.INVISIBLE
        else binding.infoFA.visibility = View.GONE

        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (favChanged) {
            favAdapter.updateFavourites(favouriteSongs)
            favChanged = false
        }
    }
}