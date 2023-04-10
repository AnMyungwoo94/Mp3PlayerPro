package com.example.mp3pro

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar

import com.example.mp3pro.databinding.ActivityPlayBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityPlayBinding
    val ALBUM_IMAGE_SIZE = 150
    var mediaPlayer: MediaPlayer? = null
    lateinit var musicData: MusicData
    var mp3PlayerJob: Job? = null
    var pauseFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //전달해온 intent값을 가져옴
        musicData = intent.getSerializableExtra("musicData") as MusicData
        //화면에 바인딩 진행
        binding.albumTitle.text = musicData.title
        binding.albumArtist.text = musicData.artists
        binding.totalDuration.text = SimpleDateFormat("mm:ss").format(musicData.duration)
        binding.playDuration.text = "00:00"
        val bitmap = musicData.getAblumBitmap(this, ALBUM_IMAGE_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.music_24)
        }
        //음악파일객체 가져옴
        mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri())
        //이벤트처리(정지, 실행, 리스트, 돌아가기 ,일시정지, 시크바 조절 이벤트) 일시정지, 실행 같이씀
        binding.listButton.setOnClickListener(this)
        binding.playButton.setOnClickListener(this)
        binding.stopButton.setOnClickListener(this)
        binding.seekBar.max = mediaPlayer!!.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.listButton -> {
                mp3PlayerJob?.cancel()
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                finish()
            }
            R.id.playButton -> {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.pause()
                    binding.playButton.setImageResource(R.drawable.play_24)
                    pauseFlag = true
                } else {
                    mediaPlayer?.start()
                    binding.playButton.setImageResource(R.drawable.baseline_stop_24)
                    pauseFlag = false

                    //코루틴으로 음악을 재생
                    val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                    mp3PlayerJob = backgroundScope.launch {
                        while (mediaPlayer!!.isPlaying) {
                            //코루틴속에서 화면의 값을 변동시키고자 할때 runOnUiThread
                            var currentPosition = mediaPlayer?.currentPosition!!
                            runOnUiThread {
                                binding.seekBar.progress = currentPosition
                                binding.playDuration.text =
                                    SimpleDateFormat("mm:ss").format(mediaPlayer?.currentPosition)
                            }
                            try {
                                delay(1000)
                            } catch (e: java.lang.Exception) {
                                Log.e("PlayActivity", "delay 오류발생 ${e.printStackTrace()}")
                            }
                        }//end of while
                        if (pauseFlag == false) {
                            runOnUiThread {
                                binding.seekBar.progress = 0
                                binding.playDuration.text = "00:00"
                                binding.playButton.setImageResource(R.drawable.play_24)
                            }
                        }
                    }//end of mp3playerjob
                }
            }
            R.id.stopButton -> {
                mp3PlayerJob?.cancel()
                mediaPlayer?.stop()
                mediaPlayer = MediaPlayer.create(this, musicData.getMusicUri())
                binding.seekBar.progress = 0
                binding.playDuration.text = "00:00"
                binding.seekBar.max = mediaPlayer!!.duration
                binding.totalDuration.text = SimpleDateFormat("mm:ss").format(musicData.duration)
                binding.playButton.setImageResource(R.drawable.play_24)
            }

        }
    }
    override fun onBackPressed() {
        mp3PlayerJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer=null
        finish()
    }
}