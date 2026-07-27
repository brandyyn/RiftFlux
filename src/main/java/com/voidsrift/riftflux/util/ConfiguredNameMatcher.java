package com.voidsrift.riftflux.util;

public final class ConfiguredNameMatcher {
    private ConfiguredNameMatcher() {
    }

    public static boolean matches(String candidate, String[] configuredNames) {
        if (candidate == null || configuredNames == null) {
            return false;
        }

        String trimmedCandidate = candidate.trim();
        if (trimmedCandidate.length() == 0) {
            return false;
        }

        for (String configuredName : configuredNames) {
            if (configuredName != null && trimmedCandidate.equalsIgnoreCase(configuredName.trim())) {
                return true;
            }
        }
        return false;
    }
}
