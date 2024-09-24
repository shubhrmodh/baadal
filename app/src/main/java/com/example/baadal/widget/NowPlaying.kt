package com.example.baadal.widget

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.baadal.R
import com.example.baadal.databinding.FragmentNowPlayingBinding
import com.example.baadal.model.setSongPosition
import com.example.baadal.screen.MainActivity
import com.example.baadal.screen.PlayerActivity

class NowPlaying : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var nowPlayingBinding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        nowPlayingBinding = FragmentNowPlayingBinding.bind(view)
        nowPlayingBinding.root.visibility = View.INVISIBLE
        nowPlayingBinding.playPauseBtnNP.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }
        nowPlayingBinding.nextBtnNP.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            Glide.with(requireContext())
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
                .into(nowPlayingBinding.songImgNP)
            nowPlayingBinding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause)
            playMusic()
        }
        nowPlayingBinding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            nowPlayingBinding.root.visibility = View.VISIBLE
            nowPlayingBinding.songNameNP.isSelected = true
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
                .into(nowPlayingBinding.songImgNP)
            nowPlayingBinding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.pause)
            else nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.play)
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.pause)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.play)
        PlayerActivity.musicService!!.showNotification(R.drawable.play)
    }
}