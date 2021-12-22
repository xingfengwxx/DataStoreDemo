package com.wangxingxing.datastoredemo.preferences.ui

import androidx.lifecycle.*
import com.wangxingxing.datastoredemo.preferences.data.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * author : 王星星
 * date : 2021/12/16 16:49
 * email : 1099420259@qq.com
 * description :
 */

data class TasksUiModel(
    val tasks: List<Task>,
    val showCompleted: Boolean,
    val sortOrder: SortOrder
)

class TasksViewModel(
    private val repository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    // 后续再打开应用，就要先获取之前保存过的用户首选项信息
    val initialSetupEvent = liveData {
        emit(userPreferencesRepository.fetchInitialPreferences())
    }

    // 每次发出筛选或排序任务列表时，我们都应该重新创建任务列表
    // 任务列表 Flow 和用户首选项 Flow 两个Flow合并，得到一个 Flow
    private val tasksUiModelFlow = combine(
        repository.tasks,
        userPreferencesFlow
    ) { tasks: List<Task>, userPreferences: UserPreferences ->
        return@combine TasksUiModel(
            tasks = filterSortTasks(
                tasks,
                userPreferences.showCompleted,
                userPreferences.sortOrder
            ),
            userPreferences.showCompleted,
            userPreferences.sortOrder
        )
    }

    val tasksUiModel = tasksUiModelFlow.asLiveData()

    /**
     * 对任务进行过滤和排序
     */
    private fun filterSortTasks(
        tasks: List<Task>,
        showCompleted: Boolean,
        sortOrder: SortOrder
    ): List<Task> {
        // 过滤
        val filteredTasks = if (showCompleted) {
            tasks
        } else {
            tasks.filter { !it.completed }
        }
        // 排序
        return when (sortOrder) {
            SortOrder.NONE -> filteredTasks
            SortOrder.BY_DEADLINE -> filteredTasks.sortedByDescending { it.deadline }
            SortOrder.BY_PRIORITY -> filteredTasks.sortedBy { it.priority }
            SortOrder.BY_DEADLINE_AND_PRIORITY -> filteredTasks.sortedWith(
                compareByDescending<Task> { it.deadline }.thenBy { it.priority }
            )
        }
    }

    // 修改用户首选项的值
    fun enableSortByDeadline(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.enableSortByDeadline(checked)
        }
    }

    fun enableSortByPriority(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.enableSortByPriority(checked)
        }
    }

    fun showCompletedTasks(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompleted(checked)
        }
    }
}

class TasksViewModelFactory(
    private val repository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // 类型判断
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(repository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}