package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Entity holding Webex Teams Room response
 */
public class Room {
    private String id;
    private String title;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Room room = (Room) o;
        return Objects.equals(id, room.id) &&
                Objects.equals(title, room.title);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Room{", "}")
                .add("id='" + id + "'")
                .add("title='" + title + "'")
                .toString();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
