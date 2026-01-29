package com.example.clientchodientu.untils

import android.net.Uri

object FileUtils {
    fun getFileFromUri(context: android.content.Context, uri: Uri): java.io.File? {
        val contentResolver = context.contentResolver
        val fileName = "upload_${System.currentTimeMillis()}.jpg"
        val tempFile = java.io.File(context.cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}