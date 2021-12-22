package com.wangxingxing.datastoredemo.preferences.data

import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : 王星星
 * date : 2021/12/16 16:46
 * email : 1099420259@qq.com
 * description : 任务仓库，提供一个任务 Flow
 */
object TasksRepository {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // In a real app, this would be coming from a data source like a database
    val tasks = flowOf(
        listOf(
            Task(
                name = "Open codelab",
                deadline = simpleDateFormat.parse("2020-07-03")!!,
                priority = TaskPriority.LOW,
                completed = true
            ),
            Task(
                name = "Import project",
                deadline = simpleDateFormat.parse("2020-04-03")!!,
                priority = TaskPriority.MEDIUM,
                completed = true
            ),
            Task(
                name = "Check out the code", deadline = simpleDateFormat.parse("2020-05-03")!!,
                priority = TaskPriority.LOW
            ),
            Task(
                name = "Read about DataStore", deadline = simpleDateFormat.parse("2020-06-03")!!,
                priority = TaskPriority.HIGH
            ),
            Task(
                name = "Implement each step",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
            Task(
                name = "Understand how to use DataStore",
                deadline = simpleDateFormat.parse("2020-04-03")!!,
                priority = TaskPriority.HIGH
            ),
            Task(
                name = "Understand how to migrate to DataStore",
                deadline = Date(),
                priority = TaskPriority.HIGH
            )
        )
    )
}