package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * DTO transferring notification receiver data from form to transport layer
 */
public class TeamsBotNotificationSettings {

    private String botAccessToken;
    private String roomIdentifier;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TeamsBotNotificationSettings that = (TeamsBotNotificationSettings) o;
        return Objects.equals(botAccessToken, that.botAccessToken) &&
                Objects.equals(roomIdentifier, that.roomIdentifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(botAccessToken, roomIdentifier);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "TeamsBotNotificationSettings{", "}")
                .add("botAccessToken='" + botAccessToken + "'")
                .add("roomIdentifier='" + roomIdentifier + "'")
                .toString();
    }

    public String getBotAccessToken() {
        return botAccessToken;
    }

    public void setBotAccessToken(final String botAccessToken) {
        this.botAccessToken = botAccessToken;
    }

    public String getRoomIdentifier() {
        return roomIdentifier;
    }

    public void setRoomIdentifier(final String roomIdentifier) {
        this.roomIdentifier = roomIdentifier;
    }
}
