package com.wangxingxing.datastoredemo.proto.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wangxingxing.datastoredemo.R
import com.wangxingxing.datastoredemo.databinding.TaskViewItemBinding
import com.wangxingxing.datastoredemo.proto.data.Task
import com.wangxingxing.datastoredemo.proto.data.TaskPriority
import java.text.SimpleDateFormat
import java.util.*

/**
 * 任务列表中任务项的持有者
 */
class TaskViewHolder(
    private val binding: TaskViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Format date as: Apr 6, 2020
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    /**
     * 将任务绑定到UI元素
     */
    fun bind(todo: Task) {
        binding.task.text = todo.name
        setTaskPriority(todo)
        binding.deadline.text = dateFormat.format(todo.deadline)
        // 如果任务已完成，则显示为灰色
        val color = if (todo.completed) {
            R.color.greyAlpha
        } else {
            R.color.white
        }
        itemView.setBackgroundColor(
            ContextCompat.getColor(
                itemView.context,
                color
            )
        )
    }

    private fun setTaskPriority(todo: Task) {
        binding.priority.text = itemView.context.resources.getString(
            R.string.priority_value,
            todo.priority.name
        )
        // 根据任务优先级设置优先级颜色
        val textColor = when (todo.priority) {
            TaskPriority.HIGH -> R.color.red
            TaskPriority.MEDIUM -> R.color.yellow
            TaskPriority.LOW -> R.color.green
        }
        binding.priority.setTextColor(ContextCompat.getColor(itemView.context, textColor))
    }

    companion object {
        fun create(parent: ViewGroup): TaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.task_view_item, parent, false)
            val binding = TaskViewItemBinding.bind(view)
            return TaskViewHolder(binding)
        }
    }
}
