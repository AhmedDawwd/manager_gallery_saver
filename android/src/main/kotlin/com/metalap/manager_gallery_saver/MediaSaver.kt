package com.metalap.manager_gallery_saver

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import io.flutter.plugin.common.MethodCall
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream

class MediaSaver {

    private lateinit var context: Context
    private var mediaMetadataRetriever: MediaMetadataRetriever? = null



    fun saveImageToGallery(call: MethodCall): HashMap<String, Any?> {

        val image = call.argument<ByteArray>("imageBytes")
        val quality = call.argument<Int>("quality")
        val name = call.argument<String>("name")
        val albumType = call.argument<String>("albumType")

        val fileUri = albumType?.let { generateUri("jpg", name = name, it) }

        val bmp = image?.let { BitmapFactory.decodeByteArray(image, 0, it.size) }

        return try {
            val fos = fileUri?.let { context.contentResolver?.openOutputStream(it) }!!
            // println("ImageGallerySaverPlugin $quality")
            if (quality != null) {
                bmp?.compress(Bitmap.CompressFormat.JPEG, quality, fos)
            }
            fos.flush()
            fos.close()
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri))
            bmp?.recycle()


            SaveResultModel(fileUri.toString().isNotEmpty(), fileUri.toString(), null).toHashMap()
        } catch (e: IOException) {
            SaveResultModel(false, null, e.toString()).toHashMap()
        }
    }

    fun saveFileToGallery(call: MethodCall): HashMap<String, Any?> {

        val filePath = call.argument<String>("file")
        val name = call.argument<String>("name")
        val albumType = call.argument<String>("albumType")

        return try {
            val originalFile = File(filePath)
            val fileUri = albumType?.let { generateUri(originalFile.extension, name, it) }

            val outputStream = fileUri?.let { context.contentResolver?.openOutputStream(it) }!!
            val fileInputStream = FileInputStream(originalFile)

            val buffer = ByteArray(10240)
            var count = 0
            while (fileInputStream.read(buffer).also { count = it } > 0) {
                outputStream.write(buffer, 0, count)
            }

            outputStream.flush()
            outputStream.close()
            fileInputStream.close()

            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri))
            SaveResultModel(fileUri.toString().isNotEmpty(), fileUri.toString(), null).toHashMap()
        } catch (e: IOException) {
            SaveResultModel(false, null, e.toString()).toHashMap()
        }
    }



    private fun generateUri(extension: String = "", name: String? = null,albumType:String): Uri {

        var fileName = name ?: System.currentTimeMillis().toString()

        var environmentfile = Environment.DIRECTORY_DCIM

        when (albumType){
            "DIRECTORY_DCIM" ->  environmentfile = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> environmentfile = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> environmentfile = Environment.DIRECTORY_PICTURES
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val values = ContentValues()

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, environmentfile)

            val mimeType = getMIMEType(extension)
            if (!TextUtils.isEmpty(mimeType)) {
                values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                if (mimeType!!.startsWith("video")) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, environmentfile)
                }
            }
            return context.contentResolver?.insert(uri, values)!!
        } else {
            val storePath = context.getExternalFilesDir(null)?.absolutePath + File.separator + environmentfile
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            if (extension.isNotEmpty()) {
                fileName += (".$extension")
            }
            return Uri.fromFile(File(appDir, fileName))
        }
    }

    private fun getMIMEType(extension: String): String? {
        var type: String? = null;
        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
        return type
    }



    private fun storeThumbnailToFile(call: MethodCall) : String? {
        mediaMetadataRetriever = mediaMetadataRetriever ?: MediaMetadataRetriever()

        val path = call.argument<String>("path")!!
        val thumbnailPath = call.argument<String>("thumbnailPath")
        val quality = call.argument<Int>("quality") ?: 100
        val saveToLibrary = call.argument<Boolean>("saveToLibrary") ?: false

        try {
            mediaMetadataRetriever!!.setDataSource(path)
        } catch (e: IllegalArgumentException){
            throw e
        }
        val bitmap: Bitmap? = mediaMetadataRetriever!!.frameAtTime
        var format = Bitmap.CompressFormat.JPEG
        if (thumbnailPath != null) {
            val outputDir = File(thumbnailPath).parentFile!!
            if (!outputDir.exists()) {
                outputDir.mkdir()
            }
            val extension = MediaStoreUtils.getFileExtension(thumbnailPath)
            format = if (extension == "jpg" || extension == "jpeg") {
                Bitmap.CompressFormat.JPEG
            } else if (extension == "png") {
                Bitmap.CompressFormat.PNG
            } else {
                Bitmap.CompressFormat.JPEG
            }
        }

        val file = if (thumbnailPath != null) {
            File(thumbnailPath)
        } else {
            File(MediaStoreUtils.generateTempPath(context, DirectoryType.PICTURES.value, extension = ".jpg", filename = File(path).nameWithoutExtension+"_thumbnail"))
        }
        if (file.exists()) {
            file.delete()
        }
        try {
            //outputStream获取文件的输出流对象
            val fos: OutputStream = file.outputStream()
            //压缩格式为JPEG图像，压缩质量为100%
            bitmap!!.compress(format, quality, fos)
            fos.flush()
            fos.close()
            if (saveToLibrary) {
                MediaStoreUtils.insert(context, file)
            }
            return file.absolutePath
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

class SaveResultModel(var isSuccess: Boolean,
                      var filePath: String? = null,
                      var errorMessage: String? = null) {
    fun toHashMap(): HashMap<String, Any?> {
        val hashMap = HashMap<String, Any?>()
        hashMap["isSuccess"] = isSuccess
        hashMap["filePath"] = filePath
        hashMap["errorMessage"] = errorMessage
        return hashMap
    }
}