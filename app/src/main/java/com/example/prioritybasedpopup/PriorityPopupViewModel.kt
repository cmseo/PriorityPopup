package com.example.prioritybasedpopup

import android.app.Dialog
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.PriorityQueue

class PriorityPopupViewModel : ViewModel() {
    private val queue = Channel<Unit>(Channel.UNLIMITED)
    private val next = Channel<Unit>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val pq = PriorityQueue<PriorityBasedPopupTask>(11, Comparator { o1, o2 ->
        return@Comparator o2.priority - o1.priority
    })
    private val _popupState: MutableStateFlow<Any?> = MutableStateFlow(null)
    val popupState: StateFlow<Any?> = _popupState

    init {
        viewModelScope.launch {
            queue.consumeEach {
                val task = pq.peek()?.also {
                    Log.d("PriorityBasedPopupTask", "peek: $it")
                } ?: return@consumeEach
                val nextFunc = fun () {
                    _popupState.value = null
                    pq.remove(task)
                    next.trySend(Unit) // 작업 목록 재개
                }
                try {
                    _popupState.value = task.dialogBuilder(nextFunc)
                    next.receive() // 작업 목록 대기
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("PriorityBasedPopupTask", "onCleared()")
        _popupState.value?.let {
            when (it) {
                is Dialog -> it.dismiss()
                is BottomSheetDialogFragment -> it.dismiss()
            }
        }
        _popupState.value = null
    }

    fun offer(tag: String, priority: Int, dialogBuilder: (next: () -> Unit) -> Any) {
        val task = PriorityBasedPopupTask(tag, priority, dialogBuilder)
        if (pq.contains(task)) {
            return
        }

        // 우선순위 큐에 집어 넣는다
        pq.offer(task)

        // 작업 목록에 넣는다.
        queue.trySend(Unit)
    }
}

data class PriorityBasedPopupTask(
    val tag: String,
    val priority: Int,
    val dialogBuilder: (next: () -> Unit) -> Any
)