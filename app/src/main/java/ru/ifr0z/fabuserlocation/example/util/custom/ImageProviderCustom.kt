package ru.ifr0z.fabuserlocation.example.util.custom

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.yandex.runtime.image.ImageProvider
import ru.ifr0z.fabuserlocation.example.util.extension.vectorDrawableToBitmap

class ImageProviderCustom(
    private val context: Context, @DrawableRes val id: Int
) : ImageProvider() {

    override fun getImage(): Bitmap? = context.vectorDrawableToBitmap(id)

    override fun getId(): String = id.toString()
}