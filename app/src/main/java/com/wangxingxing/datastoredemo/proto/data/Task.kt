package com.wangxingxing.datastoredemo.proto.data

import java.util.Date

/**
 * 任务优先级，高、中、低
 */
enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

/**
 * 任务
 */
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
