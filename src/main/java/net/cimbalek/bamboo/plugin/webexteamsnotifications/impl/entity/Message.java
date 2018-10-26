package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Entity holding Webex Teams Message post request
 */
public class Message {
    private String roomId;
    private String text;
    private String markdown;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Message message = (Message) o;
        return Objects.equals(roomId, message.roomId) &&
                Objects.equals(text, message.text) &&
                Objects.equals(markdown, message.markdown);
    }

    @Override
    public int hashCode() {

        return Objects.hash(roomId, text, markdown);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Message{", "}")
                .add("roomId='" + roomId + "'")
                .add("text='" + text + "'")
                .add("markdown='" + markdown + "'")
                .toString();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(final String markdown) {
        this.markdown = markdown;
    }
}
