package com.example.dailysummary.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

object GlobalClickBlocker {
    private val mutex = Mutex()

    suspend fun runExclusive( onUnlock: (suspend () -> Unit)? = null,block: suspend () -> Unit,) {
        if (!mutex.tryLock()) return
        try {
            block()
        } finally {
            // unlock 시점을 연기할 수도 있도록 콜백 추가
            if (onUnlock != null) {
                onUnlock()
            } else {
                mutex.unlock()
            }
        }
    }

    fun unlock() {
        if (mutex.isLocked) mutex.unlock()
    }
}


suspend fun NavController.popBackStackExclusive(
    animationDelay: Long = 300L
) {
    GlobalClickBlocker.runExclusive {
        this.popBackStack()
        waitUntilBackStackChanged(animationDelay)
        GlobalClickBlocker.unlock()
    }
}

private suspend fun NavController.waitUntilBackStackChanged(
    delayAfterPop: Long
) {
    currentBackStackEntryFlow.collectLatest {
        // pop 후 새로운 destination이 활성화되면 unlock
        delay(delayAfterPop)
        return@collectLatest
    }
}