package com.metalap.manager_gallery_saver


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import java.io.*

class FilesUtils {
    private lateinit var mContext: Context
    private  lateinit var cursor: Cursor
    private val TAG = "FileUtils"
    private  val SCALE_FACTOR = 50.0
    private  val BUFFER_SIZE = 1024 * 1024 * 8
    private  val DEGREES_90 = 90
    private  val DEGREES_180 = 180
    private  val DEGREES_270 = 270
    private  val EOF = -1


    /**
     * Inserts image into external storage
     *
     * @param contentResolver - content resolver
     * @param path            - path to temp file that needs to be stored
     * @param folderName      - folder name for storing image
     * @param toDcim          - whether the file should be saved to DCIM
     * @return true if image was saved successfully
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun insertImage(
        contentResolver: ContentResolver,
        path: String,
        folderName: String?,
        albumType:String
    ): Boolean{

        val file = File(path)
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        var source = getBytesFromFile(file)
        var directory = Environment.DIRECTORY_DCIM


        when (albumType){
            "DIRECTORY_DCIM" ->  directory = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> directory = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> directory = Environment.DIRECTORY_PICTURES
        }

        val rotatedBytes = getRotatedBytesIfNecessary(source, path)
        if (rotatedBytes != null) {
            source = rotatedBytes
        }

        val albumDir = File(getAlbumFolderPath(folderName,albumType))
        val imageFilePath = File(albumDir, file.name).absolutePath

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, file.name)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        values.put(MediaStore.Images.Media.SIZE, file.length())


        if (android.os.Build.VERSION.SDK_INT < 29) {
            values.put(MediaStore.Images.ImageColumns.DATA, imageFilePath)
        }
        else {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.RELATIVE_PATH, directory + File.separator + folderName)
        }

        
        var imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        try {
            imageUri = contentResolver.insert(imageUri, values)

            if (source != null) {
                var outputStream: OutputStream? = null
                if (imageUri != null) {
                    outputStream = contentResolver.openOutputStream(imageUri)
                }

                outputStream?.use {
                    outputStream.write(source)
                }

                if (imageUri != null && android.os.Build.VERSION.SDK_INT < 29) {
                    val pathId = ContentUris.parseId(imageUri)
                    val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver, pathId, MediaStore.Images.Thumbnails.MINI_KIND, null)
                    storeThumbnail(contentResolver, miniThumb, pathId)
                    //ThumbnailUtils.
                }
            } else {
                if (imageUri != null) {
                    contentResolver.delete(imageUri, null, null)
                }
                imageUri = null
            }
        } catch (e: IOException) {
            contentResolver.delete(imageUri!!, null, null)
            return false
        } catch (t: Throwable) {
            return false
        }

        return true
    }

    /**
     * @param contentResolver - content resolver
     * @param source          - bitmap source image
     * @param id              - path id
     */
    @SuppressLint("Recycle")
    private fun storeThumbnail(
        contentResolver: ContentResolver,
        source: Bitmap,
        id: Long
    ) {

        val matrix = Matrix()

        val scaleX = SCALE_FACTOR.toFloat() / source.width
        val scaleY = SCALE_FACTOR.toFloat() / source.height

        matrix.setScale(scaleX, scaleY)

        val thumb = Bitmap.createBitmap(
            source, 0, 0,
            source.width,
            source.height,
            matrix,
            true
        )

        val values = ContentValues()
//        android.graphics.Bitmap.createBitmap()
        //KIND
//        context.contentResolver.loadThumbnail().width
//        values.put("kind", MediaStore.Images.Thumbnails.MICRO_KIND)
        values.put("image_id", id.toInt())
        values.put("height", thumb.height)
        values.put("width", thumb.width)

        val thumbUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )


//        val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val cs = CancellationSignal()
////            context.contentResolver.loadThumbnail(mediaUri, Size(100, 100), cs)
//        } else {
//            MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
//        }

//        val thumbBitmap: Bitmap
//        thumbBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            mContext.getContentResolver().loadThumbnail(thumbUri, thumbSize, null)
//        } else {
//            MediaStore.Images.Thumbnails.getThumbnail(
//                mContext.getContentResolver(),
//                id, MediaStore.Images.Thumbnails.MINI_KIND, null
//            )
//        }

