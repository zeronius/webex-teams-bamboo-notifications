package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Rooms;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.teamsclient.TeamsRestHttpClient;

import static java.util.Objects.requireNonNull;

/**
 * Service taking care of loading Webex Teams Rooms
 */
@Service
public class TeamsRoomService {

    private static final Logger logger = LoggerFactory.getLogger(TeamsRoomService.class);

    private static final String URL_ROOMS = "rooms";
    private static final String URL_ROOM_PATTERN = URL_ROOMS + "/%s";

    private final TeamsRestHttpClient teamsRestHttpClient;

    /**
     * Constructs new instance of the service {@link TeamsRoomService}
     */
    @Autowired
    public TeamsRoomService(@NotNull final TeamsRestHttpClient teamsRestHttpClient) {
        this.teamsRestHttpClient = requireNonNull(teamsRestHttpClient, "'teamsRestHttpClient' cannot be null");
    }

    /**
     * Finds Room with bot access token by room identifier or title (name)
     *
     * @param botAccessToken
     *         access token for Webex Teams API
     * @param roomIdentifierOrName
     *         room identifier or title (name)
     * @return {@link Optional} with found {@link Room} or empty {@link Optional} if room was not found
     */
    @NotNull
    public Optional<Room> findByIdentifierOrName(@NotNull final String botAccessToken, @NotNull final String roomIdentifierOrName) {
        requireNonNull(botAccessToken, "'botAccessToken' cannot be null");
        requireNonNull(roomIdentifierOrName, "'roomIdentifier' cannot be null");

        logger.debug("Finding room by indentifier or name '{}' for bot access token '{}'.", roomIdentifierOrName, botAccessToken);
        Optional<Room> result = findByIdentifier(botAccessToken, roomIdentifierOrName);
        if (!result.isPresent()) {
            result = findByName(botAccessToken, roomIdentifierOrName);
        }
        logEmptyResult(botAccessToken, roomIdentifierOrName, result);
        return result;
    }

    private Optional<Room> findByIdentifier(final String botAccessToken, final String roomIdentifier) {
        Optional<Room> result;
        try {
            final ResponseEntity<Room> roomResponseEntity = teamsRestHttpClient.sendGetRequest(
                    String.format(URL_ROOM_PATTERN, roomIdentifier),
                    botAccessToken,
                    Room.class);
            result = Optional.of(roomResponseEntity.getBody());
        } catch (final RestClientException e) {
            result = Optional.empty();
            logger.debug("Cannot find room by identifier", e);
        }
        return result;
    }

    private Optional<Room> findByName(final String botAccessToken, final String roomName) {
        Optional<Room> result;
        try {
            final ResponseEntity<Rooms> roomsResponseEntity = teamsRestHttpClient.sendGetRequest(
                    URL_ROOMS,
                    botAccessToken,
                    Rooms.class);
            result = findOneRoomByName(roomName, roomsResponseEntity.getBody());
        } catch (final RestClientException e) {
            result = Optional.empty();
            logger.debug("Cannot find room by name", e);
        }
        return result;
    }

    private Optional<Room> findOneRoomByName(final String roomName, final Rooms rooms) {
        return rooms.getItems().stream()
                .filter(room -> room.getTitle().equalsIgnoreCase(roomName))
                .findAny();
    }

    private void logEmptyResult(final String botAccessToken, final String roomIdentifierOrName, final Optional<Room> result) {
        if (!result.isPresent()) {
            logger.warn("Room by identifier or name '{}' for bot access token '{}' was not found.", roomIdentifierOrName, botAccessToken);
        }
    }
}
