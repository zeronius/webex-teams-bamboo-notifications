package ut.net.cimbalek.bamboo.plugin.webexteamsnotifications;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atlassian.bamboo.plugin.descriptor.NotificationRecipientModuleDescriptor;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;

import junit.framework.Assert;

import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationRecipient;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationRecipientValidator;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationTransport;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.dto.TeamsBotNotificationSettings;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.entity.Room;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsMessageService;
import net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.service.TeamsRoomService;

import static net.cimbalek.bamboo.plugin.webexteamsnotifications.impl.TeamsBotNotificationRecipient.ERROR_MESSAGE_CANNOT_OBTAIN_ROOM;

public class TeamsBotNotificationRecipientTest {

    private TemplateRenderer templateRenderer;
    private I18nResolver i18nResolver;
    private TeamsMessageService teamsMessageService;
    private TeamsRoomService teamsRoomService;
    private TeamsBotNotificationRecipientValidator teamsBotNotificationRecipientValidator;
    private NotificationRecipientModuleDescriptor notificationRecipientModuleDescriptor;

    private static final String DEFAULT_BOT_TOKEN = "token";
    private static final String DEFAULT_ROOM_ID = "roomId";

    @Before
    public void setupMocks() {
        templateRenderer = Mockito.mock(TemplateRenderer.class);
        i18nResolver = Mockito.mock(I18nResolver.class);
        teamsMessageService = Mockito.mock(TeamsMessageService.class);
        teamsRoomService = Mockito.mock(TeamsRoomService.class);
        teamsBotNotificationRecipientValidator = Mockito.mock(TeamsBotNotificationRecipientValidator.class);
        notificationRecipientModuleDescriptor = Mockito.mock(NotificationRecipientModuleDescriptor.class);
    }

