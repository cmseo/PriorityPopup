package com.example.prioritybasedpopup.ui.theme.aitsuki

import android.app.Dialog
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.PriorityQueue

class DialogQueue(
    lifecycleOwner: LifecycleOwner,
    private val fm: FragmentManager
) {

    private val queue = Channel<Unit>(Channel.UNLIMITED)
    private val next = Channel<Unit>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val pq = PriorityQueue<Task>(11, Comparator { o1, o2 ->
        return@Comparator o2.priority - o1.priority
    })
    init {
        var activeDialog: Any? = null

        lifecycleOwner.lifecycleScope.launch {
            queue.consumeEach {
                val task = pq.peek()?.also {
                    Log.d("DialogQueue", "peek: $it")
                } ?: return@consumeEach
                val nextFunc = {
                    activeDialog = null
                    pq.remove(task)
                    queue.trySend(Unit)
                    next.trySend(Unit) // 작업 목록 재개
                }
                try {
                    activeDialog = task.dialogBuilder(nextFunc).also {
                        when (it) {
                            is Dialog -> it.show()
                            is BottomSheetDialogFragment -> it.show(fm, "")
                        }
                    }
                    next.receive() // 작업 목록 대기
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                Log.d("DialogQueue", "onDestroy called")
                activeDialog?.let {
                    when (it) {
                        is Dialog -> it.dismiss()
                        is BottomSheetDialogFragment -> it.dismiss()
                    }
                }
                activeDialog = null
            }
        })
    }

    fun offer(tag: String, priority: Int, dialogBuilder: (next: () -> Unit) -> Any) {
        val task = Task(tag, priority, dialogBuilder)
        if (pq.contains(task)) {
            return
        }

        // 우선순위 큐에 집어 넣는다
        pq.offer(task)

        // 작업 목록에 넣는다.
        queue.trySend(Unit)
    }

    class Task(
        val tag: String,
        val priority: Int,
        val dialogBuilder: (next: () -> Unit) -> Any
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Task
            if (tag != other.tag) return false
            return true
        }

        override fun hashCode(): Int {
            return tag.hashCode()
        }

        override fun toString(): String {
            return "Task(tag='$tag', priority=$priority, dialogBuilder=$dialogBuilder)"
        }
    }
}