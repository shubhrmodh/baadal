package com.example.baadal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.baadal.R
import com.example.baadal.databinding.FavouriteViewBinding
import com.example.baadal.model.Music
import com.example.baadal.screen.PlayerActivity

class FavouriteAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private val playNext: Boolean = false
): RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {

    class MyHolder(binding: FavouriteViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
            .into(holder.image)

        if(playNext) {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "PlayNext")
                ContextCompat.startActivity(context, intent, null)
            }
        } else {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFavourites(newList : ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }
}