    @Test
    public void givenInit_whenNullConfigurationProvided_thenBlankConfigurationIsInitialized() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init((String) null);
        Assert.assertEquals(teamsBotNotificationRecipient.getTeamsBotNotificationSettings(), new TeamsBotNotificationSettings());
    }

    @Test
    public void givenInit_whenBlankConfigurationProvided_thenBlankConfigurationIsInitialized() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init("");
        Assert.assertEquals(teamsBotNotificationRecipient.getTeamsBotNotificationSettings(), new TeamsBotNotificationSettings());
    }

    @Test
    public void givenInit_whenFilledConfigurationProvided_thenTheSameConfigurationIsInitialized() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init(buildDefaultJsonConfigurationData());
        final TeamsBotNotificationSettings teamsBotNotificationSettings = buildDefaultTeamsBotNotificationSettings();

        Assert.assertEquals(teamsBotNotificationSettings, teamsBotNotificationRecipient.getTeamsBotNotificationSettings());
    }

    @Test
    public void givenGetTransports_whenNullConfigurationDataAreProvided_thenEmptyTransportListIsReturned() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init((String) null);

        Assert.assertEquals(Collections.emptyList(), teamsBotNotificationRecipient.getTransports());
    }

    @Test
    public void givenGetTransports_whenEmptyConfigurationDataAreProvided_thenEmptyTransportListIsReturned() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init(buildEmptyJsonConfigurationData());

        Assert.assertEquals(Collections.emptyList(), teamsBotNotificationRecipient.getTransports());
    }

    @Test
    public void givenGetTransports_whenFilledConfigurationDataAreProvided_thenSingleTransportListIsReturned() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init(buildDefaultJsonConfigurationData());
        final List<TeamsBotNotificationTransport> teamsBotNotificationTransports = Collections.singletonList(new TeamsBotNotificationTransport
                (buildDefaultTeamsBotNotificationSettings(), teamsRoomService, teamsMessageService));

        Assert.assertEquals(teamsBotNotificationTransports, teamsBotNotificationRecipient.getTransports());
    }

    @Test
    public void givenGetDescription_thenLocalizedDescriptionIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String localizedDescription = "localizedDescription";
        Mockito.when(i18nResolver.getText(Mockito.anyString())).thenReturn(localizedDescription);
        final String description = teamsBotNotificationRecipient.getDescription();

        Assert.assertEquals(localizedDescription, description);
    }

    @Test
    public void givenGetRecipientConfig_whenNullConfigurationDataAreProvided_thenBlankSerializedConfigurationIsReturned() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init((String) null);

        Assert.assertEquals(buildBlankJsonConfigurationData(), teamsBotNotificationRecipient.getRecipientConfig());
    }

    @Test
    public void givenGetRecipientConfig_whenEmptyConfigurationDataAreProvided_thenBlankSerializedConfigurationIsReturned() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init(buildEmptyJsonConfigurationData());

        Assert.assertEquals(buildBlankJsonConfigurationData(), teamsBotNotificationRecipient.getRecipientConfig());
    }

    @Test
    public void givenGetRecipientConfig_whenFilledConfigurationDataAreProvided_thenFilledSerializedConfigurationIsReturned() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String defaultValidJsonConfigurationData = buildDefaultJsonConfigurationData();
        teamsBotNotificationRecipient.init(defaultValidJsonConfigurationData);

        Assert.assertEquals(defaultValidJsonConfigurationData, teamsBotNotificationRecipient.getRecipientConfig());
    }

    @Test(expected = NullPointerException.class)
    public void givenPopulate_whenParametersAreNotProvided_thenExceptionIsThrown() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.populate(Collections.emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void givenPopulate_whenNullParametersAreProvided_thenExceptionIsThrown() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final Map<String, String[]> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY, null);
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, null);
        teamsBotNotificationRecipient.populate(parameters);
    }

    @Test
    public void givenPopulate_whenBlankParametersAreProvided_thenBlankConfigurationIsSet() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final TeamsBotNotificationSettings teamsBotNotificationSettings = new TeamsBotNotificationSettings();
        teamsBotNotificationSettings.setRoomIdentifier("");
        teamsBotNotificationSettings.setBotAccessToken("");
        final Map<String, String[]> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY, new String[]{teamsBotNotificationSettings.getBotAccessToken()});
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, new String[]{teamsBotNotificationSettings.getRoomIdentifier()});
        teamsBotNotificationRecipient.populate(parameters);

        Assert.assertEquals(teamsBotNotificationSettings, teamsBotNotificationRecipient.getTeamsBotNotificationSettings());
    }

    @Test
    public void givenPopulate_whenFilledParametersAreProvided_thenFilledConfigurationIsSet() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final Map<String, String[]> parameters = new HashMap<>();
        final TeamsBotNotificationSettings teamsBotNotificationSettings = buildDefaultTeamsBotNotificationSettings();
        parameters.put(TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY, new String[]{teamsBotNotificationSettings.getBotAccessToken()});
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, new String[]{teamsBotNotificationSettings.getRoomIdentifier()});
        teamsBotNotificationRecipient.populate(parameters);

        Assert.assertEquals(teamsBotNotificationSettings, teamsBotNotificationRecipient.getTeamsBotNotificationSettings());
    }

    @Test
    public void givenGetEditHtml_whenEditHtmlForNullConfigurationIsRequired_thenTemplateWithoutContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String expectedResult = "expectedResult";
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        Mockito.when(notificationRecipientModuleDescriptor.getEditTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, new HashMap<>())).thenReturn(expectedResult);
        final String result = teamsBotNotificationRecipient.getEditHtml();

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void givenGetEditHtml_whenEditHtmlForBlankConfigurationIsRequired_thenTemplateWithBlankContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String expectedResult = "expectedResult";
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        teamsBotNotificationRecipient.init(buildBlankJsonConfigurationData());
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY, null);
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, null);
        Mockito.when(notificationRecipientModuleDescriptor.getEditTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, parameters)).thenReturn(expectedResult);
        final String result = teamsBotNotificationRecipient.getEditHtml();

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void givenGetEditHtml_whenEditHtmlForFilledConfigurationIsRequired_thenTemplateWithFilledContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String expectedResult = "expectedResult";
        final TeamsBotNotificationSettings teamsBotNotificationSettings = buildDefaultTeamsBotNotificationSettings();
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        teamsBotNotificationRecipient.init(buildDefaultJsonConfigurationData());
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY, teamsBotNotificationSettings.getBotAccessToken());
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, teamsBotNotificationSettings.getRoomIdentifier());
        Mockito.when(notificationRecipientModuleDescriptor.getEditTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, parameters)).thenReturn(expectedResult);
        final String result = teamsBotNotificationRecipient.getEditHtml();

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void givenGetViewHtml_whenViewHtmlForNullConfigurationIsRequired_thenTemplateWithErrorContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String renderResult = "renderResult";
        final String unknownRoomMessage = "unknownRoomMessage";
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, unknownRoomMessage);
        Mockito.when(i18nResolver.getText(ERROR_MESSAGE_CANNOT_OBTAIN_ROOM)).thenReturn(unknownRoomMessage);
        Mockito.when(notificationRecipientModuleDescriptor.getViewTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, parameters)).thenReturn(renderResult);
        Mockito.when(teamsRoomService.findByIdentifierOrName(DEFAULT_BOT_TOKEN, DEFAULT_ROOM_ID)).thenReturn(Optional.empty());
        final String result = teamsBotNotificationRecipient.getViewHtml();
    }

    @Test
    public void givenGetViewHtml_whenViewHtmlForBlankConfigurationIsRequired_thenTemplateWithErrorContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String renderResult = "renderResult";
        final String unknownRoomMessage = "unknownRoomMessage";
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        teamsBotNotificationRecipient.init(buildBlankJsonConfigurationData());
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, unknownRoomMessage);
        Mockito.when(i18nResolver.getText(ERROR_MESSAGE_CANNOT_OBTAIN_ROOM)).thenReturn(unknownRoomMessage);
        Mockito.when(notificationRecipientModuleDescriptor.getViewTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, parameters)).thenReturn(renderResult);
        Mockito.when(teamsRoomService.findByIdentifierOrName(DEFAULT_BOT_TOKEN, DEFAULT_ROOM_ID)).thenReturn(Optional.empty());
        final String result = teamsBotNotificationRecipient.getViewHtml();

        Assert.assertEquals(renderResult, result);
    }

    @Test
    public void givenGetViewHtml_whenViewHtmlForUnknownRoomIsRequired_thenTemplateWithErrorContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String renderResult = "renderResult";
        final String unknownRoomMessage = "unknownRoomMessage";
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        teamsBotNotificationRecipient.init(buildDefaultJsonConfigurationData());
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, unknownRoomMessage);
        Mockito.when(i18nResolver.getText(ERROR_MESSAGE_CANNOT_OBTAIN_ROOM)).thenReturn(unknownRoomMessage);
        Mockito.when(notificationRecipientModuleDescriptor.getViewTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, parameters)).thenReturn(renderResult);
        Mockito.when(teamsRoomService.findByIdentifierOrName(DEFAULT_BOT_TOKEN, DEFAULT_ROOM_ID)).thenReturn(Optional.empty());
        final String result = teamsBotNotificationRecipient.getViewHtml();

        Assert.assertEquals(renderResult, result);
    }

    @Test
    public void givenGetViewHtml_whenViewHtmlForKnownRoomIsRequired_thenTemplateWithFilledContextIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        final String template = "template";
        final String renderResult = "renderResult";
        final String roomName = "roomName";
        final Room room = new Room();
        room.setTitle(roomName);
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        teamsBotNotificationRecipient.init(buildDefaultJsonConfigurationData());
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, roomName);
        Mockito.when(notificationRecipientModuleDescriptor.getViewTemplate()).thenReturn(template);
        Mockito.when(templateRenderer.render(template, parameters)).thenReturn(renderResult);
        Mockito.when(teamsRoomService.findByIdentifierOrName(DEFAULT_BOT_TOKEN, DEFAULT_ROOM_ID)).thenReturn(Optional.of(room));
        final String result = teamsBotNotificationRecipient.getViewHtml();

        Assert.assertEquals(renderResult, result);
    }

    @Test
    public void givenValidate_whenParametersAreValidated_thenResultAccordingToValidatorIsProvided() {
        final TeamsBotNotificationRecipient teamsBotNotificationRecipient = buildTeamsBotNotificationRecipient();
        teamsBotNotificationRecipient.init(notificationRecipientModuleDescriptor);
        teamsBotNotificationRecipient.init(buildDefaultJsonConfigurationData());
        final TeamsBotNotificationSettings teamsBotNotificationSettings = buildDefaultTeamsBotNotificationSettings();
        final Map<String, String[]> parameters = new HashMap<>();
        parameters.put(TeamsBotNotificationRecipient.ROOM_IDENTIFIER_KEY, new String[]{teamsBotNotificationSettings.getRoomIdentifier()});
        parameters.put(TeamsBotNotificationRecipient.BOT_ACCESS_TOKEN_KEY, new String[]{teamsBotNotificationSettings.getBotAccessToken()});
        Mockito.when(teamsBotNotificationRecipientValidator.validate(teamsBotNotificationSettings)).thenReturn(new SimpleErrorCollection());
        final ErrorCollection validationResult = teamsBotNotificationRecipient.validate(parameters);

        Assert.assertFalse(validationResult.hasAnyErrors());
    }

    private TeamsBotNotificationRecipient buildTeamsBotNotificationRecipient() {
        return new TeamsBotNotificationRecipient(templateRenderer, i18nResolver, teamsMessageService, teamsRoomService,
                teamsBotNotificationRecipientValidator);
    }

    private TeamsBotNotificationSettings buildDefaultTeamsBotNotificationSettings() {
        final TeamsBotNotificationSettings teamsBotNotificationSettings = new TeamsBotNotificationSettings();
        teamsBotNotificationSettings.setBotAccessToken(DEFAULT_BOT_TOKEN);
        teamsBotNotificationSettings.setRoomIdentifier(DEFAULT_ROOM_ID);
        return teamsBotNotificationSettings;
    }

    private String buildDefaultJsonConfigurationData() {
        return "{\"botAccessToken\":\"" + DEFAULT_BOT_TOKEN + "\",\"roomIdentifier\":\"" + DEFAULT_ROOM_ID + "\"}";
    }

    private String buildBlankJsonConfigurationData() {
        return "{\"botAccessToken\":null,\"roomIdentifier\":null}";
    }

    private String buildEmptyJsonConfigurationData() {
        return "{}";
    }
}