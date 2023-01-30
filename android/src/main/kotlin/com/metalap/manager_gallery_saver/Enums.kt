package com.metalap.manager_gallery_saver

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi

enum class enums {
}

enum class MediaType { Image, Video }

enum class DirectoryType(val value: String) {
    MOVIES(Environment.DIRECTORY_MOVIES),
    PICTURES(Environment.DIRECTORY_PICTURES),
    MUSIC(Environment.DIRECTORY_MUSIC),
    DCIM(Environment.DIRECTORY_DCIM),
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    DOCUMENTS(Environment.DIRECTORY_DOCUMENTS),
    DOWNLOADS(Environment.DIRECTORY_DOWNLOADS)
}
