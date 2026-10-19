package com.shumanyu.app

// A new controller always checks on cold start; old persisted 12-hour timestamps
// must not suppress it. Subsequent foreground checks use a monotonic clock.
internal fun updateCheckDue(now:Long,lastAttempt:Long?,failed:Boolean,revisionChanged:Boolean=false):Boolean {
    if(lastAttempt==null || now<lastAttempt)return true
    if(failed)return now-lastAttempt>=60_000L
    return revisionChanged || now-lastAttempt>=5*60_000L
}

internal fun updateProbeDue(now:Long,lastProbe:Long?):Boolean =
    lastProbe==null || now<lastProbe || now-lastProbe>=15_000L
