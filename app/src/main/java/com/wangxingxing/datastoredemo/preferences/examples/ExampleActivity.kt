package com.wangxingxing.datastoredemo.preferences.examples

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.wangxingxing.datastoredemo.databinding.ActivityExampleBinding
import kotlinx.coroutines.flow.collectLatest

private val Context.dataStore by preferencesDataStore(name = "settings")
private const val TAG = "wxx"

class ExampleActivity : AppCompatActivity() {

    private val mBinding by lazy {
        ActivityExampleBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        val key = stringPreferencesKey("language")
        with(mBinding) {
            btnAdd.setOnClickListener {
                Log.d(TAG, "onCreate: 添加")
                lifecycleScope.launchWhenCreated {
                    dataStore.edit { preferences ->
                        preferences[key] = "English"
                    }
                }
            }

            //查询
            lifecycleScope.launchWhenCreated {
                dataStore.data.collectLatest { preferences ->
                    Log.d(TAG, "onCreate: ${preferences[key]}")
                }
            }

            // 修改
            btnUpdate.setOnClickListener {
                lifecycleScope.launchWhenCreated {
                    dataStore.edit { preferences ->
                        preferences[key] = "Chinese"
                    }
                }
            }

            // 删除
            btnDel.setOnClickListener {
                Log.d(TAG, "onCreate: 删除")
                lifecycleScope.launchWhenCreated {
                    dataStore.edit { preferences ->
                        preferences.remove(key)
                    }
                }
            }
        }


    }
}