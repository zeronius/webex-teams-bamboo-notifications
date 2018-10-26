package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Message;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.teamsclient.TeamsRestHttpClient;

import static java.util.Objects.requireNonNull;

/**
 * Service taking care of Teams Webex Messages
 */
@Service
public class TeamsMessageService {

    private static final Logger logger = LoggerFactory.getLogger(TeamsRoomService.class);

    private static final String URL_MESSAGES = "messages";

    private final TeamsRestHttpClient teamsRestHttpClient;

    /**
     * Constructs new instance of the service {@link TeamsMessageService}
     */
    @Autowired
    public TeamsMessageService(@NotNull final TeamsRestHttpClient teamsRestHttpClient) {
        this.teamsRestHttpClient = requireNonNull(teamsRestHttpClient, "'teamsRestHttpClient' cannot be null");
    }

    /**
     * Send given message to the Webex Teams API
     *
     * @param botAccessToken
     *         access token for Webex Teams API
     * @param message
     *         message to send
     * @return true if message was successfully sent
     */
    public boolean sendMessage(@NotNull final String botAccessToken, @NotNull final Message message) {
        requireNonNull(botAccessToken, "'botAccessToken' cannot be null");
        requireNonNull(message, "'message' cannot be null");

        boolean result;
        try {
            teamsRestHttpClient.sendPostRequest(URL_MESSAGES, botAccessToken, String.class, message);
            result = true;
        } catch (final RestClientException e) {
            logger.warn("Cannot send message", e);
            result = false;
        }
        return result;
    }
}
