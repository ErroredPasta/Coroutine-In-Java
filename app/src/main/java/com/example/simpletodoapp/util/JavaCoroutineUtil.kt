package com.example.simpletodoapp.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object JavaFlowHelper {
    @JvmStatic
    fun <T> collectInJava(
        scope: CoroutineScope,
        flow: Flow<T>,
        action: (T) -> Unit,
    ) {
        scope.launch {
            flow.collect {
                action(it)
            }
        }
    }

    @JvmStatic
    fun <T> collectWithLifecycleWithJava(
        scope: CoroutineScope,
        lifecycle: Lifecycle,
        flow: Flow<T>,
        action: (T) -> Unit,
    ) {
        scope.launch {
            flow.flowWithLifecycle(lifecycle).collect {
                action(it)
            }
        }
    }

    @JvmStatic
    fun getLifecycleScopeFromOwner(lifecycleOwner: LifecycleOwner): LifecycleCoroutineScope =
        lifecycleOwner.lifecycleScope
}