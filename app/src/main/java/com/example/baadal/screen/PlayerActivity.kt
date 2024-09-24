package com.example.baadal.screen

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.baadal.R
import com.example.baadal.databinding.ActivityPlayerBinding
import com.example.baadal.model.Music
import com.example.baadal.model.MusicService
import com.example.baadal.model.exitApplication
import com.example.baadal.model.formatDuration
import com.example.baadal.model.getImgArt
import com.example.baadal.model.getMainColor
import com.example.baadal.model.setDialogBtnBackground
import com.example.baadal.model.setSongPosition
import com.example.baadal.widget.NowPlaying
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var playerBinding: ActivityPlayerBinding
        lateinit var musicListPA : ArrayList<Music>
        var songPosition : Int = 0
        var isPlaying : Boolean = false
        var musicService : MusicService? = null
        var repeat: Boolean = false
        var min5: Boolean = false
        var min10: Boolean = false
        var min15: Boolean = false
        var nowPlayingId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
        lateinit var loudnessEnhancer: LoudnessEnhancer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)

        if(intent.data?.scheme.contentEquals("content")) {
            songPosition = 0
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))
            Glide.with(this)
                .load(getImgArt(musicListPA[songPosition].path))
                .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
                .into(playerBinding.songImgPA)
            playerBinding.songNamePA.text = musicListPA[songPosition].title
        }
        else { initializeLayout() }

        playerBinding.boosterBtnPA.setOnClickListener {
//            val customDialogBooster = LayoutInflater.from(this).inflate(R.layout.)
        }
        playerBinding.backBtnPA.setOnClickListener { finish() }
        playerBinding.playPauseBtnPA.setOnClickListener{ if(isPlaying) pauseMusic() else playMusic() }
        playerBinding.previousBtnPA.setOnClickListener { prevNextSong(increment = false) }
        playerBinding.nextBtnPA.setOnClickListener { prevNextSong(increment = true) }
        playerBinding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                    musicService!!.showNotification(if (isPlaying) R.drawable.pause else R.drawable.play)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        playerBinding.repeatBtnPA.setOnClickListener{
            if (!repeat) {
                repeat = true
                playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
            } else {
                repeat = false
                playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.teal_200))
            }
        }
        playerBinding.equalizerBtnPA.setOnClickListener{
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 12)
            } catch (e: Exception) {
                Toast.makeText(this,  "Equalizer not Supported", Toast.LENGTH_SHORT).show()
            }
        }
        playerBinding.timerBtnPA.setOnClickListener{
            val timer = min5 || min10 || min15
            if(!timer) showBottomSheetDialog()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes"){ _, _ ->
                        min5 = false
                        min10 = false
                        min15 = false
                        playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    }
                    .setNeutralButton("No"){ dialog, _ -> dialog.dismiss() }
                val customDialog = builder.create()
                customDialog.show()
                setDialogBtnBackground(this, customDialog)
            }
        }
        playerBinding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_5)?.setOnClickListener{
            Toast.makeText(baseContext, "Music will stop after 5min", Toast.LENGTH_SHORT).show()
            playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.teal_700))
            min5 = true
            Thread{
                Thread.sleep((5 * 60000).toLong())
                if (min5) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_10)?.setOnClickListener{
            Toast.makeText(baseContext, "Music will stop after 10min", Toast.LENGTH_SHORT).show()
            playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.teal_700))
            min10 = true
            Thread{
                Thread.sleep((10 * 60000).toLong())
                if (min10) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext, "Music will stop after 15min", Toast.LENGTH_SHORT).show()
            playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.teal_700))
            min15 = true
            Thread{
                Thread.sleep((15 * 60000).toLong())
                if (min15) exitApplication()
            }.start()
            dialog.dismiss()
        }
    }

    private fun getMusicDetails(contentUri: Uri): Music {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            return Music(id = "Unknown", title = path.toString(), album = "Unknown", artist = "Unknown", duration = duration,
                artUri = "Unknown", path = path.toString())
        }finally {
            cursor?.close()
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "NowPlaying" -> {
                setLayout()
                playerBinding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                playerBinding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                playerBinding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                playerBinding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause)
                else playerBinding.playPauseBtnPA.setIconResource(R.drawable.play)
            }
            "MusicAdapterSearch" -> initServiceAndPlaylist(MainActivity.musicListSearch, shuffle = false)
            "MusicAdapter" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = false)
            "MainActivity" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = true)
        }
        if (musicService != null && !isPlaying) playMusic()
    }

    private fun initServiceAndPlaylist(
        playlist : ArrayList<Music>,
        shuffle: Boolean,
        playNext: Boolean = false
    ) {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicListPA = ArrayList()
        musicListPA.addAll(playlist)
        if (shuffle) musicListPA.shuffle()
        setLayout()
    }

    private fun setLayout() {
//        fIndex = favouriteChecker(musicListPA[songPosition].id)
        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
            .into(playerBinding.songImgPA)
        playerBinding.songNamePA.text = musicListPA[songPosition].title
        if (repeat) playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.black))
        if(min5 || min10 || min15) playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(applicationContext, R.color.teal_700))
        if(isFavourite) playerBinding.favouriteBtnPA.setImageResource(R.drawable.ic_favorite)
        else playerBinding.favouriteBtnPA.setImageResource(R.drawable.fav_outlined)

        val img = getImgArt(musicListPA[songPosition].path)
        val image = if (img != null) {
            BitmapFactory.decodeByteArray(img, 0, img.size)
        } else {
            BitmapFactory.decodeResource(
                resources,
                R.drawable.baadal
            )
        }
        val bgColor = getMainColor(image)
        val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(0xFFFFFF, bgColor))
        playerBinding.root.background = gradient
        window?.statusBarColor = bgColor
    }

    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause)
            musicService!!.showNotification(R.drawable.pause)
            playerBinding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            playerBinding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            playerBinding.seekBarPA.progress = 0
            playerBinding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id
            playMusic()
            loudnessEnhancer = LoudnessEnhancer(musicService!!.mediaPlayer!!.audioSessionId)
            loudnessEnhancer.enabled = true
        }catch (e: Exception){ Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()} }

    private fun playMusic(){
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause)
        musicService!!.showNotification(R.drawable.pause)
    }

    private fun pauseMusic(){
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.play)
        musicService!!.showNotification(R.drawable.play)
    }

    private fun prevNextSong(increment: Boolean) {
        if(increment)
        {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        setLayout()
        NowPlaying.nowPlayingBinding.songNameNP.isSelected = true
        Glide.with(applicationContext)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.baadal).centerCrop())
            .into(NowPlaying.nowPlayingBinding.songImgNP)
        NowPlaying.nowPlayingBinding.songNameNP.text = musicListPA[songPosition].title
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 12 || resultCode == RESULT_OK) return
    }
}