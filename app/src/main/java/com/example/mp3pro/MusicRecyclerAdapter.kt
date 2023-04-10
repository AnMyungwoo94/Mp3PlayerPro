package com.example.mp3pro

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3pro.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat

class MusicRecyclerAdapter(val context: Context , val musicList: MutableList<MusicData>) :
    RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {
    val ALBUM_IMAGE_SIZE = 90


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val binding : ItemRecyclerBinding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
     val binding = holder.binding
        //이미지 , artist, title, duration binding
        val bitmap =  musicList.get(position).getAblumBitmap(context, ALBUM_IMAGE_SIZE)
        if(bitmap != null){
                binding.imageAlbum.setImageBitmap(bitmap)
        }else{
                binding.imageAlbum.setImageResource(R.drawable.music_24)
        }
        binding.textArtist.text = musicList.get(position).artists
        binding.textTitle.text = musicList.get(position).title
        binding.textDuration.text = SimpleDateFormat("mm:ss").format(musicList.get(position).duration)
        //아이템항목 클릭시 PlayActivity로 뮤직데이터를 전달한다.
        binding.root.setOnClickListener {
                val intent = Intent(context, PlayActivity::class.java)
                intent.putExtra("musicData", musicList.get(position))
                context.startActivity(intent)
        }
        binding.imagelike.setOnClickListener {
            binding.imagelike.setImageResource(R.drawable.heart)
        }
    }
    inner class CustomViewHolder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
}