package com.shumanyu.app

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Coalesces image work without tying a shared request to any one UI collector. */
internal class SharedImageRequests<T>(
    private val executor: Executor,
    private val load: (String) -> T?,
) {
    private val pending = ConcurrentHashMap<String, CompletableFuture<T?>>()
    val size: Int get() = pending.size
    fun contains(url: String): Boolean = pending.containsKey(url)

    fun request(url: String): CompletableFuture<T?> {
        val result = CompletableFuture<T?>()
        pending.putIfAbsent(url, result)?.let { return it }
        // Register before scheduling, including when the executor completes synchronously.
        try {
            executor.execute {
                try {
                    result.complete(load(url))
                } catch (error: Exception) {
                    result.completeExceptionally(error)
                } finally {
                    pending.remove(url, result)
                }
            }
        } catch (error: Exception) {
            pending.remove(url, result)
            result.completeExceptionally(error)
        }
        return result
    }

    suspend fun await(url: String): T? = suspendCancellableCoroutine { continuation ->
        // A leaving card cancels only its wait; another card may need this same download.
        request(url).whenComplete { bitmap, _ -> continuation.resume(bitmap) }
    }
}
