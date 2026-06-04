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

    /**
     * Zwraca:
     *  - File dla ścieżek bezwzględnych (avatar użytkownika z pamięci wewnętrznej)
     *  - @DrawableRes Int dla zasobów android.resource:// (lokalne drawable) — Coil3 ładuje po ID
     *  - String dla zewnętrznych URL-i (fallback)
     */
    fun imageModel(context: Context, path: String): Any = when {
        path.startsWith("/") -> File(path)
        path.startsWith("android.resource://") -> {
            val resName = path.substringAfterLast("/")
            val id = context.resources.getIdentifier(resName, "drawable", context.packageName)
            if (id != 0) id else path
        }
        else -> path
    }
}
