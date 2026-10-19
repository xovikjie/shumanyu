package com.shumanyu.app;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/** Network configuration adds candidates; it must never remove working bootstrap APIs. */
final class CopyApiRoutes {
    private final List<String> bootstrap;
    private List<String> discovered = new ArrayList<>();
    private String preferred;
    private long discoveryAt = -1;

    CopyApiRoutes(List<String> bootstrap) {
        this.bootstrap = normalized(bootstrap);
    }

    synchronized List<String> candidates() {
        LinkedHashSet<String> hosts = new LinkedHashSet<>();
        if (preferred != null) hosts.add(preferred);
        hosts.addAll(bootstrap);
        hosts.addAll(discovered);
        return new ArrayList<>(hosts);
    }

    synchronized void succeeded(String host) {
        preferred = normalize(host);
    }

    synchronized void discovered(List<String> hosts) {
        discovered = normalized(hosts);
    }

    synchronized boolean beginDiscovery(long now) {
        if (discoveryAt >= 0 && now >= discoveryAt && now - discoveryAt < 60_000L) return false;
        discoveryAt = now;
        return true;
    }

    private static List<String> normalized(List<String> values) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String value : values) {
            String host = normalize(value);
            if (host != null) result.add(host);
        }
        return new ArrayList<>(result);
    }

    private static String normalize(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String raw = value.trim();
        try {
            URI uri = URI.create(raw.contains("://") ? raw : "https://" + raw);
            String path = uri.getPath();
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null
                    || (uri.getPort() != -1 && uri.getPort() != 443)
                    || (path != null && !path.isEmpty() && !path.equals("/"))) return null;
            String host = uri.getHost();
            return host == null ? null : host.toLowerCase(Locale.ROOT);
        } catch (IllegalArgumentException error) {
            return null;
        }
    }
}
