package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto.TeamsBotNotificationSettings;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;

import static java.util.Objects.requireNonNull;

import static net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY;
import static net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY;

/**
 * Business validator for Webex Teams recipient settings {@link TeamsBotNotificationSettings}
 */
@Component
public class TeamsBotNotificationRecipientValidator {

    private static final int INPUT_MAX_LENGTH = 256;

    private final TeamsRoomService teamsRoomService;
    private final I18nResolver i18nResolver;

    /**
     * Constructs new instance of {@link TeamsBotNotificationRecipientValidator}
     */
    @Autowired
    public TeamsBotNotificationRecipientValidator(@NotNull final TeamsRoomService teamsRoomService,
                                                  @NotNull final I18nResolver i18nResolver) {
        this.teamsRoomService = requireNonNull(teamsRoomService, "'teamsRoomService' cannot be null");
        this.i18nResolver = requireNonNull(i18nResolver, "'i18nResolver' cannot be null");
    }

    /**
     * Validates given temas bot notification settings.
     *
     * @param teamsBotNotificationSettings
     *         settings to validate
     * @return collections of errors (blank one of there are no errors)
     */
    @NotNull
    public ErrorCollection validate(@NotNull final TeamsBotNotificationSettings teamsBotNotificationSettings) {
        requireNonNull(teamsBotNotificationSettings, "'teamsBotNotificationSettings' cannot be null");

        final ErrorCollection errorCollection = new SimpleErrorCollection();
        final String botAccessToken = teamsBotNotificationSettings.getBotAccessToken();
        final String roomIdentifier = teamsBotNotificationSettings.getRoomIdentifier();
        validateRequiredInput(i18nResolver.getText(ErrorMessageKeys.BOT_ACCESS_TOKEN_LABEL), BOT_ACCESS_TOKEN_KEY, botAccessToken, errorCollection);
        validateRequiredInput(i18nResolver.getText(ErrorMessageKeys.ROOM_IDENTIFIER_LABEL), ROOM_IDENTIFIER_KEY, roomIdentifier, errorCollection);
        if (!errorCollection.hasAnyErrors()) {
            validateRoomByIdentifierExists(botAccessToken, roomIdentifier, errorCollection);
        }
        return errorCollection;
    }

    private void validateRequiredInput(final String messagePrefix, final String inputValueKey, final String inputValue, final ErrorCollection
            errorCollection) {
        if (StringUtils.isBlank(inputValue)) {
            errorCollection.addError(inputValueKey, i18nResolver.getText(ErrorMessageKeys.NOT_BLANK, messagePrefix));
        } else if (inputValue.length() > INPUT_MAX_LENGTH) {
            errorCollection.addError(inputValueKey, i18nResolver.getText(ErrorMessageKeys.TOO_LONG, messagePrefix));
        }
    }

    private void validateRoomByIdentifierExists(final String botAccessToken, final String roomIdentifier, final ErrorCollection errorCollection) {
        final Optional<Room> roomByIdentifierOrName = teamsRoomService.findByIdentifierOrName(botAccessToken, roomIdentifier);
        if (!roomByIdentifierOrName.isPresent()) {
            errorCollection.addError(ROOM_IDENTIFIER_KEY, i18nResolver.getText(ErrorMessageKeys.ROOM_NOT_FOUND));
        }
    }

    private final class ErrorMessageKeys {
        private static final String ROOM_NOT_FOUND = "nofification.recipient.webexTeamsNotifications.roomIdentifier.error.notFound";
        private static final String NOT_BLANK = "nofification.recipient.webexTeamsNotifications.error.notBlank";
        private static final String TOO_LONG = "nofification.recipient.webexTeamsNotifications.error.tooLong";
        private static final String BOT_ACCESS_TOKEN_LABEL = "nofification.recipient.webexTeamsNotifications.accessToken.label";
        private static final String ROOM_IDENTIFIER_LABEL = "nofification.recipient.webexTeamsNotifications.roomIdentifier.label";
    }
}
