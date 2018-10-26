package net.cimbalek.bamboo.plugin.webexteamsnotifications.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto.TeamsBotNotificationSettings;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsMessageService;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;

import static java.util.Objects.requireNonNull;

/**
 * Recipient plugin
 */
@Named("teamsBotNotificationRecipient")
public class TeamsBotNotificationRecipient extends AbstractNotificationRecipient {

    private static final Logger logger = LoggerFactory.getLogger(TeamsBotNotificationRecipient.class);

    public static final String BOT_ACCESS_TOKEN_KEY = "botAccessToken";
    public static final String ROOM_IDENTIFIER_KEY = "roomIdentifier";
    private static final String PLUGIN_DESCRIPTION_KEY = "nofification.recipient.webexTeamsNotifications.description";
    public static final String ERROR_MESSAGE_CANNOT_OBTAIN_ROOM = "nofification.recipient.webexTeamsNotifications.roomIdentifier.error.cannotFindInApi";

    private final TemplateRenderer templateRenderer;
    private final I18nResolver i18nResolver;
    private final ObjectMapper objectMapper;
    private final TeamsRoomService teamsRoomService;
    private final TeamsBotNotificationRecipientValidator teamsBotNotificationRecipientValidator;
    private final TeamsMessageService teamsMessageService;

    private TeamsBotNotificationSettings teamsBotNotificationSettings;

    @Inject
    public TeamsBotNotificationRecipient(
            @NotNull @ComponentImport final TemplateRenderer templateRenderer,
            @NotNull @ComponentImport final I18nResolver i18nResolver,
            @NotNull final TeamsMessageService teamsMessageService,
            @NotNull final TeamsRoomService teamsRoomService,
            @NotNull final TeamsBotNotificationRecipientValidator teamsBotNotificationRecipientValidator) {
        this.templateRenderer = requireNonNull(templateRenderer, "'templateRenderer' cannot be null");
        this.i18nResolver = requireNonNull(i18nResolver, "'i18nResolver' cannot be null");
        this.teamsMessageService = requireNonNull(teamsMessageService, "'teamsMessageService' cannot be null");
        this.objectMapper = new ObjectMapper();
        this.teamsRoomService = requireNonNull(teamsRoomService, "'teamsRoomService' cannot be null");
        this.teamsBotNotificationRecipientValidator = requireNonNull(teamsBotNotificationRecipientValidator, "'teamsBotNotificationRecipientValidator' "
                + "cannot be null");
    }

    @Override
    public void init(@Nullable final String configurationData) {
        logger.debug("action = init '{}'", configurationData);
        try {
            if (configurationData != null) {
                teamsBotNotificationSettings = objectMapper.readValue(configurationData, TeamsBotNotificationSettings.class);
            } else {
                teamsBotNotificationSettings = new TeamsBotNotificationSettings();
            }
        } catch (final IOException e) {
            teamsBotNotificationSettings = new TeamsBotNotificationSettings();
            logger.error("Cannot load settings", e);
        }
    }

    @NotNull
    @Override
    public List<NotificationTransport> getTransports() {
        logger.debug("action = getTransports");
        if (isSettingsFilled(teamsBotNotificationSettings)) {
            return Collections.singletonList(
                    new TeamsBotNotificationTransport(teamsBotNotificationSettings, teamsRoomService, teamsMessageService));
        } else {
            return Collections.emptyList();
        }
    }

    @NotNull
    @Override
    public String getDescription() {
        logger.debug("action = getDescription");
        return i18nResolver.getText(PLUGIN_DESCRIPTION_KEY);
    }

    @NotNull
    @Override
    public String getRecipientConfig() {
        logger.debug("action = getRecipientConfig, teamsBotNotificationSettings = {}", teamsBotNotificationSettings);
        try {
            return objectMapper.writeValueAsString(teamsBotNotificationSettings);
        } catch (final JsonProcessingException e) {
            logger.error("Cannot save settings", e);
            return "";
        }
    }

    @Override
    public void populate(@NotNull final Map<String, String[]> params) {
        requireNonNull(params, "'params' cannot be null");

        logger.debug("action = populate {}", params);
        teamsBotNotificationSettings = new TeamsBotNotificationSettings();
        teamsBotNotificationSettings.setBotAccessToken(this.getParam(BOT_ACCESS_TOKEN_KEY, params));
        teamsBotNotificationSettings.setRoomIdentifier(this.getParam(ROOM_IDENTIFIER_KEY, params));
    }

    @Override
    protected String getParam(@NotNull final String key, @NotNull final Map<String, String[]> params) {
        requireNonNull(key, "'key' cannot be null");
        requireNonNull(params, "'params' cannot be null");

        logger.debug("action = getParam key {}, params {}", key, params);
        return super.getParam(key, params);
    }

    @NotNull
    public String getEditHtml() {
        logger.debug("action = getEditHtml");

        final String editTemplate = this.notificationRecipientModuleDescriptor.getEditTemplate();
        final Map<String, Object> context = buildEditPageModel();
        return requireNonNull(this.templateRenderer.render(editTemplate, context));
    }

    @NotNull
    public String getViewHtml() {
        logger.debug("action = getViewHtml");

        final String viewTemplate = this.notificationRecipientModuleDescriptor.getViewTemplate();
        final Map<String, Object> context = buildViewPageModel();
        return requireNonNull(this.templateRenderer.render(viewTemplate, context));
    }

    @NotNull
    @Override
    public ErrorCollection validate(@NotNull final Map<String, String[]> params) {
        requireNonNull(params, "'params' cannot be null");

        logger.debug("action = validate, params {}", params);
        populate(params);
        return teamsBotNotificationRecipientValidator.validate(teamsBotNotificationSettings);
    }

    @Nullable
    public TeamsBotNotificationSettings getTeamsBotNotificationSettings() {
        return teamsBotNotificationSettings;
    }

    private Map<String, Object> buildEditPageModel() {
        final Map<String, Object> context = new HashMap<>();
        if (teamsBotNotificationSettings != null) {
            context.put(BOT_ACCESS_TOKEN_KEY, teamsBotNotificationSettings.getBotAccessToken());
            context.put(ROOM_IDENTIFIER_KEY, teamsBotNotificationSettings.getRoomIdentifier());
        }
        return context;
    }

    private Map<String, Object> buildViewPageModel() {
        final Map<String, Object> context = new HashMap<>();
        final String roomName = findRoomByIdentifierOrName(teamsBotNotificationSettings)
                .map(Room::getTitle)
                .orElse(i18nResolver.getText(ERROR_MESSAGE_CANNOT_OBTAIN_ROOM));
        context.put(ROOM_IDENTIFIER_KEY, roomName);
        return context;
    }

    private Optional<Room> findRoomByIdentifierOrName(final TeamsBotNotificationSettings teamsBotNotificationSettings) {
        final Optional<Room> result;
        if (isSettingsFilled(teamsBotNotificationSettings)) {
            result = teamsRoomService.findByIdentifierOrName(teamsBotNotificationSettings.getBotAccessToken(),
                    teamsBotNotificationSettings.getRoomIdentifier());
        } else {
            result = Optional.empty();
        }
        return result;
    }

    private boolean isSettingsFilled(final TeamsBotNotificationSettings teamsBotNotificationSettings) {
        return teamsBotNotificationSettings != null &&
                teamsBotNotificationSettings.getBotAccessToken() != null &&
                teamsBotNotificationSettings.getRoomIdentifier() != null;
    }
}