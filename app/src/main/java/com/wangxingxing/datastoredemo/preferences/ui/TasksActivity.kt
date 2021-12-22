package com.wangxingxing.datastoredemo.preferences.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wangxingxing.datastoredemo.databinding.ActivityTasksBinding
import com.wangxingxing.datastoredemo.preferences.data.SortOrder
import com.wangxingxing.datastoredemo.preferences.data.TasksRepository
import com.wangxingxing.datastoredemo.preferences.data.UserPreferencesRepository

const val TAG = "wxx"

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class TasksActivity : AppCompatActivity() {

    private val mBinding by lazy {
        ActivityTasksBinding.inflate(layoutInflater)
    }

    private val mViewModel by lazy {
        ViewModelProvider(
            this,
            TasksViewModelFactory(TasksRepository, UserPreferencesRepository(dataStore))
        ).get(TasksViewModel::class.java)
    }

    private val adapter by lazy { TasksAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.list.adapter = adapter

        // 获取用户首选项的历史信息
        mViewModel.initialSetupEvent.observe(this) { initialSetupEvent ->
            // 更新选项（UI）
            updateTaskFilters(initialSetupEvent.sortOrder, initialSetupEvent.showCompleted)
            observePreferenceChanges()
        }

        // 事件处理
        with(mBinding) {
            sortDeadline.setOnCheckedChangeListener { _, checked ->
                mViewModel.enableSortByDeadline(checked)
            }
            sortPriority.setOnCheckedChangeListener { _, checked ->
                mViewModel.enableSortByPriority(checked)
            }
            showCompletedSwitch.setOnCheckedChangeListener { _, checked ->
                mViewModel.showCompletedTasks(checked)
            }
        }
    }

    /**
     * 当 Flow 中有任务列表时，提交给适配器
     *
     */
    private fun observePreferenceChanges() {
        mViewModel.tasksUiModel.observe(this) {
            adapter.submitList(it.tasks)
        }
    }

    private fun updateTaskFilters(sortOrder: SortOrder, showCompleted: Boolean) {
        with(mBinding) {
            showCompletedSwitch.isChecked = showCompleted
            sortDeadline.isChecked =
                sortOrder == SortOrder.BY_DEADLINE || sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY
            sortPriority.isChecked =
                sortOrder == SortOrder.BY_PRIORITY || sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY
        }
    }
}

