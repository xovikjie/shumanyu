package com.shumanyu.app

import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SharedImageRequestsTest {
    private class QueueExecutor : Executor {
        val jobs = ArrayDeque<Runnable>()
        override fun execute(command: Runnable) { jobs.addLast(command) }
        fun finish() { jobs.removeFirst().run() }
    }

    @Test fun immediateCompletionDoesNotLeaveAStaleRequest() {
        var loads = 0
        val requests = SharedImageRequests(Executor { it.run() }) { _: String -> ++loads }
        assertEquals(1, requests.request("cover").get())
        assertEquals(0, requests.size)
        assertEquals(2, requests.request("cover").get())
    }

    @Test fun duplicateCardsShareOneDownload() {
        val executor = QueueExecutor()
        val requests = SharedImageRequests(executor) { _: String -> "bitmap" }
        val first = requests.request("cover")
        val second = requests.request("cover")
        assertSame(first, second)
        assertEquals(1, executor.jobs.size)
        executor.finish()
        assertEquals("bitmap", second.get())
        assertEquals(0, requests.size)
    }

    @Test fun cancelledCardDoesNotCancelAnotherCardsResult() = runBlocking {
        val executor = QueueExecutor()
        val requests = SharedImageRequests(executor) { _: String -> "bitmap" }
        val first = async(start = CoroutineStart.UNDISPATCHED) { requests.await("cover") }
        val second = async(start = CoroutineStart.UNDISPATCHED) { requests.await("cover") }
        first.cancelAndJoin()
        executor.finish()
        assertEquals("bitmap", second.await())
        assertTrue(executor.jobs.isEmpty())
        assertEquals(0, requests.size)
    }

    @Test fun failedDownloadCanBeRetried() {
        var loads = 0
        val requests = SharedImageRequests(Executor { it.run() }) { _: String ->
            if (++loads == 1) error("temporary failure") else "bitmap"
        }
        assertTrue(requests.request("cover").isCompletedExceptionally)
        assertEquals(0, requests.size)
        assertEquals("bitmap", requests.request("cover").get())
    }
}
