package com.example.baadal.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baadal.R
import com.example.baadal.adapter.MusicAdapter
import com.example.baadal.databinding.ActivitySelectionBinding
import com.example.baadal.screen.MainActivity

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectionRV.setItemViewCacheSize(12)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true)
        binding.selectionRV.adapter = adapter

        binding.backBtnSA.setOnClickListener { finish() }
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.MusicListMA = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }

        })
    }

    override fun onResume() {
        super.onResume()
        if (MainActivity.themeIndex == 4)
            binding.searchViewSA.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
    }
}