package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Entity holding Webex Teams Rooms response
 */
public class Rooms {

    private List<Room> items;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Rooms rooms = (Rooms) o;
        return Objects.equals(items, rooms.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Rooms{", "}")
                .add("items=" + items)
                .toString();
    }

    public List<Room> getItems() {
        return items;
    }

    public void setItems(final List<Room> items) {
        this.items = items;
    }
}
