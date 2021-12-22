package com.wangxingxing.datastoredemo.preferences.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.wangxingxing.datastoredemo.preferences.ui.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * author : 王星星
 * date : 2021/12/16 16:50
 * email : 1099420259@qq.com
 * description : 通过此类来操作用户首选项
 */

enum class SortOrder {
    NONE,
    BY_DEADLINE, // 最后期限
    BY_PRIORITY, // 优先级
    BY_DEADLINE_AND_PRIORITY // 最后期限和优先级
}

// 用户首选项
data class UserPreferences(
    // 是否显示已完成任务
    val showCompleted: Boolean,
    // 排序方式
    val sortOrder: SortOrder
)

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        // 排序方式
        val SORT_ORDER = stringPreferencesKey("sort_order")

        // 是否显示已完成任务
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    }

    /**
     * 获取用户首选项 flow
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    /**
     * 加载首选项，执行这个函数之后，dataStore.data（Flow）中就存在数据了
     */
    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        // 从首选项获取排序顺序，并将其转换为[SortOrder]对象
        val sortOrder =
            SortOrder.valueOf(preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.NONE.name)

        // 获取“显示完成”值，如果未设置，则默认为false：
        val showCompleted = preferences[PreferencesKeys.SHOW_COMPLETED] ?: false
        return UserPreferences(showCompleted, sortOrder)
    }

    /**
     * 启用/禁用按截止日期排序。
     *
     * @param enable
     */
    suspend fun enableSortByDeadline(enable: Boolean) {
        dataStore.edit { preferences ->
            val currentOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.NONE.name
            )

            val newSortOrder =
                if (enable) {
                    if (currentOrder == SortOrder.BY_PRIORITY) {
                        SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        SortOrder.BY_DEADLINE
                    }
                } else {
                    if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        SortOrder.BY_PRIORITY
                    } else {
                        SortOrder.NONE
                    }
                }

            preferences[PreferencesKeys.SORT_ORDER] = newSortOrder.name
        }
    }

    /**
     * 启用/禁用按优先级排序。
     *
     * @param enable
     */
    suspend fun enableSortByPriority(enable: Boolean) {
        dataStore.edit { preferences ->
            val currentOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.NONE.name
            )

            val newSortOrder =
                if (enable) {
                    if (currentOrder == SortOrder.BY_DEADLINE) {
                        SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        SortOrder.BY_PRIORITY
                    }
                } else {
                    if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        SortOrder.BY_DEADLINE
                    } else {
                        SortOrder.NONE
                    }
                }

            preferences[PreferencesKeys.SORT_ORDER] = newSortOrder.name
        }
    }

    suspend fun updateShowCompleted(showCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COMPLETED] = showCompleted
        }
    }
}