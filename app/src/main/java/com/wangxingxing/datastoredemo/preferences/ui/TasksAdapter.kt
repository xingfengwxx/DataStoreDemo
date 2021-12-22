package com.wangxingxing.datastoredemo.preferences.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.wangxingxing.datastoredemo.preferences.data.Task

/**
 * author : 王星星
 * date : 2021/12/16 16:17
 * email : 1099420259@qq.com
 * description :
 */
class TasksAdapter : ListAdapter<Task, TasksViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }
    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.name == newItem.name


            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem == newItem

        }
    }
}