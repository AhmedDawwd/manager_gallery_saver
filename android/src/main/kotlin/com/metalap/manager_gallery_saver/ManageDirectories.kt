package com.metalap.manager_gallery_saver

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.flutter.plugin.common.PluginRegistry
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import io.flutter.plugin.common.MethodCall
import java.io.File

class ManageDirectories  internal constructor(private val activity: Activity) :
    PluginRegistry.RequestPermissionsResultListener{

    private lateinit var context: Context
    private  var filesUtils : FilesUtils? =null



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION) {
            val permissionGranted = grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                // saveMediaFile()
            } else {
                //finishWithFailure()
            }
            return true
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getExternalStorageDirectories() : ArrayList<String> {
        var extRootPaths: ArrayList<String> = ArrayList<String>()
        // if (isWritePermissionGranted()|| android.os.Build.VERSION.SDK_INT >= 19) {
        val appsDir: Array<File> = context.getExternalFilesDirs(null)
        for (file: File in appsDir)
            extRootPaths.add(file.absolutePath)
        //}
        return extRootPaths;
        // return Environment.getExternalStorageDirectory().toString();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getExternalStorageDefaultDirectoriesPath(call: MethodCall): String? {
        val albumType = call.argument<String>("albumType")
        // if (isWritePermissionGranted()) {
        val environmentfile = albumType?.let { filesUtils?.getEnvironmentfile(it) }
        val path: File? = context.getExternalFilesDir(environmentfile.toString())

        return path?.path.toString()
        //  }
//        return  null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun hasExternalStoragePrivateFile(call: MethodCall): Boolean {
        val albumName = call.argument<String>("albumName")
        val albumType = call.argument<String>("albumType")
        // if (isWritePermissionGranted()) {

        var environmentfile = Environment.DIRECTORY_DCIM

        when (albumType){
            "DIRECTORY_DCIM" ->  environmentfile = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> environmentfile = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> environmentfile = Environment.DIRECTORY_PICTURES
        }

        // environmentfile = albumType?.let { filesUtils?.getEnvironmentfile(it) }
        val file = albumName?.let { File(context.getExternalFilesDir(environmentfile), it) };
        if (file != null) {
            if (file.exists()) {
                return true;
            }
        }
        //}
        return false;
    }

    fun hasExternalStorageFileWithPath(call: MethodCall): Boolean {
        val albumName = call.argument<String>("albumName")
        val path = call.argument<String>("path")
//       context.getExternalStorageDir();
        //if (isWritePermissionGranted()) {
        val file = albumName?.let { File(path, it) };
        if (file != null) {
            if (file.exists()) {
                return true;
            }
        }
        // }
        return false;
    }

    fun hasExternalStorageDirectoryWithPath(call: MethodCall): Boolean {
        val path = call.argument<String>("path")
        //if (isWritePermissionGranted()) {
        val file = path?.let { File(it) };
        if (file != null) {
            if (file.exists()) {
                return true;
            }
        }
        // }
        return false;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun createAlbum(call: MethodCall) :Boolean{
        val albumName = call.argument<String>("albumName")
        val albumType = call.argument<String>("albumType")

        var environmentfile = Environment.DIRECTORY_DCIM

        when (albumType){
            "DIRECTORY_DCIM" ->  environmentfile = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> environmentfile = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> environmentfile = Environment.DIRECTORY_PICTURES
        }

        if (isWritePermissionGranted()) {
            if (albumName != null) {
                //val environmentfile = albumType?.let { filesUtils?.getEnvironmentfile(it) }
                val rootFile = File(context.getExternalFilesDir(environmentfile), albumName)
                if (!rootFile.exists()) {
                    rootFile.mkdirs()
                    return true
                }
            }
        } else{
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION)
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getPathAlbum(call: MethodCall): String? {
        val albumName = call.argument<String>("albumName")
        val albumType = call.argument<String>("albumType")

        var environmentfile = Environment.DIRECTORY_DCIM

        when (albumType){
            "DIRECTORY_DCIM" ->  environmentfile = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> environmentfile = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> environmentfile = Environment.DIRECTORY_PICTURES
        }
         if (isWritePermissionGranted()) {
        if (albumName != null) {
            //val environmentfile = albumType?.let { filesUtils?.getEnvironmentfile(it) }
            val rootFile = File(context.getExternalFilesDir(environmentfile), albumName)
            //return rootFile.path.toString();
            if (rootFile.exists()) {
                return rootFile.path.toString()
            }
              }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getPathFileInAlbum(call: MethodCall): String? {
        val albumName = call.argument<String>("albumName")
        val albumType = call.argument<String>("albumType")
        val fileName = call.argument<String>("fileName")

        var environmentfile = Environment.DIRECTORY_DCIM

        when (albumType){
            "DIRECTORY_DCIM" ->  environmentfile = Environment.DIRECTORY_DCIM
            "DIRECTORY_MOVIES" -> environmentfile = Environment.DIRECTORY_MOVIES
            "DIRECTORY_PICTURES" -> environmentfile = Environment.DIRECTORY_PICTURES
        }

        // if (isWritePermissionGranted()) {
        if (albumName != null&& fileName !=null) {
            //val environmentfile = albumType?.let { filesUtils?.getEnvironmentfile(it) }
            val rootFile =
                File(context.getExternalFilesDir(environmentfile)?.path + albumName, fileName)
            //return rootFile.path.toString();
            if (rootFile.exists()) {
                return rootFile.path.toString()
            }
        }
        //  }
        return null
    }

        fun getExternalStoragePublicDirectory(type: String?) : String {
        val file = context.getExternalFilesDir(type);
        return  file?.path.toString();
    }

    fun   isStoragePermissionGranted() :Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PermissionChecker.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this.activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
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

    fun checkPermission():Boolean{
        val result = ContextCompat.checkSelfPermission(this.activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun  requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(this.activity, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this.activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE);
        }
    }

    companion object {

        private const val REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 2408
        private const val  PERMISSION_REQUEST_CODE = 1

        private const val KEY_PATH = "path"
        private const val KEY_ALBUM_NAME = "albumName"
        private const val KEY_TO_DCIM = "toDcim"
        private const val KEY_ALBUM_TYPE = "albumType"

    }
}