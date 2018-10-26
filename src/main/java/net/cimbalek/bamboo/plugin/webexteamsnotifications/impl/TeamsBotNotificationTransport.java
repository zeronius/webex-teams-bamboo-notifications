package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto.TeamsBotNotificationSettings;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Message;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsMessageService;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;

import static java.util.Objects.requireNonNull;

/**
 * Transport class taking care of delivering notification to the Teams API
 */
public class TeamsBotNotificationTransport implements NotificationTransport {
    private static final Logger logger = LoggerFactory.getLogger(TeamsBotNotificationTransport.class);

    private final TeamsBotNotificationSettings teamsBotNotificationSettings;
    private final TeamsRoomService teamsRoomService;
    private final TeamsMessageService teamsMessageService;

    /**
     * Constructs new instance of {@link TeamsBotNotificationTransport}
     *
     * @param teamsBotNotificationSettings
     *         settings of notification recipient
     * @param teamsRoomService
     *         service taking care of Webex Teams Rooms
     * @param teamsMessageService
     *         Service taking care of delivering messages to Webex Teams API
     */
    public TeamsBotNotificationTransport(@NotNull final TeamsBotNotificationSettings teamsBotNotificationSettings,
                                         @NotNull final TeamsRoomService teamsRoomService,
                                         @NotNull final TeamsMessageService teamsMessageService) {
        this.teamsBotNotificationSettings = requireNonNull(teamsBotNotificationSettings, "'teamsBotNotificationSettings' cannot be null");
        this.teamsRoomService = requireNonNull(teamsRoomService, "'teamsRoomService' cannot be null");
        this.teamsMessageService = requireNonNull(teamsMessageService, "'teamsMessageService' cannot be null");
    }

    @Override
    public void sendNotification(@NotNull final Notification notification) {
        requireNonNull(notification, "'notification' cannot be null");
        logger.debug("Sending notification '{}' for recipient '{}'.", notification, teamsBotNotificationSettings);
        logNotificationDetail(notification);
        sendNotificationForRoom(notification, teamsBotNotificationSettings.getBotAccessToken(), teamsBotNotificationSettings.getRoomIdentifier());
    }

    private void sendNotificationForRoom(@NotNull final Notification notification, final String botAccessToken, final String roomIdentifier) {
        final Optional<Room> destinationRoom = teamsRoomService.findByIdentifierOrName(botAccessToken, roomIdentifier);
        if (destinationRoom.isPresent()) {
            sendNotificationMessage(notification, botAccessToken, destinationRoom.get().getId());
        } else {
            logger.error("Cannot send notification to room '{}' because this probably doesn't exist.", roomIdentifier);
        }
    }

    private void sendNotificationMessage(final Notification notification, final String botAccessToken, final String roomIdentifier) {
        final Message message = buildMessage(notification, roomIdentifier);
        logger.debug("Sending notification message request '{}' for bot '{}'.", message, botAccessToken);

        final boolean sent = teamsMessageService.sendMessage(botAccessToken, message);
        if (sent) {
            logger.info("Notification message '{}' was successfully sent.", message);
        } else {
            logger.error("Sending of notification message '{}' was not successful", message);
        }
    }

    private Message buildMessage(final Notification notification, final String roomIdentifier) {
        final Message message = new Message();
        message.setRoomId(roomIdentifier);
        message.setMarkdown(notification.getIMContent());
        return message;
    }

    private void logNotificationDetail(final Notification notification) {
        try {
            logger.debug("Notification description '{}', email subject '{}', htmlemailcontent '{}', imcontent '{}', textemailcontent '{}', "
                            + "notificationrecipients '{}', excludedrecipients '{}'.",
                    notification.getDescription(),
                    notification.getEmailSubject(),
                    notification.getHtmlEmailContent(),
                    notification.getIMContent(),
                    notification.getTextEmailContent(),
                    notification.getNotificationRecipients(),
                    notification.getExcludedNotificationRecipients()
            );
        } catch (final Exception e) {
            logger.debug("It's not possible to log notification details.", e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TeamsBotNotificationTransport that = (TeamsBotNotificationTransport) o;
        return Objects.equals(teamsBotNotificationSettings, that.teamsBotNotificationSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamsBotNotificationSettings);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "DefaultTeamsBotNotificationTransport{", "}")
                .add("teamsBotNotificationSettings=" + teamsBotNotificationSettings)
                .toString();
    }

}
