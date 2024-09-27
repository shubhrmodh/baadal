package com.example.baadal.model

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.baadal.ApplicationClass
import com.example.baadal.R
import com.example.baadal.screen.MainActivity
import com.example.baadal.screen.PlayerActivity
import com.example.baadal.widget.NowPlaying

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyBinder()
    var mediaPlayer : MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager : AudioManager

    override fun onBind(intent: Intent): IBinder {
        mediaSession= MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(baseContext, MainActivity::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {PendingIntent.FLAG_UPDATE_CURRENT}

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val prevIntent = Intent(baseContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java)
            .setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.baadal)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
                .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
                .setSmallIcon(R.drawable.music_icon)
                .setLargeIcon(image)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.previous, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.next, "Next", nextPendingIntent)
                .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder().putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong()
                ).build()
            )
            mediaSession.setPlaybackState(getPlayBackState())
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    handlePlayPause()
                }

                override fun onPause() {
                    super.onPause()
                    handlePlayPause()
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()
                    prevNextSong(increment = true, context = baseContext)
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    prevNextSong(increment = false, context = baseContext)
                }

                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    handlePlayPause()
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }

                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer?.seekTo(pos.toInt())
                    mediaSession.setPlaybackState(getPlayBackState())
                }
            })
        }

        startForeground(12, notification)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        PlayerActivity.musicService?.createMediaPlayer()
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
        if(PlayerActivity.isFavourite) PlayerActivity.playerBinding.favouriteBtnPA.setImageResource(R.drawable.ic_favorite)
        else PlayerActivity.playerBinding.favouriteBtnPA.setImageResource(R.drawable.fav_outlined)

        mediaSession.setPlaybackState(getPlayBackState())
    }

    fun handlePlayPause() {
        if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        mediaSession.setPlaybackState(getPlayBackState())
    }

    private fun playMusic(){
        PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause)
        NowPlaying.nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.pause)
        PlayerActivity.isPlaying = true
        mediaPlayer?.start()
        showNotification(R.drawable.pause)
    }

    private fun pauseMusic(){
        PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.play)
        NowPlaying.nowPlayingBinding.playPauseBtnNP.setIconResource(R.drawable.play)
        PlayerActivity.isPlaying = false
        mediaPlayer!!.pause()
        showNotification(R.drawable.play)
    }

    private fun getPlayBackState(): PlaybackStateCompat {
        val playbackSpeed = if (PlayerActivity.isPlaying) 1F else 0F
        return PlaybackStateCompat.Builder().setState(
            if (mediaPlayer?.isPlaying == true) PlaybackStateCompat.STATE_PLAYING
            else PlaybackStateCompat.STATE_PAUSED,
            mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO
                 or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .build()
    }

    fun createMediaPlayer() {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            mediaPlayer?.prepare()

            PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause)
            showNotification(R.drawable.pause)
            PlayerActivity.playerBinding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.playerBinding.tvSeekBarEnd.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.playerBinding.seekBarPA.progress = 0
            PlayerActivity.playerBinding.seekBarPA.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
            PlayerActivity.loudnessEnhancer = LoudnessEnhancer(mediaPlayer!!.audioSessionId)
            PlayerActivity.loudnessEnhancer.enabled = true
        } catch (e: Exception) { return }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlayerActivity.playerBinding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.playerBinding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) pauseMusic() else playMusic()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}