        var outputStream: OutputStream? = null
        try{
            outputStream.use {
                if (thumbUri != null) {
                    outputStream = contentResolver.openOutputStream(thumbUri)
                }
            }}catch (e: Exception){
            //avoid crashing on devices that do not support thumb
        }
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let { context.getContentResolver().query(it, projection, selection, selectionArgs,null) }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    /**
     * @param contentResolver - content resolver
     * @param path            - path to temp file that needs to be stored
     * @param folderName      - folder name for storing video
     * @param toDcim          - whether the file should be saved to DCIM
     * @return true if video was saved successfully
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun insertVideo(
        contentResolver: ContentResolver,
        inputPath: String,
        folderName: String?,
        albumType:String,
        bufferSize: Int = BUFFER_SIZE
    ): Boolean {
        val inputFile = File(inputPath)
        val inputStream: InputStream?
        val outputStream: OutputStream?

        val extension = MimeTypeMap.getFileExtensionFromUrl(inputFile.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        var directory = Environment.DIRECTORY_DCIM



        when (albumType){
            "DIRECTORY_DCIM" ->  directory = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> directory = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> directory = Environment.DIRECTORY_PICTURES
        }

        val albumDir = File(getAlbumFolderPath(folderName, albumType))
        val videoFilePath = File(albumDir, inputFile.name).absolutePath

        val values = ContentValues()
        values.put(MediaStore.Video.Media.TITLE, inputFile.name)
        values.put(MediaStore.Video.Media.DISPLAY_NAME, inputFile.name)
        values.put(MediaStore.Video.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis())
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())

        if (android.os.Build.VERSION.SDK_INT < 29) {
            try {
                val r = MediaMetadataRetriever()
                r.setDataSource(inputPath)
                val durString = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durString!!.toInt()
                values.put(MediaStore.Video.Media.DURATION, duration)
                values.put(MediaStore.Video.VideoColumns.RELATIVE_PATH, videoFilePath)

//              val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)
//                cursor.getString(dataColumn)

            } catch(e: Exception) {}
        } else {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, directory + File.separator + folderName)
        }

        try {
            val url = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            inputStream = FileInputStream(inputFile)
            if (url != null) {
                outputStream = contentResolver.openOutputStream(url)
                val buffer = ByteArray(bufferSize)
                inputStream.use {
                    outputStream?.use {
                        var len = inputStream.read(buffer)
                        while (len != EOF) {
                            outputStream.write(buffer, 0, len)
                            len = inputStream.read(buffer)
                        }
                    }
                }
            }
        } catch (fnfE: FileNotFoundException) {
            Log.e("Gallery Manager Saver", fnfE.message ?: fnfE.toString())
            return false
        } catch (e: Exception) {
            Log.e("Gallery Manager Saver", e.message ?: e.toString())
            return false
        }
        return true
    }

    /**
     * @param source -  array of bytes that will be rotated if it needs to be done
     * @param path   - path to image that needs to be checked for rotation
     * @return - array of bytes from rotated image, if rotation needs to be performed
     */
    private fun getRotatedBytesIfNecessary(source: ByteArray?, path: String): ByteArray? {
        var rotationInDegrees = 0

        try {
            rotationInDegrees = exifToDegrees(getRotation(path))
        } catch (e: IOException) {
            Log.d(TAG, e.toString())
        }

        if (rotationInDegrees == 0) {
            return null
        }

        val bitmap = BitmapFactory.decodeByteArray(source, 0, source!!.size)
        val matrix = Matrix()
        matrix.preRotate(rotationInDegrees.toFloat())
        val adjustedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true
        )
        bitmap.recycle()

        val rotatedBytes = bitmapToArray(adjustedBitmap)

        adjustedBitmap.recycle()

