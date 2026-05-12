package com.voidsrift.riftflux.client.chatbubbles;

import java.util.regex.Pattern;

public final class ChatParseLine {
    private final String regex;
    private final Pattern pattern;
    private final int[] nameRefs;
    private final int textRef;

    public ChatParseLine(String regex) {
        this(regex, 1, 2);
    }

    public ChatParseLine(String regex, int nameRef, int textRef) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        this.nameRefs = new int[]{nameRef};
        this.textRef = textRef;
    }

    public ChatParseLine(String regex, String nameRefs) {
        this(regex, nameRefs, parseLastRef(nameRefs) + 1);
    }

    public ChatParseLine(String regex, String nameRefs, int textRef) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        String[] split = nameRefs.split(",");
        this.nameRefs = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            this.nameRefs[i] = Integer.parseInt(split[i]);
        }
        this.textRef = textRef;
    }

    public String getRegex() {
        return regex;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int[] getNameRefs() {
        return nameRefs;
    }

    public int getTextRef() {
        return textRef;
    }

    private static int parseLastRef(String nameRefs) {
        String[] split = nameRefs.split(",");
        return Integer.parseInt(split[split.length - 1]);
    }
}
