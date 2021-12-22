package com.wangxingxing.datastoredemo.proto.examples

import androidx.datastore.core.Serializer
import com.wangxingxing.datastoredemo.proto.Settings
import java.io.InputStream
import java.io.OutputStream

/**
 * author : 王星星
 * date : 2021/12/21 16:21
 * email : 1099420259@qq.com
 * description :
 */
object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings
        get() = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings = Settings.parseFrom(input)

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)
}