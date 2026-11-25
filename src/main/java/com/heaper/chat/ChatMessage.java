package com.heaper.chat;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ChatMessage {
    private final ServerPlayer sender;
    private String text;
    private List<ServerPlayer> recipients;
    private boolean modified = false;

    public ChatMessage(ServerPlayer sender, String text, List<ServerPlayer> recipients) {
        this.sender = sender;
        this.text = text;
        this.recipients = recipients;
    }

    public ServerPlayer sender() { return sender; }
    public String text() { return text; }
    public List<ServerPlayer> recipients() { return recipients; }
    public boolean modified() { return modified; }

    public void setText(String text) {
        if (!text.equals(this.text)) {
            this.text = text;
            modified = true;
        }
    }

    public void setRecipients(List<ServerPlayer> recipients) {
        this.recipients = recipients;
        modified = true;
    }
}
