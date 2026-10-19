package com.shumanyu.app

import org.junit.Assert.*
import org.junit.Test

class UpdateCheckScheduleTest {
    @Test fun coldStartAlwaysChecksEvenSoonAfterPreviousProcess() {
        assertTrue(updateCheckDue(1_000L,null,false))
        assertTrue(updateCheckDue(1_000L,null,true))
    }
    @Test fun successfulForegroundChecksAreBoundedToFiveMinutes() {
        assertFalse(updateCheckDue(60_000L,1_000L,false))
        assertFalse(updateCheckDue(300_999L,1_000L,false))
        assertTrue(updateCheckDue(301_000L,1_000L,false))
    }
    @Test fun failedRequestRetriesSoonerThanSuccessfulCheck() {
        assertFalse(updateCheckDue(60_999L,1_000L,true))
        assertTrue(updateCheckDue(61_000L,1_000L,true))
        assertFalse(updateCheckDue(61_000L,1_000L,false))
    }
    @Test fun clockResetCannotDisableChecks() {
        assertTrue(updateCheckDue(1_000L,9_000L,false))
    }
}
