package com.wangxingxing.datastoredemo.preferences.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wangxingxing.datastoredemo.R
import com.wangxingxing.datastoredemo.databinding.TaskViewItemBinding
import com.wangxingxing.datastoredemo.preferences.data.Task
import com.wangxingxing.datastoredemo.preferences.data.TaskPriority
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : 王星星
 * date : 2021/12/16 16:23
 * email : 1099420259@qq.com
 * description : 任务列表中任务项的持有者
 */
class TasksViewHolder(
    private val mBinding: TaskViewItemBinding
) : RecyclerView.ViewHolder(mBinding.root) {

    // Format date as: Apr 6, 2020
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    /**
     * 将任务绑定到UI元素
     *
     * @param todo
     */
    fun bind(todo: Task) {
        mBinding.task.text = todo.name
        setTaskPriority(todo)
        mBinding.deadline.text = dateFormat.format(todo.deadline)
        // 如果任务已完成，则显示为灰色
        val color = if (todo.completed) {
            R.color.greyAlpha
        } else {
            R.color.white
        }
        itemView.setBackgroundColor(
            ContextCompat.getColor(itemView.context, color)
        )
    }

    private fun setTaskPriority(todo: Task) {
        mBinding.priority.text = itemView.context.resources.getString(
            R.string.priority_value,
            todo.priority.name
        )
        // 根据任务优先级设置优先级颜色
        val textColor = when (todo.priority) {
            TaskPriority.HIGH -> R.color.red
            TaskPriority.MEDIUM -> R.color.yellow
            TaskPriority.LOW -> R.color.green
        }
        mBinding.priority.setTextColor(ContextCompat.getColor(itemView.context, textColor))
    }

    companion object {
        fun create(parent: ViewGroup): TasksViewHolder {
            val view  = LayoutInflater.from(parent.context)
                .inflate(R.layout.task_view_item, parent, false)
            val binding = TaskViewItemBinding.bind(view)
            return TasksViewHolder(binding)
        }
    }
}