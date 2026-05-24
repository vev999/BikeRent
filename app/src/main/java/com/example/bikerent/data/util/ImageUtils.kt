package com.example.bikerent.data.util

import android.content.Context
import android.net.Uri
import java.io.File

object ImageUtils {

    fun copyToAppStorage(context: Context, uri: Uri, folder: String = "bike_images"): String? {
        return try {
            val dir = File(context.filesDir, folder)
            dir.mkdirs()
            val fileName = "${folder}_${System.currentTimeMillis()}.jpg"
            val destFile = File(dir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /** Zwraca File dla ścieżek lokalnych (zaczynają się od '/'), URL jako String. */
    fun imageModel(path: String): Any = if (path.startsWith("/")) File(path) else path
}
