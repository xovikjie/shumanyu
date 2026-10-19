package com.shumanyu.app

// A new controller always checks on cold start; old persisted 12-hour timestamps
// must not suppress it. Subsequent foreground checks use a monotonic clock.
internal fun updateCheckDue(now:Long,lastAttempt:Long?,failed:Boolean):Boolean {
    if(lastAttempt==null || now<lastAttempt)return true
    return now-lastAttempt >= if(failed)60_000L else 5*60_000L
}