        return rotatedBytes
    }

    /**
     * @param orientation - exif orientation
     * @return how many degrees is file rotated
     */
    private fun exifToDegrees(orientation: Int): Int {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> DEGREES_90
            ExifInterface.ORIENTATION_ROTATE_180 -> DEGREES_180
            ExifInterface.ORIENTATION_ROTATE_270 -> DEGREES_270
            else -> 0
        }
    }
    /**
     * @param path - path to bitmap that needs to be checked for orientation
     * @return exif orientation
     * @throws IOException - can happen while creating [ExifInterface] object for
     * provided path
     */
    @Throws(IOException::class)
    private fun getRotation(path: String): Int {
        val exif = ExifInterface(path)
        return exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    }

    private fun bitmapToArray(bmp: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        bmp.recycle()
        return byteArray
    }

    private fun getBytesFromFile(file: File): ByteArray? {
        val size = file.length().toInt()
        val bytes = ByteArray(size)
        val buf = BufferedInputStream(FileInputStream(file))
        buf.use {
            buf.read(bytes, 0, bytes.size)
        }

        return bytes
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getAlbumFolderPathWithCall(call: MethodCall): String{
        var folderName =""
        var albumType = ""
        folderName = call.argument<String>("folderName")as String
        albumType = call.argument<String>("albumType") as String
        return getAlbumFolderPath(folderName,albumType);
    }

    /**
     *
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getAlbumFolderPath(
        folderName: String?,
        albumType: String
        //mediaType: MediaType,
    ): String {
//        var albumFolderPath: String = Environment.getRootDirectory().path
        //getExternalFilesDir getExternalStorageDirectory
         var albumFolderPath: String;

        if(android.os.Build.VERSION.SDK_INT < 29){
            albumFolderPath = Environment.getExternalStorageDirectory().path;
        }else{
            albumFolderPath = Environment.getRootDirectory().path;
            //Environment.getRootDirectory().path;
            //getExternalStorageState
        }

        //print("albumFolderPath:" +albumFolderPath)

        if (albumType == "DIRECTORY_DCIM" && android.os.Build.VERSION.SDK_INT < 29) {
            albumFolderPath += File.separator + Environment.DIRECTORY_DCIM;
        }

        var baseFolderName = Environment.DIRECTORY_DCIM;

        when (albumType){
            "DIRECTORY_DCIM" ->  baseFolderName = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> baseFolderName = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> baseFolderName = Environment.DIRECTORY_PICTURES
        }

        if(android.os.Build.VERSION.SDK_INT < 29){
            albumFolderPath = if (TextUtils.isEmpty(folderName)) {
                createDirIfNotExist(
                    Environment.getExternalStoragePublicDirectory(baseFolderName).path
                ) ?: albumFolderPath
            } else {
                createDirIfNotExist(albumFolderPath + File.separator + folderName)
                    ?: albumFolderPath
            }
        }else{
            albumFolderPath = if (TextUtils.isEmpty(folderName)) {
                mContext.getExternalFilesDir(baseFolderName)?.path?.let {
                    createDirIfNotExist(
                        it
                    )
                } ?: albumFolderPath
            } else {
                createDirIfNotExist(albumFolderPath + File.separator + folderName)
                    ?: albumFolderPath
            }
        }


//            if(android.os.Build.VERSION.SDK_INT < 29){
//            createDirIfNotExist(
//                Environment.getExternalStoragePublicDirectory(baseFolderName).path
//            ) ?: albumFolderPath
        //            }

//            createDirIfNotExist(
//                context.getExternalFilesDir(baseFolderName).path
//            ) ?: albumFolderPath

//            if (android.os.Build.VERSION.SDK_INT as Int >= 29) {
//                jFolder = File(
//                    context.getExternalFilesDir(baseFolderName),
//                    "Camera"
//                )
//            } else {
//                jFolder = File(
//                    Environment.getExternalStoragePublicDirectory(baseFolderName),
//                    "Camera"
//                )
//            }

        return albumFolderPath
    }

    private fun createDirIfNotExist(dirPath: String): String? {
        val dir = File(dirPath)
        return if (!dir.exists()) {
            if (dir.mkdirs()) {
                dir.path
            } else {
                null
            }
        } else {
            dir.path
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getEnvironmentfile(type:String) :String?{
        when (type) {
            "DIRECTORY_DCIM" -> Environment.DIRECTORY_DCIM
            "DIRECTORY_ALARMS" -> Environment.DIRECTORY_ALARMS
            "DIRECTORY_AUDIOBOOKS" -> Environment.DIRECTORY_AUDIOBOOKS
            "DIRECTORY_RINGTONES" -> Environment.DIRECTORY_RINGTONES
            "DIRECTORY_DOWNLOADS" -> Environment.DIRECTORY_DOWNLOADS
            "DIRECTORY_MUSIC" -> Environment.DIRECTORY_MUSIC
            "DIRECTORY_MOVIES" -> Environment.DIRECTORY_MOVIES
            "DIRECTORY_NOTIFICATIONS" -> Environment.DIRECTORY_NOTIFICATIONS
            "DIRECTORY_PODCASTS" -> Environment.DIRECTORY_PODCASTS
            "DIRECTORY_PICTURES" -> Environment.DIRECTORY_PICTURES
            "DIRECTORY_DOCUMENTS" -> Environment.DIRECTORY_DOCUMENTS
            "DIRECTORY_RECORDINGS" -> Environment.DIRECTORY_SCREENSHOTS
        }
        return null
    }
}