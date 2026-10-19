package com.shumanyu.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

public class CopyApiRoutesTest {
    @Test public void unexpectedDiscoveryCannotReplaceWorkingBootstrap() {
        CopyApiRoutes routes = new CopyApiRoutes(Arrays.asList("api.one.test", "api.two.test"));
        routes.discovered(Collections.singletonList("unrelated.test"));
        assertEquals(Arrays.asList("api.one.test", "api.two.test", "unrelated.test"), routes.candidates());
    }

    @Test public void aSuccessfulFallbackIsTriedFirstNextTime() {
        CopyApiRoutes routes = new CopyApiRoutes(Arrays.asList("api.one.test", "api.two.test"));
        routes.succeeded("api.two.test");
        assertEquals(Arrays.asList("api.two.test", "api.one.test"), routes.candidates());
    }

    @Test public void discoveryAcceptsHttpsHostnamesAndRejectsMalformedOrigins() {
        CopyApiRoutes routes = new CopyApiRoutes(Collections.singletonList("api.one.test"));
        routes.discovered(Arrays.asList("https://API.TWO.TEST/", "api.two.test", "http://wrong.test",
                "https://user@wrong.test", "https://wrong.test/path", "https://wrong.test?q=1"));
        assertEquals(Arrays.asList("api.one.test", "api.two.test"), routes.candidates());
    }

    @Test public void failedDiscoveryIsThrottledButCanRecoverLater() {
        CopyApiRoutes routes = new CopyApiRoutes(Collections.singletonList("api.one.test"));
        assertTrue(routes.beginDiscovery(0));
        assertFalse(routes.beginDiscovery(0));
        assertFalse(routes.beginDiscovery(59_999));
        assertTrue(routes.beginDiscovery(60_000));
    }

    @Test public void concurrentFailuresStartOnlyOneDiscovery() throws Exception {
        CopyApiRoutes routes = new CopyApiRoutes(Collections.singletonList("api.one.test"));
        var pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1), finished = new CountDownLatch(8);
        AtomicInteger discoveries = new AtomicInteger();
        try {
            for (int i = 0; i < 8; i++) pool.execute(() -> {
                try {
                    start.await();
                    if (routes.beginDiscovery(100)) discoveries.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally { finished.countDown(); }
            });
            start.countDown();
            assertTrue(finished.await(5, TimeUnit.SECONDS));
            assertEquals(1, discoveries.get());
        } finally { pool.shutdownNow(); }
    }
}
