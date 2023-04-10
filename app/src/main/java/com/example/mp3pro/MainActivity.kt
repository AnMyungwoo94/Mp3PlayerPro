package com.example.mp3pro

import android.content.ContentResolver
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mp3pro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val REQUEST_CODE = 2
    lateinit var musicDataList : MutableList<MusicData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //외장메모리 읽기 승인받음
        var flag =  ContextCompat.checkSelfPermission(this, permission[0])
        if(flag == PackageManager.PERMISSION_GRANTED){
            startProcess()
        }else{
            //승인이 되지 않았으니 승인요청
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
        }
    }
    //승인 요청을 하면
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startProcess()
            }else{
                Toast.makeText(this,"권한승인을 해야만 앱을 실행할 수 있습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun startProcess() {
        //어댑터랑 연결, 음원정보를 가져옴
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION)

        val cursor = contentResolver.query(musicUri, projection, null,null, null)
        musicDataList= mutableListOf<MusicData>()
        if (cursor!!.count <= 0){
            Toast.makeText(this,"메모리에 음악이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        while (cursor.moveToNext()){
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artists = cursor.getString(2)
            val albumId = cursor.getString(3)
            val duration = cursor.getLong(4)
            val musicData = MusicData(id, title, artists, albumId, duration)
            musicDataList.add(musicData)
        }

        //어댑터와 리사이클러뷰 연결
        val musicRecyclerAdapter = MusicRecyclerAdapter(this,musicDataList)
        binding.recyclerView.adapter = musicRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}