package com.example.mp3pro

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import java.io.Serializable

class MusicData(id:String , title : String?, artists : String?, albumId : String?, duration : Long?) : Serializable {
    var id:String = ""
    var title : String?
    var artists : String?
    var albumId : String?
    var duration : Long?
    init {
        this.id = id
        this.title = title
        this.artists = artists
        this.albumId = albumId
        this.duration = duration
    }
    //음악 id를 통해서 음악파일 uri를 가져오는 함수
    fun getMusicUri(): Uri =
        Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.id)

    //음악앨범을 가져오는 Uri 함수
    fun getAlbumUri() : Uri=
         Uri.parse("content://media/external/audio/albumart/${this.albumId}")

    //음악앨범을 Uri를 통해서 bitmap으로 가져오는 함수
    fun getAblumBitmap(context: Context, albumImageSize : Int) : Bitmap?{
        val contentResolver: ContentResolver = context.contentResolver
        val albumUri =  getAlbumUri()
        val options = BitmapFactory.Options()
        var bitmap:Bitmap? = null
        var parcelFileDescriptor : ParcelFileDescriptor? = null

        try{
            if (albumUri !=  null){
                //음악이미지를 가져와서 BitmapFactory.decodeFileDescriptor
                parcelFileDescriptor = contentResolver.openFileDescriptor(albumUri,"r")
                bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor?.fileDescriptor, null, options)

                //비트맵 사이즈를 결정함
                if(bitmap != null){
                    //화면에 보여줄 이미지 사이즈가 맞지 않을경우 강제로 사이즈를 정해버림
                    if(options.outHeight != albumImageSize || options.outWidth !=albumImageSize){
                        val tempBitmap = Bitmap.createScaledBitmap(bitmap, albumImageSize, albumImageSize, true)
                        bitmap.recycle()
                        bitmap = tempBitmap
                    }
                }
            }
        }catch (e:java.lang.Exception){
            Log.e("MusicData", e.toString())
        }finally {
            try{
                if(parcelFileDescriptor != null){
                    parcelFileDescriptor.close()
                }
            }catch (e:java.lang.Exception){
                Log.e("MusicData", e.toString())
            }
        }
        return bitmap
    }
}