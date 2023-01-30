package com.metalap.manager_gallery_saver


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore.Images.Media.insertImage
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.*
import java.io.File

class ManagerGallerySaver internal constructor(private val activity: Activity) :
    PluginRegistry.RequestPermissionsResultListener{

    private lateinit var context: Context

    private var pendingResult: MethodChannel.Result? = null
    private var mediaType: MediaType? = null
    private var filePath: String = ""
    private var albumName: String = ""
    private var albumType:String =""
    private var toDcim: Boolean = false
    private var toMovies: Boolean = false
    private var toPictures: Boolean = false

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)




    /**
     * Saves image or video to device
     *
     * @param methodCall - method call
     * @param result     - result to be set when saving operation finishes
     * @param mediaType    - media type
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    internal fun checkPermissionAndSaveFile(
        methodCall: MethodCall,
        result: MethodChannel.Result,
        mediaType: MediaType
    ) {
        filePath = methodCall.argument<Any>(KEY_PATH)?.toString() ?: ""
        albumName = methodCall.argument<Any>(KEY_ALBUM_NAME)?.toString() ?: ""
//        toDcim = methodCall.argument<Any>(KEY_TO_DCIM) as Boolean
//        toMovies =methodCall.argument<Boolean>(KEY_TO_MMVIES) as Boolean
//        toPictures =methodCall.argument<Boolean>(KEY_TO_PICTURES) as Boolean
        albumType = methodCall.argument<Any>(KEY_ALBUM_TYPE)?.toString() ?: ""
        this.mediaType = mediaType
        this.pendingResult = result

        if (isWritePermissionGranted() || android.os.Build.VERSION.SDK_INT >= 29) {
            saveMediaFile()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION
            )
        }
    }




    private  fun isWritePermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
    }

    private fun isReadPermissionGranted(): Boolean {
        // isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.READ_EXTERNAL_STORAGE
                )
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun saveMediaFile() {
        uiScope.launch {
            val success = async(Dispatchers.IO) {
                if (mediaType == MediaType.Video) {
                    FilesUtils().insertVideo(activity.contentResolver, filePath, albumName, albumType)
                } else {
                    FilesUtils().insertImage(activity.contentResolver, filePath, albumName, albumType)
                }
            }
            success.await()
            finishWithSuccess()
        }
    }

    private fun finishWithSuccess() {
        pendingResult!!.success(true)
        pendingResult = null
    }

    private fun finishWithFailure() {
        pendingResult!!.success(false)
        pendingResult = null
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {

        if (requestCode == REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION) {
            val permissionGranted = grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                saveMediaFile()
            } else {
                //finishWithFailure()
            }
            return true
        }
        return false
    }

    companion object {

        private const val REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 2408

        private const val KEY_PATH = "path"
        private const val KEY_ALBUM_NAME = "albumName"
        private const val KEY_TO_DCIM = "toDcim"
        private const val KEY_TO_MMVIES = "toMovies"
        private const val KEY_TO_PICTURES = "toPictures"
        private const val KEY_ALBUM_TYPE = "albumType"
    }

}