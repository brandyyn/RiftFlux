package com.voidsrift.riftflux.world;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class MoonPhaseHelper {
    public static final int PHASE_COUNT = 8;
    public static final int WANING_CRESCENT = 3;

    private static final String[] PHASE_NAMES = new String[]{
            "full_moon",
            "waning_gibbous",
            "last_quarter",
            "waning_crescent",
            "new_moon",
            "waxing_crescent",
            "first_quarter",
            "waxing_gibbous"
    };

    private MoonPhaseHelper() {
    }

    public static int parsePhase(String value, int fallback) {
        if (value == null) {
            return normalizePhase(fallback);
        }

        String trimmed = value.trim();
        try {
            int numeric = Integer.parseInt(trimmed);
            return numeric >= 0 && numeric < PHASE_COUNT ? numeric : normalizePhase(fallback);
        } catch (NumberFormatException ignored) {
        }

        String token = trimmed.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (token.isEmpty()) {
            return normalizePhase(fallback);
        }

        if ("full".equals(token) || "fullmoon".equals(token)) {
            return 0;
        }
        if ("waninggibbous".equals(token)) {
            return 1;
        }
        if ("lastquarter".equals(token) || "thirdquarter".equals(token)) {
            return 2;
        }
        if ("waningcrescent".equals(token)) {
            return 3;
        }
        if ("new".equals(token) || "newmoon".equals(token)) {
            return 4;
        }
        if ("waxingcrescent".equals(token)) {
            return 5;
        }
        if ("firstquarter".equals(token)) {
            return 6;
        }
        if ("waxinggibbous".equals(token)) {
            return 7;
        }
        return normalizePhase(fallback);
    }

    public static String canonicalName(int phase) {
        return PHASE_NAMES[normalizePhase(phase)];
    }

    public static String canonicalName(String value, int fallback) {
        return canonicalName(parsePhase(value, fallback));
    }

    public static String[] sanitizePhaseList(String[] values) {
        if (values == null || values.length == 0) {
            return new String[0];
        }

        Set<String> phases = new LinkedHashSet<String>();
        for (String value : values) {
            if (value == null || value.trim().isEmpty() || !isKnownPhase(value)) {
                continue;
            }
            phases.add(canonicalName(parsePhase(value, 0)));
        }
        return phases.toArray(new String[phases.size()]);
    }

    public static boolean[] parsePhaseFlags(String[] values) {
        boolean[] flags = new boolean[PHASE_COUNT];
        if (values == null) {
            return flags;
        }

        for (String value : values) {
            if (value != null && isKnownPhase(value)) {
                flags[parsePhase(value, 0)] = true;
            }
        }
        return flags;
    }

    public static int offsetPhase(int vanillaPhase, int startingPhase) {
        return normalizePhase(vanillaPhase + normalizePhase(startingPhase));
    }

    public static int normalizePhase(int phase) {
        int normalized = phase % PHASE_COUNT;
        return normalized < 0 ? normalized + PHASE_COUNT : normalized;
    }

    private static boolean isKnownPhase(String value) {
        if (value == null) {
            return false;
        }

        String trimmed = value.trim();
        try {
            int numeric = Integer.parseInt(trimmed);
            return numeric >= 0 && numeric < PHASE_COUNT;
        } catch (NumberFormatException ignored) {
        }

        String token = trimmed.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (token.isEmpty()) {
            return false;
        }
        return "full".equals(token)
                || "fullmoon".equals(token)
                || "waninggibbous".equals(token)
                || "lastquarter".equals(token)
                || "thirdquarter".equals(token)
                || "waningcrescent".equals(token)
                || "new".equals(token)
                || "newmoon".equals(token)
                || "waxingcrescent".equals(token)
                || "firstquarter".equals(token)
                || "waxinggibbous".equals(token);
    }
}
