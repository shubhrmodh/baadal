package com.example.baadal.screen

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.baadal.R
import com.example.baadal.adapter.PlaylistAdapter
import com.example.baadal.databinding.ActivityPlaylistBinding
import com.example.baadal.databinding.AddPlaylistDialogBinding
import com.example.baadal.model.MusicPlaylist
import com.example.baadal.model.Playlist
import com.example.baadal.model.setDialogBtnBackground
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(12)
        binding.playlistRV.layoutManager = GridLayoutManager(this, 2)
        adapter = PlaylistAdapter(this, playlist = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter

        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog, _ ->
                val playlistName = binder.playlistName.text
                if (playlistName != null && playlistName.isNotEmpty()) {
                    addPlaylist(name = playlistName.toString())
                }
                dialog.dismiss()
            }.create()
        dialog.show()
        setDialogBtnBackground(this, dialog)
    }

    private fun addPlaylist(name : String) {
        var playlistExists = false
        for (i in musicPlaylist.ref) {
            if (name == i.name) {
                playlistExists = true
                break
            }
        }
        if (playlistExists) Toast.makeText(this, "Playlist Exists", Toast.LENGTH_SHORT).show()
        else {
            val tempList = Playlist()
            tempList.name = name
            tempList.playlist = ArrayList()
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempList.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempList)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}