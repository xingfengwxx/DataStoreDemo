package com.wangxingxing.datastoredemo.proto.data

import android.util.Log
import androidx.datastore.core.DataStore
import com.wangxingxing.datastoredemo.proto.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException


/**
 * 通过此类来操作用户首选项
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<UserPreferences>
) {
    /**
     * 获取用户首选项 flow
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("ning", "Error reading preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            }
        }


    /**
     * 加载首选项，执行这个函数之后，dataStore.data（Flow）中就存在数据了
     */
    suspend fun fetchInitialPreferences() = dataStore.data.first()


    /**
     * 启用/禁用按截止日期排序。
     */
    suspend fun enableSortByDeadline(enable: Boolean) {
        dataStore.updateData { preferences ->
            val currentOrder = preferences.sortOrder

            val newSortOrder =
                if (enable) {
                    if (currentOrder == UserPreferences.SortOrder.BY_PRIORITY) {
                        UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        UserPreferences.SortOrder.BY_DEADLINE
                    }
                } else {
                    if (currentOrder == UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        UserPreferences.SortOrder.BY_PRIORITY
                    } else {
                        UserPreferences.SortOrder.NONE
                    }
                }

            preferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    /**
     * 启用/禁用按优先级排序。
     */
    suspend fun enableSortByPriority(enable: Boolean) {
        dataStore.updateData { preferences ->
            val currentOrder = preferences.sortOrder

            val newSortOrder =
                if (enable) {
                    if (currentOrder == UserPreferences.SortOrder.BY_DEADLINE) {
                        UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        UserPreferences.SortOrder.BY_PRIORITY
                    }
                } else {
                    if (currentOrder == UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        UserPreferences.SortOrder.BY_DEADLINE
                    } else {
                        UserPreferences.SortOrder.NONE
                    }
                }

            preferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    suspend fun updateShowCompleted(showCompleted: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setShowCompleted(showCompleted).build()
        }
    }
}