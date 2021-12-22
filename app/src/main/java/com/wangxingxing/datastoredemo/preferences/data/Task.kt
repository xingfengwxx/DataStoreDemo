package com.wangxingxing.datastoredemo.preferences.data

import java.util.*

/**
 * author : 王星星
 * date : 2021/12/16 16:18
 * email : 1099420259@qq.com
 * description : 任务
 */

/**
 * 任务优先级，高、中、低
 */
enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

data class Task(
    // 任务名称
    val name: String,
    // 最后期限
    val deadline: Date,
    // 优先级
    val priority: TaskPriority,
    // 是否已完成
    val completed: Boolean = false
)