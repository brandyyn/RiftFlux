package com.voidsrift.riftflux.client.chatbubbles;

public final class ChatBubbleMessage {
    private final int updateCounterCreated;
    private final String author;
    private final String[] messageLines;

    public ChatBubbleMessage(String author, String[] messageLines, int updateCounterCreated) {
        this.author = author;
        this.messageLines = messageLines;
        this.updateCounterCreated = updateCounterCreated;
    }

    public String getAuthor() {
        return author;
    }

    public String[] getMessageLines() {
        return messageLines;
    }

    public int getUpdateCounterCreated() {
        return updateCounterCreated;
    }
}
