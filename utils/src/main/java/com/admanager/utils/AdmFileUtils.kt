package com.admanager.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import java.io.*
import java.nio.channels.FileChannel


class AdmFileUtils {

    companion object {
        @Throws(IOException::class)

        @JvmStatic
        fun saveBitmapToGallery(
            context: Context, bitmap: Bitmap,
            displayName: String,
            relativeLocation: String
        ): String {
//            val relativeLocation =
//                Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_directory_name)

            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            try {
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            } catch (e: java.lang.Exception) {
                //just ignore
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            }

            val resolver = context.contentResolver

            var stream: OutputStream? = null
            var uri: Uri? = null

            try {
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                uri = resolver.insert(contentUri, contentValues)

                if (uri == null) {
                    logd("Failed to create new MediaStore record.")
                    return "";
                }

                stream = resolver.openOutputStream(uri)

                if (stream == null) {
                    logd("Failed to get output stream.")
                    return "";
                }

                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream)) {
                    logd("Failed to save bitmap.")
                    return "";
                }
                return uri.toString()
            } catch (e: IOException) {
                if (uri != null) {
                    resolver.delete(uri, null, null)
                }
                e.printStackTrace()
                return "";
            } finally {
                stream?.close()
            }

        }

        @JvmStatic
        @JvmOverloads
        fun saveFileToGallery(
            context: Context, file: File,
            showToast: Boolean = true,
            displayName: String,
            relativeLocation: String = getPictureRelativeDirPath(context)
        ): Uri? {
//            val relativeLocation =
//                Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_nam)

            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/" + getExtension(displayName)
            )
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            try {
                //not in  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)  because some before Q device galleries may show at the end
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            } catch (e: Exception) {
                //just ignore
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            }

            val resolver = context.contentResolver

            var stream: OutputStream? = null
            var uri: Uri? = null

            try {
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                uri = resolver.insert(contentUri, contentValues)

                if (uri == null) {
                    logd("Failed to create new MediaStore record.")
                    return null;
                }

                stream = resolver.openOutputStream(uri)

                if (stream == null) {
                    logd("Failed to get output stream.")
                    return null;
                }

                copyStream(FileInputStream(file), stream)
                if (showToast) {
                    toast(context, R.string.saved_to_gallery)
                }
                return uri
            } catch (e: IOException) {
                if (uri != null) {
                    resolver.delete(uri, null, null)
                }
                e.printStackTrace()
                if (showToast) {
                    toast(context, R.string.saved_to_gallery_error)
                }
                return null;
            } finally {
                stream?.close()
            }


        }

        private fun toast(context: Context, @StringRes resId: Int) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    context.getString(resId),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        @JvmStatic
        fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
            val buffer = ByteArray(8192)
            inputStream.use { input ->
                outputStream.use { fileOut ->

                    while (true) {
                        val length = input.read(buffer)
                        if (length <= 0)
                            break
                        fileOut.write(buffer, 0, length)
                    }
                    fileOut.flush()
                    fileOut.close()
                }
            }
            inputStream.close()
        }

        @JvmStatic
        fun getUriFromProvider(
            activity: Context,
            file: File
        ) = GenericFileProvider.getUriForFile(
            activity,
            "com.admanager.utils.fileprovider",
            file
        )

        @JvmStatic
        fun getUriFromProvider(
            activity: Context,
            file: String
        ) = GenericFileProvider.getUriForFile(
            activity,
            "com.admanager.utils.fileprovider",
            File(file)
        )


        @JvmStatic
        fun getFilesDir(c: Context): File {
            val imagesFolder = File(c.filesDir, "images")
            imagesFolder.mkdirs()
            return imagesFolder
        }

        @JvmStatic
        @JvmOverloads
        fun getFilesDirFile(c: Context, fileName: String = "latest_shared.png"): File {
            val imagesFolder = getFilesDir(c)

            return File(imagesFolder, fileName)
        }

        @JvmStatic
        @JvmOverloads
        fun getCacheFile(c: Context, fileName: String = "latest_shared.png"): File {
            val imagesFolder = File(c.cacheDir, "images")
            imagesFolder.mkdirs()

            return File(imagesFolder, fileName)
        }

        @JvmStatic
        fun getExtension(file: File?): String {
            if (file == null) {
                return "*"
            }
            val name = file.name
            return getExtension(name)
        }

        @JvmStatic
        fun getExtension(name: String): String {
            val i = name.lastIndexOf('.')
            return if (i > 0) {
                name.substring(i + 1)
            } else "*"
        }


        @JvmStatic
        fun getApplicationName(context: Context): String {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                stringId
            )
        }

        @JvmStatic
        fun getPictureRelativeDirPath(context: Context): String {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            val appName =
                if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                    stringId
                )
            return Environment.DIRECTORY_PICTURES + File.separator + appName
        }

        @JvmStatic
        fun nameToFileName(name: String): String {
            return name.replace(" ", "_")
        }


        @JvmStatic
        fun getLastBitFromUrl(url: String): String {
            var name = url.replaceFirst(".*/([^/?]+).*".toRegex(), "$1")
            val i = name.lastIndexOf(".")
            if (i > 0) {
                name = name.substring(0, i)
            }
            return name
        }


        @Throws(IOException::class)
        @JvmStatic
        fun copyFile(sourceFile: File, destFile: File) {
            destFile.parentFile?.apply {
                if (!exists()) mkdirs()
            }

            if (!destFile.exists()) {
                destFile.createNewFile()
            }
            var source: FileChannel? = null
            var destination: FileChannel? = null
            try {
                source = FileInputStream(sourceFile).channel
                destination = FileOutputStream(destFile).channel
                destination.transferFrom(source, 0, source.size())
            } finally {
                source?.close()
                destination?.close()
            }
        }
    }

}


fun Any.logd(message: String) {
//    if (BuildConfig.DEBUG)
    Log.d(this::class.java.simpleName, message)
}