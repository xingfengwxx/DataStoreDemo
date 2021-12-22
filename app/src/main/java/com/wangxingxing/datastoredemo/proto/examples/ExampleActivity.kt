package com.wangxingxing.datastoredemo.proto.examples

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.wangxingxing.datastoredemo.databinding.ActivityExampleBinding
import kotlinx.coroutines.flow.collectLatest

private val Context.dataStore by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)
private const val TAG = "wxx"

class ExampleActivity : AppCompatActivity() {

    private val mBinding by lazy {
        ActivityExampleBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        with(mBinding) {
            btnAdd.setOnClickListener {
                Log.d(TAG, "onCreate: 添加")
                lifecycleScope.launchWhenCreated {
                    dataStore.updateData { preferences ->
                        preferences.toBuilder().setLanguage("English").setCode(404).build()
                    }
                }
            }

            //查询
            lifecycleScope.launchWhenCreated {
                dataStore.data.collectLatest { settings ->
                    Log.d(TAG, "onCreate: ${settings.language},${settings.code}")
                }
            }

            // 修改
            btnUpdate.setOnClickListener {
                Log.d(TAG, "onCreate: 修改")
                lifecycleScope.launchWhenCreated {
                    dataStore.updateData { preferences ->
                        preferences.toBuilder().setLanguage("Chinese").setCode(502).build()
                    }
                }
            }

            // 删除
            btnDel.setOnClickListener {
                Log.d(TAG, "onCreate: 删除")
                lifecycleScope.launchWhenCreated {
                    dataStore.updateData { preferences ->
                        preferences.toBuilder().clearLanguage().clearCode().build()
                    }
                }
            }
        }


    }
}