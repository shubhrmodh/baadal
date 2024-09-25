package com.example.baadal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.baadal.R
import com.example.baadal.databinding.PlaylistViewBinding
import com.example.baadal.model.Playlist
import com.example.baadal.model.setDialogBtnBackground
import com.example.baadal.screen.MainActivity
import com.example.baadal.screen.PlaylistActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(
    private val context: Context,
    private var playlist: ArrayList<Playlist>
): RecyclerView.Adapter<PlaylistAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
        val delete = binding.playlistDeleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        if (MainActivity.themeIndex == 4) {
            holder.root.strokeColor = ContextCompat.getColor(context, R.color.white)
        }
        holder.name.text = playlist[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlist[position].name)
                .setMessage("Do you want to delete this playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNeutralButton("No") { dialog, _ -> dialog.dismiss()}

            val customDialog = builder.create()
            customDialog.show()
            setDialogBtnBackground(context, customDialog)
        }
        holder.root.setOnClickListener {
//            val intent = Intent(context, Playlist)
        }

        if (PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
                .into(holder.image)
        }
    }

    fun refreshPlaylist() {
        playlist = ArrayList()
        playlist.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

}