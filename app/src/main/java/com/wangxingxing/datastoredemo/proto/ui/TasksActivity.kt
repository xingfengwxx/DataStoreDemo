package com.wangxingxing.datastoredemo.proto.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import com.wangxingxing.datastoredemo.databinding.ActivityTasksBinding
import com.wangxingxing.datastoredemo.proto.UserPreferences
import com.wangxingxing.datastoredemo.proto.data.TasksRepository
import com.wangxingxing.datastoredemo.proto.data.UserPreferencesRepository
import com.wangxingxing.datastoredemo.proto.data.UserPreferencesSerializer

const val TAG = "wxx"
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

private val Context.dataStore by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)

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

    private fun updateTaskFilters(sortOrder: UserPreferences.SortOrder, showCompleted: Boolean) {
        with(mBinding) {
            showCompletedSwitch.isChecked = showCompleted
            sortDeadline.isChecked =
                sortOrder == UserPreferences.SortOrder.BY_DEADLINE || sortOrder == UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY
            sortPriority.isChecked =
                sortOrder == UserPreferences.SortOrder.BY_PRIORITY || sortOrder == UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY
        }
    }
}

