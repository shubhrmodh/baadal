package com.example.baadal.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.baadal.ApplicationClass
import com.example.baadal.R
import com.example.baadal.screen.PlayerActivity
import com.example.baadal.widget.NowPlaying

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            ApplicationClass.EXIT -> exitApplication()
            ApplicationClass.PREVIOUS -> if (PlayerActivity.musicListPA.size > 1) prevNextSong(increment = false, context = context!!)
            ApplicationClass.NEXT -> if (PlayerActivity.musicListPA.size > 1) prevNextSong(increment = true, context = context!!)
            ApplicationClass.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause)
        PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause)
        try{ NowPlaying.nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.pause) }catch (_: Exception){}
    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play)
        PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.play)
        try{ NowPlaying.nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.play) }catch (_: Exception){}
    }

    private fun prevNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
            .into(PlayerActivity.playerBinding.songImgPA)
        PlayerActivity.playerBinding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
            .into(NowPlaying.nowPlayingBinding.songImgNP)
        NowPlaying.nowPlayingBinding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFavourite) PlayerActivity.playerBinding.favouriteBtnPA.setImageResource(
            R.drawable.ic_favorite)
        else PlayerActivity.playerBinding.favouriteBtnPA.setImageResource(R.drawable.fav_outlined)
    }
}