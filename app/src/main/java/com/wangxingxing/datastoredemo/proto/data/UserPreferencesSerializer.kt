package com.wangxingxing.datastoredemo.proto.data

import androidx.datastore.core.Serializer
import com.wangxingxing.datastoredemo.proto.UserPreferences
import java.io.InputStream
import java.io.OutputStream

/**
 * author : 王星星
 * date : 2021/12/21 16:42
 * email : 1099420259@qq.com
 * description : 序列化器
 */
object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences = UserPreferences.parseFrom(input)


    override suspend fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}